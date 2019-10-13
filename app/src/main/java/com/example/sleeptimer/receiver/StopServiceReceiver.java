package com.example.sleeptimer.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sleeptimer.SleepService;

public class StopServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("StopServiceReceiver ", "onReceive");
        Intent service = new Intent(context, SleepService.class);
        context.stopService(service);
        cancelNotification(context);
        context.sendBroadcast(new Intent(SleepService.STOP_TIMER));
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(SleepService.NOTIFICATION_ID);
        }
    }
}
