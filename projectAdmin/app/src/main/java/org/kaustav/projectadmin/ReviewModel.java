package org.kaustav.projectadmin;

public class ReviewModel {
    private float rating;
    private String review;

    public ReviewModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ReviewModel.class)
    }

    public ReviewModel(float rating, String review) {
        this.rating = rating;
        this.review = review;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
