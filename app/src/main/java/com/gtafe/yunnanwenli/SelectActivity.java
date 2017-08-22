package com.gtafe.yunnanwenli;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ZhouJF on 2017/8/17.
 */

public class SelectActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView mCurtain;
    public TextView mLight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        mCurtain = (TextView) findViewById(R.id.select_control_curtain);
        mLight = (TextView) findViewById(R.id.select_control_light);
        mCurtain.setOnClickListener(this);
        mLight.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent();
        if (view.getId()==R.id.select_control_curtain){
            intent.setClass(this,CurtainControlActivity.class);
        }else {
            intent.setClass(this,LightControlActivity.class);
        }
        startActivity(intent);
    }
}
