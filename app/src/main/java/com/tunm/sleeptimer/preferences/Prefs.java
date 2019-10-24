package com.tunm.sleeptimer.preferences;

import android.content.Context;

import androidx.annotation.NonNull;

public class Prefs {
    private static SharedPrefs sharedPrefs;

    public static void init(@NonNull Context context) {
//        if (sharedPrefs != null) {
//            throw new RuntimeException("Prefs has already been instantiated");
//        }
        sharedPrefs = new SharedPrefs(context);
    }

    @NonNull
    private static SharedPrefs getPrefs() {
        if (sharedPrefs == null) {
            throw new RuntimeException("Prefs has not been instantiated. Call init() with context");
        }
        return sharedPrefs;
    }

    /********** GETTERS **********/
    public static int getPrimaryColor(Context context) {
        return getPrefs().get(Keys.COLOR_PRIMARY, context.getColor(Defaults.COLOR_PRIMARY));
    }

    public static int getSeekbarHourColor(Context context) {
        return getPrefs().get(Keys.COLOR_SEEKBAR_HOUR, context.getColor(Defaults.COLOR_SEEKBAR_HOUR));
    }

    public static int getSeekbarMinuteColor(Context context) {
        return getPrefs().get(Keys.COLOR_SEEKBAR_MINUTE, context.getColor(Defaults.COLOR_SEEKBAR_MINUTE));
    }

    public static int getExtendedTime() {
        return getPrefs().get(Keys.EXTENDED_TIME, Defaults.EXTENDED_TIME);
    }

    public static boolean getGoHomeScreen() {
        return getPrefs().get(Keys.GO_HOME_SCREEN, Defaults.GO_HOME_SCREEN);
    }

    public static boolean getOffScreen() {
        return getPrefs().get(Keys.OFF_SCREEN, Defaults.OFF_SCREEN);
    }

    public static boolean getSilentMode() {
        return getPrefs().get(Keys.SILENT_MODE, Defaults.SILENT_MODE);
    }

    public static boolean getOffWifi() {
        return getPrefs().get(Keys.OFF_WIFI, Defaults.OFF_WIFI);
    }

    public static boolean getOffBlueTooth() {
        return getPrefs().get(Keys.OFF_BLUETOOTH, Defaults.OFF_BLUETOOTH);
    }

    public static int getTimeOffScreen() {
        return getPrefs().get(Keys.TIME_OFF_SCREEN, Defaults.TIME_OFF_SCREEN);
    }

    /********** SETTERS **********/
    public static void setPrimaryColor(int value) {
        getPrefs().put(Keys.COLOR_PRIMARY, value);
    }

    public static void setSeekbarHourColor(@NonNull int value) {
        getPrefs().put(Keys.COLOR_SEEKBAR_HOUR, value);
    }

    public static void setSeekbarMinuteColor(@NonNull int value) {
        getPrefs().put(Keys.COLOR_SEEKBAR_MINUTE, value);
    }

    public static void setExtendedTime(@NonNull int value) {
        getPrefs().put(Keys.EXTENDED_TIME, value);
    }

    public static void setGoHomeScreen(@NonNull boolean value) {
        getPrefs().put(Keys.GO_HOME_SCREEN, value);
    }
    public static void setOffScreen(@NonNull boolean value) {
        getPrefs().put(Keys.OFF_SCREEN, value);
    }

    public static void setSilentMode(@NonNull boolean value) {
        getPrefs().put(Keys.SILENT_MODE, value);
    }

    public static void setOffWifi(@NonNull boolean value) {
        getPrefs().put(Keys.OFF_WIFI, value);
    }

    public static void setOffBlueTooth(@NonNull boolean value) {
        getPrefs().put(Keys.OFF_BLUETOOTH, value);
    }

    public static void setTimeOffScreen(@NonNull int value) {
        getPrefs().put(Keys.TIME_OFF_SCREEN, value);
    }
}
