package com.onlyknow.toy.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.onlyknow.toy.R;
import com.onlyknow.toy.api.OKLoadGanKApi;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.component.adapter.OKGanKViewAdapter;
import com.onlyknow.toy.component.view.OKEmptyView;
import com.onlyknow.toy.component.view.OKRecyclerView;
import com.onlyknow.toy.data.OKDatabase;
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

public class OKGanKFavActivity extends OKBaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.ok_activity_fav_toolbar)
    Toolbar toolbar;

    @Bind(R.id.ok_content_collapsing_RecyclerView)
    OKRecyclerView recyclerView;

    @Bind(R.id.ok_content_collapsing_refresh)
    SmartRefreshLayout refreshLayout;

    @Bind(R.id.ok_activity_fav_top_image)
    FloatingActionButton toppingButton;

    @Bind(R.id.ok_content_collapsing_emptyView)
    OKEmptyView emptyView;

    private OKGanKViewAdapter gankViewAdapter;

    private OKLoadGanKApi loadGanKApi;

    private List<OKGanKResultModel> modelList = new ArrayList<>();

    private int page = 1;

    private boolean isPause = false;

    public void stickTop() {
        if (!isPause) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    private void init() {
        gankViewAdapter = new OKGanKViewAdapter(this, modelList, OKGanKViewAdapter.ViewType.TYPE_ARTICLE_CARD);

        recyclerView.setAdapter(gankViewAdapter);

        loadGanKApi = new OKLoadGanKApi(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.setRefreshHeader(new TaurusHeader(this));
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setOnLoadMoreListener(this);

        emptyView.setRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshLayout.autoRefresh();
            }
        });

        emptyView.setRetryBackground(getColorInTheme());

        recyclerView.setEmptyView(emptyView);

        toppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickTop();
            }
        });

        refreshLayout.autoRefresh();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ok_activity_fav);

        ButterKnife.bind(this);

        initStatusBar();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setStatusBar(colorInTheme);

        toppingButton.setBackgroundTintList(ColorStateList.valueOf(colorInTheme));

        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (toolbar != null) {
            toolbar.setBackgroundColor(colorInTheme);
            toolbar.setTitle(getText(R.string.act_fav_toolbar));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        isPause = true;

        refreshLayout.finishRefresh();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_menu_fav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();

                break;

            case R.id.ok_menu_fav_clear:
                AlertDialog.Builder AlertDialog = new AlertDialog.Builder(this);

                AlertDialog.setTitle(getText(R.string.fav_alert_title));

                AlertDialog.setMessage(getText(R.string.fav_alert_tip));

                AlertDialog.setIcon(R.mipmap.ic_launcher);

                AlertDialog.setPositiveButton(getText(R.string.fav_alert_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        OKDatabase helper = OKDatabase.getHelper(OKGanKFavActivity.this);

                        helper.onUpgrade(helper.getWritableDatabase(), helper.getConnectionSource(), 2, 3);

                        refreshLayout.autoRefresh();

                        showSnackBar(recyclerView, getString(R.string.fav_clear_complete), null);
                    }
                });

                AlertDialog.setNegativeButton(getText(R.string.fav_alert_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                AlertDialog.show();

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        OKLoadGanKApi.Params pam = loadGanKApi.getLastParam().setPageCount(++page);

        loadGanKApi.requestDatabase(pam, new OKLoadGanKApi.RequestHandle() {
            @Override
            public void onLoadComplete(List<OKGanKResultModel> list) {
                if (list != null) {
                    modelList.addAll(list);

                    recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    --page;
                }

                OKGanKFavActivity.this.refreshLayout.finishLoadMore();
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        OKLoadGanKApi.Params pam = loadGanKApi.getLastParam().setPageCount(1);

        loadGanKApi.requestDatabase(pam, new OKLoadGanKApi.RequestHandle() {
            @Override
            public void onLoadComplete(List<OKGanKResultModel> list) {
                if (list != null) {
                    modelList.clear();

                    modelList.addAll(list);

                    page = 1;

                    recyclerView.getAdapter().notifyDataSetChanged();
                }

                OKGanKFavActivity.this.refreshLayout.finishRefresh();
            }
        });
    }
}
