package com.canopus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.canopus.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView mSplashImg;
    private static int TIME_OUT = 1500;

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
            }
        }, TIME_OUT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoomout);
        mSplashImg.setVisibility(View.VISIBLE);
        mSplashImg.startAnimation(animation);
    }

    private void init() {
        getSupportActionBar().hide();
        mSplashImg = (ImageView) findViewById(R.id.ivSplashLogo);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
