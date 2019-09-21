package com.onlyknow.toy.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.onlyknow.toy.data.OKDatabase;
import com.onlyknow.toy.data.model.OKGanKModel;
import com.onlyknow.toy.data.model.OKGanKResultModel;
import com.onlyknow.toy.utils.OKGsonUtil;
import com.onlyknow.toy.utils.OKLogUtil;
import com.onlyknow.toy.utils.OKNetUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 干货营界面数据源加载Api
 * <p>
 * Created by Administrator on 2018/2/6.
 */

public class OKLoadGanKApi {
    private final String TAG = "OKLoadGanKApi";

    private RequestHandle requestHandle;

    private PushHandle pushHandle;

    private Context context;

    private LoadGanKTask loadGanKTask;

    private LoadDatabaseTask loadDatabaseTask;

    private PushTask pushTask;

    // 格式 http://gank.io/api/data/福利/20/2
    private final String URL_STENCIL_TYPE = "{[TYPE]}";
    private final String URL_STENCIL_SIZE = "{[SIZE]}";
    private final String URL_STENCIL_PAGE = "{[PAGE]}";
    private final String URL_STENCIL_SEARCH_VALUE = "{[VALUE]}";

    private final String URL = "http://gank.io/api/data/" + URL_STENCIL_TYPE + "/" + URL_STENCIL_SIZE + "/" + URL_STENCIL_PAGE;

    private final String SEARCH_URL = "http://gank.io/api/search/query/" + URL_STENCIL_SEARCH_VALUE + "/category/" + URL_STENCIL_TYPE + "/count/" + URL_STENCIL_SIZE + "/page/" + URL_STENCIL_PAGE;

    private final String PUSH_URL = "https://gank.io/api/add2gank";

    private OKLoadGanKApi.Params lastParam = new OKLoadGanKApi.Params();

    public OKLoadGanKApi(Context con) {
        this.context = con;
    }

    public interface RequestHandle {
        void onLoadComplete(List<OKGanKResultModel> list);
    }

    public interface PushHandle {
        void onPush(OKGanKModel model);
    }

    public void request(Params params, RequestHandle call) {
        if (params == null || call == null) {
            return;
        }

        this.requestHandle = call;

        cancelTask();

        loadGanKTask = new LoadGanKTask();

        lastParam = params;

        loadGanKTask.executeOnExecutor(OKNetUtil.exe, lastParam);
    }

    public void requestDatabase(Params params, RequestHandle call) {
        if (params == null || call == null) {
            return;
        }

        this.requestHandle = call;

        cancelTask();

        loadDatabaseTask = new LoadDatabaseTask();

        lastParam = params;

        loadDatabaseTask.executeOnExecutor(OKNetUtil.exe, lastParam);
    }

    public void push(PushParams pam, PushHandle handle) {
        if (pam == null || handle == null) {
            return;
        }

        this.pushHandle = handle;

        cancelTask();

        pushTask = new PushTask();

        pushTask.executeOnExecutor(OKNetUtil.exe, pam);

    }

    public OKLoadGanKApi.Params getLastParam() {
        return lastParam;
    }

