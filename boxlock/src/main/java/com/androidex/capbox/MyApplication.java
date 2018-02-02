package com.androidex.capbox;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.androidex.capbox.base.imageloader.UILKit;
import com.androidex.capbox.service.MyBleService;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.e.ble.BLESdk;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    protected static Context mContext;
    AssetManager mgr;
    Typeface tf;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
        /************初始化ImageLoader*******************/
        UILKit.init(getApplicationContext());
        /**********设置全局字体格式*********/
        this.setTypeface();
        /**********初始化蓝牙**************/
        BLESdk.get().init(mContext);
        BLESdk.get().setMaxConnect(5);
        Intent bleServer = new Intent(mContext, MyBleService.class);
        startService(bleServer);
        /*********初始化百度地图***************/
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public Typeface getTypeface() {
        return tf;
    }

    public void setTypeface() {
        mgr = getAssets();//得到AssetManager
        tf = Typeface.createFromAsset(mgr, "fonts/ibontenyouyuan.ttf");//根据路径得到Typeface
    }
}
