
package com.hornettao.mychat.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.utils.L;

import cn.bmob.v3.BmobUser;

public class FriendCircleActivity extends Base2Activity {

    private RelativeLayout layout_all;
    private WebView webView;
    private String url = "http://10.8.93.230/present/newfreshnow.html";
    private String url_publish = "http://10.8.93.230/present/publish.html";

    public ValueCallback<Uri> mUploadMessage;
    public final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_circle);
        showBackAndHidelogo();
        setUpView();
    }

    private void setUpView() {
        webView = (WebView) this.findViewById(R.id.web_view);
        layout_all = (RelativeLayout) this.findViewById(R.id.layout_all);
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
        webView.setWebChromeClient(new WebChromClientImpl());
    }

    private class WebChromClientImpl extends WebChromeClient {
        //扩展支持alert事件
        @Override
        public boolean onJsAlert(WebView view, String url, String message,JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("商机通提示").setMessage(message).setPositiveButton("确定", null);
            builder.setCancelable(false);
            builder.setIcon(R.mipmap.ic_launcher);
            AlertDialog dialog = builder.create();
            dialog.show();
            result.confirm();
            return true;
        }
        //扩展浏览器上传文件
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            FriendCircleActivity.this.startActivityForResult(
                    Intent.createChooser(intent, "完成操作需要使用"),
                    FriendCircleActivity.FILECHOOSER_RESULTCODE);

        }
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
                showAvatarPop();
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
    public String getCurrentUserId() {
        return BmobUser.getCurrentUser(this, User.class).getObjectId();
    }

    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;

    public String filePath = "";

    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator1,
                null);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatorPop.dismiss();
            }
        });
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);

        avatorPop = new PopupWindow(view, mScreenWidth, 600);
        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
        layout_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(url_publish);
                avatorPop.dismiss();
            }
        });
        layout_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
    * 返回文件选择
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if(requestCode==FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
            L.e("hi" + result);
        }
    }
}
