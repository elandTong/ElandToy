package com.onlyknow.toy;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;

import com.onlyknow.toy.utils.OKLogUtil;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * OnlyKnow应用基类.
 * <p>
 * Created by Administrator on 2017/12/8.
 */

public class OKApplication extends Application {
    private final String TAG = "OKApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        OKLogUtil.print(TAG, "onCreate");

        init();
    }

    private void init() {
        File imageFile = new File(OKConstant.IMAGE_PATH);
        File glideFile = new File(OKConstant.GLIDE_PATH);

        if (!imageFile.exists()) {
            imageFile.mkdirs();
        }

        if (!glideFile.exists()) {
            glideFile.mkdirs();
        }
    }

    private String getProcessName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processName;
    }
}
