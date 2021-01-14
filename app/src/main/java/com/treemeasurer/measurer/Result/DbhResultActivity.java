package com.treemeasurer.measurer.Result;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.entity.TrunkResult;
import com.treemeasurer.measurer.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DbhResultActivity extends AppCompatActivity {
    private final static String TAG = "DbhResultActivity";
    private final static MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    private final static String HOST = "http://www.wzfry.com";
    private final static int FINISH = 1;
    private final static int ERROR = -1;
    private final OkHttpClient client = new OkHttpClient();

    private ImageView imageView;
    private TextView textViewHeight;
    private TextView textViewDis;
    private TextView textViewDbh;
    private ConstraintLayout resultLayout;
    private ProgressBar progressBar;

    private SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbh);
        imageView = findViewById(R.id.img_result);
        textViewHeight = findViewById(R.id.txt_height);
        textViewDis = findViewById(R.id.txt_dis);
        textViewDbh = findViewById(R.id.txt_dbh);
        resultLayout = findViewById(R.id.result_layout);
        progressBar = findViewById(R.id.progress_bar);
        Button measureAgain = findViewById(R.id.messure_again);
        measureAgain.setOnClickListener((v)->{
            finish();
        });

        DbhResultHandler handler = new DbhResultHandler(this);
        pref = getSharedPreferences("treemeasurer", Context.MODE_PRIVATE);
        String fy = pref.getString("fx", null);
        String cy = pref.getString("cy", null);
        String dis = pref.getString("dis", null);
        String path = pref.getString("tree_img",null);
        Glide.with(this).load(new File(path)).into(imageView);
        Log.d(TAG, "onCreate: " + fy + "  " + cy + " " + dis + " " + path);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (fy != null && cy != null && dis != null && path != null) {
                    //发送参数到服务器
                    RequestBody fileBody = RequestBody.create(MEDIA_TYPE_JPG, new File(path));
                    Log.d(TAG, "文件是否存在 "+ (new File(path)).exists());
                    //3.构建MultipartBody
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("img", "trunk.jpg", fileBody)
                            .addFormDataPart("fy",Integer.valueOf(fy).toString())
                            .addFormDataPart("cy", Integer.valueOf(cy).toString())
                            .addFormDataPart("Z", dis)
                            .build();
                    //4.构建请求
                    Request request = new Request.Builder()
                            .url(HOST + "/processimg/")
                            .post(requestBody)
                            .build();
                    //5.发送请求
                    try {
                        Response response = client.newCall(request).execute();
                        Log.d(TAG, "run: "+ response.code());
                        if (response.isSuccessful()) {
                            String body = response.body().string();
                            Log.d(TAG, "run: " + body);
                            Gson gson = new Gson();
                            TrunkResult result = gson.fromJson(body, TrunkResult.class);
                            Message message = new Message();
                            message.what = FINISH;
                            message.obj = result;
                            handler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = ERROR;
                            message.arg1 = response.code();
                            handler.sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DbhResultActivity.this,
                            "参数为空", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "run: " + "参数为空");
                    finish();
                }
            }
        });
    }

    static class DbhResultHandler extends Handler {

        private DbhResultActivity activity;

        public DbhResultHandler(DbhResultActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FINISH:
                    // 删除保存在本地的图片
                    AppUtils.deleteAllFiles(AppUtils.getImageSaveFile(activity,
                            AppUtils.TREE_IMAGE_SAVE_FILE));
                    Glide.with(activity).load(((TrunkResult)msg.obj).getImg())
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(activity.imageView);
                    activity.textViewHeight
                            .setText(activity
                                    .pref.getString("height","176") + "cm");
                    activity.textViewDis.setText(activity
                            .pref.getString("dis","176") + "cm");
                    activity.textViewDbh.setText(String.format(Locale.CHINA,"%.1f cm",Double
                                    .valueOf(((TrunkResult)msg.obj).getDbh())));
                    activity.resultLayout.setVisibility(View.VISIBLE);
                    activity.progressBar.setVisibility(View.INVISIBLE);
                    break;
                case ERROR:
                    activity.progressBar.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder  builder = new AlertDialog.Builder(activity)
                            .setTitle("错误:")
                            .setMessage("没有识别到树干, code: " + msg.arg1);
                    builder.setPositiveButton("重新测量", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.finish();
                        }
                    });
                    builder.create().show();
                    break;
            }
        }
    }
}
