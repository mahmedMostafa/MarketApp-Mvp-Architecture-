package com.example.market.cart;

public interface CartView {
    void hideProgress();
    void hideLoginLayout();
    void showNoItemsLayout();
    void hideNoItemLayout();
    void showCompleteOrderLayout();
    void hideCompleteOrderLayout();
    void setTotalPrice();
    void itemRemovedToast();
    void updateCartAdapter();
    void showLoginLayout();
}
