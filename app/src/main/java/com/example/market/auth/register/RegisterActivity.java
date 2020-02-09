package com.example.market.auth.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.market.R;

public class RegisterActivity extends AppCompatActivity implements RegisterView {


    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneEditText;
    private Button createButton;


    private RegisterPresenter registerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_register);
        registerPresenter = new RegisterPresenter(this,this);
        initializeUI();
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPresenter.signUpUser(firstNameEditText.getText().toString().trim(),
                        lastNameEditText.getText().toString().trim(),
                        emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim(),
                        phoneEditText.getText().toString().trim());
            }
        });

    }


    private void initializeUI() {

        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        createButton = findViewById(R.id.create_button);
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
    public void passwordIsShort() {
        Toast.makeText(this, "Password is less than 7", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void authFailed() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void authSucceeded() {
        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
    }
}
