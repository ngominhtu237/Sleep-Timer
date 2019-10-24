package com.tunm.sleeptimer.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.tunm.sleeptimer.view.CircleSeekBar;

public class GradientAnimation {

    private Activity mActivity;
    public GradientAnimation(Activity activity) {
        this.mActivity = activity;
    }

    public void setCustomSeekbarAnimation(final CircleSeekBar seekBar, final int primaryColor, final int secondColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new android.animation.ArgbEvaluator(), primaryColor, secondColor);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                seekBar.setReachedColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(2800L);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.setInterpolator(new LinearInterpolator());
        colorAnimation.setRepeatCount(Animation.INFINITE);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.start();
    }
}
