package com.treemeasurer.measurer.camera;

import com.treemeasurer.measurer.BaseView;

public interface CameraView extends BaseView {
    void showMessage(String msg);
    void showElevation(String x);
    void showDistance(String dis);
    // 显示设置的身高
    void showCurrentHeight(int height);
    void showUseTip(int code);
    void showUseTip(String msg);
}
