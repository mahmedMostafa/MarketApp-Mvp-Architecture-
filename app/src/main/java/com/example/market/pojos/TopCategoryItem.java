package com.example.market.pojos;

public class TopCategoryItem {

    private String label;
    private String image;
    private String itemId;
    private String category;

    public TopCategoryItem(){
        //empty constructor for fire base
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public TopCategoryItem(String category ,String label, String image, String itemId) {
        this.label = label;
        this.image = image;
        this.itemId = itemId;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
