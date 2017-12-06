package com.androidex.capbox.data.cache;

import android.content.Context;

/**
 * @author liyp
 * @version 1.0.0
 * @description 缓存管理
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class CacheManage {

    public static FastCache getFastCache() {
        return FastCache.getInstance();
    }

    public static DiskCache getDiskCache(Context context) {
        return DiskCache.getInstance(context);
    }

    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    public static void clearDiskCache(Context context) {
        getDiskCache(context).deleteAll();
    }

    /**
     * 清除内存缓存
     */
    public static void clearFastCache() {
        getFastCache().deleteAll();
    }

    /**
     * 清除内存缓存
     *
     * @param key
     */
    public static void removeFast(String key) {
        getFastCache().remove(key);
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    public static void clearCache(Context context) {
        getDiskCache(context).deleteAll();
        getFastCache().deleteAll();
    }

}
