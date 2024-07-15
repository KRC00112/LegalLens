package org.kaustav.projectadmin;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserReviewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private TextView summaryCountTextView;
    private TextView feedbackCountTextView;

    private TextView averageRatingTextView;
    private ImageView barChartImage;
    private ImageView legends;
    private TextView veryDissatisfiedText;
    private TextView dissatisfiedText;
    private TextView neutralText;
    private TextView satisfiedText;
    private TextView verySatisfiedText;
    TextView EmpID;
    private ImageButton backButton;
    TextView pieChart;

    TextView userNameHeader;

    TextView userRegistrationDate;

    TextView satisLevel;


    private ImageView pieChartImage;
    private FirebaseRecyclerAdapter<ReviewModel, ReviewViewHolder> adapter;

    private List<Float> ratings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);
        backButton = findViewById(R.id.goToFuncFromReview);
        recyclerView = findViewById(R.id.reviews_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        summaryCountTextView = findViewById(R.id.summary_count_text_view);
        feedbackCountTextView = findViewById(R.id.feedback_count_text_view);
        averageRatingTextView = findViewById(R.id.average_rating_text_view);
//        barChartImage = findViewById(R.id.bar_chart_image);
        pieChartImage = findViewById(R.id.pie_chart_image);
        legends=findViewById(R.id.legends);
        EmpID = findViewById(R.id.user_details);
        userNameHeader=findViewById(R.id.userNameHeader);
        recyclerView.setNestedScrollingEnabled(false);
        userRegistrationDate=findViewById(R.id.registeredOn);
//        pieChart.setText("Rating Distribution\nPie Chart:");
        veryDissatisfiedText = findViewById(R.id.very_dissatisfied_text);
        dissatisfiedText = findViewById(R.id.dissatisfied_text);
        neutralText = findViewById(R.id.neutral_text);
        satisfiedText = findViewById(R.id.satisfied_text);
        verySatisfiedText = findViewById(R.id.very_satisfied_text);
        satisLevel=findViewById(R.id.levelsOfSatisfaction);
        satisLevel.setText("Levels of Satisfaction conveyed\nby the user's ratings:");



        // Get user ID from intent
        String userId = getIntent().getStringExtra("USER_ID");
        fetchUserName(userId);


        fetchSummaryCount(userId);
        fetchFeedbackCount(userId);
        fetchUserRegistrationDate(userId); // Fetch and set the user registration date


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


        // Setup FirebaseRecyclerAdapter
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("feedback");
        FirebaseRecyclerOptions<ReviewModel> options = new FirebaseRecyclerOptions.Builder<ReviewModel>()
                .setQuery(reviewsRef, ReviewModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ReviewModel, ReviewViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull ReviewModel model) {

                int reversePosition = getItemCount() - position - 1;
                ReviewModel reverseModel = getItem(reversePosition);
                holder.ratingBar.setRating(reverseModel.getRating());
                holder.reviewTextView.setText(reverseModel.getReview());
                ratings.add(reverseModel.getRating());
                calculateAndDisplayResults();
            }

            @NonNull
            @Override
            public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
                return new ReviewViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    private void fetchSummaryCount(String userId) {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("history");
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long summaryCount = dataSnapshot.getChildrenCount();
                summaryCountTextView.setText("Total no. of Summaries generated: " + summaryCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
    private void fetchFeedbackCount(String userId) {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("feedback");
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long feedbackCount = dataSnapshot.getChildrenCount();
                feedbackCountTextView.setText("Total no. of Feedbacks: " + feedbackCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
    private void fetchUserRegistrationDate(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("timestampreg");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long timestamp = dataSnapshot.getValue(Long.class);
                if (timestamp != null) {
                    String formattedDate = formatTimestamp(timestamp);
                    userRegistrationDate.setText("User registered on: " + formattedDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }


    private void calculateAndDisplayResults() {
        if (ratings.isEmpty()) return;

        float sum = 0;
        for (float rating : ratings) {
            sum += rating;
        }
        float averageRating = sum / ratings.size();
        averageRatingTextView.setText("Average Rating: " + String.format("%.2f", averageRating));

        int[] satisfactionCounts = new int[5]; // Indexes: 0-4 for Very Dissatisfied to Very Satisfied
        for (float rating : ratings) {
            if (rating >= 0.0 && rating <= 1.9) satisfactionCounts[0]++;
            else if (rating >= 2.0 && rating <= 2.9) satisfactionCounts[1]++;
            else if (rating >= 3.0 && rating <= 3.9) satisfactionCounts[2]++;
            else if (rating >= 4.0 && rating <= 4.9) satisfactionCounts[3]++;
            else if (rating == 5.0) satisfactionCounts[4]++;
        }

//        Bitmap barChartBitmap = ChartUtils.createBarChart(satisfactionCounts);
//        barChartImage.setImageBitmap(barChartBitmap);

        Bitmap pieChartBitmap = ChartUtils.createPieChart(satisfactionCounts);
        pieChartImage.setImageBitmap(pieChartBitmap);

        Bitmap legendsBitmap = ChartUtils.createLegends(satisfactionCounts); // Create legends bitmap
        legends.setImageBitmap(legendsBitmap);

        // Update TextViews with satisfaction levels
        int totalRatings = ratings.size();
        veryDissatisfiedText.setText(String.format("Very Dissatisfied: %.1f%%", (satisfactionCounts[0] / (float) totalRatings) * 100));
        dissatisfiedText.setText(String.format("Dissatisfied: %.1f%%", (satisfactionCounts[1] / (float) totalRatings) * 100));
        neutralText.setText(String.format("Neutral: %.1f%%", (satisfactionCounts[2] / (float) totalRatings) * 100));
        satisfiedText.setText(String.format("Satisfied: %.1f%%", (satisfactionCounts[3] / (float) totalRatings) * 100));
        verySatisfiedText.setText(String.format("Very Satisfied: %.1f%%", (satisfactionCounts[4] / (float) totalRatings) * 100));

    }
    private void fetchUserName(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue(String.class);
                userNameHeader.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView reviewTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            reviewTextView = itemView.findViewById(R.id.review_text_view);
        }
    }
}
