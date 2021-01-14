package com.treemeasurer.measurer.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.Locale;

public class DataSourceImpl implements DataSource {



    public static DataSourceImpl getInstance() {
        return new DataSourceImpl();
    }

    @Override
    public void saveCalibrateData(Context context,double[] matrix) {
        SharedPreferences prefs = context.getSharedPreferences("treemeasurer", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("fx", String.valueOf((int)((matrix[0] + matrix[4]) / 2)));
//        Log.d("CalibActivity", "saveCalibrateData: " + String.format(Locale.CHINA,"%.2f",matrix[2]));
        edit.putString("cx", String.valueOf(matrix[2]));
        edit.putString("cy", String.valueOf((int)matrix[5]));
        edit.putBoolean("isCalibrate", true);
        edit.apply();
    }

    @Override
    public void saveDistance(Context context, double dis) {
        SharedPreferences prefs = context.getSharedPreferences("treemeasurer", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("dis", String.format(Locale.CHINA, "%.2f", dis));
        edit.apply();
    }

    @Override
    public void saveTreeFile(Context context, File file) {
        SharedPreferences prefs = context.getSharedPreferences("treemeasurer", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("tree_img", file.getAbsolutePath());
        edit.apply();
    }

    @Override
    public void saveSettingHeight(Context context, String height) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("height", height);
        edit.apply();
    }

    @Override
    public String loadSettingHeight(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("height","176");
    }
}
