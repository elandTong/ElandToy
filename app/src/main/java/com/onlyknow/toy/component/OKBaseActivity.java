package com.onlyknow.toy.component;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onlyknow.toy.GlideApp;
import com.onlyknow.toy.R;
import com.onlyknow.toy.component.view.OKCatLoadingView;
import com.onlyknow.toy.utils.OKLogUtil;
import com.onlyknow.toy.utils.os.OKBarTintUtil;
import com.onlyknow.toy.utils.transform.OKBlurTransformation;

/**
 * 扩展Activity基本能力;
 * <p>
 * 所有Activity都必须继承该基本Activity
 * <p>
 * Created by Administrator on 2017/12/8.
 */

public class OKBaseActivity extends AppCompatActivity {
    public final static String ACT_COLOR_THEME = "ACT_COLOR_THEME";

    protected OKCatLoadingView mCatLoadingView = null;

    protected OKBarTintUtil tintManager = null;

    protected SharedPreferences appPreferences = null; // 应用配置项

    protected Bundle intentBundle;

    protected int colorInTheme = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colorInTheme = getResources().getColor(R.color.colorPrimary);

        intentBundle = getIntent().getExtras();

        if (intentBundle != null) {
            colorInTheme = intentBundle.getInt(ACT_COLOR_THEME, getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
        System.gc();
    }

    @Override
    public Resources getResources() {
        // 设置android app 的字体大小不受系统字体大小改变的影响
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        OKLogUtil.print("onConfigurationChanged : " + newConfig.toString());
    }


    protected final void showSnackBar(View view, String message, String code) {

        if (TextUtils.isEmpty(code)) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, "MESSAGE:" + message + " CODE:" + code, Snackbar.LENGTH_SHORT).show();
        }

    }

    protected final void GlideApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(this, 25)).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(this, 25)).into(mView);
    }

    protected final void initStatusBar() { // 初始化状态栏颜色
        setStatusBar(getResources().getColor(R.color.md_light_green_500));
    }

    protected final void setStatusBar(int color) { // 设置状态栏颜色
        if (tintManager == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window win = this.getWindow();

                WindowManager.LayoutParams winParams = win.getAttributes();

                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

                winParams.flags |= bits;

                win.setAttributes(winParams);
            }

            tintManager = new OKBarTintUtil(this);

            tintManager.setStatusBarTintEnabled(true);
        }

        tintManager.setStatusBarTintColor(color);
    }

    protected final void showProgressDialog(String content) { // 打开加载部件
        if (mCatLoadingView == null) {
            mCatLoadingView = new OKCatLoadingView();
            mCatLoadingView.setLoadText(content);
            mCatLoadingView.show(getSupportFragmentManager(), "");
        }
    }

    protected final void closeProgressDialog() { // 关闭加载部件
        if (mCatLoadingView != null) {
            mCatLoadingView.dismiss();
            mCatLoadingView = null;
        }
    }

    protected final void startUserActivity(Bundle mBundle, Class mClass) { // 启动界面
        Intent intent = new Intent();
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        intent.setClass(this, mClass);
        startActivity(intent);
    }

    protected final void sendUserBroadcast(String Action, Bundle bundle) { // 发送广播
        Intent intentB = new Intent(Action);
        if (bundle != null) {
            intentB.putExtras(bundle);
        }
        sendBroadcast(intentB);
    }

    protected SharedPreferences initPreferences() {
        if (appPreferences == null) {
            appPreferences = this.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        }

        return appPreferences;
    }

    public int getColorInTheme() {
        return colorInTheme;
    }
}
