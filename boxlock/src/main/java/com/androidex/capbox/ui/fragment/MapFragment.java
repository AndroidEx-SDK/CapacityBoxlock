package com.androidex.capbox.ui.fragment;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaiduModel;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.ui.activity.LoginActivity;
import com.androidex.capbox.ui.listener.MyOrientationListener;
import com.androidex.capbox.ui.widget.ThirdTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

public class MapFragment extends BaseFragment {
    private static final String TAG = "MapFragment";
    @Bind(R.id.bmapView)
    MapView airpotrtmapView;
    @Bind(R.id.titlebar)
    ThirdTitleBar titleBar;
    @Bind(R.id.iv_location)
    ImageView iv_location;

    // 百度地图控件
    public BaiduMap baiduMap = null;// 百度地图对象
    public LocationClient locationClient = null;// 定位相关声明
    boolean isFirstLoc = true;// 是否首次定位
    private static List<Map<String, String>> mylist = new ArrayList<>();
    List<Map<String, String>> loclist = new ArrayList();
    private Timer timer_location = new Timer();// 设计定时器
    private TimerTask timer_getlocation;
    private MyOrientationListener myOrientationListener;//方向传感器
    private int mXDirection;
    private float mCurrentAccracy;
    private double mCurrentLantitude;
    private double mCurrentLongitude;

    @Override
    public void initData() {
        boxlist();
        initMap();
        titleBar.getLeftBtn().setVisibility(View.GONE);
        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                center2myLoc();
            }
        });
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker mark) {
                // showPop(mark);
                Log.e(TAG, "mark position=" + mark.getPosition());
                return true;
            }
        });
        initOritationListener();
        getLocation(true);
    }

    @Override
    public void setListener() {

    }

    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentAccracy)
                        .direction(mXDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                        .latitude(mCurrentLantitude)
                        .longitude(mCurrentLongitude).build();
                // 设置定位数据
                baiduMap.setMyLocationData(locData);
                // 设置自定义图标
//                        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                                .fromResource(R.drawable.navi_map_gps_locked);
//                        MyLocationConfigeration config = new MyLocationConfigeration(
//                                mCurrentMode, true, mCurrentMarker);
                //baiduMap.setMyLocationConfigeration(config);
            }
        });
    }

    private void initMap() {
        baiduMap = airpotrtmapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启定位图层
        locationClient = new LocationClient(getActivity().getApplicationContext()); // 实例化LocationClient类
        locationClient.registerLocationListener(myListener); // 注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);                // 打开GPS
        //option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(2000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向

        locationClient.setLocOption(option);
        baiduMap.setMyLocationConfiguration(
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        airpotrtmapView.showZoomControls(true);//显示加减按钮
        airpotrtmapView.showScaleControl(true);//显示比例尺
        setwaterload();
        locationClient.start(); // 开始定位
    }

    private void getLocation(boolean flag) {
        if (flag) {
            timer_getlocation = new TimerTask() {
                @Override
                public void run() {
                    Log.i("LockFragment", "开始定位");
                    boxlist();
                }
            };
            timer_location.schedule(timer_getlocation, 1000, 1 * 60 * 1000);
        } else {
            if (timer_getlocation != null) {
                timer_getlocation.cancel();
                timer_location.cancel();
                timer_getlocation = null;
                timer_location = null;
                Log.i("LockFragment", "停止定位");
            }
        }
    }

    private void setwaterload() {
        List<LatLng> airpolist = new ArrayList<LatLng>();
        for (int i = 0; i < loclist.size(); i++) {
            double lat = 0;
            double lon = 0;
            Log.e(TAG, loclist.get(i).get("lat"));
            Log.e(TAG, loclist.get(i).get("lon"));
            String str_lat_64 = loclist.get(i).get("lat");
            String str_lon_64 = loclist.get(i).get("lon");
            String str_lat = new String(Base64.decode(str_lat_64, Base64.DEFAULT));
            String str_lon = new String(Base64.decode(str_lon_64, Base64.DEFAULT));
            Log.e("Base64", "Base64--lat-->" + str_lat);
            Log.e("Base64", "Base64--lon-->" + str_lon);
            if (!str_lat.equals("0") && !str_lat.equals("") && !str_lon.equals("0") && !str_lon.equals("")) {
                lat = Double.valueOf(str_lat).doubleValue();
                lon = Double.valueOf(str_lon).doubleValue();
            }
            if (lat == 0 && lon == 0) {
            } else {
                LatLng point = new LatLng(lat, lon);//正确的
                airpolist.add(point);
            }
        }
//        if (airpolist.size() == 0) {
//            airpolist.clear();
//            double lon = 0;
//            double lat = 0;
//            Log.e(TAG, "自身的经纬度 latitude=" + mCurrentLantitude);
//            Log.e(TAG, "自身的经纬度 longitude=" + mCurrentLongitude);
//            lat = Double.valueOf(mCurrentLantitude).doubleValue();
//            lon = Double.valueOf(mCurrentLongitude).doubleValue();
//            if (lat == 0 && lon == 0) {
//            } else {
//                LatLng point = new LatLng(lat, lon);//正确的
//                airpolist.add(point);
//            }
//        }
        for (int i = 0; i < airpolist.size(); i++) {
            // 构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_map);
            // 构建MarkerOption，用于在地图上添加
            MarkerOptions option = new MarkerOptions()
                    .position(airpolist.get(i))
                    .icon(bitmap)
                    .zIndex(i)
                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                    .draggable(true);           // 是否可拖拽，默认不可拖拽;
            // 在地图上添加Marker，并显示
            baiduMap.addOverlay(option);
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(airpolist.get(i)));
        }
        //locationClient.start(); // 开始定位
    }

    /**
     * 获取设备列表
     * {"code":0,"devicelist":[
     * {"boxName":"Box","deviceStatus":"1","isDefault":"0","isOnLine":1,
     * "lat":"0","lon":"0",
     * "mac":"B0:91:22:69:42:0A","uuid":"B0912269420A0000000070D042190000"}]}
     */
    public void boxlist() {
        NetApi.boxlist(getToken(), ((MainActivity) context).username, new ResultCallBack<BoxDeviceModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            Log.e(TAG, "loclist.size=" + loclist.size() + "mylist.size=" + mylist.size());
                            if (loclist.size() >= mylist.size()) {
                                Log.e(TAG, "loclist.size=" + loclist.size());
                                if (baiduMap != null) {
                                    baiduMap.clear();
                                }
                            }
                            mylist.clear();
                            for (BoxDeviceModel.device device : model.devicelist) {
                                Map<String, String> map = new HashMap<>();
                                map.put("name", device.boxName);
                                map.put("uuid", device.uuid);
                                map.put("mac", device.mac);
                                map.put("lat", device.lat);
                                map.put("lon", device.lon);
                                mylist.add(map);
                                getBaiduLocation(device.lat, device.lon);
                            }
                            if (loclist.size() == 0) {

                            } else {
                                L.e(TAG + "收到绑定设备的数量=" + mylist.size());
                                setwaterload();//加载经纬度数据
                            }
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "账号在其他地方登录");
                            LoginActivity.lauch(context);
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            locationClient.start(); // 开始定位
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            locationClient.start(); // 开始定位
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                locationClient.start(); // 开始定位
                if (context != null) {
                    CommonKit.showErrorShort(context, "网络出现异常");
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    /**
     * 经纬度纠偏
     *
     * @param latitude
     * @param longitude
     */
    public void getBaiduLocation(String latitude, String longitude) {
        NetApi.getLocation(latitude, longitude, new ResultCallBack<BaiduModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaiduModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    if (model.error == 0) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("lat", model.y);
                        map.put("lon", model.x);
                        loclist.add(map);
                    } else {
                        Log.e(TAG, "经纬度转换出错 error=" + model.error);
                    }
                } else {
                    Log.e(TAG, "经纬度转换出错 model=null");
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                Log.e(TAG, "经纬度转换出错");
                CommonKit.showErrorShort(context, "网络连接失败");
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    public void onResume() {
        if (airpotrtmapView != null) {
            airpotrtmapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (airpotrtmapView != null) {
            airpotrtmapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        baiduMap.clear();
        airpotrtmapView.onDestroy();
        airpotrtmapView = null;
        getLocation(false);
        super.onDestroy();
    }

    /**
     * 实现实位回调监听
     */
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || airpotrtmapView == null)
                return;
            // 构造定位数据
            mCurrentAccracy = location.getRadius();
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    .direction(mXDirection) //此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLantitude)
                    .longitude(mCurrentLongitude)
                    .build();
            baiduMap.setMyLocationData(locData); // 设置定位数据

            // 第一次定位时，将地图位置移动到当前位置
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latlng, 14.0f); // 设置地图中心点以及缩放级别
                baiduMap.animateMapStatus(u);
                Log.i("vvvv", "----zb=" + latlng.latitude);
                Log.i("vvvv", "----zb=" + latlng.longitude);
            }
        }
    };

    @Override
    public void onStart() {
        // 开启图层定位
        baiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        // 开启方向传感器
        myOrientationListener.start();
        super.onStart();
    }

    @Override
    public void onStop() {
        // 关闭图层定位
        baiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        // 关闭方向传感器
        myOrientationListener.stop();
        loclist.clear();
        super.onStop();
    }

    /**
     * 地图移动到我的位置,此处可以重新发定位请求，然后定位；
     * 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
     */
    private void center2myLoc() {
        LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(u);
    }

    @Override
    public void onClick(View view) {

    }

    //    private void showPop(Marker marker) {
