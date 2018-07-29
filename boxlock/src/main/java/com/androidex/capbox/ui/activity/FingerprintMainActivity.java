package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;

import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.core.FingerprintCore;
import com.androidex.capbox.core.FingerprintUtil;
import com.androidex.capbox.core.KeyguardLockScreenManager;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.RLog;

import butterknife.Bind;

import static com.androidex.capbox.ui.activity.LoginActivity.callBackAction;

/**
 * 调用手机指纹验证，验证成功后即可开启APP
 */
public class FingerprintMainActivity extends UserBaseActivity {
    private FingerprintCore mFingerprintCore;
    private KeyguardLockScreenManager mKeyguardLockScreenManager;
    private TextView mFingerGuideTxt;

    @Bind(R.id.tv_usePassword)
    TextView tv_usePassword;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initData(Bundle savedInstanceState) {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            CommonKit.finishActivity(this);
            return;
        }
        initViews();
        initFingerprintCore();
        startFingerprintRecognition();
    }

    @Override
    public void setListener() {
        tv_usePassword.setOnClickListener(this);
    }

    /**
     * 初始化指纹
     */
    private void initFingerprintCore() {
        mFingerprintCore = new FingerprintCore(this);
        mFingerprintCore.setFingerprintManager(mResultListener);
        mKeyguardLockScreenManager = new KeyguardLockScreenManager(this);
    }

    private void initViews() {
        mFingerGuideTxt = (TextView) findViewById(R.id.fingerprint_guide_tip);
    }

    /**
     * 取消指纹识别
     */
    private void cancelFingerprintRecognition() {
        if (mFingerprintCore.isAuthenticating()) {
            mFingerprintCore.cancelAuthenticate();
            resetGuideViewState();
        }
    }

    /**
     * 开始锁屏密码
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startFingerprintRecognitionUnlockScreen() {
        if (mKeyguardLockScreenManager != null) {
            //判断是否设置锁屏密码，没有设置直接进入登陆界面
            if (!mKeyguardLockScreenManager.isOpenLockScreenPwd()) {
                CommonKit.showMsgShort(context, getString(R.string.fingerprint_not_set_unlock_screen_pws));
                Loge("无锁屏密码");
                LoginActivity.lauch(context);
            } else {
                mKeyguardLockScreenManager.showAuthenticationScreen(this);
            }
        }
    }

    /**
     * 开始指纹识别
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startFingerprintRecognition() {
        if (mFingerprintCore.isSupport()) {
            if (!mFingerprintCore.isHasEnrolledFingerprints()) {//未录入
                CommonKit.showMsgShort(context, getString(R.string.fingerprint_recognition_not_enrolled));
                //FingerprintUtil.openFingerPrintSettingPage(this);//自动跳转到指纹录入界面，在MI部分老款手机不支持指纹的也会跳转，故屏蔽。
            } else {
                mFingerGuideTxt.setText(R.string.fingerprint_recognition_start);
                if (!mFingerprintCore.isAuthenticating()) {
                    mFingerprintCore.startAuthenticate();
                } else {
                    CommonKit.showMsgShort(context, getString(R.string.fingerprint_recognition_authenticating));
                }
            }
        } else {//不支持指纹
            mFingerGuideTxt.setText(R.string.fingerprint_recognition_not_support);
            //startFingerprintRecognitionUnlockScreen();//使用锁屏密码
        }
    }

    private void resetGuideViewState() {
        mFingerGuideTxt.setText(R.string.fingerprint_recognition_start);
    }

    int error = 0;

    private FingerprintCore.IFingerprintResultListener mResultListener = new FingerprintCore.IFingerprintResultListener() {
        @Override
        public void onAuthenticateSuccess() {
            CommonKit.showMsgShort(context, getString(R.string.fingerprint_recognition_success));
            autoLogin();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onAuthenticateFailed(int helpId) {
            CommonKit.showMsgShort(context, getString(R.string.fingerprint_recognition_failed));
            mFingerGuideTxt.setText(R.string.fingerprint_recognition_failed);
            error++;
            if (error == 3) {
                startFingerprintRecognitionUnlockScreen();
                error = 0;
            }
        }

        @Override
        public void onAuthenticateError(int errMsgId) {
            resetGuideViewState();
            CommonKit.showMsgShort(context, getString(R.string.fingerprint_recognition_error));
        }

        @Override
        public void onStartAuthenticateResult(boolean isSuccess) {

        }
    };

    /**
     * 自动登录
     */
    private void autoLogin() {
        boolean boolData = SharedPreTool.getInstance(context).getBoolData(SharedPreTool.AUTOMATIC_LOGIN, false);
        if (boolData) {
            RLog.e("isaulogin=" + boolData);
            String phone = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
            String md5Pwd = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PASSWORD, null);
            if (phone != null && md5Pwd != null) {
                RLog.e("自动登录");
                automaticLogin(phone, md5Pwd);//自动登录
            } else {
                LoginActivity.lauch(context);
            }
        } else {
            LoginActivity.lauch(context);
        }
    }

    /**
     * 自动登录
     *
     * @param phone
     * @param md5Pwd
     */
    private void automaticLogin(final String phone, final String md5Pwd) {
        getAuthCode(new UserBaseActivity.CallDataBackAction() {
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

            @Override
            public void noInternet() {
                CommonKit.showErrorShort(context, "设备未连接网络");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KeyguardLockScreenManager.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                CommonKit.showMsgShort(context, getString(R.string.sys_pwd_recognition_success));
                autoLogin();
            } else {
                CommonKit.showMsgShort(context, getString(R.string.sys_pwd_recognition_failed));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFingerprintCore != null) {
            mFingerprintCore.onDestroy();
            mFingerprintCore = null;
        }
        if (mKeyguardLockScreenManager != null) {
            mKeyguardLockScreenManager.onDestroy();
            mKeyguardLockScreenManager = null;
        }
        mResultListener = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_usePassword:
                startFingerprintRecognitionUnlockScreen();
                break;
        }
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, FingerprintMainActivity.class, null, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fingerprint_main;
    }
}
