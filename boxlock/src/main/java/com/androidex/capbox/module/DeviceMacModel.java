package com.androidex.capbox.module;


/**
 * @author liyp
 * @version 1.0.0
 * @description Model基类
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class DeviceMacModel extends BaseModel {
    public int code;
    public Data data;

    public class Data {
        public String deviceMac;
    }

    /**
     * {
     * code:0,
     * data:{
     * deviceMac:’FF:FF:FF:FF:FF:FF’
     * }
     * }
     *
     * @return
     */
    @Override
    public String toString() {
        return "DeviceMacModel{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
