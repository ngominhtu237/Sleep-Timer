package com.tunm.sleeptimer.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

public class ColorUtils {

    public static String colorToString(Context context, int color) {
        return "#" + Integer.toHexString(ContextCompat.getColor(context, color) & 0xffffff);
    }

    /**
     * @param originalColor color, without alpha
     * @param alpha         from 0.0 to 1.0
     * @return
     */
    public static String addAlpha(String originalColor, double alpha) {
        long alphaFixed = Math.round(alpha * 255);
        String alphaHex = Long.toHexString(alphaFixed);
        if (alphaHex.length() == 1) {
            alphaHex = "0" + alphaHex;
        }
        originalColor = originalColor.replace("#", "#" + alphaHex);


        return originalColor;
    }
}
