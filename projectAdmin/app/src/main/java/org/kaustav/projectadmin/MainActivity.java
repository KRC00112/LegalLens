package org.kaustav.projectadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    TextView EmpID;
    TextView userCountTextView; // TextView to display user count
    private SearchView searchView;


    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backButton = findViewById(R.id.goToFuncFromMain);



        recyclerView=(RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchbar);

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users"), MainModel.class)
                        .build();


        mainAdapter=new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);
        userCountTextView = findViewById(R.id.user_count_text_view_2); // Initialize user count TextView


        EmpID = findViewById(R.id.user_details);

        // Set employee ID from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String userEid = preferences.getString("eid", "");
        EmpID.setText(userEid);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous main activity
                finish();
            }
        });



        fetchUserCount();
        setupSearchView();


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch user count from Firebase and display it
        fetchUserCount();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }

    private void fetchUserCount() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get count of children (users)
                        long userCount = dataSnapshot.getChildrenCount();
                        // Display user count in userCountTextView
                        userCountTextView.setText("Total Users: " + userCount);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // No action needed on text submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUser(newText);
                return true;
            }
        });
    }

    private void searchUser(String query) {
        Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child("users")
                .orderByChild("username")
                .startAt(query)
                .endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(firebaseSearchQuery, MainModel.class)
                        .build();

        mainAdapter.updateOptions(options);
        mainAdapter.startListening();
        recyclerView.setAdapter(mainAdapter);
    }


}