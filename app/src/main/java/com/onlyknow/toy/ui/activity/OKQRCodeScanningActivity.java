package com.onlyknow.toy.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onlyknow.toy.R;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.component.view.OKSEImageView;
import com.onlyknow.toy.utils.OKLogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class OKQRCodeScanningActivity extends OKBaseActivity implements QRCodeView.Delegate {
    @Bind(R.id.ok_activity_qrcode_back)
    OKSEImageView backButton;

    @Bind(R.id.ok_activity_qrcode_zxingview)
    ZXingView zxingview;

    @Bind(R.id.ok_activity_qrcode_openlight)
    OKSEImageView openLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.ok_activity_qrcode_rec);

        ButterKnife.bind(this);

        init();
    }

    private void init() {
        zxingview.setDelegate(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        openLight.setTag(R.id.image_tag, 0);

        openLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) openLight.getTag(R.id.image_tag);
                if (i == 0) {
                    zxingview.openFlashlight();

                    openLight.setTag(R.id.image_tag, 1);

                    openLight.setImageResource(R.drawable.ok_light_off);
                } else if (i == 1) {
                    zxingview.closeFlashlight();

                    openLight.setTag(R.id.image_tag, 0);

                    openLight.setImageResource(R.drawable.ok_light_on);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        zxingview.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        zxingview.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        zxingview.onDestroy();
    }

    private void handleDecode(String result) {
        if (!TextUtils.isEmpty(result)) {
            Bundle bundle = new Bundle();
            bundle.putString(OKWebActivity.WEB_LINK, result);
            bundle.putString(OKWebActivity.WEB_TITLE, getString(R.string.act_qrcode_result_title));
            bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);

            startUserActivity(bundle, OKWebActivity.class);
        } else {
            showSnackBar(zxingview, getString(R.string.act_qrcode_result_null_tip), null);
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        handleDecode(result);

        OKLogUtil.print("二维码识别结果!" + result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        OKLogUtil.print("二维码识别错误!");
    }
}