//        View view = null;
//        Bundle bundle = marker.getExtraInfo();
//        final LatLng ll = marker.getPosition();
//        if (marker.getZIndex() == 5) {
//            view = LayoutInflater.from(getActivity()).inflate(
//                    R.layout.apriport_invest_item, null);
//            LinearLayout ll_invest = (LinearLayout) view
//                    .findViewById(R.id.ll_invest);
//            ll_invest.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    showToast("页面跳转");
//
//                }
//            });
//            InfoWindow mInfoWindow = new InfoWindow(view, ll, -70);
//            // 显示InfoWindow
//            baiduMap.showInfoWindow(mInfoWindow);
//        }
//        if (marker.getZIndex() == 1) {
//            //bean = (RowsBean) bundle.getSerializable("info");
//            view = LayoutInflater.from(getActivity()).inflate(
//                    R.layout.apirport_item, null);
//            LinearLayout ll_airport = (LinearLayout) view
//                    .findViewById(R.id.ll_airport);
//            ll_airport.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    Intent intent = new Intent(getActivity(),
//                            AirportDetailsActivity.class);
//                    //intent.putExtra("airport_id", bean.getId());
//                    startActivity(intent);
//
//                }
//            });
//            InfoWindow mInfoWindow = new InfoWindow(view, ll, -70);
//            // 显示InfoWindow
//            baiduMap.showInfoWindow(mInfoWindow);
//        }
//    }
}
