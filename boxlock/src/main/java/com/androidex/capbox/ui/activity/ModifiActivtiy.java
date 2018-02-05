package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.androidex.capbox.R;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * @author liyp
 * @editTime 2017/9/29
 */

public class ModifiActivtiy extends RegisterActivity {

    @Override
    public void initData(Bundle savedInstanceState) {
        rl_title.setTitle("修改密码");
        setGone(rl_title.getRightTv());
        initClient();
        getAuthCode();
    }

    @Override
    protected void regist(String phone, String name, String cardId, String password, String authcode) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.forgetPassword(getToken(), phone, CommonKit.getMd5Password(password), name, cardId, authcode, new ResultCallBack<BaseModel>() {

            @Override
            public void onStart() {
                super.onStart();
                showSpinnerDlg(getString(R.string.label_ing));
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                dismissSpinnerDlg();
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CommonKit.showOkShort(context, getString(R.string.hint_find_pwd_ok));
                            CommonKit.finishActivity(context);
                            break;

                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "身份信息不匹配");
                            break;

                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "提交信息失败");
                            break;

                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                dismissSpinnerDlg();
                CommonKit.showErrorShort(context, getString(R.string.hint_find_pwd_fail));
            }
        });
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, ModifiActivtiy.class, null, false);
    }
}
