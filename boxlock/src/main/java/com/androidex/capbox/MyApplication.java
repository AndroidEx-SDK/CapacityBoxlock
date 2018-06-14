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
import com.androidex.capbox.utils.map.CommonUtil;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.model.BaseRequest;
import com.e.ble.BLESdk;

import java.util.concurrent.atomic.AtomicInteger;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    protected static Context mContext;
    AssetManager mgr;
    Typeface tf;
    private DaoSession daoSession;
    public SharedPreferences trackConf = null;
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * 轨迹服务ID
     */
    public long serviceId = 200366;

    /**
     * Entity标识
     */
    public String entityName = "myTrace";
    public LBSTraceClient mClient;

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

        /***********百度鹰眼***************/
        entityName = CommonUtil.getImei(this);
        mClient = new LBSTraceClient(mContext);
        trackConf = getSharedPreferences("track_conf", MODE_PRIVATE);
    }

    public Typeface getTypeface() {
        return tf;
    }

    /**
     * 初始化请求公共参数
     *
     * @param request
     */
    public void initRequest(BaseRequest request) {
        request.setTag(getTag());
        request.setServiceId(serviceId);
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    public void setTypeface() {
        mgr = getAssets();//得到AssetManager
        tf = Typeface.createFromAsset(mgr, "fonts/ibontenyouyuan.ttf");//根据路径得到Typeface
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
