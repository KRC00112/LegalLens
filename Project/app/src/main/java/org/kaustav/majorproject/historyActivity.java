package org.kaustav.majorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class historyActivity extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseAuth auth;

    private TextView userDetailsTextView;

    private DatabaseReference usersRef;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyItemList;
    private ImageButton backButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backButton = findViewById(R.id.goToMainFromHistory);


        // Initialize list of history items
        historyItemList = new ArrayList<>();

        // Initialize adapter with the historyItemList
        historyAdapter = new HistoryAdapter(historyItemList);
        recyclerView.setAdapter(historyAdapter);

        // Populate historyItemList with timestamps from Firebase
        populateHistoryItemListFromFirebase();

        userDetailsTextView = findViewById(R.id.user_details);
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

    private void populateHistoryItemListFromFirebase() {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users")
                .child(auth.getCurrentUser().getUid())
                .child("history");

        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot historySnapshot : dataSnapshot.getChildren()) {
                    // For each child node under "history"
                    long timestamp = historySnapshot.child("timestamp").getValue(Long.class);
                    String input = historySnapshot.child("input").getValue(String.class);
                    String output = historySnapshot.child("output").getValue(String.class);



                    historyItemList.add(new HistoryItem(timestamp,input,output.substring(15,output.length()-2)));
                }

                Collections.reverse(historyItemList);
                // Notify the adapter that the data set has changed
                historyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }



}