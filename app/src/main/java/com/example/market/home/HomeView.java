package com.example.market.home;

import com.example.market.pojos.GridItem;

import java.util.ArrayList;

public interface HomeView {

    void notifyDealsAdapter();
    void updateDealsAdapter(int k , ArrayList<GridItem> items);
    void notifyTopCategoriesAdapter();
    void sliderError();
    void bestProductsError();
    void notifyBestProductsAdapter();
}
