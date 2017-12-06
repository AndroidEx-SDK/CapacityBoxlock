package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.utils.CommonKit;

import static com.androidex.capbox.ui.activity.LoginActivity.callBackAction;

public class WelcomeActivity extends UserBaseActivity {

    @Override
    public void initData(Bundle savedInstanceState) {
        //在manifest里面配置：直接进入全屏
        //<activity android:theme="@android:style/Theme.NoTitleBar.Fullscreen" /> 只在当前Activity内显示全屏
        // <application android:theme="@android:style/Theme.NoTitleBar.Fullscreen" /> 为整个应用配置全屏显示
        String phone = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        String md5Pwd = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PASSWORD, null);
        if (phone != null && md5Pwd != null) {
            automaticLogin(phone, md5Pwd);//自动登录
        } else {
            LoginActivity.lauch(context);
        }
    }

    private void automaticLogin(final String phone, final String md5Pwd) {
        boolean boolData = SharedPreTool.getInstance(context).getBoolData(SharedPreTool.AUTOMATIC_LOGIN, false);
        if (boolData) {
            getAuthCode(new CallDataBackAction() {
                @Override
                public void action(String authcode) {
                    if (authcode != null) {
                        userLogin(phone, md5Pwd, authcode, new CallBackAction() {
                            @Override
                            public void action() {
                                MainActivity.lauch(context);
                                if (callBackAction != null) {
                                    callBackAction.action();
                                    callBackAction = null;
                                }
                            }
                        });
                    } else {
                        CommonKit.showErrorShort(context, "自动登录失败");
                        LoginActivity.lauch(context);
                    }
                }
            });
        } else {
            LoginActivity.lauch(context);
        }
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, WelcomeActivity.class, null, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }
}
