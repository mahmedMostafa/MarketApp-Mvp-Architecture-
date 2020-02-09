package com.example.market.auth.login;

import com.facebook.login.widget.LoginButton;

public interface LoginView {

    void emailIsEmpty();
    void passwordIsEmpty();
    void loginSucceeded();
    void loginFailed();
    LoginButton getLoginButton();
}
