package com.onlyknow.toy.ui.fragement.gank;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.onlyknow.toy.R;
import com.onlyknow.toy.api.OKLoadGanKApi;
import com.onlyknow.toy.component.OKBaseFragment;
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
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/2/6.
 */

@SuppressLint("ValidFragment")
public class OKGanKWelfareFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener {
    public final static String WELFARE_MODEL = "WELFARE_MODEL";

    @Bind(R.id.ok_content_collapsing_RecyclerView)
    OKRecyclerView mOKRecyclerView;

    @Bind(R.id.ok_content_collapsing_refresh)
    SmartRefreshLayout mRefreshLayout;

    @Bind(R.id.ok_content_collapsing_emptyView)
    OKEmptyView okContentCollapsingEmptyView;

    private OKGanKViewAdapter mGanKViewAdapter;

    private OKLoadGanKApi mOKLoadGanKApi;
    private List<OKGanKResultModel> mGanKBeanList = new ArrayList<>();

    private View rootView;

    private int page = 1;

    private boolean isPause = false;

    public OKGanKWelfareFragment(int colorId) {
        super(colorId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_common, container, false);
            ButterKnife.bind(this, rootView);
            init();
            return rootView;
        } else {
            ButterKnife.bind(this, rootView);
            return rootView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        if (mOKLoadGanKApi != null) {
            mOKLoadGanKApi.cancelTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isPause = false;
    }

    private void init() {
        mGanKViewAdapter = new OKGanKViewAdapter(parentActivity, mGanKBeanList, OKGanKViewAdapter.ViewType.TYPE_WELFARE_CARD);

        mOKRecyclerView.setAdapter(mGanKViewAdapter);

        mOKLoadGanKApi = new OKLoadGanKApi(getActivity());

        mOKRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mRefreshLayout.setRefreshHeader(new TaurusHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        okContentCollapsingEmptyView.setRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshLayout.autoRefresh();
            }
        });

        okContentCollapsingEmptyView.setRetryBackground(getResources().getColor(colorInThemeId));

        mOKRecyclerView.setEmptyView(okContentCollapsingEmptyView);

        mRefreshLayout.autoRefresh();
    }

    public void stickTop() {
        if (!isPause) {
            mOKRecyclerView.smoothScrollToPosition(0);
        }
    }

    private void saveNiceImage() {
        // 随机保存图片模型到配置项
        if (mGanKBeanList.size() > 0) {
            OKGanKResultModel result = mGanKBeanList.get(new Random().nextInt(mGanKBeanList.size()));

            if (result != null) {
                initPreferences().edit().putString(WELFARE_MODEL, new Gson().toJson(result)).commit();
            }
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) { // 加载更多
        if (OKNetUtil.isNet(getActivity())) {
            OKLoadGanKApi.Params pam = mOKLoadGanKApi.getLastParam().setType(OKLoadGanKApi.Params.TYPE_FL).setPageCount(++page);

            mOKLoadGanKApi.request(pam, new OKLoadGanKApi.RequestHandle() {

                @Override
                public void onLoadComplete(List<OKGanKResultModel> list) { // 加载更多回调
                    if (list != null) {
                        mGanKBeanList.addAll(list);

                        mOKRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        --page;
                    }

                    mRefreshLayout.finishLoadMore();

                    saveNiceImage();
                }
            });
        } else {
            mRefreshLayout.finishLoadMore(1500);

            showSnackBar(mOKRecyclerView, getString(R.string.action_none_net_tip), null);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) { // 刷新
        if (OKNetUtil.isNet(getActivity())) {
            OKLoadGanKApi.Params pam = mOKLoadGanKApi.getLastParam().setType(OKLoadGanKApi.Params.TYPE_FL).setPageCount(1);

            mOKLoadGanKApi.request(pam, new OKLoadGanKApi.RequestHandle() {

                @Override
                public void onLoadComplete(List<OKGanKResultModel> list) { // 刷新回调
                    if (list != null) {
                        mGanKBeanList.clear();

                        mGanKBeanList.addAll(list);

                        page = 1;

                        mOKRecyclerView.getAdapter().notifyDataSetChanged();
                    }

                    mRefreshLayout.finishRefresh();

                    saveNiceImage();
                }
            });
        } else {
            mRefreshLayout.finishRefresh(1500);

            showSnackBar(mOKRecyclerView, getString(R.string.action_none_net_tip), null);
        }
    }
}
