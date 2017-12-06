package com.androidex.capbox.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.DeviceWatchModel;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.WatchDetialActivity;
import com.androidex.capbox.ui.adapter.WatchListAdapter;
import com.androidex.capbox.ui.widget.ThirdTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.e.ble.bean.BLEDevice;
import com.e.ble.scan.BLEScanCfg;
import com.e.ble.scan.BLEScanListener;
import com.e.ble.util.BLEError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLE.SCAN_PERIOD;

/**
 * 搜索腕表列表
 *
 * @author liyp
 * @editTime 2017/9/27
 */

public class WatchListFragment extends BaseFragment {
    private static final String TAG = "WatchListFragment";
    @Bind(R.id.thirdtitlebar)
    ThirdTitleBar thirdtitlebar;
    @Bind(R.id.device_list_connected)
    ListView listconnected;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    private static String deviceMac = "0C:61:CF:8D:9C:E2";//"0C:61:CF:8D:9C:E2";
    private BleBroadCast bleBroadCast;
    List<Map<String, String>> mylist = new ArrayList<>();
    private WatchListAdapter watchListAdapter;
    private BluetoothAdapter mBtAdapter;
    private boolean mScanning = false;//控制蓝牙扫描
    private boolean isConnectBLE = false;//蓝牙是否连接

    @Override
    public void initData() {
        initTitleBar();
        iniRefreshView();
        initListView();
        watchlist();
        initBle();//蓝牙连接
    }

    private void iniRefreshView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 模拟刷新完成
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        watchlist();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    /**
     * 蓝牙初始化
     */
    private void initBle() {
        initBleReceiver();//初始化蓝牙广播
        // 初始化蓝牙adapter
        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();//打开蓝牙
            Log.d(TAG, "打开蓝牙");
        }
    }

    //开始扫描
    private void scanLeDevice() {
        showProgress("搜索设备中。。。。");
        BLEScanCfg scanCfg = new BLEScanCfg.ScanCfgBuilder(SCAN_PERIOD)
                .builder();
        MyBleService.get().startScanner(scanCfg, new BLEScanListener() {
            @Override
            public void onScannerStart() {

            }

            @Override
            public void onScanning(BLEDevice device) {
                if (device.getMac().equals(deviceMac)) {
                    showProgress("搜索到设备。。。");
                    //showProgress("正在连接设备：" + device.getName());
                    stopScanLe();
                    BleService.get().connectionDevice(context, deviceMac);
                }
            }

            @Override
            public void onScannerStop() {
                stopScanLe();
                Log.e(TAG, "扫描结束");
            }

            @Override
            public void onScannerError(int errorCode) {
                disProgress();
                if (errorCode == BLEError.BLE_CLOSE) {
                    CommonKit.showErrorShort(context, "蓝牙未打开，请打开蓝牙后重试");
                } else {
                    CommonKit.showErrorShort(context, "扫描出现异常");
                }
            }
        });
    }

    private void stopScanLe() {
        disProgress();
        MyBleService.get().stopScan();
    }

    private void initBleReceiver() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        bleBroadCast = new BleBroadCast();
        context.registerReceiver(bleBroadCast, intentFilter);
    }

    private void initListView() {
        //开始扫描
        watchListAdapter = new WatchListAdapter(context, mylist);
        //添加并且显示
        listconnected.setAdapter(watchListAdapter);
        listconnected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putString("name", mylist.get(position).get("name"));
                bundle.putString("mac", mylist.get(position).get("mac"));
                WatchDetialActivity.lauch(context, bundle);
            }
        });
    }

    private void initTitleBar() {
        thirdtitlebar.getLeftBtn().setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        }
    }

    /**
     * 获取设备列表
     */
    public void watchlist() {
        NetApi.watchlist(getToken(), ((MainActivity) context).username, new ResultCallBack<DeviceWatchModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, DeviceWatchModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            for (DeviceWatchModel.device device : model.devicelist) {
                                Map<String, String> map = new HashMap<>();
                                map.put("name", "AndroidExWatch");
                                map.put("mac", device.mac);
                                mylist.add(map);
                            }
                            if (model.devicelist.size() > 0) {
                                L.e(TAG + "刷新列表");
                                watchListAdapter.notifyDataSetChanged();
                            } else {
                                CommonKit.showErrorShort(context, "还未绑定腕表");
                                L.e(TAG + "刷新列表无数据");
                            }
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "您未绑定任何设备");
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showErrorShort(context, "获取数据失败");
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    public class BleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BLE_CONN_FAIL:
                    stopScanLe();
                    Log.e(TAG, "扫描失败");
                    break;

                case BLE_CONN_DIS://断开连接
                    isConnectBLE = false;
                    Log.e(TAG, "蓝牙连接" + "isConnectBLE=" + isConnectBLE + "  mScanning=" + mScanning);
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanLe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bleBroadCast != null) {
            context.unregisterReceiver(bleBroadCast);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_watchlist;
    }
}
