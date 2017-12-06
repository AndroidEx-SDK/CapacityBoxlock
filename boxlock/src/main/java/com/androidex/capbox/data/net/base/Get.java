package com.androidex.capbox.data.net.base;

import android.text.TextUtils;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author liyp
 * @version 1.0.0
 * @description
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public class Get extends OkRequest {

    protected Get(String url, String tag, Headers headers, RequestParams params) {
        super(url, tag, headers, params);
    }

    @Override
    protected Request buildRequest() {
        if (TextUtils.isEmpty(url)) {
            L.e("url can not be empty!");
        }
        return new Request.Builder()
                .url(params.getUrl(url))
                .headers(headers)
                .tag(tag)
                .build();
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }
}
