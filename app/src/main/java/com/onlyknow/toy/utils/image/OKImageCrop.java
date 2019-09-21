package com.onlyknow.toy.utils.image;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.onlyknow.toy.OKConstant;
import com.onlyknow.toy.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

public class OKImageCrop {
    /**
     * 启动裁剪;
     *
     * @param sourceFilePath 需要裁剪图片的绝对路径;
     * @param aspectRatioX   裁剪图片宽高比;
     * @param aspectRatioY   裁剪图片宽高比;
     */
    public static void startUCrop(Activity activity, String sourceFilePath, float aspectRatioX, float aspectRatioY) {

        Uri sourceUri = Uri.fromFile(new File(sourceFilePath));

        File outDir = new File(OKConstant.IMAGE_PATH);

        if (!outDir.exists()) outDir.mkdirs();

        File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");

        Uri destinationUri = Uri.fromFile(outFile); // 输出uri;

        UCrop uCrop = UCrop.of(sourceUri, destinationUri); // 初始化,第一个参数:需要裁剪的图片,第二个参数:裁剪后图片;

        UCrop.Options options = new UCrop.Options(); // 初始化UCrop配置;

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL); // 设置裁剪图片可操作的手势;
        options.setHideBottomControls(false); // 是否隐藏底部容器,默认显示;
        options.setToolbarColor(ActivityCompat.getColor(activity, R.color.md_light_green_500)); // 设置toolbar颜色;
        options.setStatusBarColor(ActivityCompat.getColor(activity, R.color.md_light_green_600)); // 设置状态栏颜色;
        options.setFreeStyleCropEnabled(true); // 是否能调整裁剪框;

        uCrop.withOptions(options); // UCrop配置;

        uCrop.withAspectRatio(aspectRatioX, aspectRatioY); // 设置裁剪图片的宽高比,比如16：9;

        uCrop.start(activity, UCrop.REQUEST_CROP); // 跳转裁剪页面;
    }
}
