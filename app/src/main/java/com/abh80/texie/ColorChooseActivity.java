package com.abh80.texie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ColorChooseActivity extends Activity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_choose);
        sharedPreferences = getSharedPreferences("com.abh80.texie.store", MODE_PRIVATE);

        findViewById(R.id.white).setOnClickListener(l -> {
            sharedPreferences.edit().putInt("fc", android.support.wearable.R.color.white).apply();
            finish();
        });
        findViewById(R.id.yellow).setOnClickListener(l -> {
            sharedPreferences.edit().putInt("fc", R.color.yellow).apply();
            finish();
        });
        findViewById(R.id.blue).setOnClickListener(l -> {
            sharedPreferences.edit().putInt("fc", android.support.wearable.R.color.blue_a400).apply();
            finish();
        });
        findViewById(R.id.orange).setOnClickListener(l -> {
            sharedPreferences.edit().putInt("fc", android.support.wearable.R.color.orange).apply();
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("com.abh80.texie.settings_update"));
    }
}
