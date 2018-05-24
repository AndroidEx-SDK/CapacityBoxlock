package com.androidex.capbox.callback;

/**
 * @author liyp
 * @version 1.0.0
 * @description WebView回调
 * @createTime 2015/11/6
 * @editTime
 * @editor
 */
public abstract class WebViewCallBack {
    /**
     * 页面加载完成
     */
    public void onLoadFinish() {
    }

    /**
     * 页面加载进度
     *
     * @param progress
     */
    public void onProgress(int progress) {
    }

    /**
     * 获取到页面标题
     *
     * @param title
     */
    public void onReceiveTitle(String title) {
    }
}
