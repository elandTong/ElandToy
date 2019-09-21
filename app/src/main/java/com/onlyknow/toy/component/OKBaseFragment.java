package com.onlyknow.toy.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onlyknow.toy.GlideApp;
import com.onlyknow.toy.R;
import com.onlyknow.toy.component.view.OKCatLoadingView;
import com.onlyknow.toy.utils.transform.OKBlurTransformation;

/**
 * 扩展Fragment基本能力;
 * <p>
 * 所有Fragment都必须继承该基本Fragment;
 * <p>
 * Created by Reset on 2018/05/24;
 */

@SuppressLint("ValidFragment")
public class OKBaseFragment extends Fragment {
    protected OKCatLoadingView mCatLoadingView;

    protected SharedPreferences appPreferences = null; // 应用配置项

    protected OKBaseActivity parentActivity = null;

    protected int colorInThemeId = R.color.colorPrimary;

    public OKBaseFragment(int colorId) {
        super();

        colorInThemeId = colorId;
    }

    @Override
    public void onResume() {
        super.onResume();

        Glide.with(this).resumeRequests();
    }

    @Override
    public void onPause() {
        super.onPause();

        Glide.with(this).pauseRequests();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        closeProgressDialog();
    }

    public void onTopResume() {
    }

    public void onTopPause() {
    }

    protected final void showSnackBar(View view, String message, String code) {

        if (TextUtils.isEmpty(code)) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, "MSG:" + message + " CODE:" + code, Snackbar.LENGTH_SHORT).show();
        }

    }

    protected final void GlideApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(getActivity(), 25)).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(getActivity(), 25)).into(mView);
    }

    protected final void startUserActivity(Bundle mBundle, Class mClass) {
        Intent intent = new Intent();
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        intent.setClass(getActivity(), mClass);
        getActivity().startActivity(intent);
    }

    protected final void sendUserBroadcast(String Action, Bundle bundle) {
        Intent mIntent = new Intent(Action);
        if (bundle != null) {
            mIntent.putExtras(bundle);
        }
        getActivity().sendBroadcast(mIntent);
    }

    protected final void showProgressDialog(String content) {
        if (mCatLoadingView == null) {
            mCatLoadingView = new OKCatLoadingView();
            mCatLoadingView.setLoadText(content);
            mCatLoadingView.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    protected final void closeProgressDialog() {
        if (mCatLoadingView != null) {
            mCatLoadingView.dismiss();
            mCatLoadingView = null;
        }
    }

    protected SharedPreferences initPreferences() {
        if (appPreferences == null) {
            appPreferences = getActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        }

        return appPreferences;
    }

    public void setParentActivity(OKBaseActivity activity) {
        parentActivity = activity;
    }
}
