package com.androidex.capbox.data.cache;

import android.support.v4.util.LruCache;

/**
 * @author liyp
 * @version 1.0.0
 * @description 快速缓存（内存）
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class FastCache {

    private LruCache<String, Object> memoryCache;

    private static FastCache instance;

    private FastCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Object>(cacheSize);
    }

    public static FastCache getInstance() {
        if (instance == null) {
            synchronized (FastCache.class) {
                if (instance == null) {
                    instance = new FastCache();
                }
            }
        }
        return instance;
    }

    public synchronized void put(String key, Object value) {
        if (memoryCache.get(key) != null) {
            memoryCache.remove(key);
        }
        memoryCache.put(key, value);
    }


    public synchronized void remove(String key) {
        if (memoryCache.get(key) != null) {
            memoryCache.remove(key);
        }
    }

    public synchronized Object get(String key) {
        return memoryCache.get(key);
    }

    public synchronized <T> T get(String key, Class<T> clazz) {
        try {
            return (T) memoryCache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized boolean contains(String key) {
        return memoryCache.get(key) != null;
    }

    public synchronized void deleteAll() {
        memoryCache.evictAll();
    }

}
