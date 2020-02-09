package com.example.market.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.market.main.MainActivity;
import com.example.market.R;

public class SplashActivity extends AppCompatActivity {



    private TextView labelTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        labelTextView = findViewById(R.id.label_text_view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                labelTextView.setVisibility(View.VISIBLE);
                RunTextViewAnimation();
            }
        },1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },3000);
    }

    private void RunTextViewAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splash_text_show);
        animation.reset();

        labelTextView.clearAnimation();
        labelTextView.startAnimation(animation);
    }
}
