package com.androidex.capbox.service;

import android.content.Intent;
import android.util.Log;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.utils.SystemUtil;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
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
        setRssiMaxValue(-80);//设置距离报警的阈值
    }

    /**
     * 异常断开后调用，主动断开需要调用以下方法可不收到断开信息，不必做处理。
     * ServiceBean device = getConnectDevice(address);
     * if (device != null) {
     * device.setActiveDisConnect(true);
     * }
     *
     * @param address
     */
    @Override
    public void disConnect(String address) {
        super.disConnect(address);
        ServiceBean device = getConnectDevice(address);
        if (device != null) {
            device.setStopGetRssi();
            if (!device.isActiveDisConnect()) {//判断是否是主动断开，true就不报警,在主动断开的时候就要设置该值为true
                Intent intent = new Intent(BLE_CONN_DIS);
                intent.putExtra(BLECONSTANTS_ADDRESS, address);
                sendBroadcast(intent);
                SystemUtil.startPlayerRaw(getContext());
            }
        }
    }

    @Override
    public void outOfScopeRssi() {
        SystemUtil.startPlayerRaw(getContext());
    }
}
