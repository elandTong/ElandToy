package com.onlyknow.toy.component;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.onlyknow.toy.R;

/**
 * 服务基类(基本服务能力);
 * <p>
 * Created by Reset on 2018/05/24.
 */

public class OKBaseService extends Service {
    private final String TAG = "OKBaseService";

    private NotificationManager notificationManager = null;

    protected final int CODE_NOTICE_ID_MAIN = 801;
    protected final String KEY_PUSH_CHANNEL_ID = "PJD_PUSH_NOTIFY_ID";
    protected final String KEY_PUSH_CHANNEL_NAME = "PJD_PUSH_NOTIFY_NAM";

    // 显示通知信息
    protected final void showNotice(Intent intent, CharSequence title, CharSequence content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, KEY_PUSH_CHANNEL_ID);

        // 设置通知的基本信息(Icon,标题,内容)
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(content);

        // 设置通知的点击行为
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }

        // 生成通知
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // 发送通知ID,需要在应用内唯一
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // 设置渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(KEY_PUSH_CHANNEL_ID, KEY_PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // 显示通知
        notificationManager.notify(CODE_NOTICE_ID_MAIN, notification);

        // startForeground(CODE_NOTICE_ID_MAIN, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
}
