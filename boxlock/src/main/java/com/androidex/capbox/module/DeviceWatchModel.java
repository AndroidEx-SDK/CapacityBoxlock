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
public class DeviceWatchModel extends BaseModel {

    public List<device> devicelist;

    public class device {
        public String mac;

    }

    @Override
    public String toString() {
        return "DeviceModel{" +
                "code=" + code +
                ", devicelist='" + devicelist + '\'' +
                ", info='" + info + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                '}';
    }

}
