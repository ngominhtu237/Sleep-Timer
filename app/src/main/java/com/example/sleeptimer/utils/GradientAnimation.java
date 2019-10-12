package com.example.sleeptimer.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.sleeptimer.R;
import com.example.sleeptimer.view.CircleSeekBar;

import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

public class GradientAnimation {
    public static void oscillateDemo(final Activity activity, final View view, final int colorOne, final int colorTwo) {

        final int primaryColor;

        if(colorOne == 0) {
            primaryColor = activity.getColor(R.color.md_green_200);
        } else {
            primaryColor = colorOne;
        }

        final int counter = 10000;

        Thread oscillateThread = new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < counter; i++) {

                    final int fadeToColor = (i % 2 == 0)
                            ? primaryColor
                            : colorTwo;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            animateTextViewColors(view, fadeToColor);
                        }
                    });

                    try {
                        Thread.sleep(2450);
                    }
                    catch (InterruptedException iEx) {}
                }
            }
        };

        oscillateThread.start();
    }

    private static void animateTextViewColors(View textView, Integer colorTo) {

        final Property<View, Integer> property = new Property<View, Integer>(int.class, "color") {
            @Override
            public Integer get(View object) {
                if(object instanceof CircleSeekBar) {
                    return ((CircleSeekBar)object).getReachedColor();
                } else {
                    return ((TextView)object).getCurrentTextColor();
                }
            }

            @Override
            public void set(View object, Integer value) {
                if(object instanceof CircleSeekBar) {
                    ((CircleSeekBar)object).setReachedColor(value);
                } else {
                    ((TextView)object).setTextColor(value);
                }
            }
        };

        final ObjectAnimator animator = ObjectAnimator.ofInt(textView, property, colorTo);
        animator.setDuration(3533L);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new DecelerateInterpolator(2));
        animator.start();
    }
}
