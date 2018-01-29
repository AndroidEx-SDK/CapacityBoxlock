package com.androidex.capbox.data;

import com.androidex.capbox.utils.Constants;

/**
 * @author liyp
 * @version 1.0.0
 * @description 事件
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class Event {

    /**
     * 用户登录状态发生改变
     */
    public static class UserLoginEvent extends BaseEvent {

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_USER_LOGIN;
        }
    }

    /**
     * 绑定箱体数量改变
     */
    public static class BoxBindChange extends BaseEvent {

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_BOX_BIND_CHANGE;
        }
    }

    /**
     * 解除绑定箱体
     */
    public static class BoxRelieveBind extends BaseEvent {

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_BOX_RELIEVE_BIND;
        }
    }

    /**
     * 蓝牙连接
     */
    public static class BleConnected extends BaseEvent {
        String address;

        public BleConnected(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_BOX_BLE_CONNECTED;
        }
    }

    /**
     * 监控设备变更
     */
    public static class UpdateMonitorDevice extends BaseEvent {
        String address;
        String name;
        String uuid;
        int position;

        public UpdateMonitorDevice() {
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getAddress() {
            return address;
        }

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_BOX_BLE_CONNECTED;
        }
    }

    /**
     * 蓝牙断开
     */
    public static class BleDisConnected extends BaseEvent {
        String address;

        public BleDisConnected(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_BOX_BLE_DISCONNECTED;
        }
    }

    /**
     * 锁打开
     */
    public static class LockOpen extends BaseEvent {

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_BOX_LOCK_OPEN;
        }
    }

    /**
     * 锁关闭
     */
    public static class LockClose extends BaseEvent {

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_BOX_LOCK_CLOSE;
        }
    }

    /**
     * 切换下一页
     */
    public static class NextPage extends BaseEvent {

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_FRAGMENT_NEXT_PAGE;
        }
    }

    /**
     * 切换上一页
     */
    public static class PreviousPage extends BaseEvent {

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_FRAGMENT_PREVIOUS_PAGE;
        }
    }


    /**
     * 用户信息改变
     */
    public static class UserInfoChangeEvent extends BaseEvent {

        private String nickName;    //昵称
        private String phone;   //手机号

        @Override
        int getCode() {
            return Constants.EVENT.TAG_EVENT_USER_INFO_CHANGE;
        }

        public String getNickName() {
            return nickName;
        }

        public UserInfoChangeEvent nickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public UserInfoChangeEvent phone(String phone) {
            this.phone = phone;
            return this;
        }

        public String getPhone() {
            return phone;
        }
    }


}
