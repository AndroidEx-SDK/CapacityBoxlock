package com.androidex.capbox.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;

/**
 * Created by Administrator on 2018/1/26.
 */

public class MapUtils implements BDLocationListener,SensorEventListener,OnGetRoutePlanResultListener {
    private Context mContext;
    private MapUtilsEvent event;
    private boolean isFirstLocation = true;
    private CoordinateConverter converter;
    public MapUtils(Context context,MapUtilsEvent event){
        this.event= event;
        this.mContext = context;
    }
    //=========================Location=======================================
    private LocationClient mLocClient;
    public void startLocation(){
        if(mLocClient == null){
            mLocClient = new LocationClient(mContext);
            mLocClient.registerLocationListener(this);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setCoorType("bd09ll");
            option.setScanSpan(2000);
            option.setIsNeedAddress(true);
            option.setNeedDeviceDirect(true);
            mLocClient.setLocOption(option);
        }
        if(!mLocClient.isStarted()){
            mLocClient.start();
        }
    }
    public void stopLocation(){
        if(mLocClient!=null){
            mLocClient.stop();
            mLocClient = null;
        }
    }

    public LatLng GpsToBD(LatLng ll){
        if(converter == null){
            converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
        }
        converter.coord(ll);
        return converter.convert();
    }
    //=========================Sensor=======================================
    private SensorManager mSensorManager;
    public void startSensor(){
        if(mSensorManager == null){
            mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        }
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }
    public void stopSensor(){
        mSensorManager.unregisterListener(this);
    }
    //=========================RoutePlan=======================================
    private RoutePlanSearch mRoutePlan;
    public void RoutePlan(LatLng start, LatLng end){
        if(mRoutePlan == null){
            mRoutePlan = RoutePlanSearch.newInstance();
            mRoutePlan.setOnGetRoutePlanResultListener(this);
        }
        PlanNode stMassNode = PlanNode.withLocation(start);
        PlanNode enMassNode = PlanNode.withLocation(end);
        Double distance = DistanceUtil.getDistance(start,end);
        RLog.i("两点相距："+distance+"米");
        if(distance>1000){
            RLog.i("选择驱车路线");
            mRoutePlan.drivingSearch((new DrivingRoutePlanOption()).from(stMassNode).to(enMassNode));
        }else{
            RLog.i("选择步行路线");
            mRoutePlan.walkingSearch((new WalkingRoutePlanOption()).from(stMassNode).to(enMassNode));
        }
    }
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
//        if(isFirstLocation){
//            isFirstLocation = false;
//            this.event.onLocationEvent(bdLocation,true);
//            return;
//        }
        this.event.onLocationEvent(bdLocation,false);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        event.ononSensorChangedEvent(sensorEvent);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if(walkingRouteResult!=null){
            if(walkingRouteResult.getRouteLines()!=null){
                if(walkingRouteResult.getRouteLines().size()>0){
                    this.event.onWalking(walkingRouteResult.getRouteLines().get(0));
                    return;
                }
            }
        }
        this.event.onWalking(null);
    }
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
    }
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
    }
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if(drivingRouteResult!=null){
            if(drivingRouteResult.getRouteLines()!=null){
                if(drivingRouteResult.getRouteLines().size()>0){
                    this.event.onDriving(drivingRouteResult.getRouteLines().get(0));
                    return;
                }
            }
        }
        this.event.onDriving(null);
    }
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
    }
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
    }
    public interface MapUtilsEvent{
        void onLocationEvent(BDLocation bdLocation,boolean first);
        void ononSensorChangedEvent(SensorEvent sensorEvent);
        void onWalking(WalkingRouteLine line);
        void onDriving(DrivingRouteLine line);
    }
}
