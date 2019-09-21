package com.onlyknow.toy.service;

import android.content.Intent;

import com.onlyknow.toy.R;
import com.onlyknow.toy.component.OKBaseService;
import com.onlyknow.toy.utils.OKLogUtil;

public class OKMainService extends OKBaseService {
    private static final String TAG = "OKMainService";

    @Override
    public void onCreate() {
        super.onCreate();

        OKLogUtil.print(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotice(null, getResources().getString(R.string.app_id), getResources().getString(R.string.app_id) + " 主服务正在运行...");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        startService(new Intent(this, OKMainService.class));

        OKLogUtil.print(TAG, "onDestroy");
    }
}
