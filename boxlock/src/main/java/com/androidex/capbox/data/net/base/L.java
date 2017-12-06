package com.androidex.capbox.data.net.base;

import android.util.Log;

/**
 * @author liyp
 * @version 1.0.0
 * @description
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public class L {

    private static final String TAG = "NET_DEBUG";

    public static void i(Object msg) {
        if (RequestClient.MODE_DEBUG) {
            Log.i(TAG, msg.toString());
        }
    }

    public static void e(Object msg) {
        if (RequestClient.MODE_DEBUG) {
            Log.e(TAG, msg.toString());
        }
    }

    public static void e(Throwable throwable) {
        if (RequestClient.MODE_DEBUG) {
            Log.e(TAG, throwable.getMessage());
        }
    }

}
