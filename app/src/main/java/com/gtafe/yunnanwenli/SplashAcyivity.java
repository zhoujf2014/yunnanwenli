package com.gtafe.yunnanwenli;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;



/**
 * Created by ZhouJF on 2017/8/3.
 */

public class SplashAcyivity extends AppCompatActivity {
    private final String FIRSTINSTAL = "firstinstal";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                startActivity(new Intent(SplashAcyivity.this, SelectActivity.class));
                finish();
            }
        }).start();

    }

}
