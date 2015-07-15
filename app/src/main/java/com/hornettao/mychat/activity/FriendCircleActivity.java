package com.hornettao.mychat.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.User;

import cn.bmob.v3.BmobUser;

public class FriendCircleActivity extends Base2Activity {

    private WebView webView;
    private String url = "http://192.168.0.68/css/newfresh.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_circle);
        showBackAndHidelogo();
        setUpView();
    }

    private void setUpView() {
        webView = (WebView) this.findViewById(R.id.web_view);
        getCustomWebView();
        webView.loadUrl(url);
    }

    private void getCustomWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
        }
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setAllowFileAccess(true);// 设置允许访问文件数据
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabaseEnabled(true);
        webSettings.setUserAgentString(getDefaultUserAgent());
        webView.addJavascriptInterface(this, "mychat");
        webView.setWebChromeClient(new WebChromeClient());
    }

    public static String getDefaultUserAgent() {
        return System.getProperty("http.agent")
                + " AppleWebKit/537.36 (KHTML, like Gecko)"
                + String.format(" %s/%s (Linux; Android %s; Mobile; %s Build/%s)",
                "Mychat", 1.0, Build.VERSION.RELEASE,
                Build.MANUFACTURER, Build.ID);
    }

    @Override
    public void onBackPressed() {
        if (!onNavigateBack()) {
            super.onBackPressed();
        }
    }

    private boolean onNavigateBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_circle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_post:
                webView.loadUrl("javascript:showMessage()");
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @JavascriptInterface
    public void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

    @JavascriptInterface
    public User getCurrentUser() {
        return BmobUser.getCurrentUser(this, User.class);
    }
}
