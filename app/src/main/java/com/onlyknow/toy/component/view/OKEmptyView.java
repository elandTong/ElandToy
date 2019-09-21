package com.onlyknow.toy.component.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onlyknow.toy.R;

public class OKEmptyView extends RelativeLayout {
    private ImageView tipImage;

    private AppCompatButton retryButton;

    private TextView tipTextView;

    private Context context = null;

    public OKEmptyView(Context con) {
        super(con);

        this.context = con;

        init();
    }

    public OKEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OKEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("NewApi")
    public OKEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.context = context;

        init();
    }

    private void init() {
        View.inflate(this.context, R.layout.ok_content_no_data, this);

        tipImage = findViewById(R.id.ok_no_data_image);

        retryButton = findViewById(R.id.ok_no_data_retry);

        tipTextView = findViewById(R.id.ok_no_data_tip);
    }

    public void setRetryClickListener(View.OnClickListener listener) {
        if (retryButton != null) {
            retryButton.setOnClickListener(listener);
        }
    }

    public void setTipImage(int resId) {
        tipImage.setImageResource(resId);
    }

    public void setTipImage(Bitmap bitmap) {
        tipImage.setImageBitmap(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setTipImage(Icon icon) {
        tipImage.setImageIcon(icon);
    }

    public void setTipImage(Drawable drawable) {
        tipImage.setImageDrawable(drawable);
    }

    @SuppressLint("RestrictedApi")
    public void setRetryBackground(int color) {
        if (retryButton != null) {
            retryButton.setSupportBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    public void setRetryText(String title) {
        if (retryButton != null) {
            retryButton.setText(title);
        }
    }

    public void setTipText(String title) {
        if (tipTextView != null) {
            tipTextView.setText(title);
        }
    }

    public void hideRetry() {
        retryButton.setVisibility(GONE);
    }

    public void showRetry() {
        retryButton.setVisibility(VISIBLE);
    }

    @Override
    public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
        super.updateViewLayout(view, params);
    }
}
