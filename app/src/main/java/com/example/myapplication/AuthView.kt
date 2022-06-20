package com.example.myapplication

interface AuthView {
    fun openContentScreen(welcomeMSG: String)
    fun showLoginError()
    fun showPasswordError()
}