package com.androidex.capbox.data.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.androidex.capbox.utils.Constants;


/**
 * @author liyp
 * @version 1.0.0
 * @description SharedPre工具类
 * @createTime 2015/9/19
 * @editTime
 * @editor
 */
public class SharedPreTool {
    private static SharedPreTool instance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String AUTOMATIC_LOGIN = "automatic_login";//自动登录
    public static final String TOKEN = "token";
    public static final String NEED_GUIDE = "need_guide"; //是否需要引导
    public static final String IS_REGISTED = "isregist"; //是否提交审核，提交后，遭到拒绝与未提交一样
    public static final String DEFAULT_MAC = "default_mac"; //默认设备的mac

    public static final String LOWEST_TEMP = "lowest_temp"; //最低温度
    public static final String HIGHEST_TEMP = "highest_temp"; //最高温度



    private SharedPreTool(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SP.SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreTool getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPreTool.class) {
                if (instance == null) {
                    instance = new SharedPreTool(context);
                }
            }
        }
        return instance;
    }

    public int getIntData(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLongData(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public String getStringData(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public boolean getBoolData(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public void setIntData(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void setLongData(String key, Long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void setStringData(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setBoolData(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 保存对象
     *
     * @param obj
     */
    public void saveObj(Object obj) {
        saveObj(obj, null);
    }


    /**
     * 保存对象
     *
     * @param obj
     * @param tag 对象标识
     */
    public void saveObj(Object obj, String tag) {
        try {
            String key = getKey(obj.getClass(), tag);
            editor.putString(key, JSON.toJSONString(obj));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取保存的对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getObj(Class clazz) {
        return getObj(clazz, null);
    }

    /**
     * 获取保存的对象
     *
     * @param clazz
     * @param tag   对象标识
     * @param <T>
     * @return
     */
    public <T> T getObj(Class clazz, String tag) {
        try {
            String key = getKey(clazz, tag);
            String value = sharedPreferences.getString(key, null);
            return (T) JSON.parseObject(value, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 清除对象
     *
     * @param clazz
     */
    public void clearObj(Class clazz) {
        clearObj(clazz, null);
    }

    /**
     * 清除对象
     *
     * @param clazz
     * @param tag
     */
    public void clearObj(Class clazz, String tag) {
        String key = getKey(clazz, tag);
        editor.remove(key);
        editor.commit();
    }


    private static String getKey(Class clazz, String tag) {
        StringBuilder builder = new StringBuilder(Constants.SP.SP_OBJ_PREFIX);

        String formatClassName = clazz.getName().replaceAll("\\.", "_");
        builder.append(formatClassName);

        if (!TextUtils.isEmpty(tag)) {
            builder.append("_").append(tag);
        }

        return builder.toString();
    }
}
