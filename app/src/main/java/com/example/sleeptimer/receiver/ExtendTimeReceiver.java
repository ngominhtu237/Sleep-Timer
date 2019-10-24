package com.example.sleeptimer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sleeptimer.service.SleepService;

public class ExtendTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.v("ExtendTimeReceiver ", "onReceive");
        int currentNotificationTime = intent.getIntExtra("current_notification_time", 0);
        Log.v("currentNotificationTime", currentNotificationTime + "");
//        context.sendBroadcast(new Intent(SleepService.UPDATE_COUNT));

        Intent myIntent = new Intent(context, SleepService.class);
        myIntent.setAction(SleepService.UPDATE_COUNT_ACTION);
        context.startService(myIntent);
    }
}
