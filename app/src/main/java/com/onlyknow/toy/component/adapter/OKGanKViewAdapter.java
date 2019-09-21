package com.onlyknow.toy.component.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onlyknow.toy.GlideApp;
import com.onlyknow.toy.R;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.component.view.like.LikeButton;
import com.onlyknow.toy.component.view.like.OnLikeListener;
import com.onlyknow.toy.data.model.OKGanKResultModel;
import com.onlyknow.toy.ui.activity.OKDragPhotoActivity;
import com.onlyknow.toy.ui.activity.OKWebActivity;

import java.util.List;

public class OKGanKViewAdapter extends RecyclerView.Adapter<OKGanKViewAdapter.GanKViewHolder> {
    public enum ViewType {
        TYPE_ARTICLE_CARD,
        TYPE_WELFARE_CARD
    }

    private final ViewType viewType;

    private OKBaseActivity context;

    private List<OKGanKResultModel> modelList;

    public OKGanKViewAdapter(OKBaseActivity con, List<OKGanKResultModel> list, ViewType type) {
        this.context = con;

        this.modelList = list;

        this.viewType = type;
    }

    @Override
    public GanKViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (this.viewType == ViewType.TYPE_WELFARE_CARD) {
            view = LayoutInflater.from(context).inflate(R.layout.ok_item_gank_welfare, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.ok_item_gank_card, parent, false);
        }

        return new GanKViewHolder(view, this.viewType);
    }

    @Override
    public void onBindViewHolder(GanKViewHolder holder, final int position) {
        final OKGanKResultModel bean = modelList.get(position);

        holder.bind(bean);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class GanKViewHolder extends RecyclerView.ViewHolder {
        private final ViewType viewType;

        // 容器
        private CardView cardView; // 根容器

        private RelativeLayout menuZone; // 二级菜单容器

        private CardView imageZone;

        private RelativeLayout imageOpenOpt;

        // 控件
        private ImageView imageView;

        private TextView textViewContent, textViewName, textViewNameDate;

        private LikeButton likeButton;

        private GanKViewHolder(View itemView, ViewType type) {
            super(itemView);

            this.viewType = type;

            if (this.viewType == ViewType.TYPE_WELFARE_CARD) {
                cardView = itemView.findViewById(R.id.ok_item_gank_welfare_root);

                imageView = itemView.findViewById(R.id.ok_item_gank_welfare_image);
            } else {
                cardView = itemView.findViewById(R.id.ok_item_gank_card_root);

                menuZone = itemView.findViewById(R.id.ok_item_gank_card_menu_zone);

                imageZone = itemView.findViewById(R.id.ok_item_gank_card_image_root);

                imageOpenOpt = itemView.findViewById(R.id.ok_item_gank_card_open_image);

                imageView = itemView.findViewById(R.id.ok_item_gank_card_image);

                textViewContent = itemView.findViewById(R.id.ok_item_gank_card_content_text);

                textViewName = itemView.findViewById(R.id.ok_item_gank_card_name_text);

                textViewNameDate = itemView.findViewById(R.id.ok_item_gank_card_date_text);

                likeButton = itemView.findViewById(R.id.ok_item_gank_card_like_btn);
            }
        }

        private void bindArticleCard(final OKGanKResultModel model) {
            if (model.getImages() != null && model.getImages().size() > 0) {
                imageZone.setVisibility(View.VISIBLE);

                GlideApp.with(context)
                        .load(model.getImages().get(0))
                        .centerCrop()
                        .placeholder(R.drawable.ok_gank_item)
                        .error(R.drawable.ok_gank_item)
                        .into(imageView);
            } else {
                imageZone.setVisibility(View.GONE);
            }

            menuZone.setVisibility(View.GONE);

            textViewContent.setText(model.getDesc());

            textViewName.setText(model.getWho());

            textViewNameDate.setText(model.getPublishedAt().substring(0, 10));

            cardView.setOnClickListener(new View.OnClickListener() {// 点击事件 打开文章
                @Override
                public void onClick(View view) {
                    if (menuZone.getVisibility() == View.GONE) {
                        Bundle bundle = new Bundle();

                        bundle.putString(OKWebActivity.WEB_LINK, model.getUrl());

                        bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, context.getColorInTheme());

                        Intent intent = new Intent(context, OKWebActivity.class);

                        intent.putExtras(bundle);

                        context.startActivity(intent);
                    } else {
                        menuZone.setVisibility(View.GONE);
                    }
                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() { // 长按事件 打开 item view 二级菜单
                @Override
                public boolean onLongClick(View v) {
                    if (menuZone.getVisibility() == View.GONE) {
                        likeButton.setLiked(model.isLikedForDatabase(context));

                        menuZone.setVisibility(View.VISIBLE);
                    } else {
                        menuZone.setVisibility(View.GONE);
                    }

                    return true; // 阻止冒泡
                }
            });

            likeButton.setOnLikeListener(new OnLikeListener() { // 二级菜单 like 按钮 点击事件
                @Override
                public void liked(LikeButton likeButton) {
                    model.saveForDatabase(context);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    model.deleteForDatabase(context);
                }
            });

            imageOpenOpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImage(model.getImages().get(0));
                }
            });
        }

        private void bindWelfareCard(final OKGanKResultModel model) {
            GlideApp.with(context)
                    .load(model.getUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ok_gank_item)
                    .error(R.drawable.ok_gank_item)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openImage(model.getUrl());
                }
            });
        }

        private void openImage(String url) {
            int location[] = new int[2];

            imageView.getLocationOnScreen(location);

            Bundle bundle = new Bundle();
            bundle.putInt("left", location[0]);
            bundle.putInt("top", location[1]);
            bundle.putInt("height", imageView.getHeight());
            bundle.putInt("width", imageView.getWidth());
            bundle.putString("url", url);

            Intent intent = new Intent(context, OKDragPhotoActivity.class);

            intent.putExtras(bundle);

            context.startActivity(intent);

            context.overridePendingTransition(0, 0);
        }

        public void bind(final OKGanKResultModel model) { // bind view 模型
            if (this.viewType == ViewType.TYPE_WELFARE_CARD) { // 福利模型
                bindWelfareCard(model);
            } else { // 文章模型
                bindArticleCard(model);
            }
        }
    }
}
