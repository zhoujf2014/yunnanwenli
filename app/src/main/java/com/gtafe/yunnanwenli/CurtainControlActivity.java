package com.gtafe.yunnanwenli;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ZhouJF on 2017/8/17.
 */

public class CurtainControlActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CurtainControlActivity";

    public ImageView mImgLeft;
    public ImageView mImgRight;
    public Button mClose;
    public Button mOppen;
    public ImageView mScenery;
    public Button mMode;
    private boolean outoMode = false;
    private boolean curtainState = false;
    //copy
    public WifiUtils mWifiUtils;
    private String[] mEquipmentList;
    public WifiReceiver mWifiReceiver;
    public Socket mSocket;
    private boolean isReceiver = true;
    public InputStream mInputStream;
    public OutputStream mOutputStream;
    public Button mConnet;
    public ImageView mHint;
    private long duration = 1500;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtaincontrol);
        regiseterReceiver();
        initEquipmentList();
        initView();
        mWifiUtils = new WifiUtils(this);
        showHint();
    }

    private void showHint() {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, -0.6f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(600);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        mHint.startAnimation(animation);
    }

    private void initView() {
        mHint = (ImageView) findViewById(R.id.curtain_hint);
        mImgLeft = (ImageView) findViewById(R.id.curtain_left);
        mImgRight = (ImageView) findViewById(R.id.curtain_right);
        mScenery = (ImageView) findViewById(R.id.curtain_scenery);
        mOppen = (Button) findViewById(R.id.curtain_oppen);
        mClose = (Button) findViewById(R.id.curtain_close);
        mMode = (Button) findViewById(R.id.curtain_mode);
        mConnet = (Button) findViewById(R.id.curtain_connet);
        mOppen.setOnClickListener(this);
        mClose.setOnClickListener(this);
        mMode.setOnClickListener(this);
        mConnet.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.curtain_oppen:
                if (!outoMode){
                    oppenCurtain();
                }


                break;
            case R.id.curtain_close:
                if (!outoMode){


                    closeCurtain();
                }

                break;
            case R.id.curtain_mode:
                if (outoMode) {
                    outoMode = false;
                    mMode.setBackgroundResource(R.drawable.bg_green);
                    mMode.setText("手动模式");
                } else {
                    outoMode = true;
                    mMode.setBackgroundResource(R.drawable.bg_red);
                    mMode.setText("自动模式");
                }
                break;
            case R.id.curtain_connet:
                connect();
                break;
        }
    }

    private void closeCurtain() {
        if (curtainState) {
            curtainState = false;
            mOppen.setEnabled(true);
            mClose.setEnabled(false);
            ObjectAnimator.ofFloat(mImgRight, "translationX", 350, 0) // 初始化动画，设置各个参数
                    .setDuration(duration) // 设置动画持续时间
                    .start();
            ObjectAnimator.ofFloat(mImgLeft, "translationX", -350, 0) // 初始化动画，设置各个参数
                    .setDuration(duration) // 设置动画持续时间
                    .start();
            if (outoMode) {
                ObjectAnimator.ofFloat(mScenery, "alpha", 0.4f, 1f) // 初始化动画，设置各个参数
                        .setDuration(duration) // 设置动画持续时间
                        .start();
            }
            try {
                send((byte) 0x02);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void oppenCurtain() {
        if (!curtainState) {
            curtainState = true;
            mOppen.setEnabled(false);
            mClose.setEnabled(true);
            ObjectAnimator.ofFloat(mImgLeft, "translationX", 0f, -350f) // 初始化动画，设置各个参数
                    .setDuration(duration) // 设置动画持续时间
                    .start();
            ObjectAnimator.ofFloat(mImgRight, "translationX", 0f, 350f) // 初始化动画，设置各个参数
                    .setDuration(duration) // 设置动画持续时间
                    .start();
            if (outoMode) {
                ObjectAnimator.ofFloat(mScenery, "alpha", 1f, 0.4f) // 初始化动画，设置各个参数
                        .setDuration(duration) // 设置动画持续时间
                        .start();
            }
            try {
                send((byte) 0x01);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void initEquipmentList() {
        mEquipmentList = new String[16];
        for (int i = 1; i < 17; i++) {
            if (i < 10) {
                mEquipmentList[i - 1] = "GTAWJ-WX-02-00" + i;

            } else {
                mEquipmentList[i - 1] = "GTAWJ-WX-02-0" + i;
            }
        }
    }

    private void connect() {
        if (mWifiUtils.checkState() == WifiManager.WIFI_STATE_DISABLED) {
            mWifiUtils.openWifi();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("请选择要连接的设备:")
                    .setSingleChoiceItems(mEquipmentList, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            connectWIfi(which);
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    private void connectWIfi(int which) {

        mWifiUtils.disConnectionWifi(mWifiUtils.getNetWordId());
        mWifiUtils.addNetWork(mEquipmentList[which], "1234567890");
    }


    //wifi状态改变回调
    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            unConnet();
            isReceiver = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mSocket = new Socket("192.168.1.1", 8888);
                        if (mSocket.isConnected()) {
                            mInputStream = mSocket.getInputStream();
                            mOutputStream = mSocket.getOutputStream();
                            byte[] bytes = null;
                            while (isReceiver) {
                                Log.e(TAG, "run: ");
                                if (mInputStream.available() > 0) {
                                    bytes = new byte[1024];
                                    mInputStream.read(bytes);
                                    Log.e(TAG, "run: " + bytes[0]);
                                    //如果能连接上服务器，设备返回-1，
                                    if (bytes[0] == 66) {

                                        final String SSID = mWifiUtils.mWifiManager.getConnectionInfo().getSSID();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                conneted();
                                                mConnet.setText("当前连接的设备：" + SSID.replace("\"", " "));
                                            }
                                        });
                                        send((byte) 0x42);
                                    }
//test


                                    Log.e(TAG, "run: " + bytes[0]);
                                    if (outoMode) {


                                        //自动模式

                                        //白天
                                        if (bytes[0] == 16) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    closeCurtain();
                                                    Toast.makeText(CurtainControlActivity.this, "closeCurtain", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        //晚上
                                        if (bytes[0] == 32) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    oppenCurtain();
                                                    Toast.makeText(CurtainControlActivity.this, "oppenCurtain", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                }
                                Thread.sleep(200);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private void send(byte i) throws InterruptedException, IOException {
        if (mOutputStream != null) {
            Thread.sleep(200);
            byte[] bytes1 = "\r\n".getBytes();
            byte[] bytes2 = {i, bytes1[0], bytes1[1]};
            mOutputStream.write(bytes2);
            mOutputStream.flush();

        }
    }

    private void unConnet() {
        if (curtainState) {
            closeCurtain();
        }
        mHint.setVisibility(View.VISIBLE);
        mClose.setEnabled(false);
        mOppen.setEnabled(false);
        mMode.setEnabled(false);
        mMode.setBackgroundResource(R.drawable.bg_green);
        mMode.setText("手动模式");
        outoMode = false;
    }

    private void conneted() {
        mHint.setVisibility(View.GONE);
        mClose.setEnabled(false);
        mOppen.setEnabled(true);
        mMode.setEnabled(true);
        mMode.setBackgroundResource(R.drawable.bg_green);
        mMode.setText("手动模式");
        outoMode = false;
    }


    private void regiseterReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mWifiReceiver);
        isReceiver = false;
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前index状态，［0,1］
     *
     * @param value 存储的开关数据int32
     * @param index 第几位
     * @return 当前位置的值
     */
    private boolean getState(int value, int index) {
        return (value >> index & 1) == 1;
    }

    /**
     * 处理后的存储数据 int32
     *
     * @param value 存储的开关数据int32
     * @param index 第几位
     * @param set   开关状态
     * @return 当前位置的值［0,1］
     */
    private int setState(int value, int index, boolean set) {

        if (set) {
            value = 1 << index | value;
        } else {
            value = ~(1 << index) & value;
        }
        return value;
    }
}
