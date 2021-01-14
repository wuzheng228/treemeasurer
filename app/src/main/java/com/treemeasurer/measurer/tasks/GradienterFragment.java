package com.treemeasurer.measurer.tasks;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.widget.LevelView;

import org.opencv.core.Mat;

import java.util.Locale;

public class GradienterFragment extends Fragment implements SensorEventListener {
    public final static int MAX_ANGEL = 90;
    private static final String TAG = "GradienterFragment";
    // 定义水平仪的表盘
    private LevelView show;
    // Sensor 管理器
    private SensorManager sensorManager;

    private TextView horizontal;
    private TextView vertical;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gradienter, container, false);
        show = view.findViewById(R.id.show);
        horizontal = view.findViewById(R.id.text_horizontal);
        vertical = view.findViewById(R.id.text_vertical);
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
                ,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float yAngle = values[1];// 与Y轴的夹角
        float zAngele = values[2];// 与Z轴夹角
        horizontal.setText(String.format(Locale.CHINA,"%.1f", yAngle));
        vertical.setText(String.format(Locale.CHINA,"%.1f",zAngele));
        // 中心点坐标
        float maxXY = (show.getBack().getWidth() - show.getBubble().getWidth()) / 2f;
        float x = (show.getBack().getWidth() - show.getBubble().getWidth()) / 2f;
        float y = (show.getBack().getHeight() - show.getBubble().getHeight()) / 2f;
        if (event.sensor.getType()  == Sensor.TYPE_ORIENTATION) {
            if (Math.abs(zAngele) <= MAX_ANGEL) {
                // 根据z轴的倾斜角计算X坐标的变化值
                int deltaX = (int)(maxXY * zAngele / MAX_ANGEL);
                x += deltaX;
            } else if (zAngele > MAX_ANGEL) {
                x = maxXY * 2;
            } else {
                x = 0;
            }
            if (Math.abs(yAngle) <= MAX_ANGEL) {
                // 根据z轴的倾斜角计算Y坐标的变化值
                int deltaY = (int)(maxXY * yAngle / MAX_ANGEL);
                y += deltaY;
            } else if (yAngle > MAX_ANGEL) {
                y = maxXY * 2;
            } else {
                y = 0;
            }
            if (isContain(x, y)) {
                show.setBubbleX(x);
                show.setBubbleY(y);
            }
            show.postInvalidate();
        }
    }

    private boolean isContain(float x, float y) {
        // 计算气泡圆心坐标
        float bubbleCx = x + show.getBubble().getWidth() / 2f;
        float bubbleCy = y + show.getBubble().getHeight() / 2f;
        // 仪表盘中心坐标
        float backCx = show.getBack().getWidth() / 2;
        float backCy = show.getBack().getHeight() / 2;
        double dis = Math.sqrt((bubbleCx - backCx)*(bubbleCx - backCx) +
                (bubbleCy - backCy)*(bubbleCy - backCy));
        return dis < (show.getBack().getWidth() - show.getBubble().getWidth()) / 2;
    }

    private boolean isContainX(float x) {
        float bubbleCx = x + show.getBubble().getWidth() / 2f;
        float backCx = show.getBack().getWidth() / 2;
        return Math.abs(bubbleCx - backCx) < (show.getBack().getWidth() - show.getBubble().getWidth()) / 2;
    }

    private boolean isContainY(float y) {
        float bubbleCy = y + show.getBubble().getHeight() / 2f;
        float backCy = show.getBack().getHeight() / 2;
        return Math.abs(bubbleCy - backCy) < (show.getBack().getWidth() - show.getBubble().getWidth()) / 2;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
