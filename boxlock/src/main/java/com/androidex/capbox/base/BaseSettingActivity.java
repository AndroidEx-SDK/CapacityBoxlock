package com.androidex.capbox.base;

import android.os.Environment;
import android.util.Log;

import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.RLog;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * @title 设置界面
 */
public abstract class BaseSettingActivity extends UserBaseActivity {


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
