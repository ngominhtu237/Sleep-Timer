package com.example.sleeptimer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SleepTimerUtils {
    public static String ConvertMinutesTimeToHHMMString(int minutesTime) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(timeZone);
        String time = df.format(new Date(minutesTime * 60 * 1000L));

        return time;
    }

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
}
