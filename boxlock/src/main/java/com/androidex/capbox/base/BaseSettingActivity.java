package com.androidex.capbox.base;

import android.os.Environment;
import android.util.Log;

import com.androidex.capbox.callback.NetSucceedCallBack;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.CheckVersionModel;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.RLog;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * @title 设置界面
 */
public abstract class BaseSettingActivity extends UserBaseActivity {
    /**
     * 检测版本号，包括APP的，箱体的，腕表的
     * {"appFileName":"boxlock-3.apk",
     * "appVersion":"3","boxFileName":"20171129.hex",
     * "boxVersion":"0.0.1","code":0,"watchFileName":"20171129.hex",
     * "watchVersion":"0.0.2"}
     */
    public void checkVersion(final NetSucceedCallBack callBack) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.checkVersion(getToken(), getUserName(), new ResultCallBack<CheckVersionModel>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, CheckVersionModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            RLog.d(model.toString());
                            callBack.onSuccess(model);
                            break;
                        case Constants.API.API_FAIL:
                            RLog.d("网络连接失败");
                            CommonKit.showErrorShort(context, "网络连接失败");
                            break;
                        default:
                            if (model.info != null) {
                                RLog.d(model.info);
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                RLog.d("网络连接失败");
                                CommonKit.showErrorShort(context, "网络连接失败");
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                RLog.d("网络连接失败");
                CommonKit.showErrorShort(context, "网络连接失败");
            }
        });
    }

    /**
     * 下载Apk
     *
     * @param appFireName
     */
    public void downloadAppApk(final String appFireName) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        final String SDCard = Environment.getExternalStorageDirectory() + "/androidex";
        Log.v("downloadFile", "File path: " + SDCard);
        NetApi.downloadAppApk(getToken(), SDCard, appFireName, new ResultCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                RLog.d("开始下载新版本");
                CommonKit.showOkShort(context, "开始下载新版本");
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, Object model) {
                super.onSuccess(statusCode, headers, model);
                RLog.d("下载完成");
                CommonKit.installNormal(context, SDCard + "/" + appFireName);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                RLog.d("bytesWritten=" + bytesWritten + "\ntotalSize=" + totalSize);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                RLog.d("下载失败" + e.getMessage());
            }
        });
    }

    /**
     * 下载箱体的下载文件
     *
     * @param boxFileName
     */
    public void downloadBoxHex(final String boxFileName) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        final String SDCard = Environment.getExternalStorageDirectory() + "/androidex";
        Log.v("downloadFile", "File path: " + SDCard);
        NetApi.downloadBoxHex(getToken(), SDCard, boxFileName, new ResultCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                RLog.d("开始下载新版本");
                CommonKit.showOkShort(context, "开始下载新版本");
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, Object model) {
                super.onSuccess(statusCode, headers, model);
                RLog.d("下载完成");
                CommonKit.installNormal(context, SDCard + "/" + boxFileName);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                RLog.d("bytesWritten=" + bytesWritten + "\ntotalSize=" + totalSize);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                RLog.d("下载失败" + e.getMessage());
            }
        });
    }
}
