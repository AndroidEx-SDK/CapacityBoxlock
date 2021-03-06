package com.androidex.capbox.ui.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.acker.simplezxing.activity.CaptureActivity;
import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.capbox.MainActivity;
import com.androidex.capbox.MyApplication;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.db.DaoSession;
import com.androidex.capbox.db.DeviceInfo;
import com.androidex.capbox.db.DeviceInfoDao;
import com.androidex.capbox.module.ActionItem;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.module.DeviceModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.AddDeviceActivity;
import com.androidex.capbox.ui.activity.BoxDetailActivity;
import com.androidex.capbox.ui.activity.BoxStatusActivity;
import com.androidex.capbox.ui.activity.ChatActivity;
import com.androidex.capbox.ui.activity.LoginActivity;
import com.androidex.capbox.ui.adapter.BoxListAdapter;
import com.androidex.capbox.ui.view.TitlePopup;
import com.androidex.capbox.ui.widget.ThirdTitleBar;
import com.androidex.capbox.utils.CalendarUtil;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.RLog;
import com.androidex.capbox.utils.SystemUtil;
import com.e.ble.bean.BLEDevice;
import com.e.ble.scan.BLEScanCfg;
import com.e.ble.scan.BLEScanListener;
import com.e.ble.util.BLEError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import okhttp3.Headers;
import okhttp3.Request;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.androidex.boxlib.cache.SharedPreTool.IS_BIND_NUM;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_UNBIND;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.provider.WidgetProvider.ACTION_UPDATE_ALL;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_ITEM_POSITION;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_ADD_DEVICE;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_NAME;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_UUID;
import static com.androidex.capbox.utils.Constants.EXTRA_DEVICE;
import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;
import static com.androidex.capbox.utils.Constants.EXTRA_PAGER_SIGN;

/**
 * 箱体列表
 *
 * @author liyp
 * @editTime 2017/9/27
 */

public class BoxListFragment extends BaseFragment {
    private static final String TAG = "BoxListFragment";
    @Bind(R.id.titlebar)
    ThirdTitleBar titlebar;
    @Bind(R.id.device_list_connected)
    ListView listconnected;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;
    @Bind(R.id.ll_boxlist)
    LinearLayout ll_boxlist;

    List<Map<String, String>> mylist = new ArrayList<>();
    private static final long SCAN_PERIOD = 12000;
    private static final int REQUEST_ENABLE_BT = 1;// 用于蓝牙setResult
    private boolean mScanning = false;//控制蓝牙扫描
    private BoxListAdapter boxListAdapter;
    private TitlePopup titlePopup;
    private DeviceInfoDao deviceInfoDao;
    private String uuid;
    private int unBindPosition;
    private BleBroadCast bleBroadCast;
    private boolean isShow = true;//解决与监控页面的广播冲突
    private boolean inUnbind;//是否解绑

    @Override
    public void initData() {
        initTitleBar();
        initDB();
        iniRefreshView();
        initListView();
        initBle();//蓝牙连接
    }

    public void initDB() {
        DaoSession daoSession = ((MyApplication) context.getApplication()).getDaoSession();
        deviceInfoDao = daoSession.getDeviceInfoDao();
    }

    private void initTitleBar() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(context, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(context, "添加设备", R.mipmap.finish_carry));
        titlePopup.addAction(new ActionItem(context, "扫一扫", R.mipmap.connectlist));
        // titlePopup.addAction(new ActionItem(context, "箱体设置", R.mipmap.setting));
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                switch (position) {
                    case 0:
                        AddDeviceActivity.lauch(context, null, REQUESTCODE_ADD_DEVICE);
                        break;
                    case 1:
                        Intent intent = new Intent(context, CaptureActivity.class);
                        startActivityForResult(intent, CaptureActivity.REQ_CODE);// ,//Activity.RESULT_FIRST_USER
                        break;
                    default:
                        break;
                }
            }
        });
        titlebar.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titlePopup.show(view);
            }
        });
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
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_BOX_NAME, mylist.get(position).get(EXTRA_BOX_NAME));
                        bundle.putString(EXTRA_BOX_UUID, mylist.get(position).get(EXTRA_BOX_UUID));
                        bundle.putString(EXTRA_ITEM_ADDRESS, mylist.get(position).get(EXTRA_ITEM_ADDRESS));
                        bundle.putInt(EXTRA_PAGER_SIGN, 0);//0表示从设备列表跳转过去的1表示从监控页跳转
                        bundle.putInt(EXTRA_ITEM_POSITION, position);//position选择的是第几个设备
                        BoxStatusActivity.lauch(getActivity(), bundle);
