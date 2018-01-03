package com.androidex.capbox.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;

public class WelcomeActivity extends UserBaseActivity {

    @Override
    public void initData(Bundle savedInstanceState) {
        //在manifest里面配置：直接进入全屏
        //<activity android:theme="@android:style/Theme.NoTitleBar.Fullscreen" /> 只在当前Activity内显示全屏
        // <application android:theme="@android:style/Theme.NoTitleBar.Fullscreen" /> 为整个应用配置全屏显示
        try {
            Thread.sleep(2000);
            FingerprintMainActivity.lauch(context);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }
}
