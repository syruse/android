package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements AuthView {

    public static final String MESSAGE = "MESSAGE";

    static {
        // Used to load the 'myapplication' library on application startup.
        System.loadLibrary("myapplication");
        //System.loadLibrary("opencv_java4");
    }

    private ActivityMainBinding binding;
    private AuthPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPresenter = new AuthPresenter(this);
        mPresenter.init();

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }

    public void onEnterPressed(View view) {
        EditText editText = binding.editName;
        EditText editPassword = binding.editPassword;
        String login = editText.getText().toString();
        String password = editPassword.getText().toString();
        mPresenter.tryLogIn(login, password);
    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void openContentScreen(@NonNull String welcomeMSG) {
        //Intent intent = new Intent(this, SecondActivity.class);
        //intent.putExtra(MESSAGE, welcomeMSG);
        Intent intent = new Intent(this, CameraCapture.class);
        startActivity(intent);
    }

    @Override
    public void showLoginError() {
        FragmentManager manager = getSupportFragmentManager();
        AlertFragment alert = new AlertFragment("wrong login");
        alert.show(manager, "alert");
    }

    @Override
    public void showPasswordError() {
        FragmentManager manager = getSupportFragmentManager();
        AlertFragment alert = new AlertFragment("wrong password");
        alert.show(manager, "alert");
    }
}