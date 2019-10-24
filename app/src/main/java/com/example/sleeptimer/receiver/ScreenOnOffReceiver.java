package com.example.sleeptimer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("ScreenOnOffReceiver", "Screen Off");

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("ScreenOnOffReceiver", "Screen On");
        }
    }
}
