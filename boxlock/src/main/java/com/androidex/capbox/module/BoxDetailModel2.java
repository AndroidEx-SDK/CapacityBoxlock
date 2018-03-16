package com.androidex.capbox.module;

/**
 * 获取箱体的详细信息
 *
 * Created by cts on 17/10/13.
 */

public class BoxDetailModel2 extends BaseModel {
    public Data data;

    public class Data {
//        boxName:’密藏’,            //密运箱的昵称
//        deviceStatus:0，  //空闲0，配置1，携行2，开箱3，静默4
//        longitude:’12138.7451E’,    //箱体的经度
//        latitude:’2503.7148N’,      //纬度
//        elevation:’116.7M’,         //高程
//        isDefault:0，         //是否为默认设备，默认为0，否为1
//        carryPersonNum:2,   //携行人员人数跟腕表数量对应
//        heartbeatRate:60,     //心跳更新频率60秒
//        locationRate:60,      //定位更新频率为60秒
//        become:’A’,         //静默开启A 关闭B

        public String boxName;      //密运箱的昵称
        public int deviceStatus;    //空闲0，配置1，携行2，开箱3，静默4
        public String longitude;
        public String latitude;
        public String elevation;
        public int isDefault;
        public int carryPersonNum;
        public int heartbeatRate;
        public int locationRate;
        public String become;

        @Override
        public String toString() {
            return "Data{" +
                    "boxName='" + boxName + '\'' +
                    ", deviceStatus=" + deviceStatus +
                    ", longitude='" + longitude + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", elevation='" + elevation + '\'' +
                    ", isDefault=" + isDefault +
                    ", carryPersonNum=" + carryPersonNum +
                    ", heartbeatRate=" + heartbeatRate +
                    ", locationRate=" + locationRate +
                    ", become='" + become + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BoxDetailModel{" +
                "data=" + data +
                '}';
    }
}
