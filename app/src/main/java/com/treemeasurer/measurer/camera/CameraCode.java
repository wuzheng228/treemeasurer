package com.treemeasurer.measurer.camera;

import android.content.Context;
import android.content.Intent;

public enum CameraCode {
    REQUEST_CODE_MEASURE_DBH(1, "测量胸径"),
    REQUEST_CODE_CALIBRATE(2, "相机标定"),
    REQUEST_CODE_MEASURE_DBH_DIS(3, "测量胸径"),
    REQUEST_CODE_MEASURE_DIS(4,"测量距离"),
    REQUEST_CODE_MEASURE_HEIGHT(5, "测量高度")
    ;
    private int code;
    private String title;


    CameraCode(int code) {
        this.code = code;
    }

    CameraCode(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public static Intent getIntent(Context context, Class<?> clazz, CameraCode code) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra("requestCode", code.getCode());
        return  intent;
    }
}
