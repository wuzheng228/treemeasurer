package com.treemeasurer.measurer.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.treemeasurer.measurer.BaseActivity;
import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.entity.HeightResult;
import com.treemeasurer.measurer.guide.CalibGuideActivity;
import com.treemeasurer.measurer.guide.MeasureDbhGuideActivity;
import com.treemeasurer.measurer.guide.MeasureHeightGuideActivity;
import com.treemeasurer.measurer.setting.DefaultParams;
import com.treemeasurer.measurer.utils.AppUtils;
import com.treemeasurer.measurer.utils.ValidHelper;
import com.treemeasurer.measurer.widget.FontIconView;

public class CameraActivity extends BaseActivity<CameraPresenter, CameraView>
        implements CameraView, SensorEventListener{
    private static final String TAG = "CameraXBasic";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String REQUIRED_PERMISSIONS = Manifest.permission.CAMERA;

    private PreviewView viewFinder;
    private TextView textViewDis;
    private TextView textViewOritation;
    private TextView textViewUseTip;
    private TextView textViewEditHeight;
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        viewFinder = findViewById(R.id.viewFinder);
        if (allPermissionsGranted()) {
            presenter.startCamera(viewFinder);
        } else {
            ActivityCompat.requestPermissions(CameraActivity.this
                    , new String[]{REQUIRED_PERMISSIONS}, REQUEST_CODE_PERMISSIONS);
        }
        FontIconView cameraCaptureButton = findViewById(R.id.camera_capture_button);
        textViewDis = findViewById(R.id.text_view_dis);
        textViewOritation = findViewById(R.id.text_view_oritation);
        TextView fontTarget = findViewById(R.id.font_target);
        textViewUseTip = findViewById(R.id.text_view_use_tip);
        textViewEditHeight = findViewById(R.id.text_view_edit_height);
        TextView  title = findViewById(R.id.text_title);
        TextView helepCenter = findViewById(R.id.help_center);
        ConstraintLayout editHeight = findViewById(R.id.height_edit_layout);
        editHeight.setOnClickListener((v)->{
            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("请输入身高:")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = editText.getText().toString();
                            if (ValidHelper.isEmptyAndNotDigitOnly(CameraActivity.this, text))
                                return;
                            if (ValidHelper.isDigitOutRange(CameraActivity.this, text,
                                    DefaultParams.MIN_HEIGHT, DefaultParams.MAX_HEIGHT, "输入范围错误，高度范围为"))
                                return;
                            presenter.saveSettingHeight(text);
                            Toast.makeText(CameraActivity.this, "设置成功", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }).create();
            dialog.show();
        });
        Intent intent = getIntent();
        int requestCode = intent.getIntExtra("requestCode", 0);
        if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DIS.getCode()
                || requestCode == CameraCode.REQUEST_CODE_MEASURE_DBH_DIS.getCode()){
            textViewDis.setVisibility(View.VISIBLE);
            fontTarget.setVisibility(View.VISIBLE);
            editHeight.setVisibility(View.VISIBLE);
        }
        if (requestCode == CameraCode.REQUEST_CODE_MEASURE_HEIGHT.getCode()) {
            textViewDis.setVisibility(View.VISIBLE);
            fontTarget.setVisibility(View.VISIBLE);
            editHeight.setVisibility(View.VISIBLE);
        }
        cameraCaptureButton.setOnClickListener(v -> {
            presenter.takePhotoForResult();
        });
        if (requestCode != CameraCode.REQUEST_CODE_CALIBRATE.getCode()) presenter.loadHeight();
        showUseTip(requestCode);
        setTitleAndHelpCenter(requestCode, title, helepCenter);
    }

    private void setTitleAndHelpCenter(int requestCode, TextView title, TextView helepCenter) {
        if (requestCode == CameraCode.REQUEST_CODE_MEASURE_HEIGHT.getCode()) {
            title.setText("测量高度");
            helepCenter.setOnClickListener(v -> {
                startActivity(new Intent(this, MeasureHeightGuideActivity.class));
            });
        } else if (requestCode == CameraCode.REQUEST_CODE_CALIBRATE.getCode()) {
            title.setText("相机标定");
            helepCenter.setOnClickListener(v -> {
                startActivity(new Intent(this, CalibGuideActivity.class));
            });
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DIS.getCode()) {
            title.setText("测量距离");
            helepCenter.setVisibility(View.INVISIBLE);
        } else if (requestCode == CameraCode.REQUEST_CODE_MEASURE_DBH.getCode() ||
        requestCode == CameraCode.REQUEST_CODE_MEASURE_DBH_DIS.getCode()) {
            title.setText("测量胸径");
            helepCenter.setOnClickListener(v -> {
                startActivity(new Intent(this, MeasureDbhGuideActivity.class));
            });
        }
    }


    public void showCurrentHeight(int height) {
        textViewEditHeight.setText("身高" + height);
    }

    public void showUseTip(int code) {
       if (code == CameraCode.REQUEST_CODE_CALIBRATE.getCode()) {
           textViewUseTip.setText("请不同角度拍摄标定板图片");
           AppUtils.setSpecifiedText(textViewUseTip,"不同角度");
       } else if (code == CameraCode.REQUEST_CODE_MEASURE_DBH.getCode()) {
           textViewUseTip.setText("请拍摄目标立木树干图片");
           AppUtils.setSpecifiedText(textViewUseTip,"立木树干");
       } else if (code == CameraCode.REQUEST_CODE_MEASURE_DBH_DIS.getCode()
       || code == CameraCode.REQUEST_CODE_MEASURE_DIS.getCode()) {
           textViewUseTip.setText("靶心对准目标立木底部按下");
           AppUtils.setSpecifiedText(textViewUseTip,"底部");
       } else if (code == CameraCode.REQUEST_CODE_MEASURE_HEIGHT.getCode()) {
           textViewUseTip.setText("靶心对准目标立木底部按下");
           AppUtils.setSpecifiedText(textViewUseTip,"底部");
       }
    }

    public void switchMeasureTip() {
        textViewDis.setVisibility(View.INVISIBLE);
        textViewOritation.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUseTip(String msg) {
        textViewUseTip.setText(msg);
        AppUtils.setSpecifiedText(textViewUseTip,"顶部");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        presenter.measureDis(event.values[1]);
//        Log.d(TAG, "onSensorChanged: " + event.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected CameraPresenter selectPreSenter() {
        return new CameraPresenter();
    }
    

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showElevation(String x) {
        textViewOritation.setText(x);
    }

    @Override
    public void showDistance(String dis) {
        textViewDis.setText(dis);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                presenter.startCamera(viewFinder);
            } else {
                Toast.makeText(this,
                        "Permission not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        if (ContextCompat.
                checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
}
