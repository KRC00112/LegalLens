package org.kaustav.majorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button resetBtn;
    EditText enterEmail;
    FirebaseAuth mAuth;
    String strEmail;
    DatabaseReference databaseReference;
    private ImageButton backButton;
    private TextView userNotExist;
    private Button registerHere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetBtn = findViewById(R.id.reset_password);
        enterEmail = findViewById(R.id.enter_email_edittext_forgot);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        backButton = findViewById(R.id.goToLoginFromReset);
        userNotExist=findViewById(R.id.textViewNotExist);
        registerHere=findViewById(R.id.registerHere);
        registerHere.setVisibility(View.GONE);
        userNotExist.setVisibility(View.GONE);

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
                strEmail = enterEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    checkEmailExists(strEmail);
                } else {
                    enterEmail.setError("Email field cannot be empty");
                }
            }
        });
    }

    public void RegisterHereButton(View v){
        Intent i=new Intent(this, registerActivity.class);
        startActivity(i);
    }

    private void checkEmailExists(final String email) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean emailExists = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    if (userEmail != null && userEmail.equals(email)) {
                        emailExists = true;
                        break;
                    }
                }

                if (emailExists) {
                    ResetPassword();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email does not exist in our records.", Toast.LENGTH_SHORT).show();
                    userNotExist.setVisibility(View.VISIBLE);
                    registerHere.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ResetPassword() {
        mAuth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPasswordActivity.this, "Reset Password link has been sent to your registered Email.", Toast.LENGTH_SHORT).show();

                // Example: Update user information in Realtime Database after password reset
                String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                // Update a specific field, e.g., passwordResetRequested
                userRef.child("passwordResetRequested").setValue(true);

                Intent intent = new Intent(ForgotPasswordActivity.this, loginActivity.class); // Ensure that your LoginActivity class name is correct
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPasswordActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
