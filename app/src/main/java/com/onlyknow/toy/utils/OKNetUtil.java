package com.onlyknow.toy.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.URLUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.DOWNLOAD_SERVICE;

public class OKNetUtil {
    private final static String TAG = "OKNetUtil";

    public final static ExecutorService exe = Executors.newFixedThreadPool(100);

    public static String OkHttpApiPost(String url, Map<String, String> params) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody.Builder mBuilder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();

                    if (TextUtils.isEmpty(value)) continue;

                    if (isChinese(value)) {
                        try {
                            value = URLEncoder.encode(value, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            OKLogUtil.print("OKNet 参数编码错误");
                        }
                    }
                    mBuilder.add(entry.getKey(), value);
                }
            }

            RequestBody body = mBuilder.build();
            Request request = new Request.Builder().url(url).post(body).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                return bodyToString(response.body().byteStream());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            OKLogUtil.print(ex.getMessage());
        }
        return null;
    }

    public static String OKHttpApiPostFromData(String url, Map<String, String> params) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody.Builder mBuilder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();

                    if (TextUtils.isEmpty(value)) continue;

                    if (isChinese(value)) {
                        try {
                            value = URLEncoder.encode(value, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            OKLogUtil.print("OKNet 参数编码错误");
                        }
                    }
                    mBuilder.add(entry.getKey(), value);
                }
            }

            RequestBody body = mBuilder.build();
            Request request = new Request.Builder().url(url).addHeader("Content-Type", "multipart/form-data").post(body).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                return bodyToString(response.body().byteStream());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            OKLogUtil.print(ex.getMessage());
        }
        return null;
    }

    public static String OKHttpApiPostFromFile(String url, Map<String, File> files, Map<String, String> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, File> entry : files.entrySet()) {
            builder.addFormDataPart("file", entry.getKey(), RequestBody.create(MediaType.parse("application/octet-stream"), entry.getValue()));
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();

            if (TextUtils.isEmpty(value)) continue;

            if (isChinese(value)) {
                try {
                    value = URLEncoder.encode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    OKLogUtil.print("OKNet 参数编码错误");
                }
            }
            builder.addFormDataPart(entry.getKey(), value);
        }
        MultipartBody multipartBody = builder.build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(multipartBody).build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response mResponse = call.execute();
            if (mResponse.isSuccessful()) {
                return bodyToString(mResponse.body().byteStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String OKHttpApiGet(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return bodyToString(response.body().byteStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bodyToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }

        OKLogUtil.print("WebService return : " + sb.toString());

        return sb.toString();
    }

    public static void downloadForSystem(Context context, String url, String contentDisposition, String mimetype) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();

        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 设置通知栏的标题，如果不设置，默认使用文件名

        //  request.setTitle("This is title");
        // 设置通知栏的描述
        // request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);

        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);

        // 允许漫游时下载
        request.setAllowedOverRoaming(true);

        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        // 设置下载文件保存的路径和文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);

        OKLogUtil.print(TAG, "download file name:" + fileName);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // 另外可选一下方法，自定义下载路径
        // request.setDestinationUri()
        // request.setDestinationInExternalFilesDir()

        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);

        OKLogUtil.print(TAG, "download manager Id:" + downloadId);
    }

    public static boolean isNet(Context context) {
        // 获取手机所以连接管理对象 (包括WIFI，数据等连接的管理)
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn != null) {
            // 网络管理连接对象
            NetworkInfo info = conn.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 判断当前网络是否连接
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    // 完整的判断中文汉字和符号
    private static boolean isChinese(String strName) {
        if (TextUtils.isEmpty(strName)) return false;

        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (isChinese(ch[i])) {
                return true;
            }
        }
        return false;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public static boolean isUrlAgreement(String mUrl) {
        boolean b;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(mUrl.trim());
        b = mat.matches();//判断是否匹配
        boolean b2 = mUrl.startsWith("http://") || mUrl.startsWith("https://") || mUrl.startsWith("file://") || mUrl.startsWith("ftp://");
        return b || b2;
    }

    private OKNetUtil() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    private Handler mDelivery;// 主线程返回
    private Call downCall; // 下载的call
    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mDiskDir = "";
    private String mFileName = "";
    private static OKNetUtil mWebServiceInstance; // 单例

    public static OKNetUtil getInstance() {
        if (mWebServiceInstance == null) {
            mWebServiceInstance = new OKNetUtil();
        }
        return mWebServiceInstance;
    }

    /**
     * 异步下载文件
     *
     * @param url         文件的下载地址
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void okHttpDownload(final String url, final String destFileDir, final ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        downCall = mOkHttpClient.newCall(request);
        downCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(call.request(), e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    double current = 0;
                    double total = response.body().contentLength();

                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);

                        sendProgressCallBack(total, current, callback);

                        OKLogUtil.print("download current------>" + current);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String getFileName(String url) {
        String ss[] = url.split("/");
        return ss[ss.length - 1];
    }

    //下载失败ui线程回调
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    //下载成功ui线程回调
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    //下载回调接口
    public static abstract class ResultCallback<T> {

        //下载错误
        public abstract void onError(Request request, Exception e);

        //下载成功
        public abstract void onResponse(T response);

        //下载进度
        public abstract void onProgress(double total, double current);
    }

    /**
     * 进度信息ui线程回调
     *
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     * @param <T>
     */
    private <T> void sendProgressCallBack(final double total, final double current, final ResultCallback<T> callBack) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url      文件链接
     * @param destDir  下载保存地址
     * @param callback 回调
     */
    public void downloadFile(String url, String destDir, ResultCallback callback) {
        mDiskDir = destDir;
        mFileName = getFileName(url);
        okHttpDownload(url, destDir, callback);
    }

    /**
     * 取消下载
     */
    public void cancelDown() {
        if (downCall != null) {
            downCall.cancel();
            File file = new File(mDiskDir, mFileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
