package org.kaustav.projectadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    Button resetBtn;
    EditText enterEmail, newPassword;

    FirebaseDatabase database;
    DatabaseReference reference;

    ImageView passwordVisibilityToggle;

    private ImageButton backButton;

    private TextView problemPrompt;
    private Button resetToReg;


    boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        backButton = findViewById(R.id.goToLoginFromReset);
        resetBtn = findViewById(R.id.reset_password);
        enterEmail = findViewById(R.id.enter_email_edittext_forgot);
        newPassword = findViewById(R.id.new_password_edittext_forgot);
        problemPrompt=findViewById(R.id.problemPrompt);
        problemPrompt.setVisibility(View.GONE);
        resetToReg=findViewById(R.id.resetToReg);
        resetToReg.setVisibility(View.GONE);

        resetToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ForgotPasswordActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });


        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Admins");
        passwordVisibilityToggle=findViewById(R.id.passwordVisibilityToggle);
        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous main activity
                finish();
            }
        });


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = enterEmail.getText().toString().trim();
                String password = newPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                resetPassword(email, password);
            }
        });
    }

    private void resetPassword(String email, String password) {
        String encodedEmail = encodeEmail(email);
        String hashedPassword = hashPassword(password);

        reference.child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Update only the password field
                    reference.child(encodedEmail).child("password").setValue(hashedPassword, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Error resetting password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Admin not found", Toast.LENGTH_SHORT).show();
                    problemPrompt.setVisibility(View.VISIBLE);
                    resetToReg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibility() {
        int cursorPosition = newPassword.getSelectionEnd(); // Save cursor position

        if (isPasswordVisible) {
            // hide password
            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            // show password
            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        // Restore cursor position
        newPassword.setSelection(cursorPosition);

        // Toggle visibility state
        isPasswordVisible = !isPasswordVisible;

        // Change visibility toggle icon
        if (isPasswordVisible) {
            passwordVisibilityToggle.setImageResource(R.drawable.visibility_on);
        } else {
            passwordVisibilityToggle.setImageResource(R.drawable.visibility_off);
        }
    }


    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}
