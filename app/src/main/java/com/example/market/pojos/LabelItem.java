package com.example.market.pojos;

public class LabelItem {
    private String id;


    private String title;
    private String description;

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

    public LabelItem(){
        //empty constructor for fire base work
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LabelItem(String title, String price) {
        this.title = title;
        this.description = price;
    }
}
