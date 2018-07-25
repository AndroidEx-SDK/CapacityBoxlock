package com.androidex.capbox.ui.activity;

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
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.module.DeviceModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.adapter.BLEDeviceListAdapter;
import com.androidex.capbox.ui.fragment.LockFragment;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.RLog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BIND;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_UUID;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLE.SCAN_PERIOD;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_ADD_DEBUG_DEVICE;
import static com.androidex.capbox.utils.Constants.boxName;

public class AddDeviceActivity extends BaseActivity {
    @Bind(R.id.deviceListView)
    ListView deviceList;
    @Bind(R.id.titlebar)
    SecondTitleBar titlebar;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int DURATION = 1000 * 2;// 动画持续时间
    private static final int REQUEST_ENABLE_BT = 1;// 用于蓝牙setResult
    private BLEDeviceListAdapter mDeviceListAdapter;
    private BleBroadCast bleBroadCast;
    private boolean mScanning = false;//控制蓝牙扫描
    private Animation animation;//动画
    private Handler mHandler;
    private Runnable mRunnable;
    private static int code;
    private String uuid = null;
    private boolean isBind = false;
    private TimerTask timerTask;//定时发送获取UUID
    private Timer timer;

    @Override
    public void initData(Bundle savedInstanceState) {
        initBle();
        initTitle();
        initHandler();
        iniRefreshView();
        initAnimation();//初始化动画
        initListView();
    }

