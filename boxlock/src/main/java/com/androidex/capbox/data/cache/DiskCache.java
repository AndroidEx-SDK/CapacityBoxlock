package com.androidex.capbox.data.cache;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liyp
 * @version 1.0.0
 * @description 磁盘缓存
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class DiskCache {
    private DiskLruCache diskCache;
    private static DiskCache instance;
    private Pattern compile;

    static String TAG_CACHE = "=====createTime{createTime}expireTime{expireTime}";
    static String REGEX = "=====createTime\\{(\\d{1,})\\}expireTime\\{(\\d{1,})\\}";

    private DiskCache(Context context) {
        compile = Pattern.compile(REGEX);
        try {
            File cacheDir = CommonKit.getDiskCacheDir(context, Constants.CONFIG.API_CACHE_DIR);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            diskCache = DiskLruCache.open(cacheDir, CommonKit.getAppVersionCode(context), 1, 10 * 1024 * 1024);        //10M
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskCache getInstance(Context context) {
        if (instance == null) {
            synchronized (DiskCache.class) {
                if (instance == null) {
                    instance = new DiskCache(context);
                }
            }
        }
        return instance;
    }


    public synchronized void put(String key, Object obj, long expireMills) {
        if (obj == null) return;

        put(key, JSON.toJSONString(obj), expireMills);
    }

    public synchronized void put(String key, String value, long expireMills) {
        if (TextUtils.isEmpty(key)|| TextUtils.isEmpty(value)) return;

        String name = getMd5Key(key);
        try {
            if (!TextUtils.isEmpty(get(name))){     //如果存在，先删除
                diskCache.remove(name);
            }

            DiskLruCache.Editor editor = diskCache.edit(name);
            StringBuilder content = new StringBuilder(value);
            content.append(TAG_CACHE.replace("createTime", "" + Calendar.getInstance().getTimeInMillis()).replace("expireTime", "" + expireMills));
            editor.set(0, content.toString());
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized <T> T get(String key, Class<T> clazz) {
        try {
            String content = get(key);
            if (!TextUtils.isEmpty(content)) {
                return JSON.parseObject(content, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized <T> List<T> getArray(String key, Class<T> clazz) {
        try {
            String content = get(key);
            if (!TextUtils.isEmpty(content)) {
                return JSON.parseArray(content, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized String get(String key) {
        try {
            String md5Key = getMd5Key(key);
            DiskLruCache.Snapshot snapshot = diskCache.get(md5Key);
            if (snapshot != null) {
                String content = snapshot.getString(0);

                if (!TextUtils.isEmpty(content)) {
                    Matcher matcher = compile.matcher(content);
                    long createTime = 0;
                    long expireTime = 0;
                    while (matcher.find()) {
                        createTime = Long.parseLong(matcher.group(1));
                        expireTime = Long.parseLong(matcher.group(2));
                    }

                    if (createTime + expireTime < Calendar.getInstance().getTimeInMillis()) {
                        int index = content.indexOf("=====createTime");
                        return content.substring(0, index);
                    } else {
                        //过期
                        diskCache.remove(md5Key);       //删除
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void deleteAll() {
        try {
            diskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取MD5键名
     *
     * @param key
     * @return
     */
    public static String getMd5Key(String key) {
        return CommonKit.md5Encoder(key);
    }
}
