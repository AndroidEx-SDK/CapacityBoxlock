package com.androidex.capbox.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.androidex.capbox.R;
import com.androidex.capbox.service.DfuService;

import no.nordicsemi.android.dfu.DfuServiceInitiator;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/8/2
 */
public class BleUpdateUtil {

    /**
     * 启动DFU升级服务
     *
     * @param bluetoothDevice 蓝牙设备
     * @param keepBond        升级后是否保持连接
     * @param force           将DFU设置为true将防止跳转到DFU Bootloader引导加载程序模式
     * @param PacketsReceipt  启用或禁用数据包接收通知（PRN）过程。
     *                        默认情况下，在使用Android Marshmallow或更高版本的设备上禁用PEN，并在旧设备上启用。
     * @param numberOfPackets 如果启用分组接收通知过程，则此方法设置在接收PEN之前要发送的分组数。 PEN用于同步发射器和接收器。
     * @param filePath        约定匹配的ZIP文件的路径。
     */
    public static void startDFU(Context context, BluetoothDevice bluetoothDevice, boolean keepBond, boolean force,
                                boolean PacketsReceipt, int numberOfPackets, String filePath) {
        final DfuServiceInitiator stater = new DfuServiceInitiator(bluetoothDevice.getAddress())
                .setDeviceName(bluetoothDevice.getName())
                .setKeepBond(keepBond)
                .setForceDfu(force)
                .setPacketsReceiptNotificationsEnabled(PacketsReceipt)
                .setPacketsReceiptNotificationsValue(numberOfPackets);
        stater.setZip(R.raw.update);//这个方法可以传入raw文件夹中的文件、也可以是文件的string或者url路径。
        stater.start(context, DfuService.class);
    }
}
