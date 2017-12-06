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
