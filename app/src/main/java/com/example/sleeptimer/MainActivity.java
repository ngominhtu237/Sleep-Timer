package com.example.sleeptimer;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sleeptimer.utils.SleepTimerUtils;
import com.example.sleeptimer.view.CircleSeekBar;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.sleeptimer.utils.GradientAnimation.oscillateDemo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartSleepButton, mStopSleepButton;
    private CircleSeekBar mHourSeekbar;
    private CircleSeekBar mMinuteSeekbar;
    private TextView mClockTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setTitle("Sleep Timer");

        mStartSleepButton = findViewById(R.id.buttonStartSleep);
        mStopSleepButton = findViewById(R.id.buttonStopSleep);
        mClockTV = findViewById(R.id.clockTV);
        mStartSleepButton.setOnClickListener(this);
        mStopSleepButton.setOnClickListener(this);
        mClockTV.setOnClickListener(this);

        mHourSeekbar = findViewById(R.id.seek_hour);
        mMinuteSeekbar = findViewById(R.id.seek_minute);

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

        registerReceiver(stopSleepReceiver, new IntentFilter(SleepService.STOP_TIMER));
        registerReceiver(updateTimerReceiver, new IntentFilter(SleepService.UPDATE_TIME_UI));
    }

    @SuppressLint("SetTextI18n")
    private void changeText(int hour, int minute) {
        String hourStr = hour > 9 ? hour + "" : "0" + hour;
        String minuteStr = minute > 9 ? minute + "" : "0" + minute;
        mClockTV.setText(hourStr + ":" + minuteStr);
    }

    private void startSleepService() {
        Intent intent = new Intent(MainActivity.this, SleepService.class);
        intent.putExtra("time", mMinuteSeekbar.getCurProcess() + mHourSeekbar.getCurProcess() * 60);
        stopService(intent); // when user multiple click => need to restart service to prevent duplicate notification
        startService(intent);
    }

    private void stopSleepService() {
        Intent service = new Intent(this, SleepService.class);
        stopService(service);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(SleepService.NOTIFICATION_ID);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStartSleep:
                if (mMinuteSeekbar.getCurProcess() == 0 && mHourSeekbar.getCurProcess() == 0) {
                    Toast.makeText(this, "Please choose sleep time!", Toast.LENGTH_SHORT).show();
                } else {
                    startSleepService();
                    adjustLayoutWhenClickStart();
                }
                break;
            case R.id.buttonStopSleep:
                stopSleepService();
                adjustLayoutWhenClickStop();
                break;
            case R.id.clockTV:
                adjustLayoutWhenClickClock();
                break;
        }
    }

    private void adjustLayoutWhenClickClock() {
        addOrRemoveProperty(mClockTV, RelativeLayout.CENTER_IN_PARENT, false);
        mHourSeekbar.setVisibility(View.VISIBLE);
        mMinuteSeekbar.setVisibility(View.VISIBLE);
        mHourSeekbar.setCanTouch(false);
        mMinuteSeekbar.setCanTouch(false);
        mStopSleepButton.setVisibility(View.VISIBLE);
        mClockTV.setClickable(false);
    }

    private void adjustLayoutWhenClickStart() {
        addOrRemoveProperty(mClockTV, RelativeLayout.CENTER_IN_PARENT, true);
        mStartSleepButton.setVisibility(View.GONE);
        mHourSeekbar.setVisibility(View.GONE);
        mMinuteSeekbar.setVisibility(View.GONE);
        mClockTV.setClickable(true);
        Toast.makeText(this, "Timer has been set", Toast.LENGTH_SHORT).show();
    }

    private void adjustLayoutWhenClickStop() {
        mStartSleepButton.setVisibility(View.VISIBLE);
        mStopSleepButton.setVisibility(View.GONE);
        mHourSeekbar.setVisibility(View.VISIBLE);
        mMinuteSeekbar.setVisibility(View.VISIBLE);
        mHourSeekbar.setCanTouch(true);
        mMinuteSeekbar.setCanTouch(true);
        mHourSeekbar.setCurProcess(0);
        mMinuteSeekbar.setCurProcess(0);
    }

    private void addOrRemoveProperty(View view, int property, boolean isAdd) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (isAdd) {
            layoutParams.addRule(property);
        } else {
            layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }

    BroadcastReceiver stopSleepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addOrRemoveProperty(mClockTV, RelativeLayout.CENTER_IN_PARENT, false);
            adjustLayoutWhenClickStop();
        }
    };

    BroadcastReceiver updateTimerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long second = intent.getLongExtra("second_update", 0);
            mClockTV.setText(SleepTimerUtils.secondToFullTime(second));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopSleepReceiver);
        unregisterReceiver(updateTimerReceiver);
    }
}
