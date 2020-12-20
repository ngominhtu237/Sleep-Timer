package com.tunm.sleeptimer.utils;

import android.os.CountDownTimer;
import android.util.Log;

import com.tunm.sleeptimer.service.SleepService;

public class SleepCountDownTimer extends CountDownTimer {

    private SleepService mSleepService;

    public SleepCountDownTimer(SleepService sleepService, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        mSleepService = sleepService;
    }

    @Override
    public void onTick(long l) {
        mSleepService.updateNotification(l);
    }

    @Override
    public void onFinish() {
        Log.v("SleepCountDownTimer", "onFinish");
        mSleepService.stop();
    }
}
