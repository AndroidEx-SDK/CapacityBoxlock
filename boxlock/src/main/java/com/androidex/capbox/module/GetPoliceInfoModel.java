package com.androidex.capbox.module;

/**
 * 获取报警信息开关
 * <p>
 * Created by cts on 17/10/13.
 */

public class GetPoliceInfoModel extends BaseModel {
    public Data data;

    public class Data {
//        police:’A’,           //报警开启A和关闭B
//        policeDiatance:’A’     //距离报警， 开启A 关闭B
//        dismountPolice:’A’,    //破拆报警的开启A和关闭B
//        tamperPolice:’A’,      //防拆报警，开启A和关闭B
//        tempPolice:’A’,        //温度报警开关，开启A 关闭B
//        humPolice:’A’，      //湿度报警开关，开启A 关闭B
//        highestTemp:”80”,     //最高温度，超出报警
//        lowestTemp:”0”，     //最低温
//        highestHum:’80’,      //最高湿度
//        lowestHum:’20’       //最低湿度

        public String police;
        public String policeDiatance;
        public String dismountPolice;
        public String tamperPolice;
        public String tempPolice;
        public String humPolice;
        public String highestTemp;
        public String lowestTemp;
        public String highestHum;
        public String lowestHum;

        @Override
        public String toString() {
            return "Data{" +
                    "police='" + police + '\'' +
                    ", policeDiatance='" + policeDiatance + '\'' +
                    ", dismountPolice='" + dismountPolice + '\'' +
                    ", tamperPolice='" + tamperPolice + '\'' +
                    ", tempPolice='" + tempPolice + '\'' +
                    ", humPolice='" + humPolice + '\'' +
                    ", highestTemp='" + highestTemp + '\'' +
                    ", lowestTemp='" + lowestTemp + '\'' +
                    ", highestHum='" + highestHum + '\'' +
                    ", lowestHum='" + lowestHum + '\'' +
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
