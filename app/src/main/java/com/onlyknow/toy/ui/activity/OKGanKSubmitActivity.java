package com.onlyknow.toy.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.onlyknow.toy.R;
import com.onlyknow.toy.api.OKLoadGanKApi;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.data.model.OKGanKModel;
import com.onlyknow.toy.utils.OKNetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OKGanKSubmitActivity extends OKBaseActivity {
    private final String TAG = "OKGanKSubmitActivity";

    @Bind(R.id.ok_activity_submit_toolbar)
    Toolbar okActivitySubmitToolbar;

    @Bind(R.id.ok_activity_submit_link)
    AppCompatEditText okActivitySubmitLink;

    @Bind(R.id.ok_activity_submit_desc)
    AppCompatEditText okActivitySubmitDesc;

    @Bind(R.id.ok_activity_submit_nick)
    AppCompatEditText okActivitySubmitNick;

    @Bind(R.id.ok_activity_submit_btn)
    AppCompatButton submitBtn;

    @Bind(R.id.ok_activity_submit_link_input)
    TextInputLayout okActivitySubmitLinkInput;

    @Bind(R.id.ok_activity_submit_desc_input)
    TextInputLayout okActivitySubmitDescInput;

    @Bind(R.id.ok_activity_submit_nick_input)
    TextInputLayout okActivitySubmitNickInput;

    @Bind(R.id.ok_activity_submit_spinner)
    AppCompatSpinner okActivitySubmitSpinner;

    OKLoadGanKApi loadGanKApi;

    @SuppressLint({"RestrictedApi", "NewApi"})
    private void init() {
        loadGanKApi = new OKLoadGanKApi(this);

        okActivitySubmitLink.setTextColor(colorInTheme);
        okActivitySubmitDesc.setTextColor(colorInTheme);
        okActivitySubmitNick.setTextColor(colorInTheme);

        okActivitySubmitNick.setSingleLine(true);
        okActivitySubmitNick.setEllipsize(TextUtils.TruncateAt.END);

        okActivitySubmitLink.setMaxLines(2);
        okActivitySubmitLink.setEllipsize(TextUtils.TruncateAt.END);

        okActivitySubmitDesc.setMaxLines(4);
        okActivitySubmitDesc.setEllipsize(TextUtils.TruncateAt.END);

        submitBtn.setSupportBackgroundTintList(ColorStateList.valueOf(colorInTheme));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();

                String link = okActivitySubmitLink.getText().toString();

                String desc = okActivitySubmitDesc.getText().toString();

                String who = okActivitySubmitNick.getText().toString();

                if (TextUtils.isEmpty(link)) {
                    okActivitySubmitLinkInput.setError(getText(R.string.act_submit_err_tip_link_null));

                    return;
                }

                if (TextUtils.isEmpty(desc)) {
                    okActivitySubmitDescInput.setError(getText(R.string.act_submit_err_tip_desc_null));

                    return;
                }

                if (TextUtils.isEmpty(who)) {
                    okActivitySubmitNickInput.setError(getText(R.string.act_submit_err_tip_who_null));

                    return;
                }

                if (!OKNetUtil.isUrlAgreement(link)) {
                    okActivitySubmitLinkInput.setError(getText(R.string.act_submit_err_tip_link));

                    return;
                }

                OKLoadGanKApi.PushParams pam = new OKLoadGanKApi.PushParams();

                pam.setUrl(link);
                pam.setDesc(desc);
                pam.setWho(who);
                pam.setType(okActivitySubmitSpinner.getSelectedItem().toString());

                showProgressDialog(getString(R.string.act_submit_loading));

                loadGanKApi.push(pam, new OKLoadGanKApi.PushHandle() { // 推送干货
                    @Override
                    public void onPush(OKGanKModel model) {
                        closeProgressDialog();

                        if (model != null) {
                            if (model.isError()) {
                                Toast.makeText(OKGanKSubmitActivity.this, getString(R.string.act_submit_err) + model.getMsg(), Toast.LENGTH_SHORT).show();
                            } else {
                                clearInput();

                                Toast.makeText(OKGanKSubmitActivity.this, getText(R.string.act_submit_succ), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OKGanKSubmitActivity.this, getString(R.string.act_submit_err_net), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void clearInput() {
        okActivitySubmitLink.setText("");

        okActivitySubmitDesc.setText("");

        okActivitySubmitNick.setText("");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ok_activity_submit);

        ButterKnife.bind(this);

        initStatusBar();

        setSupportActionBar(okActivitySubmitToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setStatusBar(colorInTheme);

        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (okActivitySubmitToolbar != null) {
            okActivitySubmitToolbar.setBackgroundColor(colorInTheme);
            okActivitySubmitToolbar.setTitle(getText(R.string.act_submit_toolbar));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                break;

            case R.id.ok_menu_submit_clear:
                hideInput();

                clearInput();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
