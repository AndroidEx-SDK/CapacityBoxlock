package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.callback.WebViewCallBack;
import com.androidex.capbox.ui.widget.ProgressWebView;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;

/**
 * @author liyp
 * @version 1.0.0
 * @description WebView通用
 * @createTime 2015/11/6
 * @editTime
 * @editor
 */
public class WebViewActivity extends BaseActivity {
    @Bind(R.id.rl_title)
    SecondTitleBar rl_title;
    @Bind(R.id.webView)
    ProgressWebView webView;
    String url;
    public static final String PARAM_URL = "param_url";

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(PARAM_URL)) {
                url = getIntent().getExtras().getString(PARAM_URL);
                initWebView(url);
            }
        }
    }

    @Override
    public void setListener() {

    }

    private void initWebView(String url) {
        webView.loadUrl(url);
        webView.setWebViewCallBack(new WebViewCallBack() {
            @Override
            public void onReceiveTitle(String title) {
                super.onReceiveTitle(title);
                rl_title.setTitle(title);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public static void lauch(Activity activity, String url) {
        Bundle param = new Bundle();
        if (!TextUtils.isEmpty(url)) {
            param.putString(PARAM_URL, url);
        }
        CommonKit.startActivity(activity, WebViewActivity.class, param, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }
}
