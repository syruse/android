package com.example.myapplication;

import androidx.annotation.NonNull;

public interface AuthView {

    void openContentScreen(@NonNull String welcomeMSG);

    void showLoginError();

    void showPasswordError();
}
