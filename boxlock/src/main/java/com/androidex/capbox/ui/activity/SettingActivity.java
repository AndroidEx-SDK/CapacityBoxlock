package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.module.CheckVersionModel;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

public class SettingActivity extends UserBaseActivity {
    @Bind(R.id.ll_notification)
    LinearLayout ll_notification;
    @Bind(R.id.ll_changePassword)
    LinearLayout ll_changePassword;
    @Bind(R.id.ll_clearCache)
    LinearLayout ll_about;
    @Bind(R.id.ll_about)
    LinearLayout ll_clearCache;
    @Bind(R.id.ll_searchVersion)
    LinearLayout ll_searchVersion;
    @Bind(R.id.tv_versionNum)
    TextView tv_versionNum;

    @Override
    public void initData(Bundle savedInstanceState) {
        tv_versionNum.setText(getResources().getString(R.string.about_tv_versionNum) + CommonKit.getVersionName(context));
    }

    @Override
    public void setListener() {

    }

    @OnClick({
            R.id.tv_logout,
            R.id.tv_logoff,
            R.id.ll_about,
            R.id.ll_searchVersion,
    })
    public void clickEvent(View view) {
        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        switch (view.getId()) {
            case R.id.ll_about:
                AboutActivity.lauch(context);
                break;
            case R.id.tv_logout:
                if (username != null) {
                    if (!CommonKit.isNetworkAvailable(context)) {
                        CommonKit.showErrorShort(context, "设备未连接网络");
                        return;
                    }
                    NetApi.userLogout(getToken(), username, new ResultCallBack<BaseModel>() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                            super.onSuccess(statusCode, headers, model);
                            if (model != null) {
                                switch (model.code) {
                                    case Constants.API.API_OK:
                                        CommonKit.showOkShort(context, getString(R.string.hint_logout_ok));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Request request, Exception e) {
                            super.onFailure(statusCode, request, e);
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                        }
                    });
                }
                removeCacheForSp();//删除缓存
                postSticky(new Event.UserLoginEvent());//登录状态发生改变
                LoginActivity.lauch(context);
                break;
            case R.id.tv_logoff:
                if (username != null) {
                    if (!CommonKit.isNetworkAvailable(context)) {
                        CommonKit.showErrorShort(context, "设备未连接网络");
                        return;
                    }
                    NetApi.userLogoff(getToken(), username, new ResultCallBack<BaseModel>() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                            super.onSuccess(statusCode, headers, model);
                            if (model != null) {
                                switch (model.code) {
                                    case Constants.API.API_OK:
                                        CommonKit.showOkShort(context, getString(R.string.hint_logoff_ok));
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Request request, Exception e) {
                            super.onFailure(statusCode, request, e);
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                        }
                    });
                }
                removeCacheForSp();//删除缓存
                postSticky(new Event.UserLoginEvent());//登录状态发生改变
                LoginActivity.lauch(context);
                break;

            case R.id.ll_searchVersion:
                checkVersion();
                break;
            default:
                break;
        }
    }

    /**
     * 检测版本号，包括APP的，箱体的，腕表的
     * {"appFileName":"boxlock-3.apk",
     * "appVersion":"3","boxFileName":"20171129.hex",
     * "boxVersion":"0.0.1","code":0,"watchFileName":"20171129.hex",
     * "watchVersion":"0.0.2"}

     */
    public void checkVersion() {
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
                            Log.d(TAG, model.toString());
                            if (model.appVersion > CommonKit.getAppVersionCode(context)) {
                                Log.d(TAG, "发现新版本");
                                downloadAppApk(model.appFileName);
                            } else {
                                CommonKit.showOkShort(context, "已经是最新版本");
                            }
                            break;
                        case Constants.API.API_FAIL:
                            Log.d(TAG, "网络连接失败");
                            CommonKit.showErrorShort(context, "网络连接失败");
                            break;
                        default:
                            if (model.info != null) {
                                Log.d(TAG, model.info);
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                Log.d(TAG, "网络连接失败");
                                CommonKit.showErrorShort(context, "网络连接失败");
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                Log.d(TAG, "网络连接失败");
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
                Log.e(TAG, "开始下载新版本");
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, Object model) {
                super.onSuccess(statusCode, headers, model);
                Log.e(TAG, "下载完成");
                CommonKit.installNormal(context, SDCard + "/" + appFireName);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                Log.e(TAG, "bytesWritten=" + bytesWritten + "\ntotalSize=" + totalSize);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                Log.e(TAG, "下载失败" + e.getMessage());
            }
        });
    }

    /**
     * 删除缓存本地的账号和密码
     */
    private void removeCacheForSp() {
        SharedPreTool.getInstance(context).remove(SharedPreTool.TOKEN);
        SharedPreTool.getInstance(context).remove(SharedPreTool.PHONE);
        SharedPreTool.getInstance(context).remove(SharedPreTool.PASSWORD);
        SharedPreTool.getInstance(context).remove(SharedPreTool.AUTOMATIC_LOGIN);
        SharedPreTool.getInstance(context).remove(SharedPreTool.IS_REGISTED);
    }

    @Override
    public void onClick(View v) {

    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, SettingActivity.class, null, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

}
