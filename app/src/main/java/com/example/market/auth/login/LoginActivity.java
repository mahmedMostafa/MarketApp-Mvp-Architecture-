package com.example.market.auth.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.market.R;
import com.example.market.auth.forgotPassowrd.ForgotPasswordActivity;
import com.example.market.auth.register.RegisterActivity;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements LoginView, View.OnClickListener {


    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private TextView createAccountTextView;
    private LoginButton facebookLoginButton;
    private ImageButton visibilityImageButton;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);
        initializeUI();
        loginPresenter = new LoginPresenter(this, this);
        loginPresenter.checkLoginStatus();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeUI() {
        visibilityImageButton = findViewById(R.id.visibility_icon);
        facebookLoginButton = findViewById(R.id.facebook_login);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password_text_view);
        createAccountTextView = findViewById(R.id.create_acc_text_view);
        loginButton.setOnClickListener(this);
        facebookLoginButton.setOnClickListener(this);
        createAccountTextView.setOnClickListener(this);
        facebookLoginButton.setPermissions(Arrays.asList("email"));
        visibilityImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebook_login:
                loginPresenter.signInWithFacebook(facebookLoginButton);
                break;
            case R.id.login_button:
                loginPresenter.loginUser(emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
                break;
            case R.id.create_acc_text_view:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.forgot_password_text_view :
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenter.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void emailIsEmpty() {
        Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void passwordIsEmpty() {
        Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSucceeded() {
        Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginFailed() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    @Override
    public LoginButton getLoginButton() {
        return facebookLoginButton;
    }


}
