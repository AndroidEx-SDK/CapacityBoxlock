package com.androidex.capbox.ui.fragment;

import android.app.ProgressDialog;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.db.Note;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.module.BoxMovePathModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.LoginActivity;
import com.androidex.capbox.ui.widget.ThirdTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.DrivingRouteOverlay;
import com.androidex.capbox.utils.MapUtils;
import com.androidex.capbox.utils.RLog;
import com.androidex.capbox.utils.WalkingRouteOverlay;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.capbox.utils.MapUtils.degreeToDB;

/**
 * Created by Administrator on 2018/1/26.
 */

public class MapFragment extends BaseFragment implements MapUtils.MapUtilsEvent {
    private static final String TAG = "MapFragment";
    @Bind(R.id.bmapView)
    MapView airpotrtmapView;
    @Bind(R.id.titlebar)
    ThirdTitleBar titleBar;
    @Bind(R.id.iv_location)
    ImageView iv_location;
    @Bind(R.id.navigation_exit)
    Button navigationButton;
    @Bind(R.id.routeplan_exit)
    Button routeplanButton;
    @Bind(R.id.navigation_animation)
    Button navigation_animation;
    @Bind(R.id.shock_box)
    Button shockBox;
    private View markerDialog;
    private ProgressDialog progressDialog;
    private boolean isOverTime = false;
    private MapMode mapMode = MapMode.NORMAL;
    private final int UPDATE_TIME = 60 * 1000;
    private final int DELAY_TIME = 10 * 1000;

