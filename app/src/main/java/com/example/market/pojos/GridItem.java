package com.example.market.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class GridItem implements Parcelable {
    private String imageUrl;
    private String title;
    private String price;
    private String oldPrice;
    private String deliveryInfo;
    private String brand;
    private String id;
    private String warranty;

    protected GridItem(Parcel in) {
        imageUrl = in.readString();
        title = in.readString();
        price = in.readString();
        oldPrice = in.readString();
        deliveryInfo = in.readString();
        brand = in.readString();
        id = in.readString();
        warranty = in.readString();
        keyFeatures = in.readString();
        Description = in.readString();
        discount = in.readString();
        returnPolicy = in.readString();
        freeShipping = in.readByte() != 0;
        totalVoters = in.readInt();
        totalRating = in.readDouble();
        star1 = in.readInt();
        star2 = in.readInt();
        star3 = in.readInt();
        star4 = in.readInt();
        star5 = in.readInt();
    }

    public static final Creator<GridItem> CREATOR = new Creator<GridItem>() {
        @Override
        public GridItem createFromParcel(Parcel in) {
            return new GridItem(in);
        }

        @Override
        public GridItem[] newArray(int size) {
            return new GridItem[size];
        }
    };

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    private String keyFeatures;
    private String Description;
    private String discount;
    private String returnPolicy;

    public String getReturnPolicy() {
        return returnPolicy;
    }

    public void setReturnPolicy(String returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

    private boolean freeShipping;
    private int totalVoters;
    private double totalRating;
    private int star1;
    private int star2;
    private int star3;
    private int star4;
    private int star5;

    public int getTotalVoters() {
        return totalVoters;
    }

    public void setTotalVoters(int totalVoters) {
        this.totalVoters = totalVoters;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public int getStar1() {
        return star1;
    }

    public void setStar1(int star1) {
        this.star1 = star1;
    }

    public int getStar2() {
        return star2;
    }

    public void setStar2(int star2) {
        this.star2 = star2;
    }

    public int getStar3() {
        return star3;
    }

    public void setStar3(int star3) {
        this.star3 = star3;
    }

    public int getStar4() {
        return star4;
    }

    public void setStar4(int star4) {
        this.star4 = star4;
    }

    public int getStar5() {
        return star5;
    }

    public void setStar5(int star5) {
        this.star5 = star5;
    }

    public GridItem(String url, String title, String price,String discount) {
        this.imageUrl = url;
        this.title = title;
        this.price = price;
        this.discount = discount;
    }

    public GridItem() {
        //empty constructor for fire base
    }


    public String getKeyFeatures() {
        return keyFeatures;
    }

    public void setKeyFeatures(String keyFeatures) {
        this.keyFeatures = keyFeatures;
    }


    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(String deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(oldPrice);
        dest.writeString(deliveryInfo);
        dest.writeString(brand);
        dest.writeString(id);
        dest.writeString(warranty);
        dest.writeString(keyFeatures);
        dest.writeString(Description);
        dest.writeString(discount);
        dest.writeString(returnPolicy);
        dest.writeByte((byte) (freeShipping ? 1 : 0));
        dest.writeInt(totalVoters);
        dest.writeDouble(totalRating);
        dest.writeInt(star1);
        dest.writeInt(star2);
        dest.writeInt(star3);
        dest.writeInt(star4);
        dest.writeInt(star5);
    }
}
