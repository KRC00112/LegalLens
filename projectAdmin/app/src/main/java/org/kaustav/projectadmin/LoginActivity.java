package org.kaustav.projectadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText editTextEid, editTextPassword;
    Button buttonLogin;
    ImageView passwordVisibilityToggle;

    TextView forgotPassword;

    boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEid = findViewById(R.id.emp_id_login);
        editTextPassword = findViewById(R.id.passwordLogin);
        buttonLogin = findViewById(R.id.loginToTheFunc);
        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);

        forgotPassword=findViewById(R.id.go_to_forgot_password);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        // Check if user is already logged in
        if (isLoggedIn()) {
            String emp_id = getLoggedInUserEid();
            redirectToFunctionActivity(emp_id);
            return; // Skip login
        }

        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validateEmail() | !validatePassword()){

                }else{

                    checkUser();

                }



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

    public void redirectToRegister(View v) {
        Intent i = new Intent(this, registerActivity.class);
        startActivity(i);

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

    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }

    // Get the email of the logged-in user
    private String getLoggedInUserEid() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return preferences.getString("eid", "");
    }

    // Redirect to functionActivity
    private void redirectToFunctionActivity(String emp_id) {
        Intent intent = new Intent(LoginActivity.this, functionActivity.class);
        intent.putExtra("EID", emp_id);
        startActivity(intent);
        finish();
    }

    public Boolean validateEmail() {
        String val = editTextEid.getText().toString().trim();
        String regex = "LL20(2[4-9]|[3-9][0-9])(DEV|MAN)\\d{4}";

        if (val.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Employee ID cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!val.matches(regex)) {
            Toast.makeText(getApplicationContext(), "Invalid Employee ID format", Toast.LENGTH_SHORT).show();

            return false;
        } else {
            editTextEid.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {

        String val = editTextPassword.getText().toString();
        if (val.isEmpty()) {

            Toast.makeText(getApplicationContext(), "password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            editTextPassword.setError(null);
            return true;
        }

    }

    public void checkUser() {
        String adminEid = editTextEid.getText().toString().trim();
        String adminPassword = editTextPassword.getText().toString().trim();

        // Check if either of the fields is empty
        if (adminEid.isEmpty() || adminPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill up all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admins");
        Query checkUserDatabase = reference.orderByChild("eid").equalTo(adminEid);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = childSnapshot.child("password").getValue(String.class);
                        // Hash the entered password
                        String hashedPassword = hashPassword(adminPassword);

                        if (passwordFromDB.equals(hashedPassword)) {
                            // Passwords match, proceed with login
                            editTextEid.setError(null);

                            // Store login status and email in SharedPreferences
                            SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("eid", adminEid); // Use "eid" instead of "EID"
                            editor.apply();

                            redirectToFunctionActivity(adminEid); // Redirect to functionActivity
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            return; // No need to continue after finding a match
                        } else {
                            // Passwords do not match, show error message
                            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                            editTextPassword.requestFocus();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Admin does not exist", Toast.LENGTH_SHORT).show();
                    editTextEid.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


}
