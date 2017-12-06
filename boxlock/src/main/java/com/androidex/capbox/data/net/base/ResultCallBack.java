package com.androidex.capbox.data.net.base;


import okhttp3.Headers;
import okhttp3.Request;

/**
 * @author liyp
 * @version 1.0.0
 * @description
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public class ResultCallBack<T> {
    public void onStart() {
    }

    public void onFinish() {
    }

    public void onSuccess(int statusCode, Headers headers, T model) {

    }

    public void onFailure(int statusCode, Request request, Exception e) {

    }

    public void onProgress(long bytesWritten, long totalSize) {

    }
}
