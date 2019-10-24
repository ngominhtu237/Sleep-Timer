package com.example.sleeptimer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.sleeptimer.CustomModelClass;
import com.example.sleeptimer.R;
import com.example.sleeptimer.SleepColor;
import com.example.sleeptimer.preferences.Prefs;
import com.example.sleeptimer.service.AdminReceiver;
import com.example.sleeptimer.view.CircleButton;
import com.example.sleeptimer.view.SwitchColorCustom;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.primaryColorBtn) CircleButton mPrimaryColorBtn;
    @BindView(R.id.hourColorBtn) CircleButton mHourColorBtn;
    @BindView(R.id.minuteColorBtn) CircleButton mMinuteColorBtn;

    @BindView(R.id.themeTitle) TextView mThemeTitleTV;
    @BindView(R.id.timeTitle) TextView mTimeTitleTV;
    @BindView(R.id.onSleepTitle) TextView mOnSleepTitleTV;
    @BindView(R.id.othersTitle) TextView mOthersTitleTV;
    @BindView(R.id.extendedTime) TextView mExtendedTimeTV;

    @BindView(R.id.goHomeSwitch) SwitchColorCustom mGoHomeSwitch;
    @BindView(R.id.offScreenSwitch) SwitchColorCustom mOffScreenSwitch;
    @BindView(R.id.silentSwitch) SwitchColorCustom mSilentModeSwitch;
    @BindView(R.id.offWifiSwitch) SwitchColorCustom mOffWifiSwitch;
    @BindView(R.id.offBluetoothSwitch) SwitchColorCustom mOffBlueToothSwitch;

    @BindView(R.id.extendedTimeContainer)
    LinearLayout mExLayout;

    NumberPicker picker;

    private int primaryColor, seekBarMinuteColor, seekBarHourColor;
    private static final int RESULT_ENABLE = 1;

    private DevicePolicyManager mDevicePolicyManger;
    private ComponentName compName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(" " + "Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDevicePolicyManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, AdminReceiver.class);

        refreshTheme();
        generateSwitch();
        eventHandler();
    }

    @Override
    public void refreshTheme() {
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Prefs.getPrimaryColor(this)));
        getWindow().setStatusBarColor(Prefs.getPrimaryColor(this));

        primaryColor = Prefs.getPrimaryColor(this);
        seekBarMinuteColor = Prefs.getSeekbarMinuteColor(this);
        seekBarHourColor = Prefs.getSeekbarHourColor(this);

        mPrimaryColorBtn.setColor(primaryColor);
        mMinuteColorBtn.setColor(seekBarMinuteColor);
        mHourColorBtn.setColor(seekBarHourColor);
        mThemeTitleTV.setTextColor(primaryColor);
        mTimeTitleTV.setTextColor(primaryColor);
        mOnSleepTitleTV.setTextColor(primaryColor);
        mOthersTitleTV.setTextColor(primaryColor);
        mExtendedTimeTV.setTextColor(primaryColor);
        mGoHomeSwitch.setToggleOnColor(primaryColor);
        mOffScreenSwitch.setToggleOnColor(primaryColor);
        mSilentModeSwitch.setToggleOnColor(primaryColor);
        mOffWifiSwitch.setToggleOnColor(primaryColor);
        mOffBlueToothSwitch.setToggleOnColor(primaryColor);
        mGoHomeSwitch.setBgOnColor(getColor(R.color.accent_grey));
        mOffScreenSwitch.setBgOnColor(getColor(R.color.accent_grey));
        mSilentModeSwitch.setBgOnColor(getColor(R.color.accent_grey));
        mOffWifiSwitch.setBgOnColor(getColor(R.color.accent_grey));
        mOffBlueToothSwitch.setBgOnColor(getColor(R.color.accent_grey));
    }

    @SuppressLint("SetTextI18n")
    private void generateSwitch() {
        mExtendedTimeTV.setText("+ " + Prefs.getExtendedTime() + " minutes");
        mGoHomeSwitch.setChecked(Prefs.getGoHomeScreen());
        mOffScreenSwitch.setChecked(Prefs.getOffScreen());
        mSilentModeSwitch.setChecked(Prefs.getSilentMode());
        mOffWifiSwitch.setChecked(Prefs.getOffWifi());
        mOffBlueToothSwitch.setChecked(Prefs.getOffBlueTooth());
    }

    private void eventHandler() {
        mPrimaryColorBtn.setOnClickListener(this);
        mHourColorBtn.setOnClickListener(this);
        mMinuteColorBtn.setOnClickListener(this);
        mGoHomeSwitch.setOnCheckedChangeListener(this);
        mOffScreenSwitch.setOnCheckedChangeListener(this);
        mSilentModeSwitch.setOnCheckedChangeListener(this);
        mOffWifiSwitch.setOnCheckedChangeListener(this);
        mOffBlueToothSwitch.setOnCheckedChangeListener(this);
        mExLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.primaryColorBtn:
                showColorPicker(SleepColor.PRIMARY);
                break;
            case R.id.minuteColorBtn:
                showColorPicker(SleepColor.MINUTE);
                break;
            case R.id.hourColorBtn:
                showColorPicker(SleepColor.HOUR);
                break;
            case R.id.extendedTimeContainer:
                openChooseDialog();
                break;
        }
    }

    private void showColorPicker(SleepColor colorEnum) {
        int initColor = (colorEnum == SleepColor.PRIMARY) ? Prefs.getPrimaryColor(this) :
                (colorEnum == SleepColor.MINUTE ? Prefs.getSeekbarMinuteColor(this) : Prefs.getSeekbarHourColor(this));
        ColorPickerDialogBuilder
                .with(this, R.style.ColorPickerDialogTheme)
                .setTitle("Choose color")
                .initialColor(initColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showColorPreview(true)
                .setPositiveButton("set", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if(colorEnum == SleepColor.PRIMARY) {
                            mPrimaryColorBtn.setColor(selectedColor);
                            Prefs.setPrimaryColor(selectedColor);
                        }
                        if(colorEnum == SleepColor.MINUTE) {
                            mMinuteColorBtn.setColor(selectedColor);
                            Prefs.setSeekbarMinuteColor(selectedColor);
                        }
                        if(colorEnum == SleepColor.HOUR) {
                            mHourColorBtn.setColor(selectedColor);
                            Prefs.setSeekbarHourColor(selectedColor);
                        }
                        CustomModelClass.getInstance().changeState();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void openChooseDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.choose_time_dialog, null);
        alertDialogBuilder.setView(v);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        picker = v.findViewById(R.id.numberPicker);
        picker.setDividerColor(Prefs.getPrimaryColor(this));
        v.findViewById(R.id.selectMinuteTV).setBackgroundColor(Prefs.getPrimaryColor(this));
        Button setBtn = v.findViewById(R.id.buttonSet);
        setBtn.setBackgroundColor(Prefs.getPrimaryColor(this));
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("SettingsActivity","value : "+ picker.getValue());
                Prefs.setExtendedTime(picker.getValue());
                mExtendedTimeTV.setText("+ " + picker.getValue() + " minutes");
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.goHomeSwitch:
                Prefs.setGoHomeScreen(b);
                break;
            case R.id.offScreenSwitch:
                if(!mDevicePolicyManger.isAdminActive(compName)) {
                    mOffScreenSwitch.setChecked(false);
                    compName = new ComponentName(this, AdminReceiver.class);
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "Allow to turn off screen");
                    startActivityForResult(intent, RESULT_ENABLE);
                } else {
                    Prefs.setOffScreen(b);
                }
                break;
            case R.id.silentSwitch:
                Prefs.setSilentMode(b);
                break;
            case R.id.offWifiSwitch:
                Prefs.setOffWifi(b);
                break;
            case R.id.offBluetoothSwitch:
                Prefs.setOffBlueTooth(b);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("DeviceAdminSample", "Admin enabled!");
                mOffScreenSwitch.setChecked(true);
                Prefs.setOffScreen(true);
            } else {
                Log.i("DeviceAdminSample", "Admin enable FAILED!");
                Toast.makeText(this, "must enable device administrator", Toast.LENGTH_LONG).show();
                mOffScreenSwitch.setChecked(false);
            }
        }
    }
}
