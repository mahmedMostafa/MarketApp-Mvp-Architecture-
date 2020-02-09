package com.example.market.auth.register;

public interface RegisterView {

    void emailIsEmpty();
    void passwordIsEmpty();
    void passwordIsShort();
    void authFailed();
    void authSucceeded();
}
