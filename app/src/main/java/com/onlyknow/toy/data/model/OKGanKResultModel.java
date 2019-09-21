package com.onlyknow.toy.data.model;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.onlyknow.toy.data.OKDatabase;
import com.onlyknow.toy.utils.OKLogUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "gank_table")
public class OKGanKResultModel {
    public final static String KEY_ID = "_id";
    @DatabaseField(columnName = KEY_ID, id = true)
    private String _id;

    public final static String KEY_CREATED_AT = "createdAt";
    @DatabaseField(columnName = KEY_CREATED_AT)
    private String createdAt;

    public final static String KEY_DESC = "desc";
    @DatabaseField(columnName = KEY_DESC)
    private String desc;

    public final static String KEY_PUBLISHED_AT = "publishedAt";
    @DatabaseField(columnName = KEY_PUBLISHED_AT)
    private String publishedAt;

    public final static String KEY_SOURCE = "source";
    @DatabaseField(columnName = KEY_SOURCE)
    private String source;

    public final static String KEY_TYPE = "type";
    @DatabaseField(columnName = KEY_TYPE)
    private String type;

    public final static String KEY_URL = "url";
    @DatabaseField(columnName = KEY_URL)
    private String url;

    public final static String KEY_USED = "used";
    @DatabaseField(columnName = KEY_USED)
    private boolean used;

    public final static String KEY_WHO = "who";
    @DatabaseField(columnName = KEY_WHO)
    private String who;

    public final static String KEY_ITEM_HEIGHT = "itemHeight";
    @DatabaseField(columnName = KEY_ITEM_HEIGHT)
    private int itemHeight;

    public final static String KEY_IMAGES_JSON = "imagesJson";
    @DatabaseField(columnName = KEY_IMAGES_JSON)
    private String imagesJson;

    public final static String KEY_SAVE_TIMES = "saveTimes";
    @DatabaseField(columnName = KEY_SAVE_TIMES)
    private long saveTimes;

    public final static String KEY_IMAGES = "images";
    private List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public String getImagesJson() {
        return imagesJson;
    }

    public void setImagesJson(String imagesJson) {
        this.imagesJson = imagesJson;
    }

    public long getSaveTimes() {
        return saveTimes;
    }

    public void setSaveTimes(long saveTimes) {
        this.saveTimes = saveTimes;
    }

    // 数据库控制
    public void saveForDatabase(Context context) {
        if (this.getImages() != null && this.getImages().size() > 0) { // 处理图片列表
            this.setImagesJson(this.getImages().get(0));
        }

        this.setSaveTimes(new Date().getTime());

        OKDatabase helper = OKDatabase.getHelper(context);

        try {
            helper.getCardDao().createOrUpdate(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteForDatabase(Context context) {
        OKDatabase helper = OKDatabase.getHelper(context);

        try {
            helper.getCardDao().delete(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isLikedForDatabase(Context context) {
        OKDatabase helper = OKDatabase.getHelper(context);

        try {
            OKGanKResultModel result = helper.getCardDao().queryBuilder().distinct().where().eq(KEY_ID, this.get_id()).queryForFirst();

            if (result != null && this.get_id().equals(result.get_id())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public OKGanKResultModel changedImageJson() {
        if (TextUtils.isEmpty(this.getImagesJson())) {
            return this;
        }

        if (this.getImages() == null || this.getImages().size() == 0) {
            this.setImages(new ArrayList<String>());
            this.getImages().add(this.getImagesJson());
        }

        return this;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
