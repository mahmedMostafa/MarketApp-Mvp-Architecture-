package com.example.market.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recently_viewed_items")
public class ViewedItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String item;

    private String imageUrl;

    private String brand;

    private String title;

    private String price;

    private String oldPrice;

    private String discount;

    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ViewedItem(){
        //for favourites firebase collection
    }

    @Ignore
    public ViewedItem(String category , String item, String imageUrl, String brand, String title, String price, String oldPrice, String discount) {
        this.category = category;
        this.item = item;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.title = title;
        this.price = price;
        this.oldPrice = oldPrice;
        this.discount = discount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String id) {
        this.item = id;
    }
}
