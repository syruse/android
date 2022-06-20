package com.example.myapplication

import android.text.TextUtils

class AuthPresenter(private val mAuthView: AuthView) {
    fun init() {
        // currently empty
    }

    fun tryLogIn(login: String, password: String) {
        if (TextUtils.isEmpty(login)) {
            mAuthView.showLoginError()
        } else if (TextUtils.isEmpty(password)) {
            mAuthView.showPasswordError()
        } else {
            mAuthView.openContentScreen("Hi $login")
        }
    }
}