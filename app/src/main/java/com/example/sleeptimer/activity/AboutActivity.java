package com.example.sleeptimer.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sleeptimer.R;
import com.example.sleeptimer.preferences.Prefs;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.email)
    TextView emailTV;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setTitle("");

        emailTV.setTextColor(Prefs.getPrimaryColor(this));
        emailTV.setText(Html.fromHtml("<a href=\"mailto:ask@me.it\">candystarvn@gmail.com</a>"));
        emailTV.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
