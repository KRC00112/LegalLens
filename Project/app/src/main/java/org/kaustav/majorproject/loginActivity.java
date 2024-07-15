package org.kaustav.majorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
//hello
public class loginActivity extends AppCompatActivity {


    TextInputEditText editTextEmail,editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ImageView passwordVisibilityToggle;
    TextView forgotPassword;

    boolean isPasswordVisible = false;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        buttonLogin=findViewById(R.id.loginToTheMain);
        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        forgotPassword=findViewById(R.id.go_to_forgot_password);
        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(loginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());


                if (TextUtils.isEmpty(password)||TextUtils.isEmpty(email)) {
                    Toast.makeText(loginActivity.this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hash the entered password
                String hashedPassword = hashPassword(password);

                // Authenticate with Firebase using the hashed password
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    try{
                                        throw task.getException();
                                    }catch (FirebaseAuthInvalidUserException e){
                                        Toast.makeText(getApplicationContext(), "User does not exist or is no longer valid. please register again. ", Toast.LENGTH_SHORT).show();

                                    }catch(FirebaseAuthInvalidCredentialsException e){
                                        Toast.makeText(getApplicationContext(), "Invalid Credentials, check password and email ID ", Toast.LENGTH_SHORT).show();
                                    }catch (Exception e){
                                        Toast.makeText(loginActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    }

                                    // If sign in fails, display a message to the user.

                                }
                            }
                        });
            }
        });
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

    public void redirectToRegister(View v){
        Intent i=new Intent(this, registerActivity.class);
        startActivity(i);
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
}