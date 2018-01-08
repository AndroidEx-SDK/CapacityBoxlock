package com.androidex.capbox.module;

/**
 * Created by cts on 17/10/13.
 */

public class BoxDetailModel extends BaseModel{
    public Data data;

    public class Data {
//        deviceStatus:0，  //空闲0，配置1，携行2，开箱3，静默4
//        longitude:’12138.7451E’,    //箱体的经度
//        latitude:’2503.7148N’,      //纬度
//        elevation:’116.7M’,         //高程
//        isDefault:0，    //是否为默认设备，默认为0，否为1
//        possessorFinger1: ‘fffffff’，   //箱体所有人的指纹信息或指纹id
//        possessorFinger2:’fffffff’,     //箱体所有人的指纹信息或id
//        possessorFinger3:’fffffff’,     //箱体所有人的指纹信息或id
//        becomeFinger1:’fffffff’,      //静默功能的指纹信息或id
//        becomeFinger2:’fffffff’’      //静默功能的指纹信息或id
//        becomeFinger3:’fffffff’’      //静默功能的指纹信息或id
//        unlocking: ‘A’ ,             //多次有效A 一次有效B
//        unlockingMode:’A’,
//        //开锁方式设定: 指纹开锁A，腕表开锁B ,指定位置开锁C，                                                                            多重开锁 D
//        carryPersonNum:2,  //携行人员人数跟腕表数量对应
//        police:’A’,          //报警开启A和关闭B
//        policeDiatance:1,    //报警距离：0脱距、1较近、2近、3较远、4远
//        heartbeatRate:60,    //心跳更新频率60秒
//        locationRate:60,     //定位更新频率为60秒
//        highestTemp:”60.55”,     //最高温度，超出报警
//        lowestTemp:”-19.22”，    //最低温
//        dismountPolice:’A’,   //破拆报警的开启A和关闭B
//        become:’A’,         //静默开启A 关闭B

        public String boxName;      //密运箱的昵称
        public int deviceStatus;    //空闲0，配置1，携行2，开箱3，静默4
        public String longitude;    //
        public String latitude;
        public String elevation;
        public int isDefault;
        public String possessorFinger1;
        public String possessorFinger2;
        public String possessorFinger3;
        public String becomeFinger1;
        public String becomeFinger2;
        public String becomeFinger3;
        public String unlocking;
        public String unlockingMode;
        public int carryPersonNum;
        public String police;
        public int policeDiatance;
        public int heartbeatRate;
        public int locationRate;
        public float highestTemp;
        public float lowestTemp;
        public String dismountPolice;
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
                    ", possessorFinger1='" + possessorFinger1 + '\'' +
                    ", possessorFinger2='" + possessorFinger2 + '\'' +
                    ", possessorFinger3='" + possessorFinger3 + '\'' +
                    ", becomeFinger1='" + becomeFinger1 + '\'' +
                    ", becomeFinger2='" + becomeFinger2 + '\'' +
                    ", becomeFinger3='" + becomeFinger3 + '\'' +
                    ", unlocking='" + unlocking + '\'' +
                    ", unlockingMode='" + unlockingMode + '\'' +
                    ", carryPersonNum=" + carryPersonNum +
                    ", police='" + police + '\'' +
                    ", policeDiatance=" + policeDiatance +
                    ", heartbeatRate=" + heartbeatRate +
                    ", locationRate=" + locationRate +
                    ", highestTemp=" + highestTemp +
                    ", lowestTemp=" + lowestTemp +
                    ", dismountPolice='" + dismountPolice + '\'' +
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
