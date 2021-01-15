package com.treemeasurer.measurer.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.treemeasurer.measurer.BaseFragment;
import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.result.HeightResultActivity;
import com.treemeasurer.measurer.calibration.CalibActivity;
import com.treemeasurer.measurer.camera.CameraActivity;
import com.treemeasurer.measurer.camera.CameraCode;
import com.treemeasurer.measurer.result.DbhResultActivity;
import com.treemeasurer.measurer.entity.HeightResult;
import com.treemeasurer.measurer.guide.CalibGuideActivity;
import com.treemeasurer.measurer.guide.MeasureDbhGuideActivity;
import com.treemeasurer.measurer.guide.MeasureHeightGuideActivity;
import com.treemeasurer.measurer.setting.SettingActivity;
import com.treemeasurer.measurer.widget.FontIconView;

public class MeasureFragment extends BaseFragment<TasksPresenter, TasksView> implements TasksView {

    public static final int FIRST_USE_DBH = 11;
    public static final int FIRST_USE_CALIB = 12;
    private SharedPreferences pref;
    private ConnectivityManager connectivityManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measure, container, false);
        ConstraintLayout measureDBH = view.findViewById(R.id.func1);
        ConstraintLayout measureDis = view.findViewById(R.id.func2);
        ConstraintLayout measureHeight = view.findViewById(R.id.func3);
        FontIconView setting = view.findViewById(R.id.text_setting);
        connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        setting.setOnClickListener(v->{
            startActivity(new Intent(getContext(), SettingActivity.class));
        });
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        measureDBH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMeasureDbhTask();
            }
        });
        measureDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMeasureDisTask();
            }
        });
        measureHeight.setOnClickListener(v -> showMeasureHeightTask());
        return view;
    }

    private void showMeasureHeightTask() {
        Intent intent = CameraCode.getIntent(getContext(), CameraActivity.class, CameraCode.REQUEST_CODE_MEASURE_HEIGHT);
        startActivityForResult(intent, CameraCode.REQUEST_CODE_MEASURE_HEIGHT.getCode());
        if (pref.getBoolean("first_use_height",true)) {
           intent = new Intent(getContext(), MeasureHeightGuideActivity.class);
           startActivity(intent);
        }
    }

    @Override
    public void showMeasureDbhTask() {
        NetworkInfo workInfo = connectivityManager.getActiveNetworkInfo();
        if (workInfo == null || !workInfo.isAvailable()) {
            Toast.makeText(getContext(), "请连接网络使用!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pref.getBoolean("isCalibrate",false)) {
            if (pref.getBoolean("first_use_dbh", true)) {
                Intent intent = new Intent(getContext(), MeasureDbhGuideActivity.class);
                startActivityForResult(intent, FIRST_USE_DBH);
            } else {
                Intent intent = CameraCode.getIntent(getContext(), CameraActivity.class,
                        CameraCode.REQUEST_CODE_MEASURE_DBH);
                startActivityForResult(intent, CameraCode.REQUEST_CODE_MEASURE_DBH.getCode());
            }
        } else {
            Toast.makeText(getContext(), "请先进行相机标定", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(getContext(), CalibGuideActivity.class), FIRST_USE_CALIB);
        }
    }

    @Override
    public void showMeasureDisTask() {
        Intent intent = CameraCode.getIntent(getContext(), CameraActivity.class, CameraCode.REQUEST_CODE_MEASURE_DIS);
        startActivity(intent);
    }

    @Override
    public void showErrorMessage(String msg) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DBH.getCode()) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = CameraCode.getIntent(getContext(), CameraActivity.class, CameraCode.REQUEST_CODE_MEASURE_DBH_DIS);
                startActivityForResult(intent, CameraCode.REQUEST_CODE_MEASURE_DBH_DIS.getCode());
            }
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DBH_DIS.getCode()) {
            if (resultCode == Activity.RESULT_OK)
                startActivity(new Intent(getContext(), DbhResultActivity.class));
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_HEIGHT.getCode()) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), HeightResultActivity.class);
                intent.putExtra("height", HeightResult.getInstance());
                startActivity(intent);
            } else  {
                HeightResult.setInstance(null);
            }
        } else if (requestCode == FIRST_USE_DBH) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = CameraCode.getIntent(getContext(), CameraActivity.class,
                        CameraCode.REQUEST_CODE_MEASURE_DBH);
                startActivityForResult(intent, CameraCode.REQUEST_CODE_MEASURE_DBH.getCode());
            }
        } else if (requestCode == FIRST_USE_CALIB) {
            if (resultCode == Activity.RESULT_OK) {
                startActivity(new Intent(getContext(), CalibActivity.class));
            }
        }
    }

    @Override
    protected TasksPresenter selectPreSenter() {
        return new TasksPresenter();
    }
}
