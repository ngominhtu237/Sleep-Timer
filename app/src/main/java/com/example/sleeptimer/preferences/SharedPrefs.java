package com.example.sleeptimer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

final class SharedPrefs {
    private static final String PREFERENCES_NAME = "com.ss.tunm.SHARED_PREF";
    private static final int PREFERENCES_MODE = Context.MODE_PRIVATE;
    private final SharedPreferences sharedPrefs;

    SharedPrefs(@NonNull Context context) {
        sharedPrefs = context.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, PREFERENCES_MODE);
    }

    private SharedPreferences.Editor getEditer() {
        return sharedPrefs.edit();
    }

    void put(String key, int value) {
        getEditer().putInt(key, value).commit();
    }

    int get(String key, int defaultValue) {
        return sharedPrefs.getInt(key, defaultValue);
    }

    void put(String key, boolean value) {
        getEditer().putBoolean(key, value).commit();
    }

    boolean get(String key, boolean defaultValue) {
        return sharedPrefs.getBoolean(key, defaultValue);
    }
}
