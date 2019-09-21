package com.onlyknow.toy.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.onlyknow.toy.OKConstant;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/12/7.
 */

public class OKLogUtil {
    private static final String TAG = "OKLogUtil";

    public static void print(String mes) {
        if (!OKConstant.DEBUG_MODE || TextUtils.isEmpty(mes)) {
            return;
        }

        Log.i(TAG, mes);
    }

    public static void print(String tag, String mes) {
        if (!OKConstant.DEBUG_MODE || TextUtils.isEmpty(tag) || TextUtils.isEmpty(mes)) {
            return;
        }

        Log.i(tag, mes);
    }

    public static void print(String tag, Object... object) {
        if (!OKConstant.DEBUG_MODE || TextUtils.isEmpty(tag) || object == null) {
            return;
        }

        Log.i(tag, new Gson().toJson(object));
    }
}
