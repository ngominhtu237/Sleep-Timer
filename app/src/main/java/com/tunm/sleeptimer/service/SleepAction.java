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

    private static final String TAG = " sleeptimer:mywakelocktag.";


    static void goHomeScreen(Service act) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivity(startMain);
    }

    @SuppressLint("LongLogTag")
    static void turnOffScreen(Service service) {
        Log.v("SleepAction", "sleep");
//        int defaultTurnOffTime =  Settings.System.getInt(service.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, 60000); // current time off screen
//        Settings.System.putInt(service.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, 500);
////        if(isScreenOn(service)) {
////            Log.v("offscreen", "ok");
//            Settings.System.putInt(service.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, defaultTurnOffTime);
////        }
//        Prefs.setTimeOffScreen(defaultTurnOffTime);
//        ComponentName compName = new ComponentName(service, AdminReceiver.class);
//        DevicePolicyManager deviceManger = (DevicePolicyManager)service.getSystemService(
//                Context.DEVICE_POLICY_SERVICE);
//        if (deviceManger != null && deviceManger.isAdminActive(compName)) {
//            deviceManger.lockNow();
//        }
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
        if (audioManager != null) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

    static void turnOffWifi(Service act) {
        WifiManager wifiManager = (WifiManager) act.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(false);
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
