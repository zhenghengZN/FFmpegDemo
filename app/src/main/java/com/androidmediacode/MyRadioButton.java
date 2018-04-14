package com.androidmediacode;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;

/**
 * Created by zhengheng on 18/4/13.
 */
public class MyRadioButton extends RadioButton {


    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRadioButton(Context context) {
        super(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Typeface createTypeface(Context context, String fontPath) {
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }

    @Override
    public void setTypeface(Typeface tf,int style) {
        Typeface typeface = createTypeface(getContext(), "iconfont.ttf");
        Log.e("MyRadioButton", "setTypeface "+ typeface);
        super.setTypeface(typeface,style);

    }
}
