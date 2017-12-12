package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.capbox.MainActivity;
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
import com.dou361.update.UpdateHelper;
import com.dou361.update.listener.ForceListener;

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
     */
    public void checkVersion() {
        NetApi.checkVersion(getToken(), MainActivity.username, new ResultCallBack<CheckVersionModel>() {
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
                                String requestStri = "{\"code\":0," +
                                        "\"data\":" +
                                        "{\"download_url\":" +NetApi.getAppUpadeUrl(model.appFileName)+
                                        "\"," +
                                        "\"force\":false," +
                                        "\"update_content\":\"测试更新接口\"," +
                                        "\"v_code\":\"10\",\"v_name\":\"v1.0.0.16070810\"," +
                                        "\"v_sha1\":\"7db76e18ac92bb29ff0ef012abfe178a78477534\"," +
                                        "\"v_size\":12365909}}";

                                networkAutoUpdate(requestStri);
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
     * 分离网络的自动检测更新
     */
    private void networkAutoUpdate(String data) {
        UpdateHelper.getInstance()
                .setRequestResultData(data)
                .setForceListener(new ForceListener() {
                    @Override
                    public void onUserCancel(boolean force) {
                        if (force) {
                            //退出应用
                            finish();
                        }
                    }
                })
                .checkNoUrl(context);
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
