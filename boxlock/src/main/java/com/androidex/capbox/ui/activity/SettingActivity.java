package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
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

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void setListener() {

    }

    @OnClick({
            R.id.tv_logout,
            R.id.tv_logoff,
    })
    public void clickEvent(View view) {
        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        switch (view.getId()) {
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
            default:
                break;
        }
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
