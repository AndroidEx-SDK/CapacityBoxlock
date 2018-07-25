package com.androidex.capbox.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.utils.CalendarUtil;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.DialogUtils;
import com.androidex.capbox.utils.RLog;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_LOCK_OPEN_SUCCED;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_LOCK_STARTS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_OFF;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_ON;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_IN;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_OUT;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_TEMP_OUT;

public class ScreenItemFragment extends BaseFragment {
    @Bind(R.id.iv_last)
    ImageView iv_last;
    @Bind(R.id.iv_next)
    ImageView iv_next;
    @Bind(R.id.iv_lock)
    ImageView iv_lock;
    @Bind(R.id.iv_connect)
    ImageView iv_connect;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.ll_fragmentScreenItem)
    LinearLayout ll_fragmentScreenItem;

    BoxDeviceModel.device item;

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        item = (BoxDeviceModel.device) bundle.getSerializable("item");
        if (item == null) return;
        initView();
        initBleBroadCast();
    }

    /**
     * 初始化蓝牙广播
     */
    private void initBleBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(BLUTOOTH_OFF);//手机蓝牙关闭
        intentFilter.addAction(BLUTOOTH_ON);//手机蓝牙关闭

        intentFilter.addAction(ACTION_LOCK_STARTS);
        intentFilter.addAction(ACTION_LOCK_OPEN_SUCCED);
        intentFilter.addAction(ACTION_TEMP_OUT);//温度超范围
        intentFilter.addAction(ACTION_RSSI_OUT);//信号值超出范围内
        intentFilter.addAction(ACTION_RSSI_IN);//信号值回到范围内
        context.registerReceiver(lockScreenReceiver, intentFilter);
    }

    BroadcastReceiver lockScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            String address = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            if (!address.equals(item.getMac())) return;
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://重复连接
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    RLog.e("lockscreen  连接");
                    iv_connect.setImageResource(R.mipmap.starts_disconnect);
                    //disSystemDialog();
                    CommonKit.showOkToast(context, getResources().getString(R.string.bledevice_toast3));
                    break;
                case BLE_CONN_DIS://蓝牙断开
                    RLog.e("lockscreen  断开");
                    iv_connect.setImageResource(R.mipmap.starts_connect);
                    CommonKit.showOkToast(context, getResources().getString(R.string.bledevice_toast4));
                    break;
                case BLE_CONN_FAIL://连接失败
                    disProgress();
                    RLog.e("lockscreen  连接失败");
                    iv_connect.setImageResource(R.mipmap.starts_connect);
                    CommonKit.showErrorToast(context, getResources().getString(R.string.bledevice_toast8));
                    break;
                case ACTION_LOCK_OPEN_SUCCED:
                    CommonKit.showOkToast(context, "开锁成功");
                    break;
                case ACTION_LOCK_STARTS://锁状态FB 32 00 01 00 00 FE
                    byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
                    if (b.length == 1) {
                        if (b[0] == (byte) 0x01) {
                            iv_lock.setImageResource(R.mipmap.lock_open);
                        } else {
                            iv_lock.setImageResource(R.mipmap.lock_close);
                        }
                    } else if (b.length > 1) {
                        if (b[1] == (byte) 0x01) {
                            iv_lock.setImageResource(R.mipmap.lock_open);
                        } else {
                            iv_lock.setImageResource(R.mipmap.lock_close);
                        }
                    }
                    break;
                case ACTION_TEMP_OUT://温度超范围
                    break;
                case ACTION_RSSI_OUT:
                    break;
                case ACTION_RSSI_IN:
                    break;
            }
        }
    };

    private void initView() {
        ll_fragmentScreenItem.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                RLog.e("fragementScreenItem onUIChange i = " + i);
            }
        });
        item.boxName = CalendarUtil.getName(item.getMac());
        tv_name.setText(item.boxName);
        iv_lock.setImageResource(R.mipmap.lock_close);
        if (MyBleService.getInstance().getConnectDevice(item.getMac()) == null) {
            RLog.e("蓝牙没连接");
            iv_connect.setImageResource(R.mipmap.starts_connect);
        } else {
            RLog.e("蓝牙已连接");
            iv_connect.setImageResource(R.mipmap.starts_disconnect);
        }
        iv_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyBleService.getInstance().getConnectDevice(item.getMac()) == null) {
                    RLog.e("lockscreen  点击连接");
                    //showSystemDialog("正在连接");
                    MyBleService.getInstance().connectionDevice(context, item.getMac());
                } else {
                    RLog.e("lockscreen  点击断开");
                    ServiceBean device = MyBleService.getInstance().getConnectDevice(item.getMac());
                    if (device != null) {
                        device.setActiveDisConnect(true);
                    }
                    MyBleService.getInstance().disConnectDevice(item.getMac());
                }
            }
        });
        iv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyBleService.getInstance().getConnectDevice(item.getMac()) == null) {
                    CommonKit.showErrorToast(context, context.getResources().getString(R.string.bledevice_toast7));
                } else {
                    MyBleService.getInstance().openLock(item.getMac());
                }
            }
        });
        iv_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new Event.PreviousPage());
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new Event.NextPage());
            }
        });
    }

    @Override
    public void setListener() {

    }

    private android.app.Dialog sysdialog;

    public void showSystemDialog(String msg) {
        if (sysdialog == null) {
            sysdialog = DialogUtils.showDialog(context, msg);
        }
        TextView tv = (TextView) sysdialog.findViewById(R.id.msg);
        tv.setText(msg);
        if (!sysdialog.isShowing()) {
            sysdialog.setCancelable(true);
            sysdialog.show();
        }
    }

    public void disSystemDialog() {
        if (sysdialog != null && sysdialog.isShowing()) {
            sysdialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lockScreenReceiver != null) {
            context.unregisterReceiver(lockScreenReceiver);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_binddevice;
    }
}
