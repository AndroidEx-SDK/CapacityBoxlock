package com.androidex.capbox.module;

import java.util.List;

/**
 * @author wanglei
 * @version 1.0.0
 * @description 用户
 * @createTime 2015/11/25
 * @editTime
 * @editor
 */
public class BoxDeviceModel extends BaseModel {
    /**
     * {code：0，
     * devicelist:[{
     * deviceType:’A’,
     * deviceName:’’,
     * uuid:’ d69a05d7b8994d07aaf81522b54c7802’,  //设备类型为A时该字段必须返回
     * mac:’ FF:FF:FF:FF:FF:FF’,
     * deviceStatus:0    //押运状态A，配置状态B，空闲状态C
     * isdefault:0        //是否为默认设备，默认为0，否为1
     * isOnLine:0        //设备是否在线，超过心跳三倍的时间未收到心跳代表离线，0在线，1离线
     * },{
     * deviceType:’B’,
     * deviceName:’’,
     * uuid:’’,        //设备类型为B时，该字段为空返回
     * mac:’FF:FF:FF:FF:FF:FF’
     * deviceStatus:’A’,    //押运状态A，配置状态B，空闲状态C
     * isDefault:0        //是否为默认设备，默认为0，否为1
     * isOnLine:0        //设备是否在线，超过心跳三倍的时间未收到心跳代表离线，0在线，1离线
     * }]
     * }
     * result:
     * {"code":0,"data":
     * {"address":"a","cardid":"410881199102106519",
     * "createTime":"2017-10-11 15:56:59",
     * "departSID":"","id":"21","isActive":"1",
     * "name":"李永平","passWord":"2e8372111871f34d4f4a61d9c9455bd1",
     * "roleIDS":"","token":"F896206E2047902986FB862AAFF9B7F5",
     * "userName":"13168035997"
     * },
     * "devicelist":[]
     * }
     *
     */

    public List<device> devicelist;

    public class device {
        public String deviceType;
        public String boxName;
        public String uuid;
        public String mac;
        public int deviceStatus;
        public int isDefault;
        public int isOnLine;
        public String lat;//纬度
        public String lon;//经度

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceName() {
            return boxName;
        }

        public void setDeviceName(String deviceName) {
            this.boxName = deviceName;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public int getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(int deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public int getIsdefault() {
            return isDefault;
        }

        public void setIsdefault(int isdefault) {
            this.isDefault = isdefault;
        }

        public int getIsOnLine() {
            return isOnLine;
        }

        public void setIsOnLine(int isOnLine) {
            this.isOnLine = isOnLine;
        }
    }

    @Override
    public String toString() {
        super.toString();
        return "BoxDeviceModel{" +
                "devicelist=" + devicelist +
                '}';
    }
}
