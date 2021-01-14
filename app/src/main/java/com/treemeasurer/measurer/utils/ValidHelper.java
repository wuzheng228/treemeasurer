package com.treemeasurer.measurer.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Locale;

public class ValidHelper {
    public static  boolean isEmptyAndNotDigitOnly(Context context, String value) {
        if (TextUtils.isEmpty(value)) {
            Toast.makeText(context, "输入不能为空",Toast.LENGTH_LONG).show();
            return true;
        }
        if (!TextUtils.isDigitsOnly(value)) {
            Toast.makeText(context, "请输入数字",Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public static boolean isDigitOutRange(Context context,String v, int lo, int hi, String tip) {
        int i = Integer.parseInt(v);
        if (v.length() > String.valueOf(hi).length() ||i < lo || i > hi) {
            String pattern = String.format(Locale.CHINA,tip +":%d~%d",lo, hi);
            Toast.makeText(context, pattern,Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
