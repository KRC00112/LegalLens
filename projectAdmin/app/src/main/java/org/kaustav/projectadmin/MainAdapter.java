package org.kaustav.projectadmin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel, MainAdapter.myViewHolder> {

    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MainModel model) {
        holder.email.setText(model.getEmail());
        holder.password.setText(model.getPassword());
        holder.username.setText(model.getUsername());

        Glide.with(holder.img.getContext())
                .load(model.getEmail())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark)
                .into(holder.img);



        // Setting OnClickListener for delete button
// Setting OnClickListener for delete button
//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Build the alert dialog
//                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//
//                // Set custom title with colored text
//                TextView customTitle = new TextView(v.getContext());
//                customTitle.setText("User Deletion");
//                customTitle.setTextSize(20); // Adjust text size if needed
//                customTitle.setTextColor(v.getContext().getResources().getColor(android.R.color.white)); // Set text color
//                customTitle.setPadding(40, 20, 20, 20); // Adjust padding if needed
//                builder.setCustomTitle(customTitle);
//
//                builder.setMessage("Are you sure you want to delete this user?");
//                builder.setCancelable(true);
//
//                // Add positive button with action to delete user
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Get the user's email and password
//                        String email = model.getEmail();
//                        String password = model.getPassword();
//                        String UserName = model.getUsername();
//
//                        // Delete user from Firebase Authentication
//                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (task.isSuccessful()) {
//                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                            user.delete()
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                // User deleted from Authentication, now delete from Realtime Database
//                                                                int adapterPosition = holder.getAdapterPosition();
//                                                                if (adapterPosition != RecyclerView.NO_POSITION) {
//                                                                    getSnapshots().getSnapshot(adapterPosition).getRef().removeValue();
//                                                                }
//                                                                // Notify user about successful deletion
//                                                                Toast.makeText(v.getContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show();
//                                                            } else {
//                                                                // Handle errors if user deletion from Authentication fails
//                                                                Toast.makeText(v.getContext(), "Failed to delete user from Authentication", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    });
//                                        } else {
//                                            // Handle errors if sign-in fails
//                                            Toast.makeText(v.getContext(), "Failed to sign in for user deletion", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                    }
//                });
//
//                // Add negative button with action to cancel deletion
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });
//
//                // Show the alert dialog
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        });

        holder.viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start UserReviewsActivity
                Intent intent = new Intent(v.getContext(), UserReviewsActivity.class);
                // Pass the user ID to the activity
                intent.putExtra("USER_ID", getRef(holder.getAdapterPosition()).getKey());
                v.getContext().startActivity(intent);
            }
        });

    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView email, password,username, deleteButton,viewMoreButton;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            email = itemView.findViewById(R.id.emailText);
            password = itemView.findViewById(R.id.passwordText);
            username=itemView.findViewById(R.id.UsernameText);
//            deleteButton = itemView.findViewById(R.id.delButton);
            viewMoreButton = itemView.findViewById(R.id.viewMore);

        }
    }


}
