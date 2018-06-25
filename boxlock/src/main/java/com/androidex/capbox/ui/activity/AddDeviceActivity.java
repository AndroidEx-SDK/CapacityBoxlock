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

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_UUID;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLE.SCAN_PERIOD;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_ADD_DEVICE;

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
    private Timer timer_scanBle;// 扫描蓝牙时定时器
    private TimerTask task_scanBle;
    private boolean mScanning = false;//控制蓝牙扫描
    private Animation animation;//动画
    private Handler mHandler;
    private Runnable mRunnable;

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
        intentFilter.addAction(ACTION_UUID);//获取UUID
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
                            RLog.d("开始绑定");
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
                RLog.d("开始绑定");
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
     * 获取设备的UUID
     *
     * @param flag
     */
    private void startGetUUID(boolean flag, final String mac) {
        if (flag) {
            RLog.e("启动自动发送");
            startGetUUID(false, null);
            if (task_scanBle == null) {
                task_scanBle = new TimerTask() {
                    @Override
                    public void run() {
                        if (mac != null) {
                            MyBleService.get().getUUID(mac);
                        }
                    }
                };
            }
            if (timer_scanBle == null) {
                timer_scanBle = new Timer();
            }
            timer_scanBle.schedule(task_scanBle, 500, 5 * 1000);//延迟1s后执行
        } else {
            RLog.e("停止自动发送");
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
     * 绑定箱体
     *
     * @param uuid
     */
    public void bindBox(String uuid) {
        NetApi.boxBind(getToken(), getUserName(), uuid, new ResultCallBack<BaseModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            mDeviceListAdapter.setTextHint(-1, "");
                            CommonKit.showOkShort(context, getString(R.string.hint_bind_ok));
                            postSticky(new Event.BoxBindChange());
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
                public void run() {
                    if (device.getName() != null) {
                        //过滤搜索到的设备的名字
                        if (device.getName().contains(LockFragment.boxName)) {
                            mDeviceListAdapter.addDevice(device);
                        }
                        mDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    /**
     * 接收广播
     */
    public class BleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://连接成功
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    BleService.get().enableNotify(mac);
                    showProgress("正在绑定...");
                    RLog.d("开始获取UUID");
                    startGetUUID(true, mac);
                    break;

                case BLE_CONN_DIS://断开连接
                    startGetUUID(false, null);
                    disProgress();
                    mDeviceListAdapter.setTextHint(-1, "");
                    break;
                case BLE_CONN_FAIL:
                    disProgress();
                    CommonKit.showErrorShort(context, "连接错误，请重新绑定");
                    break;
                case ACTION_UUID:
                    String uuid = intent.getStringExtra(BLECONSTANTS_DATA);
                    if (!TextUtils.isEmpty(uuid)&&uuid.length()>=32){
                        RLog.e("uuid=" + uuid);
                        startGetUUID(false, null);
                        showProgress("正在绑定...");
                        bindBox(uuid.trim());
                    }
                    if (MyBleService.get().getConnectDevice(mac) != null) {
                        MyBleService.get().getConnectDevice(mac).setActiveDisConnect(true);
                        MyBleService.get().disConnectDevice(mac);
                        mDeviceListAdapter.setTextHint(-1, "");
                    }
                    if (!mScanning) {
                        scanLeDeviceList(true);
                    }
                    break;
                default:
                    break;
            }
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

    public static void lauch(Activity activity, Bundle bundle) {
        CommonKit.startActivityForResult(activity, AddDeviceActivity.class, bundle, REQUESTCODE_ADD_DEVICE);
    }
}