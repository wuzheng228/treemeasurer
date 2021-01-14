package com.treemeasurer.measurer.calibration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.treemeasurer.measurer.BaseActivity;
import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.adapter.GridAdapter;
import com.treemeasurer.measurer.camera.CameraActivity;
import com.treemeasurer.measurer.camera.CameraCode;
import com.treemeasurer.measurer.guide.CalibGuideActivity;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CalibActivity extends BaseActivity<CalibPresenter, CalibView> implements CalibView{

    private final static int MAXMUM_CHERSSBOARD_PIC_NUM = 19;
    private final static int MINIMUM_CHERSSBOARD_PIC_NUM = 9;

//    static {
//        boolean success = OpenCVLoader.initDebug();
//        Log.d("CalibActivity", "static initializer: OpenCVLoader is success:" + success);
//    }
    private ProgressBar progressBar;
    private GridView gridView;
    private GridAdapter adapter;
    private List<File> paths = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbt);
        gridView = findViewById(R.id.grid_view);
        Button btnTakePiecture = findViewById(R.id.btn1);
        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setText("相机标定");
        TextView helpCenter = findViewById(R.id.help_center);
        helpCenter.setOnClickListener(v->{
            startActivity(new Intent(this, CalibGuideActivity.class));
        });
        btnTakePiecture.setOnClickListener(v -> {
            if (paths.size() > MAXMUM_CHERSSBOARD_PIC_NUM) {
                showErrorMessage("已超过最大照片数量" + MAXMUM_CHERSSBOARD_PIC_NUM);
            } else {
                showCamera();
            }
        });
        Button btnCalRes = findViewById(R.id.btn2);
        btnCalRes.setOnClickListener(v -> {
            if (paths.size() < MINIMUM_CHERSSBOARD_PIC_NUM) {
                showErrorMessage("小于最小照片数量" + MINIMUM_CHERSSBOARD_PIC_NUM);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            presenter.cameraCalibration(paths);
        });
        progressBar = findViewById(R.id.progressbar_calibrate);
    }

    @Override
    public void showImages(File newPath) {
        paths.add(newPath);
        adapter = new GridAdapter(this, R.layout.grid_item, paths);
        gridView.setAdapter(adapter);
        progressBar.setMax(paths.size());
    }

    @Override
    public void showCalibRes(StringBuilder bd) {
        // 删除标定板缓存
        presenter.deleteChessBoardCatch();
        AlertDialog.Builder  builder = new AlertDialog.Builder(this)
                .setTitle("标定结果")
                .setMessage(bd)
                .setPositiveButton("确定", (v,w)->{
                    presenter.setHasCalib();
                    finish();
                    v.dismiss();
                });
        builder.create().show();
    }


    @Override
    public void showCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("requestCode", CameraCode.REQUEST_CODE_CALIBRATE.getCode());
        startActivityForResult(intent, CameraCode.REQUEST_CODE_CALIBRATE.getCode());
    }

    @Override
    public void updateProgress(int idx) {
        gridView.setAdapter(adapter);
        progressBar.setProgress(idx,true);
    }

    @Override
    public void showCalibSuccess() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "标定成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.result(requestCode, resultCode, data);
    }

    @Override
    public void showErrorMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected CalibPresenter selectPreSenter() {
        return new CalibPresenter();
    }

}
