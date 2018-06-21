package com.androidex.capbox.module;

import android.graphics.Bitmap;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/6/20
 */
public class BaiduPointModel {
    String entity_name;
    long loc_time;//Unix时间戳
    double latitude;
    double longitude;
    String coord_type_input;
//    double speed;
//    int direction;//方向
//    double height;
//    double radius;//定位精度，GPS或定位SDK返回的值
    String devicename;

    @Override
    public String toString() {
        return "BaiduPointModel{" +
                "entity_name='" + entity_name + '\'' +
                ", loc_time=" + loc_time +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", coord_type_input='" + coord_type_input + '\'' +
                ", devicename='" + devicename + '\'' +
                '}';
    }

    public String getEntity_name() {
        return entity_name;
    }

    public void setEntity_name(String entity_name) {
        this.entity_name = entity_name;
    }

    public long getLoc_time() {
        return loc_time;
    }

    public void setLoc_time(long loc_time) {
        this.loc_time = loc_time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCoord_type_input() {
        return coord_type_input;
    }

    public void setCoord_type_input(String coord_type_input) {
        this.coord_type_input = coord_type_input;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }
}
