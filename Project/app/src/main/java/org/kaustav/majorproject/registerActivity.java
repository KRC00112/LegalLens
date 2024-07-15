package org.kaustav.majorproject;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.kaustav.majorproject.MainActivity;
import org.kaustav.majorproject.R;
import org.kaustav.majorproject.User;
import org.kaustav.majorproject.loginActivity;

public class registerActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextUsername;
    TextView passwordStrength, pUpper, pLower, pNumber, pSpecial ,pLength;
    Button buttonReg;
    FirebaseAuth mAuth;
    ImageView passwordVisibilityToggle;

    boolean isPasswordVisible = false;

    FirebaseDatabase database;

    DatabaseReference mDatabase;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        editTextEmail = findViewById(R.id.emailReg);
        editTextPassword = findViewById(R.id.passwordReg);
        editTextUsername = findViewById(R.id.username);
        passwordStrength = findViewById(R.id.pStrength);
        pUpper = findViewById(R.id.pUpper);
        pLower = findViewById(R.id.pLower);
        pNumber = findViewById(R.id.pNumber);
        pSpecial = findViewById(R.id.pSpecial);
        pLength=findViewById(R.id.pLength);
        buttonReg = findViewById(R.id.registerToTheMain);
        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        findViewById(R.id.passwordStrengthDetails).setVisibility(View.GONE);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update password strength TextView and individual criteria TextViews
                String password = s.toString();
                int upperCase = containsUpperCase(password) ? 1 : 0;
                int lowerCase = containsLowerCase(password) ? 1 : 0;
                int number = containsNumber(password) ? 1 : 0;
                int specialChar = containsSpecialCharacter(password) ? 1 : 0;
                int length = password.length(); // Calculate password length

                displayPasswordStrength(upperCase, lowerCase, number, specialChar, length);
                updateCriteriaTextViews(upperCase, lowerCase, number, specialChar, length); // Pass length to the method

                if (!TextUtils.isEmpty(password)) {
                    findViewById(R.id.passwordStrengthDetails).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.passwordStrengthDetails).setVisibility(View.GONE);
                }

//                if (password.length() < 8 || (upperCase == 0 || lowerCase == 0 || number == 0 || specialChar == 0)) {
//                    buttonReg.setEnabled(false);
//                    buttonReg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonDisabled)));
//                } else {
//                    buttonReg.setEnabled(true);
//                    buttonReg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonEnabled)));
//                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });

        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, username;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                username = String.valueOf(editTextUsername.getText());

                // Validate email and password
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                    // Show an error or toast indicating empty fields
                    Toast.makeText(registerActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    // Show an error or toast indicating invalid email format
                    Toast.makeText(registerActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 8 ||
                        !containsUpperCase(password) ||
                        !containsLowerCase(password) ||
                        !containsNumber(password) ||
                        !containsSpecialCharacter(password)) {

                    Toast.makeText(registerActivity.this, "Create a Strong password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.length() > 15) {
                    // Show a toast message indicating the maximum length of the username
                    Toast.makeText(registerActivity.this, "Username must be at most 15 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(registerActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(registerActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(registerActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hash the password using SHA-256
                String hashedPassword = hashPassword(password);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(registerActivity.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();

                                    // Get user ID
                                    String userId = mAuth.getCurrentUser().getUid();

                                    // Create a user object with email
                                    User user = new User(email, hashedPassword, username);

                                    // Add user to database
                                    mDatabase.child("users").child(userId).setValue(user);

                                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(registerActivity.this, "User already exists.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private boolean isValidEmail(String email) {
        // Define the regular expression pattern for email validation
        String emailPattern = "[a-zA-Z0-9]*@(gmail|hotmail)\\.com";

        // Return true if the email matches the pattern, false otherwise
        return email.matches(emailPattern);
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void updateCriteriaTextViews(int upperCase, int lowerCase, int number, int specialChar, int length) {
        // Update criteria TextViews to show if each criteria exists or not
        pUpper.setText(upperCase > 0 ? "Contains at least one uppercase letter: Yes" : " Contains at least one uppercase letter: No");

        pLower.setText(lowerCase > 0 ? "Contains at least one lowercase letter : Yes" : "Contains at least one lowercase letter : No");

        pNumber.setText(number > 0 ? "Contains at least one number : Yes" : "Contains at least one number : No");

        pSpecial.setText(specialChar > 0 ? "Contains at least one special Character : Yes" : "Contains at least one special Character : No");

        pLength.setText("Password Length: " + length);

    }

    private void displayPasswordStrength(int upperCase, int lowerCase, int number, int specialChar, int length) {
        // Update password strength TextView based on the presence of uppercase, lowercase, number, and special character
        if (upperCase > 0 && lowerCase > 0 && number > 0 && specialChar > 0 && length>=8) {
            passwordStrength.setText("Password strength: Strong");
            passwordStrength.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            passwordStrength.setText("Password strength: Weak");
            passwordStrength.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    private boolean containsUpperCase(String password) {
        // Check if password contains at least one uppercase letter
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLowerCase(String password) {
        // Check if password contains at least one lowercase letter
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNumber(String password) {
        // Check if password contains at least one number
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecialCharacter(String password) {
        // Check if password contains at least one special character
        String specialCharacters = "!@#$%^&*()-_=+[{]}|;:',<.>/?";
        for (char c : password.toCharArray()) {
            if (specialCharacters.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    private void togglePasswordVisibility() {
        int cursorPosition = editTextPassword.getSelectionEnd(); // Save cursor position

        if (isPasswordVisible) {
            // hide password
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            // show password
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        // Restore cursor position
        editTextPassword.setSelection(cursorPosition);

        // Toggle visibility state
        isPasswordVisible = !isPasswordVisible;

        // Change visibility toggle icon
        if (isPasswordVisible) {
            passwordVisibilityToggle.setImageResource(R.drawable.visibility_on);
        } else {
            passwordVisibilityToggle.setImageResource(R.drawable.visibility_off);
        }
    }

    public void redirectToLogin(View v) {
        Intent i = new Intent(this, loginActivity.class);
        startActivity(i);
    }
}
