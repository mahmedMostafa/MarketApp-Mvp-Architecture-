package com.example.market.pojos;

public class BestProductsItem {

    private String bestProductsID;
    private String bestProductsImage;
    private String bestProductsTitle;
    private String bestProductsOriginalPrice;
    private String bestProductsCrossedPrice;


    public BestProductsItem(){
        //empty constructor for fire base
    }

    public BestProductsItem(String bestProductsImage, String bestProductsTitle, String bestProductsOriginalPrice, String bestProductsCrossedPrice) {
        this.bestProductsImage = bestProductsImage;
        this.bestProductsTitle = bestProductsTitle;
        this.bestProductsOriginalPrice = bestProductsOriginalPrice;
        this.bestProductsCrossedPrice = bestProductsCrossedPrice;
    }

    public String getBestProductsImage() {
        return bestProductsImage;
    }

    public void setBestProductsImage(String bestProductsImage) {
        this.bestProductsImage = bestProductsImage;
    }

    public String getBestProductsTitle() {
        return bestProductsTitle;
    }

    public void setBestProductsTitle(String bestProductsTitle) {
        this.bestProductsTitle = bestProductsTitle;
    }

    public String getBestProductsOriginalPrice() {
        return bestProductsOriginalPrice;
    }

    public void setBestProductsOriginalPrice(String bestProductsOriginalPrice) {
        this.bestProductsOriginalPrice = bestProductsOriginalPrice;
    }

    public String getBestProductsCrossedPrice() {
        return bestProductsCrossedPrice;
    }

    public void setBestProductsCrossedPrice(String bestProductsCrossedPrice) {
        this.bestProductsCrossedPrice = bestProductsCrossedPrice;
    }
    public String getBestProductsID() {
        return bestProductsID;
    }

    public void setBestProductsID(String bestProductsID) {
        this.bestProductsID = bestProductsID;
    }
}
