package com.abh80.texie;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;


public class ConfigurationActivity extends Activity {
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        sharedPreferences = getSharedPreferences("com.abh80.texie.store", MODE_PRIVATE);
        findViewById(R.id.font_color_image).setBackgroundTintList(getColorStateList(sharedPreferences.getInt("fc", android.support.wearable.R.color.white)));
        findViewById(R.id.font_color).setOnClickListener(l -> {
            finish();
            startActivity(new Intent(this, ColorChooseActivity.class));

        });
    }
}
