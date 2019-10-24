package com.tunm.sleeptimer.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tunm.sleeptimer.CustomModelClass;
import com.tunm.sleeptimer.OnChooseColorListener;
import com.tunm.sleeptimer.preferences.Prefs;

public abstract class BaseActivity extends AppCompatActivity implements OnChooseColorListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.init(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Prefs.getPrimaryColor(this)));
        getWindow().setStatusBarColor(Prefs.getPrimaryColor(this));
        CustomModelClass.getInstance().setListener(this);
    }

    @Override
    public abstract void refreshTheme();
}
