package com.treemeasurer.measurer.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.calibration.CalibActivity;

import java.util.Locale;

public class SettingFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);
        EditTextPreference height = findPreference("height");
        EditTextPreference board_short = findPreference("board_short");
        board_short.setOnPreferenceChangeListener((p, v)->{
            String value = (String)(v);
            if (isEmptyAndNotDigitOnly(value))
                return false;
            if (isDigitOutRange(value, DefaultParams.MIN_BOARD_LEN, DefaultParams.MAX_BOARD_LEN,
                    "标定板短边宽度范围为"))
                return false;
            Toast.makeText(getContext(), "设置成功", Toast.LENGTH_LONG).show();
            return true;
        });
        EditTextPreference board_long = findPreference("board_long");
        board_long.setOnPreferenceChangeListener((p, v)->{
            String value = (String)(v);
            if (isEmptyAndNotDigitOnly(value))
                return false;
            if (isDigitOutRange(value, DefaultParams.MIN_BOARD_LEN, DefaultParams.MAX_BOARD_LEN,
                    "标定板长边宽度范围为"))
                return false;
            Toast.makeText(getContext(), "设置成功", Toast.LENGTH_LONG).show();
            return true;
        });
        EditTextPreference board_box_size = findPreference("board_box_size");
        board_box_size.setOnPreferenceChangeListener((p, v)->{
            String value = (String)v;
            if (isEmptyAndNotDigitOnly(value)) return false;
            if(isDigitOutRange(value, DefaultParams.MIN_BOARD_SIZE, DefaultParams.MAX_BOARD_SIZE, "标定板黑格宽度为")) return false;
            Toast.makeText(getContext(), "设置成功", Toast.LENGTH_LONG).show();
            return true;
        });
        Preference calib_activity = findPreference("calib_activity");
        calib_activity.setIntent(new Intent(getContext(), CalibActivity.class));
        height.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String)newValue;
                if (isEmptyAndNotDigitOnly(value)) return false;
                if (isDigitOutRange(value, DefaultParams.MIN_HEIGHT, DefaultParams.MAX_HEIGHT, "高度范围为:"))return false;
                Toast.makeText(getContext(), "设置成功", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    private boolean isEmptyAndNotDigitOnly(String value) {
        if (TextUtils.isEmpty(value)) {
            Toast.makeText(getContext(), "输入不能为空",Toast.LENGTH_LONG).show();
            return true;
        }
        if (!TextUtils.isDigitsOnly(value)) {
            Toast.makeText(getContext(), "请输入数字",Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean isDigitOutRange(String v, int lo, int hi, String tip) {
        int i = Integer.parseInt(v);
        if (v.length() > String.valueOf(hi).length() ||i < lo || i > hi) {
            String pattern = String.format(Locale.CHINA,tip +":%d~%d",lo, hi);
            Toast.makeText(getContext(), pattern,Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