    public void cancelTask() {
        if (loadGanKTask != null && loadGanKTask.getStatus() == AsyncTask.Status.RUNNING) {
            loadGanKTask.cancel(true);
        }

        if (loadDatabaseTask != null && loadDatabaseTask.getStatus() == AsyncTask.Status.RUNNING) {
            loadDatabaseTask.cancel(true);
        }

        if (pushTask != null && pushTask.getStatus() == AsyncTask.Status.RUNNING) {
            pushTask.cancel(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadGanKTask extends AsyncTask<Params, Void, List<OKGanKResultModel>> {

        @Override
        protected List<OKGanKResultModel> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params pam = params[0];

            if (pam.getPageCount() <= 0) {
                pam.setPageCount(1);
            }

            if (pam.getSize() <= 0) {
                pam.setSize(30);
            }

            if (TextUtils.isEmpty(pam.getType())) {
                pam.setType(Params.TYPE_ALL);

                OKLogUtil.print(TAG, "doInBackground for type is null");
            }

            String size = String.valueOf(pam.getSize());

            String page = String.valueOf(pam.getPageCount());

            String type = pam.getType();

            String url = null;

            String json = null;

            OKGanKModel bean = null;

            if (Params.TYPE_SEARCH.equals(type)) { // 搜索 类型
                url = SEARCH_URL.replace(URL_STENCIL_TYPE, Params.TYPE_ALL)
                        .replace(URL_STENCIL_SIZE, size)
                        .replace(URL_STENCIL_PAGE, page)
                        .replace(URL_STENCIL_SEARCH_VALUE, pam.getValue());

                OKLogUtil.print(TAG, "doInBackground for search api url:" + url);

                json = OKNetUtil.OKHttpApiGet(url);

                bean = OKGsonUtil.fromJson(json, OKGanKModel.class);

                if (bean == null) { // 结果为空
                    return null;
                }

                return bean.getResults();
            } else { // 分类 类型
                url = URL.replace(URL_STENCIL_TYPE, type)
                        .replace(URL_STENCIL_SIZE, size)
                        .replace(URL_STENCIL_PAGE, page);

                OKLogUtil.print(TAG, "doInBackground for category api url:" + url);

                json = OKNetUtil.OKHttpApiGet(url);

                bean = OKGsonUtil.fromJson(json, OKGanKModel.class);

                if (bean == null) { // 结果为空
                    return null;
                }

                return bean.getResults();
            }
        }

        @Override
        protected void onPostExecute(List<OKGanKResultModel> list) {
            super.onPostExecute(list);

            if (isCancelled()) {
                return;
            }

            requestHandle.onLoadComplete(list);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDatabaseTask extends AsyncTask<Params, Void, List<OKGanKResultModel>> {

        @Override
        protected List<OKGanKResultModel> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params pam = params[0];

            if (pam.getPageCount() <= 0) {
                pam.setPageCount(1);
            }

            if (pam.getSize() <= 0) {
                pam.setSize(30);
            }

            long size = pam.getSize();

            long page = pam.getPageCount();

            try {
                List<OKGanKResultModel> list = OKDatabase.getHelper(context).getCardDao().queryBuilder()
                        .orderBy(OKGanKResultModel.KEY_SAVE_TIMES, false)
                        .offset((page * size) - size)
                        .limit(size)
                        .query();

                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, list.get(i).changedImageJson());
                    }
                }

                OKLogUtil.print(TAG, "onInBackground load data base:" + new Gson().toJson(list));

                return list;
            } catch (SQLException e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(List<OKGanKResultModel> list) {
            super.onPostExecute(list);

            if (isCancelled()) {
                return;
            }

            requestHandle.onLoadComplete(list);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PushTask extends AsyncTask<PushParams, Void, OKGanKModel> {

        @Override
        protected OKGanKModel doInBackground(PushParams... params) {
            if (isCancelled()) {
                return null;
            }

            PushParams pam = params[0];

            Map<String, String> map = new HashMap<>();

            map.put(PushParams.KEY_URL, pam.getUrl());
            map.put(PushParams.KEY_DESC, pam.getDesc());
            map.put(PushParams.KEY_WHO, pam.getWho());
            map.put(PushParams.KEY_TYPE, pam.getType());
            map.put(PushParams.KEY_DEBUG, Boolean.toString(pam.isDebug()));

            String json = OKNetUtil.OkHttpApiPost(PUSH_URL, map);

            if (TextUtils.isEmpty(json)) {
                OKLogUtil.print(TAG, "doInBackground in push gank for json null");

                return null;
            }

            return OKGsonUtil.fromJson(json, OKGanKModel.class);
        }

        @Override
        protected void onPostExecute(OKGanKModel model) {
            super.onPostExecute(model);

            if (isCancelled()) {
                return;
            }

            pushHandle.onPush(model);
        }
    }

    public static class Params {
        // 字段名称
        public final static String KEY_SIZE = "size";
        public final static String KEY_PAGE_COUNT = "pageCount";
        public final static String KEY_TYPE = "type";

        // 可用类型
        public final static String TYPE_FL = "福利";
        public final static String TYPE_AN = "Android";
        public final static String TYPE_IS = "iOS";
        public final static String TYPE_VD = "休息视频";
        public final static String TYPE_RS = "拓展资源";
        public final static String TYPE_H5 = "前端";
        public final static String TYPE_APP = "App";
        public final static String TYPE_RECOMMEND = "瞎推荐";
        public final static String TYPE_SEARCH = "SEARCH";
        public final static String TYPE_ALL = "all";

        private int size = 30; // 每页数量
        private int pageCount = 1; // 页数
        private String type = TYPE_FL; // 类型
        private String value = null;

        public int getSize() {
            return size;
        }

        public Params setSize(int size) {
            this.size = size;

            return this;
        }

        public int getPageCount() {
            return pageCount;
        }

        public Params setPageCount(int pageCount) {
            this.pageCount = pageCount;

            return this;
        }

        public String getType() {
            return type;
        }

        public Params setType(String type) {
            this.type = type;

            return this;
        }

        public String getValue() {
            return value;
        }

        public Params setValue(String value) {
            this.value = value;

            return this;
        }
    }

    public static class PushParams {
        private String url = ""; // 每页数量
        private String desc = ""; // 页数
        private String who = ""; // 类型
        private String type = "";
        private boolean debug = false;

        public final static String KEY_URL = "url";
        public final static String KEY_DESC = "desc";
        public final static String KEY_WHO = "who";
        public final static String KEY_TYPE = "type";
        public final static String KEY_DEBUG = "debug";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc.trim();
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who.trim();
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type.trim();
        }

        public boolean isDebug() {
            return debug;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }
    }
}
