package com.androidex.capbox.data.pojo;


import com.androidex.capbox.data.net.base.RequestParams;

/**
 * @author liyp
 * @version 1.0.0
 * @description 文件上传
 * @createTime 2015/12/23
 * @editTime
 * @editor
 */
public class FileUploadItem {
    private String targetUrl;
    private RequestParams params;
    private int tag;

    public FileUploadItem(RequestParams params) {
        this.params = params;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public RequestParams getParams() {
        return params;
    }

    public void setParams(RequestParams params) {
        this.params = params;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
