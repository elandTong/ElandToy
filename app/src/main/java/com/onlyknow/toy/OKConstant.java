package com.onlyknow.toy;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *  2018-02-05 15:14
 *  全局静态数据
 * */

public final class OKConstant {
    public final static String APP_ID = "ElandToy";

    public final static String APP_VERSION = "2.0.9"; // app版本号

    public final static boolean DEBUG_MODE = true;

    // app本地路径
    public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/ElandToy/Image/";

    public final static String GLIDE_PATH = Environment.getExternalStorageDirectory().getPath() + "/ElandToy/Cache/";

    public final static String ONLY_KNOW_OFFICIAL_WEBSITE_URL = "";

    // 操作方法
    public static String getNowDateByString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        return dateFormat.format(new Date());
    }

    public static long getNowDateByLong() {
        return new Date().getTime();
    }
}
