package com.example.market.favourites;

public interface FavouritesView {

    void showProgress();
    void hideProgress();
    void updateFavouritesAdapter();
    void hideNoLikesLayout();
    void failedLoadingFavourites();
    void itemDeleted();
    void showNoLikesLayout();
    void showLoginLayout();
}
