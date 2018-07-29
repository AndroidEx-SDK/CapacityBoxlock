package com.androidex.capbox.base;

import com.androidex.capbox.R;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.AuthCodeModel;
import com.androidex.capbox.module.LoginModel;
import com.androidex.capbox.ui.activity.LoginActivity;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.RLog;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * @author liyp
 * @version 1.0.0
 * @description 用户模块基类：主要包含登录、注册
 * @createTime 2015/12/28
 * @editTime
 * @editor
 */
public abstract class UserBaseActivity extends BaseActivity {
    protected static String TAG = "UserBaseActivity";

    /**
     * 用户登录（显示spinner）
     *
     * @param userName
     * @param md5Pwd
     * @param callback
     */
    protected void userLogin(final String userName, final String md5Pwd, String authcode, final CallBackAction callback) {
        userLogin(userName, md5Pwd, authcode, false, callback);
    }

    /**
     * 用户登录
     *
     * @param userName
     * @param md5Pwd
     * @param secret   是否secret
     * @param callback
     */
    protected void userLogin(final String userName, final String md5Pwd, String authcode, final boolean secret, final CallBackAction callback) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.userLogin(userName, md5Pwd, authcode, new ResultCallBack<LoginModel>() {

            @Override
            public void onStart() {
                super.onStart();
                showSpinnerDlg(!secret, getString(R.string.label_login_ing));
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, LoginModel model) {
                super.onSuccess(statusCode, headers, model);
                dismissSpinnerDlg(!secret);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            cache2Sp(userName, md5Pwd);
                            if (model.token != null) {
                                setToken(model.token);
                            }
                            callback.action();
                            break;
                        case Constants.API.API_FAIL:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showErrorShort(context, "密码错误");
                            }
                            //LoginActivity.lauch(context);
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "请检查账号和密码是否正确");
                            LoginActivity.lauch(context);
                            break;
                        default:
                            LoginActivity.lauch(context);
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
                dismissSpinnerDlg(!secret);
                CommonKit.showErrorShort(context, getString(R.string.hint_login_fail));
            }
        });
    }

    /**
     * 用户注册
     *
     * @param username 手机号
     * @param name     姓名
     * @param cardId   身份证号
     * @param md5Pwd   密码
     */
    protected void userRegister(final String username, String name, String cardId, final String md5Pwd, String authcode, final CallBackAction callback) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.userRegister(username, name, cardId, md5Pwd, authcode, new ResultCallBack<LoginModel>() {
            @Override
            public void onStart() {
                super.onStart();
                showSpinnerDlg(getString(R.string.label_register_ing));
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, LoginModel model) {
                super.onSuccess(statusCode, headers, model);
                dismissSpinnerDlg();
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CommonKit.showOkShort(context, getString(R.string.hint_reg_ok));
                            cache2Sp(username, CommonKit.getMd5Password(md5Pwd));
                            if (model.token != null) {
                                setToken(model.token);
                                callback.action();
                                L.i("Regist model token=" + model.token);
                            }
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "提交信息失败");
                            break;
                        case Constants.API.API_NOPERMMISION:
                            if (model.info != null) {
                                L.e("Regist is Fail info=" + model.info);
                            }
                            if (model.stacktrace != null) {
                                L.e("Regist is Fail info=" + model.stacktrace);
                            }
                            CommonKit.showErrorShort(context, "该用户已被注册");
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
                L.e("statusCode=" + statusCode);
                CommonKit.showErrorShort(context, getString(R.string.hint_reg_fail));
                L.e(e);
            }
        });
    }

    protected void getAuthCode(final CallDataBackAction callDataBackAction) {
        if (!CommonKit.isNetworkAvailable(context)) {
            callDataBackAction.noInternet();
            return;
        }
        NetApi.getAuthCode("no", "", new ResultCallBack<AuthCodeModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, AuthCodeModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            callDataBackAction.action(model.authcode);
                            RLog.e("获取到验证码authcode=" + model.authcode);
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            callDataBackAction.action(null);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                CommonKit.showErrorShort(context, "网络连接失败");
                callDataBackAction.action(null);
            }
        });
    }

    /**
     * 缓存数据到本地
     *
     * @param phone
     * @param password
     */
    private void cache2Sp(String phone, String password) {
        setUsername(phone);
        SharedPreTool.getInstance(context).setStringData(SharedPreTool.PASSWORD, password);
    }


    /**
     * 回调
     */
    public interface CallBackAction {
        void action();
    }

    /**
     * 回调
     */
    public interface CallDataBackAction {
        void action(String authcode);
        void noInternet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