    //地图位置参数
    private BaiduMap mBaiduMap = null;
    private MapUtils mMapUtils;
    private List<Map<String, String>> mBoxDevices = new ArrayList<Map<String, String>>();
    private List<LatLng> mBoxMovePath = new ArrayList<LatLng>();
    private WalkingRouteLine wrl;
    private DrivingRouteLine drl;
    private BDLocation location;
    private MyLocationData locData;
    private int mCurrentDirection = 0;
    private Double lastX = 0.0;
    int position;//标记动画跳转到哪个地理节点
    private Marker marker;//动画绘制
    private BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_map);
    private BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(final Marker marker) {
            showLocation(marker.getPosition());
            if (markerDialog == null) {
                markerDialog = LayoutInflater.from(context).inflate(R.layout.marker_dialog, null);
            }
            markerDialog.findViewById(R.id.marker_dialog_navigation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (location != null) {
                        LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng end = marker.getPosition();
                        isOverTime = false;
                        wrl = null;
                        drl = null;
                        mMapUtils.RoutePlan(start, end);
                        progressDialog = ProgressDialog.show(context, null, "正在规划路线...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        mHandler.sendEmptyMessageDelayed(END_OVERTIME_WHAT, DELAY_TIME); //10s超时
                    } else {
                        CommonKit.showErrorShort(context, "未获取定位信息，请到开阔地带");
                    }
                    mBaiduMap.hideInfoWindow();
                }
            });
            markerDialog.findViewById(R.id.marker_dialog_trajectory).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uuid = mBoxDevices.get(marker.getExtraInfo().getInt("id")).get("uuid");
                    String address = mBoxDevices.get(marker.getExtraInfo().getInt("id")).get("mac");
                    if (uuid != null && uuid.length() > 0) {
                        getDeviceMovePath(uuid, address);
                    }
                    mBaiduMap.hideInfoWindow();
                }
            });
            mBaiduMap.showInfoWindow(new InfoWindow(markerDialog, marker.getPosition(), -47));
            return false;
        }
    };
    private Timer timer;

    //Handler
    private final int END_DEVICES_WHAT = 0x01;
    private final int END_WALKING_WHAT = 0x02;
    private final int END_DRIVING_WHAT = 0x03;
    private final int END_OVERTIME_WHAT = 0x04;
    private final int END_MOVEPATH_WHAT = 0x05;
    private final int END_ANMATION_WHAT = 0x06;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case END_DEVICES_WHAT:
                    //成功获取设备列表
                    showBoxDevice();
                    //每隔一分钟获取箱体坐标
                    if (timer == null) {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getBoxDevices();
                            }
                        }, UPDATE_TIME, UPDATE_TIME);
                    }
                    break;
                case END_WALKING_WHAT:
                case END_DRIVING_WHAT:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (wrl != null) {
                        showWalkingRouteLine(wrl);
                    } else if (drl != null) {
                        showDrivingRouteLine(drl);
                    }
                    break;
                case END_OVERTIME_WHAT:
                    isOverTime = true;
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    CommonKit.showErrorShort(context, "路线规划失败");
                    break;
                case END_MOVEPATH_WHAT:
                    //获取移动轨迹完成
                    String address = (String) msg.obj;
                    if (mBoxMovePath != null && mBoxMovePath.size() <= 0) {
                        List<Note> locList = MyBleService.getLocListData(address);
                        for (Note note : locList) {
                            String lat = note.getLat().replace("N", "");//.substring(0, 9)
                            String lon = note.getLon().replace("E", "");//.substring(0, 10)
                            LatLng ll = new LatLng(MapUtils.degreeToDB(lat), MapUtils.degreeToDB(lon));
                            ll = mMapUtils.GpsToBD(ll);
                            mBoxMovePath.add(ll);
                        }
                    }
                    showBoxDeviceMovePath();
                    break;
                case END_ANMATION_WHAT:
                    marker.setPosition(mBoxMovePath.get(position));
                    if (position == (mBoxMovePath.size() - 1)) {
                        position = 0;
                        stopDrawTrack();
                    }
                    position++;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void initData() {
        mMapUtils = new MapUtils(context, this);
        iv_location.setOnClickListener(this);
        navigationButton.setOnClickListener(this);
        routeplanButton.setOnClickListener(this);
        shockBox.setOnClickListener(this);
        navigation_animation.setOnClickListener(this);
        initMap();
    }

    /**
     * 初始化地图，并获取设备列表
     */
    private void initMap() {
        mBaiduMap = airpotrtmapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(19).build()));
        getBoxDevices();
    }

    /**
     * 回到指定的位置
     */
    private void showLocation(LatLng ll) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }

    /**
     * 根据账户&uuid获取设备移动轨迹
     */
    private void getDeviceMovePath(String uuid, final String address) {
        mBoxMovePath.clear();
        NetApi.movepath(getToken(), getUserName(), uuid, new ResultCallBack<BoxMovePathModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxMovePathModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            LatLng ll = null;
                            for (BoxMovePathModel.LatLng latLng : model.datalist) {
                                ll = new LatLng(Double.valueOf(latLng.getLatitude()), Double.valueOf(latLng.getLongitude()));
                                mBoxMovePath.add(ll);
                            }
                            ll = null;
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "账号在其他地方登录");
                            LoginActivity.lauch(context);
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备轨迹失败");
                            break;
                        default:
                            break;
                    }
                }
                RLog.d("读取设备的数据库数据 收到的address = " + address);
                Message msg = Message.obtain();
                msg.obj = address;
                msg.what = END_MOVEPATH_WHAT;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                if (context != null && !CommonKit.isNetworkAvailable(context)) {
                    CommonKit.showErrorShort(context, "网络出现异常");
                }
                Message msg = Message.obtain();
                msg.obj = address;
                msg.what = END_MOVEPATH_WHAT;
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 根据账户获取绑定设备列表
     */
    private void getBoxDevices() {
        mBoxDevices.clear();
        NetApi.boxlist(getToken(), getUserName(), new ResultCallBack<BoxDeviceModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            LatLng ll = null;
                            RLog.i("============箱子坐标=====================");
                            for (BoxDeviceModel.device device : model.devicelist) {
                                RLog.i("lat = " + device.lat + "  lon =  " + device.lon);
                                ll = new LatLng(Double.valueOf(device.lat), Double.valueOf(device.lon));
                                ll = mMapUtils.GpsToBD(ll);
                                Map<String, String> map = new HashMap<>();
                                map.put("name", device.boxName);
                                map.put("uuid", device.uuid);
                                map.put("mac", device.mac);
                                map.put("lat", ll.latitude + "");
                                map.put("lon", ll.longitude + "");
                                mBoxDevices.add(map);
                            }
                            RLog.i("============end=====================");
                            ll = null;
                            mHandler.sendEmptyMessage(END_DEVICES_WHAT);

                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "账号在其他地方登录");
                            LoginActivity.lauch(context);
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            break;
                        default:
                            break;
                    }
                }
                mHandler.sendEmptyMessage(END_DEVICES_WHAT);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                if (context != null && !CommonKit.isNetworkAvailable(context)) {
                    CommonKit.showErrorShort(context, "网络出现异常");
                }
                mHandler.sendEmptyMessage(END_DEVICES_WHAT);
            }
        });
    }

    /**
     * 将获取的移动轨迹显示在地图
     */
    private void showBoxDeviceMovePath() {
        if (mBoxMovePath != null && mBoxMovePath.size() > 0) {
            mapMode = MapMode.ROUTEPLAN;
            routeplanButton.setVisibility(View.VISIBLE);
            navigation_animation.setVisibility(View.VISIBLE);
            cleanMap(); //清除其他Overlay
            /****************轨迹绘制**********************/
            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                    .color(0xAAFF0000).points(mBoxMovePath);
            mBaiduMap.addOverlay(ooPolyline);
            /****************轨迹回放**********************/
            position = 0;
            OverlayOptions ooA = new MarkerOptions().position(mBoxMovePath.get(position)).icon(bitmap);
            marker = (Marker) (mBaiduMap.addOverlay(ooA));
            startDrawTrack();
        } else {
            CommonKit.showErrorShort(context, "未获取轨迹信息");
        }
    }


    TimerTask task;
    Timer timerDraw;

    /**
     * 开始回放
     */
    public void startDrawTrack() {
        task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = END_ANMATION_WHAT;
                mHandler.sendMessage(message);
            }
        };
        timerDraw = new Timer(true);
        timerDraw.schedule(task, 300, 1000);
    }

    public void stopDrawTrack() {
        if (timerDraw != null) {
            timerDraw.cancel();
        }
        if (task != null) {
            task.cancel();
        }
        timerDraw = null;
        task = null;
    }

    /**
     * 将设备位置显示在地图
     */
    private void showBoxDevice() {
        if (mBoxDevices != null && mBoxDevices.size() > 0 && mapMode == MapMode.NORMAL) {
            mapMode = MapMode.NORMAL;
            cleanMap(); //清除上次添加的Overlay
            LatLng ll = null;
            for (int i = 0; i < mBoxDevices.size(); i++) {
                ll = new LatLng(Double.valueOf(mBoxDevices.get(i).get("lat"))
                        , Double.valueOf(mBoxDevices.get(i).get("lon")));
                MarkerOptions option = new MarkerOptions()
                        .position(ll)
                        .icon(bitmap)
                        .zIndex(i)
                        .animateType(MarkerOptions.MarkerAnimateType.grow)
                        .draggable(true);
                Marker k = (Marker) mBaiduMap.addOverlay(option);
                Bundle bundle = new Bundle();
                bundle.putInt("id", i);
                k.setExtraInfo(bundle);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(ll));
                if (i == 0) {
                    showLocation(ll);
                }
            }
            ll = null;
            mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);
        }
    }

    private void showWalkingRouteLine(WalkingRouteLine line) {
        mapMode = MapMode.NAVIGATION;
        navigationButton.setVisibility(View.VISIBLE);
        shockBox.setVisibility(View.VISIBLE);
        cleanMap();
        WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
        overlay.setData(line);
        overlay.addToMap();
        overlay.zoomToSpan();
    }

    private void showDrivingRouteLine(DrivingRouteLine line) {
        mapMode = MapMode.NAVIGATION;
        navigationButton.setVisibility(View.VISIBLE);
        shockBox.setVisibility(View.VISIBLE);
        cleanMap();
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
        overlay.setData(line);
        overlay.addToMap();
        overlay.zoomToSpan();
    }

    private void cleanMap() {
        if (mBaiduMap != null) {
            mBaiduMap.clear();
            mBaiduMap.removeMarkerClickListener(onMarkerClickListener);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_location:
                if (location != null) {
                    showLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                } else {
                    CommonKit.showErrorShort(context, "未获取定位信息");
                }
                break;
            case R.id.navigation_exit:
                mapMode = MapMode.NORMAL;
                cleanMap();
                navigationButton.setVisibility(View.GONE);
                shockBox.setVisibility(View.GONE);
                showBoxDevice();
                break;
            case R.id.routeplan_exit:
                mapMode = MapMode.NORMAL;
                cleanMap();
                routeplanButton.setVisibility(View.GONE);
                navigation_animation.setVisibility(View.GONE);
                showBoxDevice();
                break;
            case R.id.navigation_animation:
                position = 0;
                stopDrawTrack();
                startDrawTrack();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationEvent(BDLocation bdLocation, boolean first) {
        location = bdLocation;
        locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(mCurrentDirection).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        if (first) {
            LatLng ll = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    @Override
    public void ononSensorChangedEvent(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            if (location != null) {
                locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .direction(mCurrentDirection).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
            }
        }
        lastX = x;
    }

    @Override
    public void onWalking(WalkingRouteLine line) {
        if (!isOverTime) {
            wrl = line;
            mHandler.removeMessages(END_OVERTIME_WHAT);
            mHandler.sendEmptyMessage(END_WALKING_WHAT);
        }
    }

    @Override
    public void onDriving(DrivingRouteLine line) {
        if (!isOverTime) {
            drl = line;
            mHandler.removeMessages(END_OVERTIME_WHAT);
            mHandler.sendEmptyMessage(END_DRIVING_WHAT);
        }

    }

    public enum MapMode {
        NAVIGATION, ROUTEPLAN, NORMAL
    }

    @Override
    public void onPause() {
        if (mMapUtils != null) {
            mMapUtils.stopLocation();
            mMapUtils.stopSensor();
        }
        if (airpotrtmapView != null) {
            airpotrtmapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mMapUtils != null) {
            mMapUtils.startLocation();
            mMapUtils.startSensor();
        }
        if (airpotrtmapView != null) {
            airpotrtmapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mMapUtils != null) {
            mMapUtils.stopLocation();
            mMapUtils.stopSensor();
        }
        cleanMap();
        airpotrtmapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_map;
    }
}
