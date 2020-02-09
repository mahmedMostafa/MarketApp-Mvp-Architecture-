package com.example.market.pojos;

public class PicturesItem {

    private String firstPicture;
    private String secondPicture;

    public PicturesItem(){
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public String getSecondPicture() {
        return secondPicture;
    }

    public void setSecondPicture(String secondPicture) {
        this.secondPicture = secondPicture;
    }

    public PicturesItem(String firstPicture, String secondPicture) {
        this.firstPicture = firstPicture;
        this.secondPicture = secondPicture;
    }
}
