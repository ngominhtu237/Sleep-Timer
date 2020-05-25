package com.tunm.sleeptimer.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

class SleepAction {

    private static final String TAG = "SleepAction";

    static void goHomeScreen(Service act) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivity(startMain);
    }

    @SuppressLint("LongLogTag")
    static void turnOffScreen(Service service) {
        Log.v("SleepAction", "sleep");
        lock(service);
    }

    private static void lock(Service service) {
        PowerManager pm = (PowerManager) service.getSystemService(Context.POWER_SERVICE);
        if (pm != null && pm.isScreenOn()) {
            DevicePolicyManager policy = (DevicePolicyManager) service.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (policy != null) {
                policy.lockNow();
            }
        }
    }

    static void goSilentMode(Service act) {
        AudioManager audioManager = (AudioManager) act.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        try {
            if (audioManager != null) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
    }

    static void turnOffWifi(Service act) {
        WifiManager wifiManager = (WifiManager) act.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled;
        if (wifiManager != null) {
            wifiEnabled = wifiManager.isWifiEnabled();
            Log.v(TAG, "wifiEnabled" + wifiEnabled);
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    static void turnOffBluetooth(Service act) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if(isEnabled) {
            bluetoothAdapter.disable();
        }
    }
}
