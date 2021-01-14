package com.treemeasurer.measurer.data;

import android.content.Context;

import androidx.camera.core.ImageCapture;

import java.io.File;

public interface DataSource {
    void saveCalibrateData(Context context ,double[] matrix);
    void saveDistance(Context context, double dis);
    void saveTreeFile(Context context, File file);
    void saveSettingHeight(Context context, String height);

    String loadSettingHeight(Context context);

}
