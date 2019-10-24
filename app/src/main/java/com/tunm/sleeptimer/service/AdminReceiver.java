package com.tunm.sleeptimer.service;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AdminReceiver extends DeviceAdminReceiver {
    public static final String ACTION_DISABLED = "device_admin_action_disabled";
    public static final String ACTION_ENABLED = "device_admin_action_enabled";

    static SharedPreferences getSamplePreferences(Context context) {
        return context.getSharedPreferences(
                DeviceAdminReceiver.class.getName(), 0);
    }

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Sample Device Admin: enabled");
        super.onEnabled(context, intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(
                new Intent(ACTION_ENABLED));
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "This is an optional message to warn the user about disabling.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Sample Device Admin: disabled");
        super.onDisabled(context, intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(
                new Intent(ACTION_DISABLED));
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context, "Sample Device Admin: pw changed");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        showToast(context, "Sample Device Admin: pw failed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        showToast(context, "Sample Device Admin: pw succeeded");
    }

}