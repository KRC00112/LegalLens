package org.kaustav.projectadmin;

public class Admin {
    public String email;
    public String password; // Storing passwords in plaintext is not recommended for security reasons
    public String eid;
    private String review;
    private float rating;


    public Admin() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Admin(String email, String password, String eid, String review, float rating) {
        this.email = email;
        this.password = password;
        this.eid = eid;
        this.review = review;
        this.rating = rating;
    }

    public Admin(String email, String password, String eid) {
        this.email = email;
        this.password = password;
        this.eid = eid;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEid() {
        return eid != null ? eid : "";
    }

    public void setEid(String eid) {
        this.eid = eid;
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

    public String getPassword() {
        return password != null ? password : "";
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
