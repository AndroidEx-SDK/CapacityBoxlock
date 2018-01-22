package com.androidex.capbox.core;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.androidex.capbox.utils.RLog;

/**
 * Created by popfisher on 2016/11/8.
 */

public class KeyguardLockScreenManager {
    public final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;
    private KeyguardManager mKeyManager;

    /**
     * 是否开启锁屏密码
     * @return
     */
    public boolean isOpenLockScreenPwd() {
        try {
            return mKeyManager != null && mKeyManager.isKeyguardSecure();
        } catch (Exception e) {
            return false;
        }
    }

    public KeyguardLockScreenManager(Context context) {
        mKeyManager = getKeyguardManager(context);
    }

    public static android.app.KeyguardManager getKeyguardManager(Context context) {
        KeyguardManager keyguardManager = null;
        try {
            keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        } catch (Throwable throwable) {
            RLog.e("getKeyguardManager exception");
        }
        return keyguardManager;
    }

    /**
     * 锁屏密码
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showAuthenticationScreen(Activity activity) {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("锁屏密码", "测试锁屏密码");
        if (intent != null) {
            activity.startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    public void onDestroy() {
        mKeyManager = null;
    }
}
