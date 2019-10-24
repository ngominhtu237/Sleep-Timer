package com.example.sleeptimer.preferences;

import com.example.sleeptimer.R;

public class Defaults {

    // Prevent class instantiation
    private Defaults() {}
    public static final int COLOR_PRIMARY = R.color.colorPrimary;
    public static final int COLOR_SEEKBAR_HOUR = R.color.seekbar_hour_color;
    public static final int COLOR_SEEKBAR_MINUTE = R.color.seekbar_minute_color;

    public static final int EXTENDED_TIME = 10;  // minute => 10/60h

    public static final boolean GO_HOME_SCREEN = false;
    public static final boolean OFF_SCREEN = false;
    public static final boolean SILENT_MODE = false;
    public static final boolean OFF_WIFI = false;
    public static final boolean OFF_BLUETOOTH = false;

    public static final int TIME_OFF_SCREEN = 60;  // second => 1

}
