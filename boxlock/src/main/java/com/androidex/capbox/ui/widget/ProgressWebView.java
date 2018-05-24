package com.androidex.capbox.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.androidex.capbox.R;
import com.androidex.capbox.callback.WebViewCallBack;

/**
 * @author liyp
 * @version 1.0.0
 * @description 有进度的WebView
 * @createTime 2015/11/6
 * @editTime
 * @editor
 */
public class ProgressWebView extends WebView {
    private ProgressBar progressBar;
    private WebViewCallBack webViewCallBack;


    public ProgressWebView(Context context) {
        super(context);
        initView(context);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        progressBar = (ProgressBar) inflate(context, R.layout.view_progress_bar_h_no_corner,null);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 6, 0, 0));
        addView(progressBar);

        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
                    if (webViewCallBack != null) {
                        webViewCallBack.onReceiveTitle(title);
                    }
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(GONE);
                    if (webViewCallBack != null) {
                        webViewCallBack.onLoadFinish();
                    }
                } else {
                    if (progressBar.getVisibility() == View.GONE) {
                        progressBar.setVisibility(VISIBLE);
                    }
                    progressBar.setProgress(newProgress);

                    if (webViewCallBack != null) {
                        webViewCallBack.onProgress(newProgress);
                    }
                }

            }
        });
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setWebViewCallBack(WebViewCallBack webViewCallBack) {
        this.webViewCallBack = webViewCallBack;
    }
}
