package com.example.market.pojos;

public class CartItem {

    private String imageUrl;
    private String title;
    private String price;
    private String oldPrice;
    private String brand;
    private String discount;
    private boolean freeShipping;
    private String category;
    private String id;

    public CartItem(){
        //don't forget this for firebase
    }

    public CartItem(String brand,String discount,String imageUrl, String title, String price, String oldPrice, boolean freeShipping, String category, String id) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;
        this.oldPrice = oldPrice;
        this.freeShipping = freeShipping;
        this.category = category;
        this.id = id;
        this.discount = discount;
        this.brand = brand;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
