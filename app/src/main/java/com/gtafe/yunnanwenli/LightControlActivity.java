package com.gtafe.yunnanwenli;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LightControlActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final String SWITCHBUTTONSTATE = "switchbuttonstate";
    public WifiUtils mWifiUtils;
    private String[] mEquipmentList;
    public WifiReceiver mWifiReceiver;
    public Socket mSocket;
    private boolean isReceiver = true;
    public InputStream mInputStream;
    public SwitchButton mSwitchButton0;
    public SwitchButton mSwitchButton1;
    public SwitchButton mSwitchButton2;
    public SwitchButton mSwitchButton3;
    public SwitchButton mSwitchButton4;
    public SwitchButton mSwitchButton5;
    public SwitchButton mSwitchButton6;
    public SwitchButton mSwitchButton7;
    public LinearLayout mContaner;
    public int mValue;
    public OutputStream mOutputStream;
    public Button mConnet;
    public ImageView mHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEquipmentList();
        mWifiUtils = new WifiUtils(this);
        regiseterReceiver();
        initSwitchButtonState();
        showHint();
    }
    private void showHint() {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,-0.6f,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
        animation.setDuration(600);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        mHint.startAnimation(animation);
    }

    private void initView() {
        mHint = (ImageView) findViewById(R.id.curtain_hint);

        mContaner = (LinearLayout) findViewById(R.id.contaner);
        mConnet = (Button) findViewById(R.id.main_connet);
        mSwitchButton0 = (SwitchButton) findViewById(R.id.main_switchbutton0);
        mSwitchButton1 = (SwitchButton) findViewById(R.id.main_switchbutton1);
        mSwitchButton2 = (SwitchButton) findViewById(R.id.main_switchbutton2);
        mSwitchButton3 = (SwitchButton) findViewById(R.id.main_switchbutton3);
        mSwitchButton4 = (SwitchButton) findViewById(R.id.main_switchbutton4);
        mSwitchButton5 = (SwitchButton) findViewById(R.id.main_switchbutton5);
        mSwitchButton6 = (SwitchButton) findViewById(R.id.main_switchbutton6);
        mSwitchButton7 = (SwitchButton) findViewById(R.id.main_switchbutton7);

        mSwitchButton0.setOnClickListener(this);
        mSwitchButton1.setOnClickListener(this);
        mSwitchButton2.setOnClickListener(this);
        mSwitchButton3.setOnClickListener(this);
        mSwitchButton4.setOnClickListener(this);
        mSwitchButton5.setOnClickListener(this);
        mSwitchButton6.setOnClickListener(this);
        mSwitchButton7.setOnClickListener(this);
        setButtonEnable(false);
        mConnet.setOnClickListener(this);
    }

    private void initSwitchButtonState() {
        mValue = getSharedPreferences(SWITCHBUTTONSTATE, MODE_PRIVATE).getInt(SWITCHBUTTONSTATE, 0);
        mSwitchButton0.setBackgroundState(getState(mValue, 0));
        mSwitchButton1.setBackgroundState(getState(mValue, 1));
        mSwitchButton2.setBackgroundState(getState(mValue, 2));
        mSwitchButton3.setBackgroundState(getState(mValue, 3));
        mSwitchButton4.setBackgroundState(getState(mValue, 4));
        mSwitchButton5.setBackgroundState(getState(mValue, 5));
        mSwitchButton6.setBackgroundState(getState(mValue, 6));
        mSwitchButton7.setBackgroundState(getState(mValue, 7));
    }

    private void initEquipmentList() {
        mEquipmentList = new String[16];
        for (int i = 1; i < 17; i++) {
            if (i < 10) {
                mEquipmentList[i - 1] = "GTAWJ_WX_02_00" + i;

            } else {
                mEquipmentList[i - 1] = "GTAWJ_WX_02_0" + i;
            }
        }
    }
    //连接到指定wifi
    private void connectWIfi(int which) {

        mWifiUtils.disConnectionWifi(mWifiUtils.getNetWordId());
        mWifiUtils.addNetWork(mEquipmentList[which],"1234567890");

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.main_switchbutton0:
                changState(0, mSwitchButton0);
                break;
            case R.id.main_switchbutton1:
                changState(1, mSwitchButton1);
                break;
            case R.id.main_switchbutton2:
                changState(2, mSwitchButton2);
                break;
            case R.id.main_switchbutton3:
                changState(3, mSwitchButton3);
                break;
            case R.id.main_switchbutton4:
                changState(4, mSwitchButton4);
                break;
            case R.id.main_switchbutton5:
                changState(5, mSwitchButton5);
                break;
            case R.id.main_switchbutton6:
                changState(6, mSwitchButton6);
                break;
            case R.id.main_switchbutton7:
                changState(7, mSwitchButton7);
                break;
            case R.id.main_connet:

                connect();
                break;
        }
    }

    private void connect() {
        if (mWifiUtils.checkState() == WifiManager.WIFI_STATE_DISABLED) {
            mWifiUtils.openWifi();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(LightControlActivity.this)
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

    //根据点击的按钮改变开关状态
    private void changState(int index, SwitchButton switchButton) {
        boolean b = !getState(mValue, index);
        switchButton.setBackgroundState(b);
        mValue = setState(mValue, index, b);
        try {
            if (mOutputStream != null) {
                byte[] bytes1 = "\r\n".getBytes();
                byte[] bytes = {(byte) mValue,bytes1[0],bytes1[1]};
              mOutputStream.write(bytes);

            mOutputStream.flush();}
        } catch (IOException e) {
            e.printStackTrace();
        }

        //保存开关状态
       // getSharedPreferences(SWITCHBUTTONSTATE, MODE_PRIVATE).edit().putInt(SWITCHBUTTONSTATE, mValue).commit();
    }

    //wifi状态改变回调
    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            setButtonEnable(false);
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
                                    if (bytes[0] == 65) {

                                        final String SSID = mWifiUtils.mWifiManager.getConnectionInfo().getSSID();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setButtonEnable(true);

                                                mConnet.setText("当前连接的设备：" + SSID.replace("\"", " "));
                                            }
                                        });
                                        if (mOutputStream != null) {
                                            Thread.sleep(200);
                                            byte[] bytes1 = "\r\n".getBytes();
                                            byte[] bytes2 = {0x41,bytes1[0],bytes1[1]};
                                            mOutputStream.write(bytes2);
                                            mOutputStream.flush();}
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

    private void setButtonEnable(boolean enable) {
        mConnet.setText("点击连接设备");
        mSwitchButton0.setEnabled(enable);
        mSwitchButton1.setEnabled(enable);
        mSwitchButton2.setEnabled(enable);
        mSwitchButton3.setEnabled(enable);
        mSwitchButton4.setEnabled(enable);
        mSwitchButton5.setEnabled(enable);
        mSwitchButton6.setEnabled(enable);
        mSwitchButton7.setEnabled(enable);
        mHint.setVisibility(enable?View.GONE:View.VISIBLE);
        // mContaner.setBackgroundResource();
    }

    //wifi广播注册
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
                mSocket.shutdownInput();
                mSocket.shutdownOutput();
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前index状态，［0,1］
     * @param value 存储的开关数据int32
     * @param index 第几位
     * @return 当前位置的值
     */
    private boolean getState(int value, int index) {
        return (value >> index & 1) == 1;
    }

    /**
     * 处理后的存储数据 int32
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
