package com.onlyknow.toy.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.onlyknow.toy.R;
import com.onlyknow.toy.api.OKLoadGanKApi;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.component.adapter.OKGanKViewAdapter;
import com.onlyknow.toy.component.view.OKEmptyView;
import com.onlyknow.toy.component.view.OKRecyclerView;
import com.onlyknow.toy.data.model.OKGanKResultModel;
import com.onlyknow.toy.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OKGanKSearchActivity extends OKBaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.ok_activity_search_toolbar)
    Toolbar toolbar;

    @Bind(R.id.ok_activity_search_appBarLayout)
    AppBarLayout appBarLayout;

    @Bind(R.id.ok_activity_search_edit)
    AppCompatEditText searchEdit;

    @Bind(R.id.ok_activity_search_top_image)
    FloatingActionButton toppingImage;

    @Bind(R.id.ok_content_collapsing_RecyclerView)
    OKRecyclerView recyclerView;

    @Bind(R.id.ok_content_collapsing_refresh)
    SmartRefreshLayout refreshLayout;

    @Bind(R.id.ok_content_collapsing_emptyView)
    OKEmptyView emptyView;

    private OKGanKViewAdapter mGanKViewAdapter = null;

    private OKLoadGanKApi loadGanKApi = null;

    private List<OKGanKResultModel> modelList = new ArrayList<>();

    private int page = 1;

    private boolean isPause = false;

    private void stickTop() { // 置顶
        if (!isPause) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    private void init() { // 初始化
        mGanKViewAdapter = new OKGanKViewAdapter(this, modelList, OKGanKViewAdapter.ViewType.TYPE_ARTICLE_CARD);

        recyclerView.setAdapter(mGanKViewAdapter);

        loadGanKApi = new OKLoadGanKApi(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.setRefreshHeader(new TaurusHeader(this));

        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setOnLoadMoreListener(this);

        refreshLayout.setEnableRefresh(false); // 关闭刷新

        emptyView.hideRetry();

        emptyView.setTipText(getString(R.string.action_nodata_tip));

        recyclerView.setEmptyView(emptyView);

        toppingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickTop();
            }
        });
    }

    private void search(CharSequence content) { // 搜索操作
        if (TextUtils.isEmpty(content)) {
            showSnackBar(recyclerView, getString(R.string.search_none_content_tip), null);

            return;
        }

        loadGanKApi.getLastParam().setValue(content.toString()).setType(OKLoadGanKApi.Params.TYPE_SEARCH).setPageCount(1);

        refreshLayout.setEnableRefresh(true); // 开启刷新

        refreshLayout.autoRefresh();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ok_activity_search);

        ButterKnife.bind(this);

        initStatusBar();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setStatusBar(colorInTheme);

        searchEdit.setSupportBackgroundTintList(ColorStateList.valueOf(colorInTheme));

        toppingImage.setBackgroundTintList(ColorStateList.valueOf(colorInTheme));

        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (toolbar != null) {
            toolbar.setTitle("");
            toolbar.setBackgroundColor(colorInTheme);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_menu_search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                break;
            case R.id.ok_menu_gank_search_in:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                search(searchEdit.getText());

                break;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;

        refreshLayout.finishLoadMore();

        if (loadGanKApi != null) {
            loadGanKApi.cancelTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isPause = false;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) { // 更多回调
        OKLoadGanKApi.Params pam = loadGanKApi.getLastParam();

        if (TextUtils.isEmpty(pam.getValue())) {
            this.refreshLayout.finishLoadMore(1000);

            showSnackBar(recyclerView, getString(R.string.search_none_content_tip), null);

            return;
        }

        if (OKNetUtil.isNet(this)) {
            pam.setType(OKLoadGanKApi.Params.TYPE_SEARCH).setPageCount(++page);

            loadGanKApi.request(pam, new OKLoadGanKApi.RequestHandle() {

                @Override
                public void onLoadComplete(List<OKGanKResultModel> list) {
                    if (list != null) {
                        modelList.addAll(list);

                        recyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        --page;
                    }

                    OKGanKSearchActivity.this.refreshLayout.finishLoadMore();
                }
            });
        } else {
            this.refreshLayout.finishLoadMore(1500);

            showSnackBar(recyclerView, getString(R.string.action_none_net_tip), null);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) { // 刷新回调
        OKLoadGanKApi.Params pam = loadGanKApi.getLastParam();

        if (TextUtils.isEmpty(pam.getValue())) {
            this.refreshLayout.finishRefresh(1000);

            this.refreshLayout.setEnableRefresh(false);

            showSnackBar(recyclerView, getString(R.string.search_none_content_tip), null);

            return;
        }

        if (OKNetUtil.isNet(this)) {
            pam.setType(OKLoadGanKApi.Params.TYPE_SEARCH).setPageCount(1);

            loadGanKApi.request(pam, new OKLoadGanKApi.RequestHandle() {

                @Override
                public void onLoadComplete(List<OKGanKResultModel> list) {
                    if (list != null) {
                        modelList.clear();

                        modelList.addAll(list);

                        page = 1;

                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                    OKGanKSearchActivity.this.refreshLayout.finishRefresh();

                    OKGanKSearchActivity.this.refreshLayout.setEnableRefresh(false);
                }
            });
        } else {
            this.refreshLayout.finishRefresh(1500);

            this.refreshLayout.setEnableRefresh(false);

            showSnackBar(recyclerView, getString(R.string.action_none_net_tip), null);
        }
    }
}
