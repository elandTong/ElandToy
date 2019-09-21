package com.onlyknow.toy.utils.image;

import android.content.Context;
import android.widget.ImageView;

import com.onlyknow.toy.GlideApp;
import com.onlyknow.toy.R;
import com.youth.banner.loader.ImageLoader;

public class OKBannerLoad extends ImageLoader {
    private boolean isLink = false;

    public OKBannerLoad(boolean b) {
        this.isLink = b;
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (isLink) {
            try {
                BannerImage adImage = (BannerImage) path;// 广告轮播图片加载
                GlideApp.with(context).load(adImage.getUrl()).error(R.drawable.ok_gank_item).placeholder(R.drawable.ok_gank_item).into(imageView);
            } catch (Exception ex) {
                GlideApp.with(context).load(R.drawable.ok_gank_item).into(imageView);
                ex.printStackTrace();
            }
        } else {
            GlideApp.with(context).load(path.toString()).error(R.drawable.ok_gank_item).placeholder(R.drawable.ok_gank_item).into(imageView);
        }
    }

    public static class BannerImage {
        private String url = "";
        private String link = "";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
