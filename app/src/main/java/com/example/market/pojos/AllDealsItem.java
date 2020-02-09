package com.example.market.pojos;

public class AllDealsItem {

    private String dealsImageUrl;
    private String dealsBrand;
    private String dealsTitle;
    private String dealsPrice;
    private String dealsOldPrice;
    private double dealsRating;
    private String dealsDiscount;
    private boolean dealsFreeShipping;
    private String dealsId;
    private boolean isLiked;

    public AllDealsItem(){
        //empty for firebase
    }
    public AllDealsItem(String imageUrl, String brand, String title, String price, String oldPrice, double rating, String discount, boolean freeShipping) {
        this.dealsImageUrl = imageUrl;
        this.dealsBrand = brand;
        this.dealsTitle = title;
        this.dealsPrice = price;
        this.dealsOldPrice = oldPrice;
        this.dealsRating = rating;
        this.dealsDiscount = discount;
        this.dealsFreeShipping = freeShipping;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getDealsId() {
        return dealsId;
    }

    public void setDealsId(String dealsId) {
        this.dealsId = dealsId;
    }

    public String getDealsImageUrl() {
        return dealsImageUrl;
    }

    public void setDealsImageUrl(String dealsImageUrl) {
        this.dealsImageUrl = dealsImageUrl;
    }

    public String getDealsBrand() {
        return dealsBrand;
    }

    public void setDealsBrand(String dealsBrand) {
        this.dealsBrand = dealsBrand;
    }

    public String getDealsTitle() {
        return dealsTitle;
    }

    public void setDealsTitle(String dealsTitle) {
        this.dealsTitle = dealsTitle;
    }

    public String getDealsPrice() {
        return dealsPrice;
    }

    public void setDealsPrice(String dealsPrice) {
        this.dealsPrice = dealsPrice;
    }

    public String getDealsOldPrice() {
        return dealsOldPrice;
    }

    public void setDealsOldPrice(String dealsOldPrice) {
        this.dealsOldPrice = dealsOldPrice;
    }

    public double getDealsRating() {
        return dealsRating;
    }

    public void setDealsRating(double dealsRating) {
        this.dealsRating = dealsRating;
    }

    public String getDealsDiscount() {
        return dealsDiscount;
    }

    public void setDealsDiscount(String dealsDiscount) {
        this.dealsDiscount = dealsDiscount;
    }

    public boolean isDealsFreeShipping() {
        return dealsFreeShipping;
    }

    public void setDealsFreeShipping(boolean dealsFreeShipping) {
        this.dealsFreeShipping = dealsFreeShipping;
    }
}
