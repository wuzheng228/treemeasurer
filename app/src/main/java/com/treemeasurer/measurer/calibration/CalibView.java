package com.treemeasurer.measurer.calibration;

import android.net.Uri;

import com.treemeasurer.measurer.BaseView;

import java.io.File;
import java.util.List;

public interface CalibView extends BaseView {
    // 显示拍摄的标定板图片
    void showImages(File newUri);
    // 显示标定结果
    void showCalibRes(StringBuilder bd);
    // 调用CameraActivity拍照
    void showCamera();
    // 更新标定进度
    void updateProgress(int idx);
    // 显示标定成功的结果
    void showCalibSuccess();

}
