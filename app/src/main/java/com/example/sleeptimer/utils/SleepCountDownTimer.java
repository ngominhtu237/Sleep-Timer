package com.example.sleeptimer.utils;

import android.os.CountDownTimer;
import android.util.Log;

import com.example.sleeptimer.SleepService;

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
        // done
        mSleepService.finishService();
        try {
            mSleepService.stopPlayer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
