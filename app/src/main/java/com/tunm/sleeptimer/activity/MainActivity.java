package com.tunm.sleeptimer.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.tunm.sleeptimer.R;
import com.tunm.sleeptimer.data.provider.EmojiProvider;
import com.tunm.sleeptimer.data.provider.QuoteDataProvider;
import com.tunm.sleeptimer.preferences.Prefs;
import com.tunm.sleeptimer.service.SleepService;
import com.tunm.sleeptimer.utils.GradientAnimation;
import com.tunm.sleeptimer.utils.ServiceUtils;
import com.tunm.sleeptimer.utils.TimeUtils;
import com.tunm.sleeptimer.view.CircleSeekBar;

import java.util.Objects;

import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button mStartSleepButton, mStopSleepButton;
    private CircleSeekBar mHourSeekbar;
    private CircleSeekBar mMinuteSeekbar;
    private TextView mClockTV, mQuoteTV, mAuthorTV, mEmojiTV;
    private RelativeLayout clockSeekBarContainer;
    private View blankView;

    private GradientAnimation gradientAnimation;
    private QuoteDataProvider quoteDataProvider;
    private EmojiProvider emojiProvider;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setTitle("Sleep Timer");

        mStartSleepButton = findViewById(R.id.buttonStartSleep);
        mStopSleepButton = findViewById(R.id.buttonStopSleep);
        mClockTV = findViewById(R.id.clockTV);
        mEmojiTV = findViewById(R.id.emojiTV);
        clockSeekBarContainer = findViewById(R.id.clockSeekBarContainer);
        blankView = findViewById(R.id.blankView);
        mStartSleepButton.setOnClickListener(this);
        mStopSleepButton.setOnClickListener(this);
        mClockTV.setOnClickListener(this);

        mHourSeekbar = findViewById(R.id.seek_hour);
        mMinuteSeekbar = findViewById(R.id.seek_minute);

        gradientAnimation = new GradientAnimation(this);
        gradientAnimation.setCustomSeekbarAnimation(mMinuteSeekbar, Prefs.getPrimaryColor(this), Prefs.getSeekbarMinuteColor(this));
        gradientAnimation.setCustomSeekbarAnimation(mHourSeekbar, Prefs.getPrimaryColor(this), Prefs.getSeekbarHourColor(this));
        mStartSleepButton.getBackground().setColorFilter(Prefs.getPrimaryColor(this), PorterDuff.Mode.SRC_ATOP);

        mHourSeekbar.setOnSeekBarChangeListener((seekbar, curValue) -> {
            if (!ServiceUtils.isMyServiceRunning(MainActivity.this, SleepService.class)) {
                changeText(curValue, mMinuteSeekbar.getCurProcess());
            }
        });

        mMinuteSeekbar.setOnSeekBarChangeListener((seekbar, curValue) -> {
            if (!ServiceUtils.isMyServiceRunning(MainActivity.this, SleepService.class)) {
                changeText(mHourSeekbar.getCurProcess(), curValue);
            }
        });

        mHourSeekbar.setCurProcess(0);
        mMinuteSeekbar.setCurProcess(0);

        registerReceiver(stopSleepReceiver, new IntentFilter(SleepService.STOP_TIMER));
        registerReceiver(updateTimerReceiver, new IntentFilter(SleepService.UPDATE_TIME_UI));

        if (ServiceUtils.isMyServiceRunning(this, SleepService.class)) {
            Log.v("SleepService ", "running...");
            adjustLayoutWhenClickStart();
        } else {
            Log.v("SleepService ", "stop");
            adjustLayoutWhenClickStop();
        }
        loadEmoji();
        MobileAds.initialize(this, initializationStatus -> {});
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadEmoji() {
        emojiProvider = new EmojiProvider(this);
        mEmojiTV.setText(emojiProvider.getEmojis());
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
                    Toast.makeText(this, "Timer has been set", Toast.LENGTH_SHORT).show();
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
        if (ServiceUtils.isMyServiceRunning(MainActivity.this, SleepService.class)) {
            if(mStopSleepButton.getVisibility() != View.VISIBLE) {
                addOrRemoveProperty(clockSeekBarContainer, RelativeLayout.CENTER_IN_PARENT, false);
                mHourSeekbar.setVisibility(View.VISIBLE);
                mMinuteSeekbar.setVisibility(View.VISIBLE);
                mHourSeekbar.setCanTouch(false);
                mMinuteSeekbar.setCanTouch(false);
                mStopSleepButton.setVisibility(View.VISIBLE);
                blankView.setVisibility(View.GONE);
            } else {
                adjustLayoutWhenClickStart();
            }
        }
    }

    private void adjustLayoutWhenClickStart() {
        addOrRemoveProperty(clockSeekBarContainer, RelativeLayout.CENTER_IN_PARENT, true);
        mStartSleepButton.setVisibility(View.GONE);
        mHourSeekbar.setVisibility(View.GONE);
        mMinuteSeekbar.setVisibility(View.GONE);
        mStopSleepButton.setVisibility(View.GONE);
        blankView.setVisibility(View.VISIBLE);
    }

    private void updateSeekbar(long totalSeconds) {
        int hour = (int) (totalSeconds / 3600);
        int minute = (int) ((totalSeconds % 3600) / 60);
        int seconds = (int) (totalSeconds % 60);
        Log.v("updateSeekbar", "second: " + seconds + " - minute: " + minute);
        if(minute > 0) mHourSeekbar.setCurProcess(hour+1);
        else mHourSeekbar.setCurProcess(hour);
        if(seconds > 0) mMinuteSeekbar.setCurProcess(minute+1);
        else mMinuteSeekbar.setCurProcess(minute);
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
        Log.v("stop ", "ok");
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
            addOrRemoveProperty(clockSeekBarContainer, RelativeLayout.CENTER_IN_PARENT, false);
            adjustLayoutWhenClickStop();
        }
    };

    BroadcastReceiver updateTimerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long second = intent.getLongExtra("second_update", 0);
            Log.v("updateTimerReceiver", second + "");
            mClockTV.setText(TimeUtils.secondToFullTime(second));
            updateSeekbar(second);
        }
    };

    BroadcastReceiver updateCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopSleepReceiver);
        unregisterReceiver(updateTimerReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;

            case R.id.action_rate:
                openAppInGooglePlay();
                break;

            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAppInGooglePlay() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void refreshTheme() {
        gradientAnimation.setCustomSeekbarAnimation(mMinuteSeekbar, Prefs.getPrimaryColor(this), Prefs.getSeekbarMinuteColor(this));
        gradientAnimation.setCustomSeekbarAnimation(mHourSeekbar, Prefs.getPrimaryColor(this), Prefs.getSeekbarHourColor(this));
        mStartSleepButton.getBackground().setColorFilter(Prefs.getPrimaryColor(this), PorterDuff.Mode.SRC_ATOP);
    }
}
