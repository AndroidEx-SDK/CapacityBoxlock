package com.androidex.capbox.module;

/**
 * Created by cts on 17/10/13.
 */

public class GetLockTypeModel extends BaseModel {
    public Data data;

    public class Data {

        //        bluetoothUnlocking:’A’,  //蓝牙开锁 开启”A”关闭”B”
//        fingerUnlocking:’A’,     //指纹开锁 开启”A”关闭”B”
//        remoteUnlocking:’A’     //远程开锁 开启”A”关闭”B”
        public String bluetoothUnlocking;
        public String fingerUnlocking;
        public String remoteUnlocking;

        @Override
        public String toString() {
            return "Data{" +
                    "bluetoothUnlocking='" + bluetoothUnlocking + '\'' +
                    ", fingerUnlocking='" + fingerUnlocking + '\'' +
                    ", remoteUnlocking='" + remoteUnlocking + '\'' +
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
