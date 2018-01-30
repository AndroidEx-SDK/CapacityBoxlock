package com.androidex.capbox.service;

import android.content.Intent;
import android.util.Log;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.ui.activity.LockScreenActivity;
import com.androidex.capbox.utils.RLog;
import com.androidex.capbox.utils.SystemUtil;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ISACTIVEDisConnect;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_IN;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_OUT;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_TEMP_OUT;
import static com.androidex.capbox.utils.Constants.SP.SP_DISTANCE_TYPE;
import static com.androidex.capbox.utils.Constants.SP.SP_LOST_TYPE;
import static com.androidex.capbox.utils.Constants.SP.SP_TEMP_TYPE;
import static com.baidu.mapapi.BMapManager.getContext;

/**
 * @author liyp
 *         应用的服务类
 * @editTime 2017/12/4
 */

public class MyBleService extends BleService {
    public static final String TAG = "MyBleService";
    private static BleService service;

    @Override
    public void onCreate() {
        super.onCreate();
        RLog.e("MyBleService 启动");
        setLockScreenActivity(LockScreenActivity.class);//设置锁屏界面的Activity
    }

    /**
     * @return
     */
    public static BleService getInstance() {
        try {
            service = get();
        } catch (NullPointerException e) {
            Log.e(TAG, e.toString());
            service = new MyBleService();
            Intent bleServer = new Intent(getContext(), MyBleService.class);
            getContext().startService(bleServer);
        }
        return service;
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
        ServiceBean device = getConnectDevice(address);
        if (device != null) {
            device.setStopGetRssi();
            if (device.isActiveDisConnect()) {//判断是否是主动断开，true就不报警,在主动断开的时候就要设置该值为true
                sendDisconnectMessage(address, true);
            } else {
                sendDisconnectMessage(address, false);
            }
        } else {
            sendDisconnectMessage(address, false);
        }
    }

    /**
     * 连接上设备后调用
     *
     * @param address
     */
    @Override
    protected void initDevice(String address) {
        ServiceBean connectDevice = MyBleService.get().getConnectDevice(address);
        if (connectDevice == null) return;
        ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
        if (device != null) {
            RLog.e("device====" + device.toString());
            connectDevice.setPolice(device.isPolice());
            connectDevice.setDistanceAlarm(device.isDistanceAlarm());
            connectDevice.setTamperAlarm(device.isTamperAlarm());
            connectDevice.setTempAlarm(device.isTempAlarm());
            connectDevice.setHumAlarm(device.isHumAlarm());
            RLog.e("转换后设备参数" + connectDevice.toString());
        } else {
            RLog.e("device is null");
        }
    }

    /**
     * 断开连接时发送消息通知
     *
     * @param address
     * @param isActive
     */
    private void sendDisconnectMessage(String address, boolean isActive) {
        if (SharedPreTool.getInstance(this).getBoolData(SharedPreTool.IS_POLICE, true)) {
            ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
            if (device != null && device.isPolice()) {//非主动断开时，报警
                sendBroadDis(address, isActive);
            } else {
                ServiceBean connectDevice = MyBleService.get().getConnectDevice(address);
                if (connectDevice != null && connectDevice.isPolice()) {//非主动断开时，报警
                    sendBroadDis(address, isActive);
                } else {
                    RLog.e("已关闭单个箱子报警开关");
                }
            }
        } else {
            RLog.e("已关闭报警开关");
        }
    }

    /**
     * 发送蓝牙脱距广播
     *
     * @param address
     * @param isActive
     */
    public void sendBroadDis(String address, boolean isActive) {
        switch (SharedPreTool.getInstance(this).getIntData(SP_LOST_TYPE, 0)) {
            case 0:
                sendDisBroadcast(address, isActive);
                if (!isActive) {
                    SystemUtil.startPlayerRaw(getContext());
                }
                break;
            case 1:
                if (!isActive) {
                    SystemUtil.startPlayerRaw(getContext());
                }
                sendDisBroadcast(address, true);
                break;
            case 2:
                sendDisBroadcast(address, isActive);
                break;
            default:
                break;
        }
    }

    private void sendDisBroadcast(String address, boolean isActive) {
        Intent intent = new Intent(BLE_CONN_DIS);
        intent.putExtra(BLECONSTANTS_ADDRESS, address);
        intent.putExtra(BLECONSTANTS_ISACTIVEDisConnect, isActive);
        sendBroadcast(intent);
    }


    /**
     * 连续三次超出阈值会回调这里，1s更新一次信号强度
     */
    @Override
    public void outOfScopeRssi(String address) {
        if (SharedPreTool.getInstance(this).getBoolData(SharedPreTool.IS_POLICE, true)) {
            ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
            if (device != null && device.isDistanceAlarm()) {//信号弱，报警
                sendRSSIOut(address);
            } else {
                ServiceBean connectDevice = MyBleService.get().getConnectDevice(address);
                if (connectDevice != null && connectDevice.isDistanceAlarm()) {//信号弱，报警
                    sendRSSIOut(address);
                } else {
                    RLog.e("已关闭单个箱子距离报警开关");
                }
            }
        } else {
            RLog.e("已关闭报警开关");
        }
    }

    /**
     * 发送信号弱的广播
     *
     * @param address
     */
    private void sendRSSIOut(String address) {
        switch (SharedPreTool.getInstance(this).getIntData(SP_DISTANCE_TYPE, 0)) {
            case 0:
                sendTempOutBroad(address, ACTION_RSSI_OUT);
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 1:
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 2:
                sendTempOutBroad(address, ACTION_RSSI_OUT);
                break;
            default:
                break;
        }

    }

    /**
     * 超出阈值调用报警后，又恢复到阈值范围内，停止报警回调
     */
    @Override
    public void inOfScopeRssi(String address) {
        sendTempOutBroad(address, ACTION_RSSI_IN);
        SystemUtil.stopPlayRaw();
    }

    /**
     * 温度超范围报警
     */
    @Override
    public void outOfScopeTempPolice(String address) {
        if (SharedPreTool.getInstance(this).getBoolData(SharedPreTool.IS_POLICE, true)) {
            ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
            if (device != null && device.isTempAlarm()) {//非主动断开时，报警
                RLog.e("发送广播，温度超范围报警");
                sendTempOutBroadcast(address);
            } else {
                ServiceBean connectDevice = MyBleService.get().getConnectDevice(address);
                if (connectDevice != null && connectDevice.isTempAlarm()) {//非主动断开时，报警
                    RLog.e("发送广播，温度超范围报警");
                    sendTempOutBroadcast(address);
                } else {
                    RLog.e("已关闭单个箱子温度报警开关");
                }
            }
        } else {
            RLog.e("已关闭报警开关");
        }
    }

    /**
     * 发送温度超范围广播
     *
     * @param address
     */
    private void sendTempOutBroadcast(String address) {
        switch (SharedPreTool.getInstance(this).getIntData(SP_TEMP_TYPE, 0)) {
            case 0:
                sendTempOutBroad(address, ACTION_TEMP_OUT);
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 1:
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 2:
                sendTempOutBroad(address, ACTION_TEMP_OUT);
                break;
            default:
                break;
        }
    }

    private void sendTempOutBroad(String address, String actionTempOut) {
        Intent intent = new Intent(actionTempOut);
        intent.putExtra(BLECONSTANTS_ADDRESS, address);
        sendBroadcast(intent);
    }

    /**
     * 温度又恢复到范围内
     */
    @Override
    public void inOfScopeTempPolice(String address) {
        SystemUtil.stopPlayRaw();
    }

}
