package com.androidex.capbox.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.adapter.BindDeviceAdapter;
import com.androidex.capbox.ui.view.CustomRecyclerView;
import com.androidex.capbox.ui.view.ZItem;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_HEART;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_LOCK_OPEN_SUCCED;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_LOCK_STARTS;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_TEMP_UPDATE;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_RSSI_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_RSSI_SUCCED;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_OFF;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_ON;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_IN;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_OUT;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_TEMP_OUT;

public class LockScreenActivity extends BaseActivity {
    public static String TAG = "LockScreenActivity";
    @Bind(R.id.textView1)
    ZItem xitem;
    @Bind(R.id.qtRecyclerView)
    CustomRecyclerView qtRecyclerView;
    private BindDeviceAdapter adapter;
    private List<BoxDeviceModel.device> devicelist;

    @Override
    public void initData(Bundle savedInstanceState) {
        Log.e(TAG, "锁屏界面启动");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED    //这个在锁屏状态下
                //| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON                    //这个是点亮屏幕
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD                //这个是透过锁屏界面，相当与解锁，但实质没有
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);                //这个是保持屏幕常亮。
        initData();
        registerEventBusSticky();//注册Event
        initBleBroadCast();
        initRecyclerView();
        boxlist();
    }

    private void initRecyclerView() {
        adapter = new BindDeviceAdapter(context);
        qtRecyclerView.horizontalLayoutManager(context).defaultNoDivider();
        qtRecyclerView.setAdapter(adapter);
        qtRecyclerView.setOnRefreshAndLoadMoreListener(new CustomRecyclerView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                boxlist();
            }

            @Override
            public void onLoadMore(int page) {

            }
        });
    }

    /**
     * 初始化蓝牙广播
     */
    private void initBleBroadCast() {
        Log.e("BaseActivity", "--注册蓝牙广播");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_LOCK_STARTS);
        intentFilter.addAction(ACTION_TEMP_UPDATE);
        intentFilter.addAction(BLE_CONN_RSSI_SUCCED);
        intentFilter.addAction(BLE_CONN_RSSI_FAIL);
        intentFilter.addAction(ACTION_HEART);
        intentFilter.addAction(BLUTOOTH_OFF);//手机蓝牙关闭
        intentFilter.addAction(BLUTOOTH_ON);//手机蓝牙关闭
        intentFilter.addAction(ACTION_LOCK_OPEN_SUCCED);
        intentFilter.addAction(ACTION_TEMP_OUT);//温度超范围
        intentFilter.addAction(ACTION_RSSI_OUT);//信号值超出范围内
        intentFilter.addAction(ACTION_RSSI_IN);//信号值回到范围内
        registerReceiver(lockScreenReceiver, intentFilter);
    }

    public void initData() {
        xitem.setZItemListener(new ZItem.ZItemListener() {

            @Override
            public void onRight() {
                CommonKit.finishActivity(LockScreenActivity.this);
//                Intent home = new Intent(Intent.ACTION_MAIN);
//                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                home.addCategory(Intent.CATEGORY_HOME);
//                startActivity(home);
            }

            @Override
            public void onLeft() {

            }
        });
    }

    /**
     * 更新状态
     *
     * @param event
     */
    public void onEvent(Event.BoxBindChange event) {
        //刷新数据
        boxlist();
    }

    /**
     * 解除绑定时触发
     *
     * @param event
     */
    public void onEvent(Event.BoxRelieveBind event) {
        boxlist();
    }

    /**
     * 蓝牙连接
     *
     * @param event
     */
    public void onEvent(Event.BleConnected event) {
        Log.e(TAG, "onEvent connect " + event.getAddress());
        BleService.get().connectionDevice(context, event.getAddress());
    }

    /**
     * 蓝牙断开
     *
     * @param event
     */
    public void onEvent(Event.BleDisConnected event) {
        Log.e(TAG, "onEvent disconnect " + event.getAddress());
        ServiceBean device = MyBleService.get().getConnectDevice(event.getAddress());
        if (device != null) {
            device.setActiveDisConnect(true);
        }
        MyBleService.get().disConnectDevice(event.getAddress());
    }

    @OnClick({

    })
    public void clickEvent(View view) {
        switch (view.getId()) {

        }
    }

    /**
     * 获取设备列表
     */
    public void boxlist() {
        NetApi.boxlist(getToken(), getUserName(), new ResultCallBack<BoxDeviceModel>() {

            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            if (model != null) {
                                if (model.devicelist != null && !model.devicelist.isEmpty()) {
                                    devicelist = model.devicelist;
                                    adapter.setData(model.devicelist);
                                }
                            }
                            Logd(TAG, "刷新列表");
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "账号在其他地方登录");
                            LoginActivity.lauch(context);
                            showProgress("刷新失败");
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            showProgress("刷新失败");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
                disProgress();
                //scanLeDeviceList(true);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                disProgress();
                if (context != null) {
                    showProgress("刷新列表失败");
                }
                disProgress();
                CommonKit.showErrorShort(context, "网络连接异常");
                //scanLeDeviceList(true);
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

    BroadcastReceiver lockScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            String address = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
            if (address == null) return;
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://重复连接
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    BleService.get().enableNotify(address);
                    disProgress();
                    if (devicelist != null) {
                        adapter.setData(devicelist);
                    }
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast3));
                    break;
                case BLE_CONN_DIS://蓝牙断开
                    if (devicelist != null) {
                        adapter.setData(devicelist);
                    }
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast4));
                    break;
                case BLE_CONN_FAIL://连接失败
                    disProgress();
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast8));
                    break;
                case BLUTOOTH_OFF:
                    Logd(TAG, "手机蓝牙断开");
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast9));
                    MyBleService.get().disConnectDeviceALL();
                    //刷新数据
                    if (devicelist != null) {
                        adapter.setData(devicelist);
                    }
                    break;
                case BLUTOOTH_ON:
                    Logd(TAG, "手机蓝牙开启");
                    CommonKit.showOkShort(context, "手机蓝牙开启");
                    //scanLeDevice();
                    break;
                case ACTION_LOCK_OPEN_SUCCED:
                    CommonKit.showOkShort(context, "开锁成功");
                    MyBleService.get().getLockStatus(address);
                    break;
                case ACTION_LOCK_STARTS://锁状态FB 32 00 01 00 00 FE
                    if (b[2] == (byte) 0x01) {
                        //tv_status.setText("已打开");
                    } else {
                        //tv_status.setText("已关闭");
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

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void setListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(lockScreenReceiver);
        unregisterEventBus();
        Loge(TAG, "锁屏界面退出");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lock_screen;
    }

}
