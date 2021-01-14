package com.treemeasurer.measurer.calibration;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.treemeasurer.measurer.BasePresenter;
import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.camera.CameraCode;
import com.treemeasurer.measurer.data.DataSourceImpl;
import com.treemeasurer.measurer.data.DataSource;
import com.treemeasurer.measurer.utils.AppUtils;
import com.treemeasurer.measurer.utils.CalibrateHelper;

import java.io.File;
import java.io.FileInputStream;
import java.util.Formatter;
import java.util.List;


public class CalibPresenter extends BasePresenter<CalibActivity> {

    // 持有model
    private DataSource dataSource = DataSourceImpl.getInstance();

    //执行业务逻辑
    // 得到拍完照片图片存放的路径
    void result(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CameraCode.REQUEST_CODE_CALIBRATE.getCode())
                getContext().showImages((File) data.getSerializableExtra("SAVE_DIR"));
        }
    }

    // 完成相机标定
    void cameraCalibration(List<File> paths) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int board_short = Integer.parseInt(pref.getString("board_short", "5"));
        int board_long = Integer.parseInt(pref.getString("board_long", "7"));
        int board_box_size = Integer.parseInt(pref.getString("board_box_size", "19"));
        Log.d(getTag(), "cameraCalibration: " + board_short +"  " + board_long + "  "+board_box_size);
        CalibrateHelper calibrateHelper = new CalibrateHelper(board_short, board_long,board_box_size);
        calibrateHelper.calibrate(paths.size(), paths,null,
                new CalibHandler(getContext(), calibrateHelper, dataSource));
    }

    public void deleteChessBoardCatch() {
        AppUtils.deleteAllFiles(getOutPutDirectory());
        AppUtils.deleteAllFiles(new File(getContext().getExternalMediaDirs()[0],
                getContext().getResources().getString(R.string.app_name)
                        + File.separator + "calibration"));
    }

    public void setHasCalib() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        pref.edit().putBoolean("isCalibrate", true).apply();
    }

    private File getOutPutDirectory() {
        File mediaDir = getContext().getExternalMediaDirs()[0];
        if (mediaDir == null || !mediaDir.exists())
            return null;
        File file = new File(mediaDir,
                getContext().getResources().getString(R.string.app_name)
                        + File.separator + "cornerExtraction");
        boolean mkdirs = file.mkdirs();
        if (mkdirs) return file;
        else return file;
    }

    static class CalibHandler extends Handler{
        CalibActivity view;
        CalibrateHelper calibrateHelper;
        DataSource dataSource;
        int progress = 0;
        public CalibHandler(CalibActivity view, CalibrateHelper helper, DataSource dataSource) {
            this.view = view;
            this.calibrateHelper = helper;
            this.dataSource = dataSource;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.obj == CalibrateHelper.CalibrateStatus.FAILD) {
                view.showErrorMessage("标定失败，请重新拍摄图片");
            } else if (msg.obj == CalibrateHelper.CalibrateStatus.CORNEREXTRACTION_SUCCESS) {
                view.updateProgress(++progress);
            } else if ( msg.obj == CalibrateHelper.CalibrateStatus.CALIBRATION_SUCCESS) {
                Log.d("handleMessage", "handleMessage: "+ "标定成功");
                view.showCalibSuccess();
                Log.d("CalibHandler", "handleMessage: " + calibrateHelper.getmCameraMatrix().dump());
                double[] matrix = new double[9];
                calibrateHelper.getmCameraMatrix().get(0,0, matrix);
                double fx = matrix[0], fy = matrix[4], cx = matrix[2], cy = matrix[5];
                Log.d("CalibHandler", "handleMessage: " + fx);
                Formatter formatter = new Formatter();
                formatter.format("相机焦距:\n  fx: %.2f; fy: %.2f\n" +
                        "图像中心坐标(cx, cy):\n  (%.2f, %.2f)",fx, fy, cx, cy);
                view.showCalibRes((StringBuilder) formatter.out());
                // 保存标定数据
                dataSource.saveCalibrateData(view, matrix);
            }
        }
    }

}
