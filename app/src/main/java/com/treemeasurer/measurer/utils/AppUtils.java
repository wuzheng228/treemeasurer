package com.treemeasurer.measurer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.treemeasurer.measurer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

    private final static String TAG = "AppUtils";

    public final static String TREE_IMAGE_SAVE_FILE = "dbh";

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void setSpecifiedText(TextView textView, String specifiedText) {
        String[] keyword = new String[]{specifiedText};
        SpannableStringBuilder spannable = new SpannableStringBuilder(textView.getText().toString());
        CharacterStyle span;
        String wordReg;
        for (int i = 0; i < keyword.length; i++) {
            String key = "";
            //  处理通配符问题
            if (keyword[i].contains("*") || keyword[i].contains("(") || keyword[i].contains(")")) {
                char[] chars = keyword[i].toCharArray();
                for (int k = 0; k < chars.length; k++) {
                    if (chars[k] == '*' || chars[k] == '(' || chars[k] == ')') {
                        key = key + "\\" + String.valueOf(chars[k]);
                    } else {
                        key = key + String.valueOf(chars[k]);
                    }
                }
                Log.d(TAG, "setSpecifiedText: " + key);
                keyword[i] = key;
            }

            wordReg = keyword[i];   //忽略字母大小写
            Pattern pattern = Pattern.compile(wordReg);
            Matcher matcher = pattern.matcher(textView.getText().toString());
            while (matcher.find()) {
                span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
                spannable.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_MARK_MARK);
                spannable.setSpan(new StyleSpan(Typeface.BOLD),matcher.start(), matcher.end(), Spannable.SPAN_MARK_MARK);
            }
        }
        textView.setText(spannable);
    }

    public static void deleteAllFiles(File file) {
        if (file == null || !file.exists())
            return;
        File[] files = file.listFiles();
        for (File each : files) {
            if (!each.isDirectory()) {
                each.delete();
            }
        }
    }

    public static File getImageSaveFile(Context context, String dir) {
        File mediaDir = context.getExternalMediaDirs()[0];
        if (mediaDir == null || !mediaDir.exists())
            return null;
        File file = new File(mediaDir, context.getResources().getString(R.string.app_name)
                + File.separator + dir);
        boolean mkdirs = file.mkdirs();
        if (mkdirs) return file;
        else return file;
    }

}
