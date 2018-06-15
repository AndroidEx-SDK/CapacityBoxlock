package com.androidex.capbox.map;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.androidex.capbox.MyApplication;
import com.androidex.capbox.utils.CommonKit;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.ProcessOption;

import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.MODE_PRIVATE;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/6/15
 */
public class MapManager {
    private final Context context;

    private static MapManager mapManager;

    private LocRequest locRequest = null;

    private SharedPreferences trackConf = null;

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * 轨迹服务ID
     */
    private long serviceId = 200366;

    /**
     * 轨迹客户端
     */
    private LBSTraceClient mClient = null;

    private MapManager(Context context) {
        this.context=context;
        trackConf = context.getSharedPreferences("track_conf", MODE_PRIVATE);
        mClient = new LBSTraceClient(MyApplication.mContext);//该处如果使用getApplicationContext()会导致百度鉴权错误。
        locRequest = new LocRequest(serviceId);
        clearTraceStatus();
    }

    public static MapManager getInstance(Context context) {
        if (mapManager == null) {
            synchronized (context) {
                if (mapManager == null) {
                    mapManager = new MapManager(context);
                }
            }
        }
        return mapManager;
    }

    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener, String entityName) {
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (CommonKit.isNetworkAvailable(context)
                && trackConf.contains("is_trace_started")
                && trackConf.contains("is_gather_started")
                && trackConf.getBoolean("is_trace_started", false)
                && trackConf.getBoolean("is_gather_started", false)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, entityName);
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            mClient.queryLatestPoint(request, trackListener);
        } else {
            mClient.queryRealTimeLoc(locRequest, entityListener);
        }
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
     * 清除Trace状态：初始化app时，判断上次是正常停止服务还是强制杀死进程，根据trackConf中是否有is_trace_started字段进行判断。
     * <p>
     * 停止服务成功后，会将该字段清除；若未清除，表明为非正常停止服务。
     */
    private void clearTraceStatus() {
        if (trackConf.contains("is_trace_started") || trackConf.contains("is_gather_started")) {
            SharedPreferences.Editor editor = trackConf.edit();
            editor.remove("is_trace_started");
            editor.remove("is_gather_started");
            editor.apply();
        }
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    public SharedPreferences getTrackConf() {
        return trackConf;
    }

    public LBSTraceClient getmClient() {
        return mClient;
    }
}
