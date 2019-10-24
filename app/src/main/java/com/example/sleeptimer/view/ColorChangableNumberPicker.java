package com.example.sleeptimer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import androidx.annotation.ColorInt;

import java.lang.reflect.Field;

public class ColorChangableNumberPicker extends NumberPicker {

    public ColorChangableNumberPicker(Context context) {
        super(context);
        init();
    }

    public ColorChangableNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorChangableNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorChangableNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setDividerColor(Color.RED);
    }


    public void setDividerColor(@ColorInt int color) {
        try {
            Field fDividerDrawable = NumberPicker.class.getDeclaredField("mSelectionDivider");
            fDividerDrawable.setAccessible(true);
            Drawable d = (Drawable) fDividerDrawable.get(this);
            d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            d.invalidateSelf();
            postInvalidate(); // Drawable is dirty
        }
        catch (Exception e) {

        }
    }
}