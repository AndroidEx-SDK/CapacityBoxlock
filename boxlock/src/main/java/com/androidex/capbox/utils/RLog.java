package com.androidex.capbox.utils;

import android.util.Log;


/**
 * Created by 77423 on 2016/11/7.
 */

public class RLog {
    private final static String TAG = "liyp_";

    public static void i(String message) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, message);
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, message);
    }

    public static void w(String message) {
        if (BuildConfig.DEBUG)
            Log.w(TAG, message);
    }

    public static void e(String message) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, message);
    }

    public static void v(String message) {
        if (BuildConfig.DEBUG)
            Log.v(TAG, message);
    }
}
