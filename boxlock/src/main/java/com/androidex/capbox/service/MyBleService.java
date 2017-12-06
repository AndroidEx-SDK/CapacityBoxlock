package com.androidex.capbox.service;

import android.util.Log;

import com.androidex.boxlib.modules.ConnectedDevice;
import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.utils.SystemUtil;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.baidu.mapapi.BMapManager.getContext;

/**
 * @author liyp
 *         应用的服务类
 * @editTime 2017/12/4
 */

public class MyBleService extends BleService {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BleService", "MyBleService 启动");

    }

    /**
     * 异常断开后调用，主动断开需要调用以下方法可不收到断开信息，不必做处理。
     * ServiceBean device = ConnectedDevice.get().getConnectDevice(address);
     * if (device != null) {
     * device.setActiveDisConnect(true);
     * }
     *
     * @param address
     */
    @Override
    public void disConnect(String address) {
        super.disConnect(address);
        ServiceBean device = ConnectedDevice.get().getConnectDevice(address);
        if (device != null) {
            if (!device.isActiveDisConnect()) {
                sendBleState(BLE_CONN_DIS, address);
                SystemUtil.startPlayerRaw(getContext());
            }
            ConnectedDevice.get().removeConnectMap(address);
        }
    }
}
