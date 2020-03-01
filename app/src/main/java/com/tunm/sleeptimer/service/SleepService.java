package com.tunm.sleeptimer.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.tunm.sleeptimer.R;
import com.tunm.sleeptimer.activity.MainActivity;
import com.tunm.sleeptimer.preferences.Prefs;
import com.tunm.sleeptimer.receiver.ExtendTimeReceiver;
import com.tunm.sleeptimer.receiver.StopServiceReceiver;
import com.tunm.sleeptimer.utils.SleepCountDownTimer;
import com.tunm.sleeptimer.utils.TimeUtils;

import androidx.core.app.NotificationCompat;

public class SleepService extends Service {
    public final static String UPDATE_TIME_UI = "UPDATE_TIME_UI";
    public final static String STOP_TIMER = "STOP_TIMER";
    public final static String UPDATE_COUNT_ACTION = "com.example.sleeptimer.UPDATE_COUNT";

    public static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_1";

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private SleepCountDownTimer mSleepCountDownTimer;
    int timeCountdown; // minute
    long currentNotificationTime; // updated second
    public static final double STEP_DOWN = 0.005;
    private AudioManager mAudioManager;
    private int saveVolume;

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.init(this);
        Log.v("SleepService ", "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("SleepService ", "onStartCommand");

        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
        if (action != null) {
            Log.v("SleepService action ", "UPDATE_COUNT_ACTION");
            updateCountDown(currentNotificationTime + Prefs.getExtendedTime() * 60);
            return START_STICKY;
        }

        if (intent != null) {
            timeCountdown = intent.getIntExtra("time", 0) * 60;
        }
        notificationManager = getSystemService(NotificationManager.class);

        // intent open mainActivity when click on notification
        Intent startActivityIntent = new Intent(this, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, 0);

        // intent stop service & remove notification
        Intent stopIntent = new Intent(this, StopServiceReceiver.class);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // intent add more time
        Intent extendTimeIntent = new Intent(this, ExtendTimeReceiver.class);
        extendTimeIntent.putExtra("current_notification_time", currentNotificationTime / 60);
        PendingIntent extendTimePendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), extendTimeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon_snooze_96)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_time_large_64))
                .setCategory(Notification.CATEGORY_CALL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(TimeUtils.secondToFullTime(timeCountdown) + " time left until end.")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.icon_stop_96, getString(R.string.stop), stopServicePendingIntent)
                .addAction(R.mipmap.icon_plus_96, "+ " + Prefs.getExtendedTime() + " min", extendTimePendingIntent);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH; // remove notification sound
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, builder.build());

        // count time down => update notification
        mSleepCountDownTimer = new SleepCountDownTimer(this, timeCountdown * 1000, 1000);
        mSleepCountDownTimer.start();

        return START_STICKY;
    }

    public void updateNotification(long millisUntilFinished) {
        Intent intentUpdateUI = new Intent(UPDATE_TIME_UI);
        currentNotificationTime = millisUntilFinished / 1000;
        intentUpdateUI.putExtra("second_update", currentNotificationTime);
        sendBroadcast(intentUpdateUI);
        builder.setContentText(TimeUtils.secondToFullTime(currentNotificationTime) + " time left until end.");

        // Start a lengthy operation in a background thread
        startForeground(NOTIFICATION_ID, builder.build());
    }

    public void updateCountDown(long updatedSecond) {
        if (mSleepCountDownTimer != null) {
            mSleepCountDownTimer.cancel();
        }
        mSleepCountDownTimer = new SleepCountDownTimer(this, updatedSecond * 1000, 1000);
        mSleepCountDownTimer.start();
    }

    public void finishService() {
        Log.v("SleepService ", "finishService");
        Intent currentService = new Intent(this, SleepService.class);
        stopService(currentService);
        if (notificationManager != null) {
            notificationManager.cancel(SleepService.NOTIFICATION_ID);
        }
        sendBroadcast(new Intent(SleepService.STOP_TIMER));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("SleepService ", "onDestroy");
        if (mSleepCountDownTimer != null) {
            mSleepCountDownTimer.cancel();
        }
    }

    public void stopPlayer() throws InterruptedException {
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mAudioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
        int currentVolume = 0;
        if (mAudioManager != null) {
            currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        saveVolume = currentVolume;

        // begin fade out
        int focusRequest = 0;
        int targetVol = 0;
        while (currentVolume > targetVol) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (currentVolume - STEP_DOWN), 0);
            currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Thread.sleep(1500);
        }

        // count end => requestFocus
        if (currentVolume == 0 && mAudioManager != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                focusRequest = mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            } else {
                AudioFocusRequest mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(mAudioAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                        .build();
                focusRequest = mAudioManager.requestAudioFocus(mAudioFocusRequest);
            }
        }
        switch (focusRequest) {
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                Log.v("SleepService ", "requestAudioFocus fail");
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                if (Prefs.getGoHomeScreen()) SleepAction.goHomeScreen(this);
                if (Prefs.getOffScreen()) SleepAction.turnOffScreen(this);
                if (Prefs.getSilentMode()) SleepAction.goSilentMode(this);
                if (Prefs.getOffBlueTooth()) SleepAction.turnOffBluetooth(this);
                if (Prefs.getOffWifi()) SleepAction.turnOffWifi(this);

                // stop service
                finishService();
        }
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

                Log.v("focusChange ", "pause");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

                Log.v("focusChange ", "resume");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

                // Stop or pause depending on your need
                Log.v("focusChange ", "option");
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, saveVolume, 0);
            }
        }
    };
}
