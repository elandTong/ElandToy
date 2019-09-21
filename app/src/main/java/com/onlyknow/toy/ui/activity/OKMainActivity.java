package com.onlyknow.toy.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.onlyknow.permission.HiPermission;
import com.onlyknow.permission.PermissionCallback;
import com.onlyknow.permission.PermissionItem;
import com.onlyknow.toy.GlideApp;
import com.onlyknow.toy.R;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.data.model.OKGanKResultModel;
import com.onlyknow.toy.ui.fragement.gank.OKGanKWelfareFragment;
import com.onlyknow.toy.utils.OKGsonUtil;
import com.onlyknow.toy.utils.OKLogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OKMainActivity extends OKBaseActivity {
    @Bind(R.id.ok_activity_welcome_image)
    ImageView image;

    private long splashLength = 2000;

    private void checkSelfPermission() {
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();

        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, getString(R.string.permission_camera), R.drawable.permission_ic_camera));
        // permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, getString(R.string.permission_gps_location), R.drawable.permission_ic_location));
        // permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_COARSE_LOCATION, getString(R.string.permission_net_location), R.drawable.permission_ic_location));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.permission_save_storage), R.drawable.permission_ic_storage));
        permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage), R.drawable.permission_ic_storage));
        permissionItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, getString(R.string.permission_phone_state), R.drawable.permission_ic_phone));

        HiPermission.create(this).permissions(permissionItems).checkMutiPermission(new PermissionCallback() {
            // 权限申请回调
            @Override
            public void onClose() {
                Toast.makeText(OKMainActivity.this, "您关闭了权限申请,无法启动应用!", Toast.LENGTH_SHORT).show();

                finish();

                OKLogUtil.print("用户关闭权限申请");
            }

            @Override
            public void onFinish() {
                startMainActivity();

                OKLogUtil.print("所有权限申请完成");
            }

            @Override
            public void onDeny(String permission, int position) {
                OKLogUtil.print("HiPermission onDeny");
            }

            @Override
            public void onGuarantee(String permission, int position) {
                OKLogUtil.print("HiPermission onGuarantee");
            }
        });
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Bundle bundle = new Bundle();

                bundle.putInt(OKGanKActivity.INTENT_KEY_GAN_KIO, OKGanKActivity.GAN_KIO_TYPE_FL);

                startUserActivity(bundle, OKGanKActivity.class);

                finish();
            }
        }, splashLength);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.ok_activity_main);

        ButterKnife.bind(this);

        String json = initPreferences().getString(OKGanKWelfareFragment.WELFARE_MODEL, null);

        if (!TextUtils.isEmpty(json)) {
            OKGanKResultModel result = OKGsonUtil.fromJson(json, OKGanKResultModel.class);
            if (result != null && !TextUtils.isEmpty(result.getUrl())) {
                GlideApp.with(this).load(result.getUrl()).into(image);
            }
        }

        checkSelfPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
