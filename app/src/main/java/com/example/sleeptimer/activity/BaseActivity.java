package com.example.sleeptimer.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sleeptimer.CustomModelClass;
import com.example.sleeptimer.OnChooseColorListener;
import com.example.sleeptimer.preferences.Prefs;

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
