package com.androidex.capbox.utils;

import android.content.Context;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.service.MyBleService;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/7/20
 */
public class FingerCacheUtil {


    /**
     * 添加开锁指纹缓存
     *
     * @param context
     * @param address
     */
    public static void addOpenFinger(Context context, String address) {
        ServiceBean device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        if (device != null) {
            device.setCarryFinger(true);
            SharedPreTool.getInstance(context).saveObj(device, address);
        } else {
            device = MyBleService.getInstance().getConnectDevice(address);
            if (device != null) {
                device.setCarryFinger(true);
                SharedPreTool.getInstance(context).saveObj(device, address);
            }
        }
        device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        RLog.e("转换后设备参数 录入开锁指纹" + device.toString());
    }

    /**
     * 添加静默指纹缓存
     *
     * @param context
     * @param address
     */
    public static void addBecomeFinger(Context context, String address) {
        ServiceBean device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        if (device != null) {
            device.setBecomeFinger(true);
            SharedPreTool.getInstance(context).saveObj(device, address);
        } else {
            device = MyBleService.getInstance().getConnectDevice(address);
            if (device != null) {
                device.setBecomeFinger(true);
                SharedPreTool.getInstance(context).saveObj(device, address);
            }
        }
        device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        RLog.e("转换后设备参数 录入静默指纹" + device.toString());
    }

    /**
     * 清除指纹缓存
     *
     * @param context
     * @param address
     */
    public static void clearFingerCache(Context context, String address) {
        ServiceBean device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        if (device != null) {
            device.setBecomeFinger(false);
            device.setCarryFinger(false);
            SharedPreTool.getInstance(context).saveObj(device, address);
        } else {
            device = MyBleService.getInstance().getConnectDevice(address);
            if (device != null) {
                device.setBecomeFinger(false);
                device.setCarryFinger(false);
                SharedPreTool.getInstance(context).saveObj(device, address);
            }
        }
        device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        RLog.e("转换后设备参数 录入静默指纹" + device.toString());
    }

    /**
     * 判断是否已经录入开锁指纹
     *
     * @param context
     * @param address
     * @return
     */
    public static boolean isHasOpenFinger(Context context, String address) {
        ServiceBean device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        if (device != null) {
            return device.isCarryFinger();
        }
        return false;
    }

    /**
     * 判断是否已经录入开锁指纹
     *
     * @param context
     * @param address
     * @return
     */
    public static boolean isHasBecomeFinger(Context context, String address) {
        ServiceBean device = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        if (device != null) {
            return device.isBecomeFinger();
        }
        return false;
    }
}
