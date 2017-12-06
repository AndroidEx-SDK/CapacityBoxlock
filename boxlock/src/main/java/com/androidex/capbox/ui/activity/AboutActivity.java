package com.androidex.capbox.ui.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.CheckVersionModel;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

public class AboutActivity extends BaseActivity {
    public static String TAG = "AboutActivity";
    @Bind(R.id.about_tv_versionNum)
    TextView versionNum;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        versionNum.setText(getResources().getString(R.string.about_tv_versionNum)+ getVersionName(context));
    }

    @OnClick({
            R.id.about_btn_back,
            R.id.ll_version_check,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.about_btn_back:
                CommonKit.finishActivity(this);
                break;
            case R.id.ll_version_check:
                checkVersion();
                break;
        }
    }

    @Override
    public void setListener() {

    }

    //获取版本号
    private String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    //获取版本号
    private int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }


    @Override
    public void onClick(View v) {

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
}
