package com.example.market.pojos;

public class Review {

    private String title;
    private String description;
    private String buyer;
    private String timeStamp;
    private double rating;

    public Review(String title, String description, String buyer , double rating , String timeStamp) {
        this.title = title;
        this.description = description;
        this.buyer = buyer;
        this.rating = rating;
        this.timeStamp = timeStamp;
    }

    public Review(){
        //for firebase
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
