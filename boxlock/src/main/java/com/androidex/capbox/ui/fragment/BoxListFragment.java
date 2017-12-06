package com.androidex.capbox.ui.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;

import com.acker.simplezxing.activity.CaptureActivity;
import com.androidex.boxlib.modules.ConnectedDevice;
import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.BoxDetailActivity;
import com.androidex.capbox.ui.activity.LoginActivity;
import com.androidex.capbox.ui.adapter.BLEDeviceListAdapter;
import com.androidex.capbox.ui.adapter.BoxListAdapter;
import com.androidex.capbox.ui.widget.ThirdTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.e.ble.bean.BLEDevice;
import com.e.ble.scan.BLEScanCfg;
import com.e.ble.scan.BLEScanListener;
import com.e.ble.scan.BLEScanner;
import com.e.ble.util.BLEError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;

/**
 * 箱体列表
 *
 * @author liyp
 * @editTime 2017/9/27
 */

public class BoxListFragment extends BaseFragment {
    private static final String TAG = "BoxListFragment";
    @Bind(R.id.thirdtitlebar)
    ThirdTitleBar thirdtitlebar;
    @Bind(R.id.device_list_connected)
    ListView listconnected;
    @Bind(R.id.listview_search)
    ListView listview_search;
    @Bind(R.id.swipe_searchDevices)
    SwipeRefreshLayout swipe_searchDevices;

    private BleBroadCast bleBroadCast;
    List<Map<String, String>> mylist = new ArrayList<>();
    private Animation animation;//动画
    private static final long SCAN_PERIOD = 10000;
    private int DURATION = 1000 * 2;// 动画持续时间
    private static final int REQUEST_ENABLE_BT = 1;// 用于蓝牙setResult
    private boolean mScanning = false;//控制蓝牙扫描
    private Timer timer_scanBle;// 扫描蓝牙时定时器
    private TimerTask task_scanBle;
    private BoxListAdapter boxListAdapter;
    private BLEDeviceListAdapter mDeviceListAdapter;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    public void initData() {
        initHandler();
        initAnimation();//初始化动画
        initTitleBar();
        iniRefreshView();
        initListView();
        initBle();//蓝牙连接
    }

