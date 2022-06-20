package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AuthView {
    companion object {
        const val MESSAGE = "MESSAGE"

        init {
            // opencv lib binding is executed in CameraCapture activity over special opencv initializer
            // System.loadLibrary("opencv_java4");
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mPresenter: AuthPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPresenter = AuthPresenter(this)
        mPresenter.init()

        binding.title.text = "enter your credentials"
    }

    fun onEnterPressed(view: View?) {
        val editText = binding.editName
        val editPassword = binding.editPassword
        val login = editText.text.toString()
        val password = editPassword.text.toString()
        mPresenter.tryLogIn(login, password)
    }

    override fun openContentScreen(welcomeMSG: String) {
        //Intent intent = new Intent(this, SecondActivity.class);
        //intent.putExtra(MESSAGE, welcomeMSG);
        val intent = Intent(this, CameraCapture::class.java)
        startActivity(intent)
    }

    override fun showLoginError() {
        val manager = supportFragmentManager
        val alert = AlertFragment("wrong login")
        alert.show(manager, "alert")
    }

    override fun showPasswordError() {
        val manager = supportFragmentManager
        val alert = AlertFragment("wrong password")
        alert.show(manager, "alert")
    }
}