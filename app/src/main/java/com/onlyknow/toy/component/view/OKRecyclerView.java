package com.onlyknow.toy.component.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2018/1/4.
 */

public class OKRecyclerView extends RecyclerView {
    private View mHeaderView;

    private View mFooterView;

    private View mEmptyView;

    private OKBaseAdapter mOKBaseAdapter;

    private final AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            checkEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);

            checkEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);

            checkEmpty();
        }
    };

    private void checkEmpty() { // 检查列表是否为空
        if (mEmptyView != null && this.getAdapter() != null) {
            final boolean emptyViewVisible = (this.getAdapter().getItemCount() == 0);

            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);

            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    public OKRecyclerView(Context context) {
        super(context);
    }

    public OKRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OKRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setHeaderView(View view) {
        mHeaderView = view;

        if (mOKBaseAdapter != null) {
            mOKBaseAdapter.notifyItemInserted(0);
        }
    }

    public void setFooterView(View view) {
        mFooterView = view;

        if (mOKBaseAdapter != null) {
            mOKBaseAdapter.notifyItemInserted(mOKBaseAdapter.getItemCount() - 1);
        }
    }

    public void setEmptyView(View view) {
        if (view == null) {
            return;
        }

        mEmptyView = view;

        if (mOKBaseAdapter != null) {
            mOKBaseAdapter.notifyDataSetChanged();
        }

        checkEmpty();
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    @Override
    public void setAdapter(Adapter newAdapter) {
        final Adapter oldAdapter = getAdapter();

        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        }

        if (newAdapter != null) {
            mOKBaseAdapter = new OKBaseAdapter(newAdapter);
        }

        super.setAdapter(mOKBaseAdapter);

        if (mOKBaseAdapter != null) {
            mOKBaseAdapter.registerAdapterDataObserver(adapterDataObserver);
        }

        checkEmpty();
    }

    private class OKBaseAdapter extends Adapter<ViewHolder> { // 装饰类
        private Adapter mOriginalAdapter;

        private int ITEM_TYPE_NORMAL = 0;

        private int ITEM_TYPE_HEADER = 1;

        private int ITEM_TYPE_FOOTER = 2;

        public OKBaseAdapter(Adapter originalAdapter) {
            mOriginalAdapter = originalAdapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return new OKBaseViewHolder(mHeaderView);
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return new OKBaseViewHolder(mFooterView);
            } else {
                return mOriginalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);

            if (type == ITEM_TYPE_HEADER || type == ITEM_TYPE_FOOTER) {
                return;
            }

            int realPosition = getRealItemPosition(position);

            mOriginalAdapter.onBindViewHolder(holder, realPosition);
        }

        @Override
        public int getItemCount() {
            int itemCount = mOriginalAdapter.getItemCount();

            //加上其他各种View
            if (null != mHeaderView) {
                itemCount++;
            }

            if (null != mFooterView) {
                itemCount++;
            }

            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (null != mHeaderView && position == 0) {
                return ITEM_TYPE_HEADER;
            }

            if (null != mFooterView && position == getItemCount() - 1) {
                return ITEM_TYPE_FOOTER;
            }

            return ITEM_TYPE_NORMAL;
        }

        private int getRealItemPosition(int position) {
            if (null != mHeaderView) {
                return position - 1;
            }

            return position;
        }

        /**
         * ViewHolder 是一个抽象类
         */
        class OKBaseViewHolder extends ViewHolder {
            OKBaseViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
