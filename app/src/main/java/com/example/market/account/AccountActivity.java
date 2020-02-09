package com.example.market.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.market.main.MainActivity;
import com.example.market.recentlyViewed.RecentlyViewedActivity;
import com.example.market.R;
import com.example.market.auth.login.LoginActivity;
import com.facebook.login.LoginManager;

public class AccountActivity extends AppCompatActivity implements AccountView, View.OnClickListener {

    public static final String KEY_TAB = "key_tab";


    private TextView accountTextView;
    private TextView welcomeTextView;
    private Button loginButton;
    private TextView loginTextView;
    private RelativeLayout recentlyViewedItemsLayout;
    private RelativeLayout savedItemsLayout;

    private AccountPresenter accountPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_account);

        initializeUI();
        Toolbar toolbar = findViewById(R.id.account_tool_bar);
        toolbar.setTitle("Account");
        accountPresenter = new AccountPresenter(this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        accountPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        accountPresenter.onStop();
    }

    private void initializeUI() {
        recentlyViewedItemsLayout = findViewById(R.id.recently_viewed_items);
        savedItemsLayout = findViewById(R.id.saved_items_layout);
        welcomeTextView = findViewById(R.id.welcome_text_view);
        accountTextView = findViewById(R.id.account_text_view);
        loginButton = findViewById(R.id.account_login_button);
        loginTextView = findViewById(R.id.login_text_view);
        loginButton.setOnClickListener(this);
        loginTextView.setOnClickListener(this);
        recentlyViewedItemsLayout.setOnClickListener(this);
        savedItemsLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saved_items_layout :
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.putExtra(KEY_TAB, 1);
                startActivity(intent);
                break;
            case R.id.recently_viewed_items:
                startActivity(new Intent(AccountActivity.this, RecentlyViewedActivity.class));
                finish();
                break;
            case R.id.login_text_view:
                showLogoutDialog();
                break;
            case R.id.account_login_button:
                startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                break;
        }
    }

    private void showLogoutDialog() {
        if (accountPresenter.isLoggedIn()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AccountActivity.this, R.style.AlertDialogCustom)
                    .setTitle("Logout Confirmation")
                    .setMessage("Are you sure you want to exit?")
                    //Specifying a removeListener allows you to take an action before dismissing the dialog
                    // The dialog is automatically dismissed when a dialog button is clicked
                    .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            accountPresenter.signOut();
                            LoginManager.getInstance().logOut();
                            loginTextView.setText("LOGIN");
                            loginButton.setVisibility(View.VISIBLE);
                            accountPresenter.setLoggedIn(false);
                            welcomeTextView.setText("Welcome!");
                            accountTextView.setText("Enter your account");
                        }
                    })// A null removeListener allows the button to dismiss the dialog and take no further action
                    .setPositiveButton("NO", null);

            dialog.show()
                    .getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(AccountActivity.this, R.color.alertDialogTextColor));
        } else {
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
        }
    }

    @Override
    public void hideLoginButton() {
        loginButton.setVisibility(View.GONE);
    }

    @Override
    public void setLogoutText() {
        loginTextView.setText("LOGOUT");
    }

    @Override
    public void setAccountDetails(String email) {
        accountTextView.setText(email);
    }

    @Override
    public void setWelcomeText(String firstName) {
        welcomeTextView.setText("Welcome " + firstName);
        welcomeTextView.setTextColor(ContextCompat.getColor(AccountActivity.this, R.color.colorWhite));
    }

    @Override
    public void accountError(String errorMessage) {
        Toast.makeText(AccountActivity.this, "Error is :" + errorMessage, Toast.LENGTH_SHORT).show();
    }


}
