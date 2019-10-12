package com.example.sleeptimer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sleeptimer.view.CircleSeekBar;

import static com.example.sleeptimer.utils.GradientAnimation.oscillateDemo;

public class MainActivity extends AppCompatActivity {

    private Button mStartSleepButton, mStopSleepButton;
    private CircleSeekBar mHourSeekbar;
    private CircleSeekBar mMinuteSeekbar;
    private TextView mTextView;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setTitle("Sleep Timer");

        mStartSleepButton = findViewById(R.id.buttonStartSleep);
        mStopSleepButton = findViewById(R.id.buttonStopSleep);
        mStartSleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startSleepService();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mHourSeekbar = findViewById(R.id.seek_hour);
        mMinuteSeekbar = findViewById(R.id.seek_minute);
        mTextView =  findViewById(R.id.textview);
//        oscillateDemo(this, mTextView,  getColor(R.color.accent_white), getColor(R.color.accent_pink));
        oscillateDemo(this, mMinuteSeekbar, 0, getColor(R.color.accent_cyan));
        oscillateDemo(this, mHourSeekbar, 0, getColor(R.color.accent_yellow));

        mHourSeekbar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                changeText(curValue, mMinuteSeekbar.getCurProcess());
            }
        });

        mMinuteSeekbar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                changeText(mHourSeekbar.getCurProcess(), curValue);
            }
        });

        mHourSeekbar.setCurProcess(0);
        mMinuteSeekbar.setCurProcess(0);
    }

    private void changeText(int hour, int minute) {
        String hourStr = hour > 9 ? hour + "" : "0" + hour;
        String minuteStr = minute > 9 ? minute + "" : "0" + minute;
        mTextView.setText(hourStr + ":" + minuteStr);
    }

    private void startSleepService() {
        if(mMinuteSeekbar.getCurProcess() == 0 && mHourSeekbar.getCurProcess() == 0) {
            Toast.makeText(this, "Please choose sleep time!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, SleepService.class);
            intent.putExtra("time", mMinuteSeekbar.getCurProcess() + mHourSeekbar.getCurProcess() * 60);
            stopService(intent); // when user multiple click => need to restart service to prevent duplicate notification
            startService(intent);

            doAnimationStartService();
        }
    }

    private void doAnimationStartService() {
        moveViewToScreenCenter(mTextView);
        mStartSleepButton.setVisibility(View.GONE);
        mHourSeekbar.setVisibility(View.GONE);
        mMinuteSeekbar.setVisibility(View.GONE);
        mStopSleepButton.setVisibility(View.VISIBLE);
    }

    private void moveViewToScreenCenter( final View view ){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );

        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos );

        int xDelta = (dm.widthPixels - view.getMeasuredWidth() - originalPos[0])/2;
        int yDelta = (dm.heightPixels - view.getMeasuredHeight() - originalPos[1])/2;

        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillAfter(true);
        animSet.setDuration(1000);
        animSet.setInterpolator(new LinearInterpolator());
        TranslateAnimation translate = new TranslateAnimation( 0, xDelta , 0, yDelta);
        animSet.addAnimation(translate);
        ScaleAnimation scale = new ScaleAnimation(1f, 1.5f, 1f, 1.5f, ScaleAnimation.RELATIVE_TO_PARENT, .5f, ScaleAnimation.RELATIVE_TO_PARENT, .5f);
        animSet.addAnimation(scale);
        view.startAnimation(animSet);
    }
}