    private void initTitle() {
        titlebar.setRightRes(R.mipmap.device_search);
        titlebar.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDeviceList(true);
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
     * 注册设备连接广播
     */
    private void initBleReceiver() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_UUID);
        intentFilter.addAction(ACTION_BIND);
        bleBroadCast = new BleBroadCast();
        context.registerReceiver(bleBroadCast, intentFilter);
    }

    private void iniRefreshView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        scanLeDeviceList(true);
                        swipeRefreshLayout.setRefreshing(false);
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
            RLog.d("打开蓝牙");
        }
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

    public void initListView() {
        //搜索列表的适配器
        mDeviceListAdapter = new BLEDeviceListAdapter(context, new BLEDeviceListAdapter.IClick() {

            @Override
            public void listViewItemClick(int position, View v) {
                ServiceBean device = MyBleService.get().getConnectDevice(mDeviceListAdapter.getDevice(position).getAddress());
                switch (v.getId()) {
                    case R.id.tv_connect:
                        if (device != null) {
                            RLog.d("断开连接");
                            mDeviceListAdapter.setTextHint(-1, "");//刷新列表的提醒显示
                            device.setActiveDisConnect(true);
                            MyBleService.get().disConnectDevice(mDeviceListAdapter.getDevice(position).getAddress());
                        } else {
                            RLog.d("开始连接");
                            stopScanLe();
                            showProgress(getResources().getString(R.string.device_connect));
                            MyBleService.get().connectionDevice(context, mDeviceListAdapter.getDevice(position).getAddress());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //设置搜索列表的适配器
        deviceList.setAdapter(mDeviceListAdapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                RLog.d("开始连接");
                stopScanLe();
                showProgress(getResources().getString(R.string.device_connect));
                BleService.get().connectionDevice(context, mDeviceListAdapter.getDevice(position).getAddress());
            }
        });
    }

    private void stopScanLe() {
        disProgress();
        MyBleService.get().stopScan();
    }

    /**
     * 绑定箱体
     *
     * @param uuid
     */
    public void bindBox(final String address, final String uuid) {
        NetApi.boxBind(getToken(), getUserName(), uuid, new ResultCallBack<BaseModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            mDeviceListAdapter.setTextHint(-1, "");
                            CommonKit.showOkShort(context, getString(R.string.hint_bind_ok));
                            Event.BoxBindChange boxBindChange = new Event.BoxBindChange();
                            boxBindChange.setDeviceModel(new DeviceModel(address, uuid, "Box"));
                            postSticky(boxBindChange);
                            CommonKit.finishActivity(context);
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "该设备已被绑定");
                            break;
                        case Constants.API.API_FAIL:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showErrorShort(context, "绑定失败");
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
     * 搜索BLE设备：
     *
     * @param enable
     */
    private void scanLeDeviceList(final boolean enable) {
        if (enable) {
            if (mDeviceListAdapter.getCount() > 0) {
                mDeviceListAdapter.clear();
            }
            RLog.d("开始扫描列表");
            titlebar.getRightIv().startAnimation(animation);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            CommonKit.finishActivity(context);
            return;
        }
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
                public void run() {//getDevice().getName
                    if (device.getName() != null) {
                        //过滤搜索到的设备的名字
                        if (device.getName().contains(boxName)) {
                            mDeviceListAdapter.addDevice(device);
                        }
                    } else {
                        String deviceName = Byte2HexUtil.convertHexToString(Byte2HexUtil.byte2Hex(scanRecord));
                        if (!TextUtils.isEmpty(deviceName) && deviceName.contains(boxName)) {
                            mDeviceListAdapter.addDevice(device);
                        }
                    }
                    mDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    /**
     * 接收广播
     */
    public class BleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context mContext, Intent intent) {
            final String address = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://连接成功
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    RLog.d(" AddDevice   连接成功");
                    if (code == REQUESTCODE_ADD_DEBUG_DEVICE) {
                        Intent intent1 = context.getIntent();
                        showProgress("连接成功");
                        Bundle bundle = new Bundle();
                        bundle.putString("address", address);
                        bundle.putInt("tag", 1);
                        intent1.putExtras(bundle);
                        context.setResult(RESULT_OK, intent1);
                        CommonKit.finishActivity(context);
                    } else {
                        showProgress("开始获取UUID...");
                        RLog.d("开始获取UUID");
                        isBind = false;
                        startGetUUID(address);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!isBind) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyBleService.getInstance().disConnectDevice(address);
                                            disProgress();
                                            CommonKit.showErrorShort(context, "绑定超时");
                                            stopGetUUID();
                                        }
                                    });
                                }
                            }
                        }, 4000);
                    }
                    break;
                case ACTION_UUID: {
                    String hexStr = Long.toHexString(Long.parseLong(getUserName().trim()));
                    if (hexStr.length() >= 9) {
                        byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
                        if (b.length >= 16) {
                            byte[] epcBytes = new byte[16];
                            System.arraycopy(b, 0, epcBytes, 0, 16);
                            uuid = Byte2HexUtil.byte2Hex(epcBytes);
                            RLog.d("uuid = " + uuid);
                            MyBleService.get().bind(address, hexStr);
                            showProgress("开始绑定");
                        } else {
                            RLog.d("uuid不正确");
                            showProgress("uuid不正确");
                        }
                    } else {
                        RLog.d("用户信息错误");
                        showProgress("用户信息错误");
                    }
                }
                break;
                case ACTION_BIND:
                    stopGetUUID();
                    if (uuid != null && uuid.length() >= 32) {
                        byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
                        isBind = true;
                        switch (b[1]) {
                            case (byte) 0x01:
                                showProgress("正在绑定");
                                //CommonKit.showErrorShort(context, "绑定成功");
                                bindBox(address, uuid);
                                break;
                            case (byte) 0x02://已被绑定
                                disProgress();
                                MyBleService.getInstance().disConnectDevice(address);
                                byte[] epcBytes = new byte[5];
                                System.arraycopy(b, 2, epcBytes, 0, 5);
                                String mobile = Byte2HexUtil.byte2Hex(epcBytes);
                                Long phone = Long.valueOf(mobile, 16);
                                if (Long.parseLong(getUserName().trim())==phone) {
                                    bindBox(address, uuid);
                                } else {
                                    CommonKit.showErrorShort(context, "已被" + phone + "绑定");
                                }
                                break;
                            case (byte) 0x03://等待服务器确认
                                showProgress("等待服务器确认");
                                break;
                            case (byte) 0x04:
                                disProgress();
                                MyBleService.getInstance().disConnectDevice(address);
                                CommonKit.showErrorShort(context, "连接超时");
                                break;
                            default:
                                disProgress();
                                MyBleService.getInstance().disConnectDevice(address);
                                CommonKit.showErrorShort(context, "出现未知错误");
                                break;
                        }
                    } else {
                        disProgress();
                        MyBleService.getInstance().disConnectDevice(address);
                        CommonKit.showErrorShort(context, "出现未知错误");
                    }
                    break;
                case BLE_CONN_DIS://断开连接
                    disProgress();
                    mDeviceListAdapter.setTextHint(-1, "");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 开始读取信号值
     */
    private void startGetUUID(final String address) {
        stopGetUUID();
        if (timerTask == null) {
            // 通过消息更新
            timerTask = new TimerTask() {

                @Override
                public void run() {// 通过消息更新
                    MyBleService.get().getBleUUID(address);
                }
            };
            if (timer == null) {
                timer = new Timer();
            }
            timer.schedule(timerTask, 300, 1000);// 执行心跳包任务
        }
    }

    /**
     * 停止读取信号值
     */
    private void stopGetUUID() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.cancel();
            timerTask = null;
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bleBroadCast != null)
            unregisterReceiver(bleBroadCast);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanLeDeviceList(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        titlebar.getRightIv().clearAnimation();
        titlebar.getRightIv().setEnabled(false);
        mDeviceListAdapter.clear();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_adddevice;
    }

    public static void lauch(Activity activity, Bundle bundle, int requesCode) {
        code = requesCode;
        CommonKit.startActivityForResult(activity, AddDeviceActivity.class, bundle, requesCode);
    }
}