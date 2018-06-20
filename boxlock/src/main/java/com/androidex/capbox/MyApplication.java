package com.androidex.capbox;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.androidex.capbox.base.imageloader.UILKit;

import org.greenrobot.greendao.database.Database;

import com.androidex.capbox.db.DaoMaster;
import com.androidex.capbox.db.DaoMaster.DevOpenHelper;
import com.androidex.capbox.db.DaoSession;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.map.CommonUtil;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.model.BaseRequest;
import com.e.ble.BLESdk;

import java.util.concurrent.atomic.AtomicInteger;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    public static Context mContext;
    AssetManager mgr;
    Typeface tf;
    private DaoSession daoSession;

    /**
     * 注册广播（电源锁、GPS状态）的标志
     */
    public boolean isRegisterReceiver = false;

    /**
     * 轨迹客户端
     */
    private LBSTraceClient mClient = null;


    /**
     * Entity标识
     */
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;//此处不可更改为getApplicationContext()，百度地图会鉴权错误。
        /************初始化ImageLoader*******************/
        UILKit.init(getApplicationContext());

//        /**********设置全局字体格式*********/
//        this.setTypeface();

        /**********初始化蓝牙**************/
        BLESdk.get().init(mContext);
        BLESdk.get().setMaxConnect(5);
        Intent bleServer = new Intent(mContext, MyBleService.getInstance().getClass());
        startService(bleServer);

        /*********初始化百度地图***************/
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);

        /*********GreenDao************/
        DevOpenHelper helper = new DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        /**************百度鹰眼轨迹**************/
        mClient = new LBSTraceClient(this);//该处如果使用getApplicationContext()会导致百度鉴权错误。
        // 设置协议类型，0为http，1为https
        // int protocoType = 1;
        // mClient.setProtocolType(protocoType);
        //上传轨迹  http://lbsyun.baidu.com/index.php?title=yingyan/api/v3/trackupload
    }

    /**
     * 获取字体
     *
     * @return
     */
    public Typeface getTypeface() {
        return tf;
    }

    public void setTypeface() {
        mgr = getAssets();//得到AssetManager
        tf = Typeface.createFromAsset(mgr, "fonts/ibontenyouyuan.ttf");//根据路径得到Typeface
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public LBSTraceClient getmClient() {
        return mClient;
    }
}
