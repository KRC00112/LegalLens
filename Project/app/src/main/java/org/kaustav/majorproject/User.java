package org.kaustav.majorproject;

import com.google.firebase.database.ServerValue;

public class User {
    public String email;
    public String password; // Please note that storing passwords in plaintext is not recommended for security reasons
    public String username;
    public Object timestampreg;

    String review;
    float rating;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.timestampreg = ServerValue.TIMESTAMP;
    }

    public User(String review, float rating) {
        this.review = review;
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object gettimestampreg() {
        return timestampreg;
    }

    public void settimestampreg(Object timestampreg) {
        this.timestampreg = timestampreg;
    }
}
