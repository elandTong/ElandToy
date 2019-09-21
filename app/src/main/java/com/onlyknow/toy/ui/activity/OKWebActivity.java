package com.onlyknow.toy.ui.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.onlyknow.toy.OKConstant;
import com.onlyknow.toy.R;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.utils.OKLogUtil;
import com.onlyknow.toy.utils.OKNetUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OKWebActivity extends OKBaseActivity {
    private final String TAG = "OKWebActivity";

    @Bind(R.id.ok_activity_web_toolbar)
    Toolbar toolbar;

    @Bind(R.id.ok_activity_web_progressBar)
    ProgressBar progressBar;

    @Bind(R.id.ok_activity_webview)
    WebView webview;

    private DownloadBroadcastReceived downloadBroadcastReceived = null;

    public final static String WEB_LINK = "WEB_LINK";
    public final static String WEB_TITLE = "WEB_TITLE";

    private String initUrl = "";
    private String initTitle = "";

    private final String SEARCH_STENCIL_CONTENT = "{[content]}";
    private final String searchUrl = "https://www.google.com/search?q=" + SEARCH_STENCIL_CONTENT;

    private void init() {
        if (!OKNetUtil.isUrlAgreement(initUrl)) {
            initUrl = searchUrl.replace(SEARCH_STENCIL_CONTENT, initUrl);
        }

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webview, true);
        } else {
            cookieManager.setAcceptCookie(true);
        }

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String title = view.getTitle();

                if (!TextUtils.isEmpty(title) && toolbar != null) {
                    toolbar.setTitle(title);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setProgress(newProgress);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }

                super.onProgressChanged(view, newProgress);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
                request.grant(request.getResources());
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }
        });

        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                OKNetUtil.downloadForSystem(OKWebActivity.this, url, contentDisposition, mimetype); // 系统方式下载

                // 使用
                downloadBroadcastReceived = new DownloadBroadcastReceived();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(downloadBroadcastReceived, intentFilter);

                showSnackBar(webview, getString(R.string.act_web_download_tip), null);
            }
        });

        webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webview.setBackgroundColor(getResources().getColor(R.color.md_white_1000)); // 设置背景色

        webview.loadUrl(initUrl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ok_activity_web);

        ButterKnife.bind(this);

        initStatusBar();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUrl = intentBundle.getString(WEB_LINK, OKConstant.ONLY_KNOW_OFFICIAL_WEBSITE_URL);

        initTitle = intentBundle.getString(WEB_TITLE, initUrl);

        setStatusBar(colorInTheme);

        progressBar.setVisibility(View.GONE);

        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toolbar != null) {
            toolbar.setBackgroundColor(colorInTheme);

            toolbar.setTitle(initTitle);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        webview.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (downloadBroadcastReceived != null) {
            unregisterReceiver(downloadBroadcastReceived);
            downloadBroadcastReceived = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();

                break;
            case R.id.ok_menu_web_CopyLink: // 复制
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                cm.setText(initUrl);

                Toast.makeText(this, "链接已复制", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ok_menu_web_BrowserOpen: // 外部浏览器打开
                Intent intent = new Intent();

                intent.setAction("android.intent.action.VIEW");

                Uri content_url = Uri.parse(initUrl);

                intent.setData(content_url);

                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private class DownloadBroadcastReceived extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                showSnackBar(webview, getString(R.string.act_web_download_end_tip), null);

                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                OKLogUtil.print(TAG, "downloadId:" + downloadId);

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

                String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);

                OKLogUtil.print(TAG, "getMimeTypeForDownloadedFile:" + type);

                if (TextUtils.isEmpty(type)) {
                    type = "*/*";
                }

                Uri uri = downloadManager.getUriForDownloadedFile(downloadId);

                OKLogUtil.print(TAG, "UriForDownloadedFile:" + uri.toString());

                if (uri != null) {
                    Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                    handlerIntent.setDataAndType(uri, type);
                    context.startActivity(handlerIntent);
                }

                if (downloadBroadcastReceived != null) {
                    unregisterReceiver(downloadBroadcastReceived);
                    downloadBroadcastReceived = null;
                }
            }
        }
    }
}
