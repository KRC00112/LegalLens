package org.kaustav.projectadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class registerActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextEid;
    Button buttonReg;
    TextView passwordStrength, pUpper, pLower, pNumber, pSpecial ,pLength;

    FirebaseDatabase database;
    DatabaseReference reference;

    ImageView passwordVisibilityToggle;

    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.emailReg);
        editTextPassword = findViewById(R.id.passwordReg);
        editTextEid = findViewById(R.id.emp_id);
        buttonReg = findViewById(R.id.registerToTheFunc);
        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        passwordStrength = findViewById(R.id.pStrength);
        pUpper = findViewById(R.id.pUpper);
        pLower = findViewById(R.id.pLower);
        pNumber = findViewById(R.id.pNumber);
        pSpecial = findViewById(R.id.pSpecial);
        pLength=findViewById(R.id.pLength);
        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        findViewById(R.id.passwordStrengthDetails).setVisibility(View.GONE);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Admins");

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
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String emp_id=editTextEid.getText().toString();
                // Check if all fields are filled
                if (email.isEmpty() || password.isEmpty() ||emp_id.isEmpty()) {
                    Toast.makeText(registerActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

                if (!isValidEmployeeID(emp_id)) {
                    Toast.makeText(registerActivity.this, "Please enter a valid employee ID", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

                // Check if password length is less than 8
                if (password.length() < 8 ||
                        !containsUpperCase(password) ||
                        !containsLowerCase(password) ||
                        !containsNumber(password) ||
                        !containsSpecialCharacter(password)) {
                    Toast.makeText(registerActivity.this, "Create a Strong password", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }


                // Check if email is valid
                if (!isValidEmail(email)) {
                    Toast.makeText(registerActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }


                // All conditions passed, proceed with signup
                String encodedEmail = encodeEmail(email);
                String hashedPassword = hashPassword(password);

                // Modify the Admin object creation to use the hashed password
                Admin admin = new Admin(email, hashedPassword, emp_id);
                // Check if the user already exists in the database
                // Check if the user already exists in the database
                // Check if the user already exists in the database
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean emailExists = false;
                        boolean eidExists = false;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Admin existingAdmin = dataSnapshot.getValue(Admin.class);
                            if (existingAdmin != null) {
                                if (existingAdmin.getEmail().equals(email)) {
                                    // Email already exists
                                    emailExists = true;
                                }
                                if (existingAdmin.getEid().equals(emp_id)) {
                                    // Employee ID already exists
                                    eidExists = true;
                                }
                            }

                        }

                        if (emailExists && eidExists) {
                            // Both email and employee ID exist, show error message
                            Toast.makeText(registerActivity.this, "An account with both this email ID and Employee ID already exists", Toast.LENGTH_SHORT).show();
                        } else if (emailExists) {
                            // Email exists, show error message
                            Toast.makeText(registerActivity.this, "An account with this email ID already exists", Toast.LENGTH_SHORT).show();
                        } else if (eidExists) {
                            // Employee ID exists, show error message
                            Toast.makeText(registerActivity.this, "An account with this Employee ID already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Neither email nor employee ID exists, proceed with signup
                            reference.child(encodedEmail).setValue(admin, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        // Signup successful, store login status and email in SharedPreferences
                                        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("eid", emp_id);
                                        editor.apply();

                                        // Redirect to functionActivity
                                        Intent intent = new Intent(registerActivity.this, functionActivity.class);
                                        intent.putExtra("EID", emp_id); // Pass email to FunctionActivity
                                        startActivity(intent);
                                        finish(); // Finish the current activity to prevent going back to it
                                        Toast.makeText(registerActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                        // Signup failed, handle error
                                        Toast.makeText(registerActivity.this, "Signup failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                        Toast.makeText(registerActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add password bytes to digest
            md.update(password.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // Convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            // Return the hashed password
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle NoSuchAlgorithmException appropriately
            return null;
        }
    }

    private boolean isValidEmail(String email) {
        // Define the regular expression pattern for email validation
        String emailPattern = "[a-zA-Z0-9]*@(gmail|hotmail)\\.com";

        // Return true if the email matches the pattern, false otherwise
        return email.matches(emailPattern);
    }

    private boolean isValidEmployeeID(String empID) {
        // Define the regular expression pattern for employee ID validation
        String empIDPattern = "LL20(2[4-9]|[3-9][0-9])(DEV|MAN)\\d{4}";

        // Return true if the employee ID matches the pattern, false otherwise
        return empID.matches(empIDPattern);
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


    // Method to encode the email address for Firebase Database path
    private String encodeEmail(String email) {
        return email.replace(".", ",");
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
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
