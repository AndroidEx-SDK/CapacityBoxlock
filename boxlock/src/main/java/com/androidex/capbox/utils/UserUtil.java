package com.androidex.capbox.utils;

import android.app.Activity;
import android.content.Context;

import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.ui.activity.LoginActivity;

import static com.androidex.capbox.data.cache.SharedPreTool.LOGIN_STATUS;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/8/7
 */
public class UserUtil {

    public static String getUserName(Activity context) {
        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        if (username == null) {
            CommonKit.showErrorShort(context, "账号未登录");
            SharedPreTool.getInstance(context).setBoolData(LOGIN_STATUS, false);
            LoginActivity.lauch(context);
            return "";
        }
        return username;
    }

    public static String getUserName(Context context) {
        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        if (username == null) {
            CommonKit.showErrorShort(context, "账号未登录");
            SharedPreTool.getInstance(context).setBoolData(LOGIN_STATUS, false);
        }
        return username;
    }
}
