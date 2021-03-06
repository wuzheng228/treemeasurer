package com.treemeasurer.measurer.setting;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.treemeasurer.measurer.R;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.setting_container, new SettingFragment())
                .commit();
    }
}
