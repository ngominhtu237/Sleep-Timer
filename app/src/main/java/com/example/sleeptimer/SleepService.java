package com.example.sleeptimer;

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

import com.example.sleeptimer.receiver.StopServiceReceiver;
import com.example.sleeptimer.utils.SleepCountDownTimer;
import com.example.sleeptimer.utils.SleepTimerUtils;

import androidx.core.app.NotificationCompat;

public class SleepService  extends Service {

    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_ID2 = 2;

    private static final String CHANNEL_ID = "channel_1";
    private static final String CHANNEL_ID2 = "channel_2";
    private NotificationCompat.Builder builder, builder2;
    private NotificationManager notificationManager;
    private SleepCountDownTimer mSleepCountDownTimer;
    private NotificationChannel channel;
    int timeCountdown;

    @Override
    public void onCreate() {
        super.onCreate();
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
        timeCountdown = intent.getIntExtra("time", 0) * 60;
        notificationManager = getSystemService(NotificationManager.class);

        // intent open mainActivity when click on notification
        Intent startActivityIntent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, 0);

        // intent stop service & remove notification
        Intent stopServiceIntent = new Intent(this, StopServiceReceiver.class);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), stopServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // intent add more time

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon_clock)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_time_large_64))
                .setCategory(Notification.CATEGORY_CALL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(SleepTimerUtils.secondToFullTime(timeCountdown) + " time left until end.")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.icon_stop_96, getString(R.string.stop), stopServicePendingIntent);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH; // remove notification sound
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, builder.build());

        // count time down => update notification
        mSleepCountDownTimer = new SleepCountDownTimer(this, timeCountdown * 1000, 1000);
        mSleepCountDownTimer.start();

        return START_STICKY;
    }

    public void updateNotification(long millisUntilFinished) {
        builder.setContentText(SleepTimerUtils.secondToFullTime((millisUntilFinished/1000)) + " time left until end.");

        // Start a lengthy operation in a background thread
        startForeground(NOTIFICATION_ID, builder.build());
    }

    public void finishService() {
        Log.v("SleepService ", "finishService");
        Intent currentService = new Intent(this, SleepService.class);
        stopService(currentService);
        if (notificationManager != null) {
            notificationManager.cancel(SleepService.NOTIFICATION_ID);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("SleepService ", "onDestroy");
        if(mSleepCountDownTimer != null) {
            mSleepCountDownTimer.cancel();
        }
    }

    public void stopPlayer() throws InterruptedException {
        final AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        AudioAttributes mAudioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
        int currentVolume = 0;
        if (mAudioManager != null) {
            currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        final int saveVolume = currentVolume;
        AudioFocusRequest mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mAudioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
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
                })
                .build();

        int focusRequest = 0;
        while (currentVolume != 0) {
            int newVolume = (int) (currentVolume * 0.99);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
            currentVolume = newVolume;
            Thread.sleep(1000);
            if(currentVolume == 0) {
                focusRequest = mAudioManager.requestAudioFocus(mAudioFocusRequest);
                break;
            }
        }
        switch (focusRequest) {
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
        }
    }
}
