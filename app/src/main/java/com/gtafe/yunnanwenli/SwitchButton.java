package com.gtafe.yunnanwenli;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;



/**
 * Created by ZhouJF on 2017/8/15.
 */

public class SwitchButton extends AppCompatButton {
    private static final String TAG = "SwitchButton";
    public SwitchButton(Context context) {
       this(context, null);

    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,  R.styleable.SwitchButton);
        boolean aBoolean = typedArray.getBoolean(R.styleable.SwitchButton_state, false);
        typedArray.recycle();
        setBackgroundState(aBoolean);

    }





    public void setBackgroundState(boolean state) {
        if (state){
            setBackgroundResource(R.drawable.on);
            Log.e(TAG, "setBackgroundState: "+ state);
        }else {
            setBackgroundResource(R.drawable.off);
        }
    }

}
