package com.androidex.capbox.module;

/**
 * Created by cts on 17/10/13.
 */

public class LocationModel extends BaseModel{
    public int code;
    public Data data;

    public class Data {
//        deviceStatus:0  //押运状态A，配置状态B，空闲状态C
//        longitude:’12138.7451E’,    //箱体的经度
//        latitude:’2503.7148N’,      //纬度
//        elevation:’116.7M’, //高程
//        isOnLine:0      //设备是否在线，超过心跳三倍的时间未收到心跳代表离线，0在线，1离线

        public int deviceStatus;
        public String longitude;
        public String latitude;
        public String elevation;
        public String locationTime;//locationTime
        public String lastTime;
        public int isOnLine;

        public int getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(int deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getElevation() {
            return elevation;
        }

        public void setElevation(String elevation) {
            this.elevation = elevation;
        }

        public int getIsOnLine() {
            return isOnLine;
        }

        public void setIsOnLine(int isOnLine) {
            this.isOnLine = isOnLine;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

}
