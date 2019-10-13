package com.example.sleeptimer.utils;

import android.annotation.SuppressLint;

public class SleepTimerUtils {

    @SuppressLint("DefaultLocale")
    public static String secondToFullTime(long sec) {
        long seconds = sec % 60;
        long minutes = sec / 60;
        if (minutes >= 60) {
            long hours = minutes / 60;
            minutes %= 60;
            if( hours >= 24) {
                long days = hours / 24;
                return String.format("%d days %02d:%02d:%02d", days,hours%24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatHoursAndMinutes(long totalMinutes) {
        long hours = totalMinutes / 60; //since both are ints, you get an int
        long minutes = totalMinutes % 60;
        return hours + ":" + minutes;
    }
}
