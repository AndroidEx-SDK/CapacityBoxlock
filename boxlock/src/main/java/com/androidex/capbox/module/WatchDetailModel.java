package com.androidex.capbox.module;

/**
 * Created by cts on 17/10/13.
 */

public class WatchDetailModel extends BaseModel{
    public Data data;

    public class Data {
//        carryName:’李永平’,   //携行人的姓名
//        carryCardId:’410881199102106519’, //身份证号
//        carryFinger1:’fffffffff’,  //携行人指纹信息或指纹ID
//        carryFinger2:’fffffffff’,  //携行人指纹信息或指纹ID
//        carryFinger3:’fffffffff’,  //携行人指纹信息或指纹ID

        public String carryName;
        public String carryCardID;
        public String carryFinger1;
        public String carryFinger2;
        public String carryFinger3;

    }


    @Override
    public String toString() {
        return "WatchDetailModel{" +
                "code=" + code +
                ", data='" + data + '\'' +
                ", info='" + info + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                '}';
    }

}
