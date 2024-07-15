package org.kaustav.projectadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class functionActivity extends AppCompatActivity {

    TextView EmpID;
    TextView userCountTextView; // TextView to display user count
    CardView logout;
    TextView totalSum;
    private LineChartView lineChartView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        EmpID = findViewById(R.id.user_details);
        userCountTextView = findViewById(R.id.user_count_text_view);
        logout = findViewById(R.id.logout);
        totalSum = findViewById(R.id.totalSumm);

        lineChartView = new LineChartView(this, null);

        LinearLayout chartContainer = findViewById(R.id.chart_container);
        chartContainer.addView(lineChartView);

        fetchUserRegistrationData();

        // Set employee ID from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String userEid = preferences.getString("eid", "");
        EmpID.setText(userEid + "'s");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        // Fetch user count and total summaries count from Firebase and display it
        fetchUserCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch user count and total summaries count from Firebase and display it
        fetchUserCount();
        fetchUserRegistrationData();

    }

    public void openUserProfilesList(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void fetchUserRegistrationData() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Long> timestamps = new ArrayList<>();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Long timestamp = userSnapshot.child("timestampreg").getValue(Long.class);
                            if (timestamp != null) {
                                timestamps.add(timestamp);
                            }
                        }
                        lineChartView.setData(timestamps);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
    private void fetchUserCount() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get count of children (users)
                        long userCount = dataSnapshot.getChildrenCount();
                        // Display user count in userCountTextView
                        userCountTextView.setText("Total Users: " + userCount);

                        // Calculate total summaries count
                        long totalSummaries = 0;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot historySnapshot = userSnapshot.child("history");
                            totalSummaries += historySnapshot.getChildrenCount();
                        }
                        // Display total summaries count in totalSum TextView
                        totalSum.setText("Total number of summaries made: " + totalSummaries);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
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
        // Clear the saved employee ID in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("isLoggedIn");
        editor.remove("eid");
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(functionActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Quit the application when back button is pressed
        finishAffinity();
    }
}
