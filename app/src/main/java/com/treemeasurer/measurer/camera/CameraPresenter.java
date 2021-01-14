package com.treemeasurer.measurer.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.treemeasurer.measurer.BasePresenter;
import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.data.DataSourceImpl;
import com.treemeasurer.measurer.data.DataSource;
import com.treemeasurer.measurer.entity.HeightResult;
import com.treemeasurer.measurer.setting.DefaultParams;

import org.opencv.core.Mat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraPresenter<T extends CameraView> extends BasePresenter<CameraActivity> {

    private static final String TAG = "CameraPresenter";
    private DataSource dataSource = DataSourceImpl.getInstance();

    private final static String CALIBRATE_IMG_DIR = "calibration";
    private final static String MEASURE_DBH_IMG_DIR = "dbh";

    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";


    private ImageCapture imageCapture;
    private double dis = 0d;
    private double targetHeight = 0d;
    private int height;
    private float xAngele;

    // 业务处理逻辑

    // 初始化相机
    public void startCamera(PreviewView viewFinder) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(()-> {
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {

            }
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(viewFinder.createSurfaceProvider());
            imageCapture = new ImageCapture.Builder()
                    .setTargetResolution(new Size(480,640))
                    .build();
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
            try {
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((CameraActivity) getContext(), cameraSelector,
                        preview, imageCapture);
            } catch (Exception e) {
                Log.e(getTag(), "startCamera: ", e);
            }
        }, ContextCompat.getMainExecutor((CameraActivity)getContext()));
    }
    // 拍照
    public void takePhotoForResult() {
        Intent intent = ((Activity)(getContext())).getIntent();
        int requestCode = intent.getIntExtra("requestCode", 0);
        Log.d(TAG, "takePhotoForResult: " + requestCode);
        if (requestCode == CameraCode.REQUEST_CODE_CALIBRATE.getCode()) {
            takePhoto(CALIBRATE_IMG_DIR);
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DBH.getCode()) {
            takePhoto(MEASURE_DBH_IMG_DIR);
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DBH_DIS.getCode()) {
            Log.d(TAG, "takePhotoForResult: " + dis);
            if (dis > 0) {
                dataSource.saveDistance(getContext(), dis);
                Intent intent1 = new Intent();
                getContext().setResult(Activity.RESULT_OK, intent1);
                getContext().finish();
            } else {
                getContext().showErrorMessage("请调整角度");
            }
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DIS.getCode()) {
            if (dis > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("目标距离为")
                        .setIcon(R.drawable.ic_icon_tip)
                        .setMessage(String.format(Locale.CHINA, "%.2f cm", dis))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            } else {
                getContext().showErrorMessage("请调整角度");
            }
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_HEIGHT.getCode()) {
            HeightResult instance = HeightResult.getInstance();
            if (instance.getDistance() == null) {
                if (dis > 0) {
                    instance.setDistance(dis);
                    instance.setDepression(90 + xAngele);
                    getContext().showUseTip("请将靶心对准立木顶部后按下");
                    getContext().switchMeasureTip();
                } else {
                    getContext().showErrorMessage("请调整角度");
                }
            } else if (instance.getTargetHeight() == null) {
                if (targetHeight > 0) {
                    instance.setTargetHeight(targetHeight);
                    instance.setElevation(Math.abs(90 + xAngele));
                    instance.setHeight(height);
                    getContext().setResult(Activity.RESULT_OK);
                    getContext().finish();
                } else {
                    getContext().showErrorMessage("请调整角度");
                }
            }
        }
    }

    public void measureDis(float x) {
        this.xAngele = x;
        if (x < DefaultParams.MAX_DIS_ANGEL && x > DefaultParams.MIN_DIS_ANGEL) {
            float alpha = 90f + x;
            dis = (height)/Math.tan(Math.PI * alpha / 180f);
            getContext().showDistance(String.format(Locale.CHINA,"%.2f cm", dis));
        } else {
            getContext().showDistance("超出测量范围请调整角度");
            dis = -1;
        }
        HeightResult instance = HeightResult.getInstance();
        if (instance.getDistance() != null) {
            if (x < DefaultParams.MAX_HEIGHT_ANGEL && x > DefaultParams.MIN_HEIGHT_ANGEL) {
                float elevation = Math.abs(90 + x);
                targetHeight = height + instance.getDistance() * Math.tan(Math.PI * elevation / 180f);
                getContext().showElevation(String.format(Locale.CHINA,"%.2f cm", targetHeight));
            } else {
                getContext().showElevation("超出测量范围请调整角度");
                targetHeight = -1;
            }
        }

    }

    public  void takePhoto(String dir) {
        // 获取一个可修改的稳定的image capture 用例的引用
        if (imageCapture == null)
            return;
        // 创建带有时间戳的输出文件来保存图像
        File photoFile = new File(getOutputDirectory(dir),
                new SimpleDateFormat(FILENAME_FORMAT, Locale.CHINA)
                        .format(System.currentTimeMillis()) + ".jpg"
        );
        // 创建 output Options 对象，它包含了 file + metadata
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        // 设置图像捕获监听器，图片被捕获后触发
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        String msg = "拍摄成功";
                        getContext().showMessage(msg);
                        if (dir.equals(MEASURE_DBH_IMG_DIR)) {
                            dataSource.saveTreeFile(getContext(), photoFile);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("SAVE_DIR", photoFile);
                        getContext().setResult(Activity.RESULT_OK, intent);
                        Log.d(getTag(), "onImageSaved: " + msg);
                        getContext().finish();
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraPresenter", "Photo capture failed:", exception);
                    }
                });
    }

    private File getOutputDirectory(String dir) {
        File mediaDir = getContext().getExternalMediaDirs()[0];
        if (mediaDir == null || !mediaDir.exists())
            return null;
        File file = new File(mediaDir, getContext().getResources().getString(R.string.app_name)
                + File.separator + dir);
        boolean mkdirs = file.mkdirs();
        if (mkdirs) return file;
        else return file;
    }

    public void loadHeight() {
        this.height = Integer.parseInt(dataSource.loadSettingHeight(getContext()));
        getContext().showCurrentHeight(this.height);
    }

    public void saveSettingHeight(String text) {
        dataSource.saveSettingHeight(getContext(), text);
        this.height = Integer.parseInt(text);
        getContext().showCurrentHeight(this.height);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

}