    private void initTitleBar() {
        //扫描
        thirdtitlebar.getLeftBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CaptureActivity.class);
                startActivityForResult(intent, CaptureActivity.REQ_CODE);// ,//Activity.RESULT_FIRST_USER
            }
        });
        thirdtitlebar.setRightRes(R.drawable.device_search);
        //搜索
        thirdtitlebar.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxlist();
                scanLeDeviceList(true);
            }
        });
    }

    /**
     * 旋转动画
     */
    private void initAnimation() {
        animation = AnimationUtils.loadAnimation(context, R.anim.scan_anim);
        /** 设置旋转动画 */
        animation.setDuration(DURATION);// 设置动画持续时间
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount((int) ((SCAN_PERIOD / DURATION) - 1));// 设置重复次数
        animation.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
    }

    /**
     * 初始化搜索设备的定时器
     */
    private void initHandler() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                scanLeDeviceList(false);
                animation.cancel();
            }
        };
    }

    /**
     * 初始化两个列表的ListView
     */
    private void initListView() {
        //已绑定设备的适配器
        boxListAdapter = new BoxListAdapter(context, mylist, new BoxListAdapter.IClick() {

            @Override
            public void listViewItemClick(int position, View v) {
                switch (v.getId()) {
                    case R.id.rl_normal:
                        if (ConnectedDevice.get().getConnectDevice(mylist.get(position).get("mac")) != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", mylist.get(position).get("name"));
                            bundle.putString("uuid", mylist.get(position).get("uuid"));
                            bundle.putString("mac", mylist.get(position).get("mac"));
                            BoxDetailActivity.lauch(getActivity(), bundle);
                        } else {
                            CommonKit.showOkShort(context, "开始扫描...");
                            scanLeDevice(mylist.get(position).get("mac"), mylist.get(position).get("uuid"));//开始扫描
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //设置已绑定列表的适配器
        listconnected.setAdapter(boxListAdapter);
        //搜索列表的适配器
        mDeviceListAdapter = new BLEDeviceListAdapter(context, new BLEDeviceListAdapter.IClick() {

            @Override
            public void listViewItemClick(int position, View v) {
                switch (v.getId()) {
                    case R.id.tv_connect:
                        ServiceBean device = ConnectedDevice.get().getConnectDevice(mDeviceListAdapter.getDevice(position).getAddress());
                        if (device != null) {
                            Log.d(TAG, "断开连接");
                            mDeviceListAdapter.setTextHint(-1, "");//刷新列表的提醒显示
                            device.setActiveDisConnect(true);
                            MyBleService.get().disConnectDevice(mDeviceListAdapter.getDevice(position).getAddress());
                        } else {
                            Log.d(TAG, "开始绑定");
                            showProgress(getResources().getString(R.string.device_connect));
                            stopScanLe();
                            MyBleService.get().connectionDevice(context, mDeviceListAdapter.getDevice(position).getAddress());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //设置搜索列表的适配器
        listview_search.setAdapter(mDeviceListAdapter);
        listview_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "开始绑定");
                showProgress(getResources().getString(R.string.device_connect));
                stopScanLe();
                BleService.get().connectionDevice(context, mDeviceListAdapter.getDevice(position).getAddress());

            }
        });
    }

    private void iniRefreshView() {
        swipe_searchDevices.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 模拟刷新完成
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        boxlist();
                        scanLeDeviceList(true);
                        swipe_searchDevices.setRefreshing(false);
                    }
                }, 1000);
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

    /**
     * 开始扫描
     */
    private void scanLeDevice(final String address, final String deviceUUID) {
        showProgress("搜索设备...");
        BLEScanCfg scanCfg = new BLEScanCfg.ScanCfgBuilder(SCAN_PERIOD).builder();
        BLEScanner.get().startScanner(scanCfg, new BLEScanListener() {
            boolean isScanDevice;//是否扫描到设备

            @Override
            public void onScannerStart() {
                isScanDevice = false;
            }

            @Override
            public void onScanning(BLEDevice device) {
                if (device.getMac().equals(address)) {
                    showProgress("搜索到设备...");
                    stopScanLe();
                    isScanDevice = true;
                    synchronized (this) {
                        Log.d(TAG, "搜索到设备mScanning=" + mScanning);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", device.getName());
                        bundle.putString("uuid", deviceUUID);
                        bundle.putString("mac", address);
                        BoxDetailActivity.lauch(getActivity(), bundle);
                    }
                }
            }

            @Override
            public void onScannerStop() {
                stopScanLe();
                Log.e(TAG, "扫描结束");
                if (!isScanDevice) {
                    CommonKit.showErrorShort(context, "未搜索到设备");
                }
            }

            @Override
            public void onScannerError(int errorCode) {
                stopScanLe();
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
        BLEScanner.get().stopScan();
    }

    /**
     * 获取设备的UUID
     *
     * @param flag
     */
    private void startGetUUID(boolean flag, final String mac) {
        if (flag) {
            Log.e(TAG, "启动自动发送");
            startGetUUID(false, null);
            if (task_scanBle == null) {
                task_scanBle = new TimerTask() {
                    @Override
                    public void run() {
                        if (mac != null) {
                            MyBleService.get().getDevice().getUUID(mac);
                        }
                    }
                };
            }
            if (timer_scanBle == null) {
                timer_scanBle = new Timer();
            }
            timer_scanBle.schedule(task_scanBle, 500, 5 * 1000);//延迟1s后执行
        } else {
            Log.e(TAG, "停止自动发送");
            if (task_scanBle != null) {
                task_scanBle.cancel();
                task_scanBle = null;
            }
            if (timer_scanBle != null) {
                timer_scanBle.cancel();
                timer_scanBle = null;
            }
        }
    }

    /**
     * 注册设备连接广播
     */
    private void initBleReceiver() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(Constants.BLE.ACTION_UUID);//获取UUID
        bleBroadCast = new BleBroadCast();
        context.registerReceiver(bleBroadCast, intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE://二维码扫描回调
                switch (resultCode) {
                    case RESULT_OK:
                        CommonKit.showMsgShort(context, "扫描成功");
                        String uuid = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        uuid = uuid.replace("", "-");
                        Log.e(TAG, "扫描结果：" + data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));  //or do sth
                        if (uuid.length() > 32) {
                            bindBox(uuid.trim());//扫描到UUID
                        } else {
                            CommonKit.showMsgShort(context, "扫描失败");
                        }
                        break;
                    case RESULT_CANCELED:
                        CommonKit.showMsgShort(context, "取消扫描");
                        break;
                }
                break;
        }
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            CommonKit.finishActivity(context);
            return;
        }
    }

    /**
     * 绑定箱体
     *
     * @param uuid
     */
    public void bindBox(String uuid) {
        NetApi.boxBind(getToken(), ((MainActivity) context).username, uuid, new ResultCallBack<BaseModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            showProgress("绑定成功，正在刷新列表...");
                            mDeviceListAdapter.setTextHint(-1, "");
                            CommonKit.showOkShort(context, getString(R.string.hint_bind_ok));
                            boxlist();//返回成功后刷新列表
                            postSticky(new Event.BoxBindChange());
                            break;
                        case Constants.API.API_NOPERMMISION:
                            showProgress("该设备已被绑定");
                            CommonKit.showErrorShort(context, "该设备已被绑定");
                            break;
                        case Constants.API.API_FAIL:
                            if (model.info != null) {
                                showProgress(model.info);
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showErrorShort(context, "绑定失败");
                                showProgress("绑定失败");
                            }
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showErrorShort(context, "该设备未注册");
                            }
                            break;
                    }
                    disProgress();
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                CommonKit.showErrorShort(context, "绑定失败，检查网络连接");
                disProgress();
            }
        });
    }

    /**
     * 获取设备列表
     */
    public void boxlist() {
        NetApi.boxlist(getToken(), ((MainActivity) context).username, new ResultCallBack<BoxDeviceModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            mylist.clear();
                            for (BoxDeviceModel.device device : model.devicelist) {
                                Map<String, String> map = new HashMap<>();
                                map.put("name", device.boxName);
                                map.put("uuid", device.uuid);
                                map.put("mac", device.mac);
                                map.put("deviceStatus", "" + device.deviceStatus);
                                map.put("isdefault", "" + device.isDefault);
                                map.put("isOnLine", "" + device.isOnLine);
                                mylist.add(map);
                            }
                            if (model.devicelist.size() > 0) {
                                L.e(TAG + "刷新列表");
                                boxListAdapter.notifyDataSetChanged();
                            } else {
                                CommonKit.showErrorShort(context, "请绑定箱体");
                                L.e(TAG + "刷新列表无数据");
                            }
                            showProgress("刷新完成");
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
                            disProgress();
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
                disProgress();
                scanLeDeviceList(true);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                showProgress("刷新列表失败");
                disProgress();
                CommonKit.showErrorShort(context, "网络连接异常");
                scanLeDeviceList(true);
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

    /**
     * 接收广播
     */
    public class BleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mac = intent.getStringExtra("deviceMac");
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://连接成功
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    BleService.get().enableNotify(mac);
                    showProgress("连接成功...");
                    Log.e(TAG, "开始获取UUID");
                    startGetUUID(true, mac);
                    break;

                case BLE_CONN_DIS://断开连接
                    startGetUUID(false, null);
                    disProgress();
                    mDeviceListAdapter.setTextHint(-1, "");
                    setLostAlarm("");//防丢报警设置
                    break;

                case Constants.BLE.ACTION_UUID:
                    byte[] b = intent.getByteArrayExtra("data");
                    if (b.length >= 20) {
                        showProgress("开始绑定...");
                        startGetUUID(false, null);
                        byte[] b_uuid = new byte[b.length - 4];
                        System.arraycopy(b, 4, b_uuid, 0, b.length - 4);
                        String uuid = Byte2HexUtil.byte2Hex(b_uuid).trim();
                        Log.e(TAG, "uuid=" + uuid);
                        if (uuid != null) {
                            showProgress("正在绑定...");
                            bindBox(uuid.trim());
                        }
                    }
                    ConnectedDevice.get().getConnectDevice(mac).setActiveDisConnect(true);
                    BleService.get().disConnectDevice(mac);
                    mDeviceListAdapter.setTextHint(-1, "");
                    if (!mScanning) {
                        scanLeDeviceList(true);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 搜索BLE设备：
     *
     * @param enable
     */
    private void scanLeDeviceList(final boolean enable) {
        if (enable) {
            if (mDeviceListAdapter.getCount() > 0) {
                mDeviceListAdapter.clear();
            }
            Log.d(TAG, "开始扫描列表");
            thirdtitlebar.getRightIv().startAnimation(animation);
            mScanning = true;
            mHandler.postDelayed(mRunnable, SCAN_PERIOD);
            mBtAdapter.startLeScan(mLeListScanCallback);
        } else {
            mScanning = false;
            mHandler.removeCallbacks(mRunnable);
            mBtAdapter.stopLeScan(mLeListScanCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boxlist();
        scanLeDeviceList(true);
    }

    /**
     * BluetoothAdapter.LeScanCallback接口，BLE设备的搜索结果将通过这个callback返回。
     * Device scan callback.蓝牙搜索回调
     */
    private BluetoothAdapter.LeScanCallback mLeListScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mylist.size() > 0) {
                        boolean flag = true;
                        for (Map map : mylist) {
                            if (device.getAddress().equals(map.get("mac"))) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            if (device.getName() != null) {
                                //过滤搜索到的设备的名字
                                if (device.getName().contains(LockFragment.boxName)) {
                                    mDeviceListAdapter.addDevice(device);
                                }
                            }
                        }
                    } else {
                        if (device.getName() != null) {
                            //过滤搜索到的设备的名字
                            if (device.getName().contains(LockFragment.boxName)) {
                                mDeviceListAdapter.addDevice(device);
                            }
                        }
                    }
                    mDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        thirdtitlebar.getRightIv().clearAnimation();
        thirdtitlebar.getRightIv().setEnabled(false);
        mDeviceListAdapter.clear();
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
        return R.layout.fragment_boxlist;
    }
}
