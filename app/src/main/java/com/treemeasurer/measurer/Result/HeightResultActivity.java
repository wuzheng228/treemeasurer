package com.treemeasurer.measurer.Result;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.entity.HeightResult;

import java.util.Locale;

public class HeightResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        Log.d("takePhotoForResult", "takePhotoForResult1: " + HeightResult.getInstance().getTargetHeight());
        Log.d("takePhotoForResult", "takePhotoForResult1: " + HeightResult.getInstance().getHeight());
        TextView height = findViewById(R.id.txt_height);
        TextView elevation = findViewById(R.id.txt_elevation);
        TextView depression = findViewById(R.id.txt_depres);
        TextView dis = findViewById(R.id.txt_targetdis);
        TextView targetHeight = findViewById(R.id.txt_target_height);
        Button button = findViewById(R.id.messure_again);
        button.setOnClickListener(v->finish());
        HeightResult data = HeightResult.getInstance();
        height.setText(String.format(Locale.CHINA, "%d cm",data.getHeight()));
        elevation.setText(String.format(Locale.CHINA, "%.2f cm", data.getElevation()));
        depression.setText(String.format(Locale.CHINA, "%.2f cm", data.getDepression()));
        dis.setText(String.format(Locale.CHINA, "%.2f cm", data.getDistance()));
        targetHeight.setText(String.format(Locale.CHINA, "%.2f cm", data.getTargetHeight()));
        HeightResult.setInstance(null);
    }
}
