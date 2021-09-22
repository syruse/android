package com.example.myapplication;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class AuthPresenter {

    private final AuthView mAuthView;

    public AuthPresenter(@NonNull AuthView authView) {
        mAuthView = authView;
    }

    public void init() {
        /*String token = PreferenceUtils.getToken();
        if (!TextUtils.isEmpty(token)) {
            mAuthView.openContentScreen();
        }*/
    }

    public void tryLogIn(@NonNull String login, @NonNull String password) {
        if (TextUtils.isEmpty(login)) {
            mAuthView.showLoginError();
        } else if (TextUtils.isEmpty(password)) {
            mAuthView.showPasswordError();
        } else {
            mAuthView.openContentScreen(new String("Hi " + login));
        }
    }
}
