package com.example.infobetics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 5000; //5 Seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginScreenIntent = new Intent(LoadingScreenActivity.this, LoginScreenActivity.class);
                startActivity(loginScreenIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
