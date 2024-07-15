package org.kaustav.majorproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.animation.ObjectAnimator;
import android.view.ViewPropertyAnimator;
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    public Button clear;

    public Button historyButton;
    private TextView userDetailsTextView;
    private FirebaseUser user;
    private EditText inputText;  // Add EditText for user input
    private Button summarizeButton;
    private ImageButton clipboard;
    private EditText summaryText;
    private ImageView num1;
    private ImageView num2;
    private ImageView num3;

    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;

    TextView welcome;
    private DatabaseReference usersRef;
    private String flaskURL = "http://192.168.114.218:5000/text-summarization";
    private static final int READ_REQUEST_CODE = 42; // Request code for file selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        clear = findViewById(R.id.clearInput);
        userDetailsTextView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        inputText = findViewById(R.id.input);  // Initialize EditText
        summarizeButton = findViewById(R.id.submit);  // Initialize Summarize Button
        summaryText = findViewById(R.id.output);  // Initialize TextView for Summary
        clipboard=findViewById(R.id.clipboard);
        //set history button visibility
        historyButton=findViewById(R.id.history);
        historyButton.setVisibility(View.GONE);
        final LoadingDialog loadingDialog=new LoadingDialog(MainActivity.this);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ProgressBar progressBar2 = findViewById(R.id.progressBar2);
        ProgressBar progressBar3 = findViewById(R.id.progressBar3);
        ProgressBar progressBar4 = findViewById(R.id.progressBar4);
        ProgressBar progressBar5 = findViewById(R.id.progressBar5);
        ProgressBar progressBar6 = findViewById(R.id.progressBar6);
        num1=findViewById(R.id.num_1);
        num2=findViewById(R.id.num_2);
        num3=findViewById(R.id.num_3);
        firstText=findViewById(R.id.textView13);
        secondText=findViewById(R.id.textView14);
        thirdText=findViewById(R.id.textView15);


        welcome=findViewById(R.id.textViewWelcome);
        welcome.setText("Welcome\nto LegalLens");



        arrowBars(progressBar,progressBar2 ,progressBar3, progressBar4, progressBar5, progressBar6);

        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);

        // Load the animation
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
        Animation slideInAnimation2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
        Animation slideInAnimation3 = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);


        // Apply animation to card1
        card1.startAnimation(slideInAnimation);

        // Create separate instances of the animation for card2 and card3


        // Set start offsets for card2 and card3 animations
        slideInAnimation2.setStartOffset(200);
        slideInAnimation3.setStartOffset(400);

        // Apply animations to card2 and card3
        card2.startAnimation(slideInAnimation2);
        card3.startAnimation(slideInAnimation3);






        clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager cp=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip= ClipData.newPlainText("EditText",summaryText.getText().toString());
                cp.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this,"Copied to Clipboard",Toast.LENGTH_SHORT).show();


            }
        });

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(intent);
            finish();
        } else {

            fetchAndDisplayUsername();
            checkHistoryAndSetButtonVisibility();

        }


        //ex

        ///ex
        summarizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start loading dialog when the button is clicked
                loadingDialog.startLoadingDialog();

                // Get the input text from EditText
                String inputTextValue = inputText.getText().toString();

                // Check if the input is empty
                if (inputTextValue.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Input cannot be empty", Toast.LENGTH_SHORT).show();
                    // Dismiss the loading dialog if there's an error
                    loadingDialog.dismissDialog();
                } else {
                    // If input is not empty, proceed with summarization
                    sendSummarizationRequest(inputTextValue, loadingDialog);
                }
            }
        });



        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> {
            // Open file manager to select a file
            performFileSearch();
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firstText.animate().alpha(1f).setDuration(1000).start();



            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                secondText.animate().alpha(1f).setDuration(1000).start();



            }
        }, 1500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                thirdText.animate().alpha(1f).setDuration(1000).start();



            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int pinkColor = getResources().getColor(R.color.darkLegal);
                num1.setColorFilter(pinkColor);

            }
        }, 1300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int pinkColor = getResources().getColor(R.color.darkLegal);
                num2.setColorFilter(pinkColor);

            }
        }, 3300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int pinkColor = getResources().getColor(R.color.darkLegal);

                num3.setColorFilter(pinkColor);
            }
        }, 5300);




    }





    // Method to open file manager
    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Allow all file types
        String[] mimeTypes = {"text/plain", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes); // Specify mime types
        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    private void checkHistoryAndSetButtonVisibility() {
        user = auth.getCurrentUser();
        if (user != null) {
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("history");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                        historyButton.setVisibility(View.VISIBLE);
                    } else {
                        historyButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void fetchAndDisplayUsername() {
        user = auth.getCurrentUser();
        if (user != null) {
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        if (currentUser != null) {
                            userDetailsTextView.setText("Hello, " + currentUser.getUsername() + "!");
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


    // Handle the result of file selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                try {
                    String content = "";
                    String mimeType = getContentResolver().getType(uri);
                    if (mimeType != null && mimeType.equals("text/plain")) {
                        content = readTextFromUri(uri); // Read text from .txt file
                    } else if (mimeType != null && mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                        content = readTextFromDocx(uri); // Read text from .docx file
                    }
                    inputText.setText(content);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    // Method to read text from selected URI
    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private String readTextFromDocx(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            stringBuilder.append(extractor.getText());
        }
        return stringBuilder.toString();
    }



    public void openSettingsActivity(View v){

        Intent i=new Intent(this,settingsActivity.class);
        startActivity(i);
    }



    public void arrowBars(ProgressBar progressBar1, ProgressBar progressBar2, ProgressBar progressBar3 ,ProgressBar progressBar4 ,ProgressBar progressBar5, ProgressBar progressBar6) {
        // Set up a delay before starting the animation of progressBar1
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an ObjectAnimator to animate the progress of progressBar1
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar1, "progress", 0, 100);
                progressBar1.setRotation(180);
                progressAnimator.setDuration(1000); // Set the duration of the animation in milliseconds
                progressAnimator.start(); // Start the animation
            }
        }, 980); // Delay the start of progressBar1 animation for 1000 milliseconds (1 second)

        // Set up a delay before starting the animation of progressBar2
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation of progressBar2 after 800 milliseconds
                ObjectAnimator progressAnimator2 = ObjectAnimator.ofInt(progressBar2, "progress", 0, 100);
                progressAnimator2.setDuration(1000); // Set the duration of the animation in milliseconds
                progressBar2.setRotation(90); // Rotate the progress bar
                progressAnimator2.start(); // Start the animation of progressBar2
            }
        }, 1788); // Delay the start of progressBar2 animation for 800 milliseconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation of progressBar2 after 800 milliseconds
                ObjectAnimator progressAnimator3 = ObjectAnimator.ofInt(progressBar3, "progress", 0, 100);
                progressAnimator3.setDuration(1000); // Set the duration of the animation in milliseconds// Rotate the progress bar
                progressAnimator3.start(); // Start the animation of progressBar2
            }
        }, 2570); // Delay the start of progressBar2 animation for 800 milliseconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation of progressBar2 after 800 milliseconds
                ObjectAnimator progressAnimator4 = ObjectAnimator.ofInt(progressBar4, "progress", 0, 100);
                progressAnimator4.setDuration(1000); // Set the duration of the animation in milliseconds// Rotate the progress bar
                progressAnimator4.start(); // Start the animation of progressBar2
            }
        }, 3000); // Delay the start of progressBar2 animation for 800 milliseconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation of progressBar2 after 800 milliseconds
                ObjectAnimator progressAnimator5 = ObjectAnimator.ofInt(progressBar5, "progress", 0, 100);
                progressAnimator5.setDuration(1000); // Set the duration of the animation in milliseconds// Rotate the progress bar
                progressBar5.setRotation(90);
                progressAnimator5.start(); // Start the animation of progressBar2
            }
        }, 3800); // Delay the start of progressBar2 animation for 800 milliseconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation of progressBar2 after 800 milliseconds
                ObjectAnimator progressAnimator6 = ObjectAnimator.ofInt(progressBar6, "progress", 0, 100);
                progressAnimator6.setDuration(1000); // Set the duration of the animation in milliseconds// Rotate the progress bar
                progressBar6.setRotation(180);
                progressAnimator6.start(); // Start the animation of progressBar2
            }
        }, 4600); // Delay the start of progressBar2 animation for 800 milliseconds

    }




    public void openHistoryActivity(View v){

        Intent i=new Intent(this,historyActivity.class);
        startActivity(i);
    }

    public void clearInputText(View v){
        if(clear.isPressed()){
            inputText.setText("");
            summaryText.setText("");

        }
    }

    void sendSummarizationRequest(String inputText, final LoadingDialog loadingDialog) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("inputtext_", inputText)
                .build();

        Request request = new Request.Builder()
                .url(flaskURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Dismiss loading dialog in case of failure
                loadingDialog.dismissDialog();

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Set the summary text
                        summaryText.setText(responseData);
                        // Save input and output to the database
                        saveInputOutputToDatabase(inputText, responseData);
                        // Dismiss loading dialog once summary is generated
                        loadingDialog.dismissDialog();

                    }
                });
            }
        });
    }

    void saveInputOutputToDatabase(String input, String output) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            DatabaseReference historyRef = userRef.child("history").push(); // Create a new entry in history
            // Save input, output, and current date/time
            historyRef.child("input").setValue(input); // Save input
            historyRef.child("output").setValue(output); // Save output
            historyRef.child("timestamp").setValue(ServerValue.TIMESTAMP); // Save current timestamp
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndDisplayUsername();
        checkHistoryAndSetButtonVisibility();

    }


}
