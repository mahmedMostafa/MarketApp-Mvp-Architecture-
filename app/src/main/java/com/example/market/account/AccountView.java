package com.example.market.account;

public interface AccountView {

    void hideLoginButton();
    void setLogoutText();
    void setAccountDetails(String email);
    void setWelcomeText(String firstName);
    void accountError(String errorMessage);
}
