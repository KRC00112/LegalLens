package org.kaustav.majorproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settingsActivity extends AppCompatActivity {

    private ImageButton logoutButton;
    private FirebaseUser user;
    private FirebaseAuth auth;
    RatingBar ratingBar;
    private ImageButton openFeedback;
    private ImageButton openAboutApp;



    ImageButton openChgUsername;
    private Button closeBtn;

    private ImageButton backButton;
    private TextView userDetailsTextView;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        openFeedback = findViewById(R.id.feedback);
        openChgUsername = findViewById(R.id.change_username);
        openAboutApp=findViewById(R.id.about_app);

        openAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View aboutView =getLayoutInflater().inflate(R.layout.about_activity,null);

                Dialog aboutDialog = new Dialog(settingsActivity.this);
                aboutDialog.setContentView(aboutView);
                aboutDialog.setCancelable(true);
                aboutDialog.show(); // Show the feedback dialog

            }
        });


        openFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the layout for the feedback dialog
                View feedbackView = getLayoutInflater().inflate(R.layout.activity_feedback, null);
                // Find the views within the inflated layout
                Button closeBtn = feedbackView.findViewById(R.id.closeBtn);
                EditText reviewEditText = feedbackView.findViewById(R.id.review);
                RatingBar ratingBar = feedbackView.findViewById(R.id.ratingBar);

                // Create the feedback dialog
                Dialog feedbackDialog = new Dialog(settingsActivity.this);
                feedbackDialog.setContentView(feedbackView);
                feedbackDialog.setCancelable(false);

                // Set OnClickListener for the close button
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feedbackDialog.dismiss(); // Close the dialog when close button is clicked
                    }
                });

                // Set OnClickListener for the "Send feedback" button
                Button sendFeedbackBtn = feedbackView.findViewById(R.id.sendfeedback);
                sendFeedbackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the review text and rating
                        String review = reviewEditText.getText().toString();
                        float rating = ratingBar.getRating();

                        // Save the data to Firebase Database
                        saveFeedbackToFirebase(review, rating);

                        // Close the dialog
                        feedbackDialog.dismiss();

                        Toast.makeText(settingsActivity.this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();

                    }
                });

                feedbackDialog.show(); // Show the feedback dialog
            }
        });

        openChgUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View change_username = getLayoutInflater().inflate(R.layout.change_username_activity, null);
                // Find the views within the inflated layout
                Button closeBtn = change_username.findViewById(R.id.closeBtn);
                EditText usernameEditText = change_username.findViewById(R.id.new_username);

                Dialog chgUsernameDialog = new Dialog(settingsActivity.this);
                chgUsernameDialog.setContentView(change_username);
                chgUsernameDialog.setCancelable(false);

                usernameEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // No action needed before text changes
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 15) {
                            usernameEditText.setText(s.subSequence(0, 15));
                            usernameEditText.setSelection(14);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // No action needed after text changes
                    }
                });
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chgUsernameDialog.dismiss(); // Close the dialog when close button is clicked
                    }
                });

                Button confirmChangeUsername = change_username.findViewById(R.id.change_username);

                confirmChangeUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newUsername = usernameEditText.getText().toString().trim();
                        if (!newUsername.isEmpty()) {
                            changeUsername(newUsername);
                            chgUsernameDialog.dismiss();
                            Toast.makeText(settingsActivity.this, "Username reset successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(settingsActivity.this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                chgUsernameDialog.show();
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        logoutButton = findViewById(R.id.logout);
        userDetailsTextView = findViewById(R.id.user_details);
        backButton = findViewById(R.id.goToMainFromSetting);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous main activity
                finish();
            }
        });

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Fetch username from Firebase Database
            usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        if (currentUser != null) {
                            userDetailsTextView.setText(currentUser.getUsername());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void saveFeedbackToFirebase(String review, float rating) {
        if (user != null) {
            // Get a reference to your Firebase Database
            DatabaseReference userFeedbackRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("feedback");

            // Generate a unique key for the feedback entry
            String feedbackId = userFeedbackRef.push().getKey();

            // Create a Feedback object with the review and rating
            User feedback = new User(review, rating);

            // Save the feedback object to the database under the generated key
            userFeedbackRef.child(feedbackId).setValue(feedback);
        }
    }

    private void changeUsername(String newUsername) {
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.child("username").setValue(newUsername).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userDetailsTextView.setText(newUsername);

                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", newUsername);
                    editor.apply();
                } else {
                    Toast.makeText(settingsActivity.this, "Failed to change username. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), landerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    interface OnUsernameCheckedListener {
        void onUsernameChecked(boolean exists);
    }
}