//进入设备详情的fragment
//                        Bundle bundle = new Bundle();
//                        MainActivity activity = (MainActivity) getActivity();
//                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
//                        bundle.putParcelable(EXTRA_DEVICE,
//                                new DeviceModel(mylist.get(position).get(EXTRA_ITEM_ADDRESS),
//                                        mylist.get(position).get(EXTRA_BOX_UUID),
//                                        mylist.get(position).get(EXTRA_BOX_NAME)));
//                        activity.lockFragment = new LockFragment();
//                        activity.lockFragment.setArguments(bundle);
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        transaction.replace(R.id.content,activity.lockFragment);
//                        transaction.commit();
                        break;
                    case R.id.tv_unbind:
                        inUnbind = false;
                        String address = mylist.get(position).get(EXTRA_ITEM_ADDRESS);
                        uuid = mylist.get(position).get(EXTRA_BOX_UUID);
                        unBindPosition = position;
                        unBind(unBindPosition, address, uuid);
                        if (MyBleService.getInstance().getConnectDevice(address) == null) {
                            scanLeDevice(position, 1);
                            showProgress("正在连接...");
                        } else {
                            showProgress("正在解绑...");
                            String hexStr = Long.toHexString(Long.parseLong(getUserName().trim()));
                            MyBleService.getInstance().unBind(address, hexStr);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //设置已绑定列表的适配器
        listconnected.setAdapter(boxListAdapter);
    }

    private void iniRefreshView() {
        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 模拟刷新完成
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        boxlist();
                        swipe_container.setRefreshing(false);
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
     * 开始扫描
     *
     * @param index 0 代表是点击连接后进入聊天  1代表是解除绑定
     */
    private void scanLeDevice(final int position, final int index) {
        final String address = mylist.get(position).get(EXTRA_ITEM_ADDRESS);
        final String deviceUUID = mylist.get(position).get(EXTRA_BOX_UUID);
        showProgress("搜索设备...");
        BLEScanCfg scanCfg = new BLEScanCfg.ScanCfgBuilder(SCAN_PERIOD).builder();
        MyBleService.getInstance().startScanner(scanCfg, new BLEScanListener() {
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
                    if (index == 0) {
                        synchronized (this) {
                            RLog.d("搜索到设备mScanning=" + mScanning);
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_BOX_NAME, device.getName());
                            bundle.putString(EXTRA_BOX_UUID, deviceUUID);
                            bundle.putString(EXTRA_ITEM_ADDRESS, address);
                            bundle.putInt(EXTRA_PAGER_SIGN, 0);//0表示从设备列表跳转过去的1表示从监控页跳转
                            bundle.putInt(EXTRA_ITEM_POSITION, position);//position选择的是第几个设备
                            //BoxDetailActivity.lauch(getActivity(), bundle);
                            ChatActivity.lauch(context, bundle);
                        }
                    } else {
                        MyBleService.getInstance().connectionDevice(context, address);
                        showProgress("正在连接...");
                    }
                }
            }

            @Override
            public void onScannerStop() {
                stopScanLe();
                RLog.d("onScannerStop");
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
        MyBleService.getInstance().stopScan();
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
        intentFilter.addAction(ACTION_UNBIND);
        bleBroadCast = new BleBroadCast();
        context.registerReceiver(bleBroadCast, intentFilter);
    }

    /**
     * 接收广播
     */
    public class BleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context mContext, Intent intent) {
            if (!isShow) return;
            final String address = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://连接成功
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    RLog.e("boxlistfragment isShow=" + isShow);
                    showProgress("连接成功...");
                    String hexStr = Long.toHexString(Long.parseLong(getUserName().trim()));
                    MyBleService.getInstance().unBind(address, hexStr);
                    inUnbind = false;
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!inUnbind) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyBleService.getInstance().disConnectDevice(address);
                                        disProgress();
                                        CommonKit.showErrorShort(context, "解绑超时");
                                    }
                                });
                            }
                        }
                    }, 5000);
                    break;
                case BLE_CONN_DIS:
                    disProgress();
                    CommonKit.showErrorShort(context, "蓝牙断开");
                    MyBleService.getInstance().disConnectDevice(address);
                    break;
                case BLE_CONN_FAIL:
                    disProgress();
                    CommonKit.showErrorShort(context, "连接失败");
                    MyBleService.getInstance().disConnectDevice(address);
                    break;
                case ACTION_UNBIND:
                    disProgress();
                    inUnbind = true;
                    MyBleService.getInstance().disConnectDevice(address);
                    byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
                    switch (b[2]) {
                        case (byte) 0x01:
                            if (uuid.length() >= 32) {
                                RLog.d("收到解除绑定成功指令");
                                unBind(unBindPosition, address, uuid);
                            } else {
                                CommonKit.showErrorShort(context, "uuid错误");
                            }
                            break;
                        case (byte) 0x00:
                            CommonKit.showErrorShort(context, "解绑失败");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
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
                        RLog.d("扫描结果：" + data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));  //or do sth
                        if (uuid.length() > 32) {
                            bindBox("FF:FF:FF:FF:FF:FF", uuid.trim());//扫描到UUID
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
    public void bindBox(final String address, final String uuid) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.boxBind(getToken(), getUserName(), uuid, new ResultCallBack<BaseModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CommonKit.showOkShort(context, getString(R.string.hint_bind_ok));
                            boxlist();//返回成功后刷新列表
                            Event.BoxBindChange boxBindChange = new Event.BoxBindChange();
                            boxBindChange.setDeviceModel(new DeviceModel(address, uuid, ""));
                            postSticky(boxBindChange);
                            context.sendBroadcast(new Intent(ACTION_UPDATE_ALL));
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

    private void unBind(final int position, final String address, final String uuid) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.relieveBoxBind(getToken(), ((BaseActivity) context).getUserName(), uuid, address, new ResultCallBack<BaseModel>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CommonKit.showOkShort(context, getString(R.string.hint_unbind_ok));
                            if (position >= 0 && position < mylist.size()) {
                                mylist.remove(position);
                                boxListAdapter.notifyDataSetChanged();
                            }
                            SharedPreTool.getInstance(context).clearObj(ServiceBean.class, address);
                            MyBleService.deleateData(address);//删除轨迹
                            postSticky(new Event.BoxRelieveBind());
                            context.sendBroadcast(new Intent(ACTION_UPDATE_ALL));//发送广播给桌面插件，更新列表
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, getString(R.string.hint_unbind_fail));
                            break;
                        case Constants.API.API_NOPERMMISION:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showErrorShort(context, "无权限");
                            }
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
            }
        });
    }

    /**
     * 获取设备列表
     */
    public void boxlist() {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.boxlist(getToken(), getUserName(), new ResultCallBack<BoxDeviceModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            mylist.clear();
                            for (BoxDeviceModel.device device : model.devicelist) {
                                Map<String, String> map = new HashMap<>();
                                map.put(EXTRA_BOX_NAME, device.boxName);
                                map.put(EXTRA_BOX_UUID, device.uuid);
                                map.put(EXTRA_ITEM_ADDRESS, device.mac);
                                map.put("deviceStatus", "" + device.deviceStatus);
                                map.put("isdefault", "" + device.isDefault);
                                map.put("isOnLine", "" + device.isOnLine);
                                mylist.add(map);
                                if (deviceInfoDao.queryBuilder().where(DeviceInfoDao.Properties.Address.eq(device.mac)).list().size() <= 0) {
                                    DeviceInfo deviceInfo = new DeviceInfo();
                                    deviceInfo.setAddress(device.mac);
                                    deviceInfo.setUuid(device.uuid);
                                    deviceInfo.setName(CalendarUtil.getName(device.mac));
                                    deviceInfoDao.insert(deviceInfo);
                                    RLog.d("DeviceInfo  设备不存在");
                                } else {
                                    RLog.d("DeviceInfo  设备已存在");
                                }
                            }
                            SharedPreTool.getInstance(context).setIntData(IS_BIND_NUM, model.devicelist.size());
                            if (model.devicelist.size() > 0) {
                                Logd(TAG, "刷新列表");
                                boxListAdapter.notifyDataSetChanged();
                            } else {
                                CommonKit.showErrorShort(context, "请绑定箱体");
                                Logd(TAG, "刷新列表无数据");
                            }
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "账号在其他地方登录");
                            LoginActivity.lauch(context);
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
                disProgress();
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

    @Override
    public void onResume() {
        super.onResume();
        isShow = true;
        boxlist();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanLe();
        isShow = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bleBroadCast != null)
            context.unregisterReceiver(bleBroadCast);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_boxlist;
    }
}
