package com.androidex.capbox.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.ActionItem;
import com.androidex.capbox.module.BaiduModel;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.module.LocationModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.BoxDetailActivity;
import com.androidex.capbox.ui.view.TitlePopup;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.Dialog;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.e.ble.bean.BLEDevice;
import com.e.ble.scan.BLEScanCfg;
import com.e.ble.scan.BLEScanListener;
import com.e.ble.util.BLEError;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_END_TAST;
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
import static com.androidex.boxlib.utils.BleConstants.BLE.SCAN_PERIOD;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;

public class LockFragment extends BaseFragment implements OnClickListener {
    private final static String TAG = "LockFragment";
    @Bind(R.id.main_bt_ble)
    ImageView ble;
    @Bind(R.id.iv_menu)
    ImageView iv_menu;
    @Bind(R.id.main_bt_ble_close)
    ImageView close;
    @Bind(R.id.iv_lock)
    ImageView iv_lock;
    @Bind(R.id.tv_boxConfig)
    TextView tv_boxConfig;
    @Bind(R.id.tv_latitude)
    TextView tv_latitude;
    @Bind(R.id.tv_longitude)
    TextView tv_longitude;
    @Bind(R.id.tv_elevation)
    TextView tv_elevation;
    @Bind(R.id.tv_locationTime)
    TextView tv_locationTime;
    @Bind(R.id.tv_lastTime)
    TextView tv_lastTime;
    @Bind(R.id.tv_address)
    TextView tv_address;
    @Bind(R.id.main_tv_name)
    TextView name;
    @Bind(R.id.main_tv_device)
    TextView tv_deviceMac;
    @Bind(R.id.tv_status)
    TextView tv_status;
    @Bind(R.id.current_temp)
    TextView current_temp;
    @Bind(R.id.current_hum)
    TextView current_hum;
    @Bind(R.id.tv_electric_quantity)
    TextView tv_electric_quantity;
    @Bind(R.id.main_tv_maxhum)
    TextView maxhum;
    @Bind(R.id.main_tv_minhum)
    TextView minhum;
    @Bind(R.id.main_tv_maxtemp)
    TextView maxtemp;
    @Bind(R.id.main_tv_mintemp)
    TextView mintemp;

    private Timer timer_rssi = new Timer();// 设计定时器
    private TimerTask task_sendrssi;// 心跳任务
    private Timer timer_location = new Timer();// 设计定时器
    private TimerTask timer_getlocation;
    private String address = null;
    private String uuid = null;
    private String deviceName = "Box";
    public static String boxName = "AndroidEx";
    private String latitude;
    private String longitude;
    private String elevation;
    private GeoCoder mSearch;
    private TitlePopup titlePopup;
    DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        address = bundle.getString("mac");
        uuid = bundle.getString("uuid");
        deviceName = bundle.getString("name");
        Log.e(TAG, "mac=" + address);
        if (uuid != null) getLocation(true);
        initView();
        initMap();
        initBleBroadCast();
        if (address!=null){
            if (MyBleService.get().getConnectDevice(address) == null) {
                Log.e(TAG, "开始扫描蓝牙设备1");
                scanLeDevice();
            } else {
                if (MyBleService.get().getConnectDevice(address).isActiveDisConnect()) {
                    Log.e(TAG, "开始扫描蓝牙设备2");
                    scanLeDevice();
                } else {
                    Log.e(TAG, "已连接 address=" + address);
                    CommonKit.showMsgShort(context, "设备已连接");
                    BleService.get().enableNotify(address);
                    updateBleView(View.GONE, View.VISIBLE);
                    startHeart();
                }
            }
        }
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
        intentFilter.addAction(ACTION_LOCK_STARTS);
        intentFilter.addAction(ACTION_TEMP_UPDATE);
        intentFilter.addAction(BLE_CONN_RSSI_SUCCED);
        intentFilter.addAction(BLE_CONN_RSSI_FAIL);
        intentFilter.addAction(ACTION_HEART);
        intentFilter.addAction(ACTION_END_TAST);
        intentFilter.addAction(ACTION_LOCK_OPEN_SUCCED);
        context.registerReceiver(dataUpdateRecevice, intentFilter);
    }

    @Override
    public void setListener() {
        ble.setOnClickListener(this);
        close.setOnClickListener(this);
        tv_boxConfig.setOnClickListener(this);
        iv_lock.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
    }

    public void initMap() {
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(listener);
    }

    public void getAddress(String latitudeStr, String longitudeStr) {
        LatLng latLng = new LatLng(Double.valueOf(latitudeStr).doubleValue(), Double.valueOf(longitudeStr).doubleValue());
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }

    private void initView() {
        tv_deviceMac.setText(address);
        name.setText(deviceName);
        if (address == null || address.equals("")) {
            tv_deviceMac.setText("FF:FF:FF:FF:FF:FF");
        } else {
            tv_deviceMac.setText(address);
        }
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(context, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(context, "结束携行", R.drawable.finish_carry));
        titlePopup.addAction(new ActionItem(context, "连接列表", R.drawable.connectlist));
        titlePopup.addAction(new ActionItem(context, "箱体设置", R.drawable.setting));
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                switch (position) {
                    case 0:
                        showEndCarryAffirm();//显示确认结束弹窗
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    default:
                        break;
                }
            }


        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_bt_ble_close://连接状态，点击关闭
                stopHeart();
                ServiceBean device = MyBleService.get().getConnectDevice(address);
                if (device != null) {
                    device.setActiveDisConnect(true);
                }
                MyBleService.get().disConnectDevice(address);
                updateBleView(View.VISIBLE, View.GONE);
                break;

            case R.id.main_bt_ble://蓝牙未连接，点击连接
                scanLeDevice();
                break;

            case R.id.iv_lock:
                openLock();
                break;

            case R.id.tv_boxConfig:
                Bundle bundle = new Bundle();
                bundle.putString("name", deviceName);
                bundle.putString("mac", address);
                bundle.putString("uuid", uuid);
                BoxDetailActivity.lauch(getActivity(), bundle);
                break;
            case R.id.iv_menu:
                titlePopup.show(v);
                break;
            default:
                break;
        }
    }

    /**
     * 显示确认结束弹窗
     */
    private void showEndCarryAffirm() {
        Dialog.showAlertDialog(context, "确定结束携行押运吗？", new Dialog.DialogFingerListener() {
            @Override
            public void confirm() {
                if (MyBleService.get().getConnectDevice(address) != null) {
                    MyBleService.get().endTask(address);
                } else {
                    CommonKit.showErrorShort(context, "请先连接蓝牙");
                    return;
                }
            }

            @Override
            public void cancel() {
            }
        });
    }

    /**
     * 结束携行
     */
    private void endTask() {
        NetApi.endTask(getToken(), getUserName(), uuid, new ResultCallBack<BaseModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CommonKit.showOkShort(context, "结束成功");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showOkShort(context, "结束失败");
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                CommonKit.showErrorShort(context, "网络异常");
            }
        });
    }


    /**
     * 开始和停止获取定位信息
     *
     * @param flag
     */
    private void getLocation(boolean flag) {
        if (flag) {
            timer_getlocation = new TimerTask() {
                @Override
                public void run() {
                    Log.i("LockFragment", "开始定位");
                    boxlocation(uuid);
                }
            };
            timer_location.schedule(timer_getlocation, 1000, 1 * 60 * 1000);
        } else {
            if (timer_getlocation != null) {
                timer_getlocation.cancel();
                timer_location.cancel();
                timer_getlocation = null;
                timer_location = null;
                Log.i("LockFragment", "停止定位");
            }
        }
    }

    /**
     * 开锁
     */
    private void openLock() {
        if (MyBleService.get().getConnectDevice(address) != null) {
            MyBleService.get().openLock(address);
        } else {
            CommonKit.showErrorShort(context, getResources().getString(R.string.setting_tv_ble_disconnect));
        }
    }

    /**
     * 开始扫描
     */
    private void scanLeDevice() {
        showProgress("搜索设备中...");
        BLEScanCfg scanCfg = new BLEScanCfg.ScanCfgBuilder(SCAN_PERIOD).builder();
        MyBleService.get().startScanner(scanCfg, new BLEScanListener() {
            boolean isScanDevice;//是否扫描到设备

            @Override
            public void onScannerStart() {
                isScanDevice = false;
            }

            @Override
            public void onScanning(BLEDevice device) {
                if (device.getMac().equals(address)) {
                    isScanDevice = true;
                    Log.e(TAG, "搜索到设备...");
                    CommonKit.showOkShort(context, "搜索到设备...");
                    stopScanLe();
                    BleService.get().connectionDevice(context, address);
                }
            }

            @Override
            public void onScannerStop() {
                showProgress("扫描结束...");
                Log.e(TAG, "扫描结束");
                disProgress();
                if (!isScanDevice) {
                    CommonKit.showErrorShort(context, "未搜索到设备");
                }
            }

            @Override
            public void onScannerError(int errorCode) {
                stopScanLe();
                if (errorCode == BLEError.BLE_CLOSE) {
                    Log.e(TAG, "蓝牙未打开，请打开蓝牙后重试");
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

    /**
     * 获取扫码后的设备MAC
     *
     * @param uuid
     */
    private void boxlocation(String uuid) {
        NetApi.getboxLocation(getToken(), ((MainActivity) getActivity()).username, uuid,
                new ResultCallBack<LocationModel>() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, LocationModel model) {
                        super.onSuccess(statusCode, headers, model);
                        if (model != null) {
                            switch (model.code) {
                                case Constants.API.API_OK:
                                    Log.e("TAG", "get device location");
                                    LocationModel.Data data = model.data;
                                    latitude = data.getLatitude();//纬度
                                    longitude = data.getLongitude();//经度
                                    elevation = data.getElevation();//高程
                                    tv_latitude.setText(latitude);
                                    tv_longitude.setText(longitude);
                                    tv_elevation.setText(elevation);
                                    if (latitude.equals("0") && longitude.equals("0")) {
                                        tv_address.setText("携行箱位置未知");
                                    } else {
                                        getBaiduLocation(latitude, longitude);
                                    }
                                    if (data.locationTime != null) {
                                        tv_locationTime.setText(data.locationTime);
                                    } else {
                                        tv_locationTime.setText("0");
                                    }
                                    if (data.lastTime != null) {
                                        tv_lastTime.setText(data.lastTime);
                                    } else {
                                        tv_lastTime.setText("0");
                                    }
                                    break;
                                case Constants.API.API_FAIL:
                                    if (model.info != null) {
                                        CommonKit.showErrorShort(getContext(), model.info);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            Log.e("TAG", model.toString());
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

    /**
     * 经纬度纠偏
     *
     * @param latitude
     * @param longitude
     */
    public void getBaiduLocation(String latitude, String longitude) {
        NetApi.getLocation(latitude, longitude, new ResultCallBack<BaiduModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaiduModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    if (model.error == 0) {
                        String str_lat = new String(Base64.decode(model.y, Base64.DEFAULT));
                        String str_lon = new String(Base64.decode(model.x, Base64.DEFAULT));
                        Log.e("Base64", "Base64--lat-->" + str_lat);
                        Log.e("Base64", "Base64--lon-->" + str_lon);
                        if (!str_lat.equals("0") && !str_lat.equals("") && !str_lon.equals("0") && !str_lon.equals("")) {
                            getAddress(str_lat, str_lon);
                        } else {
                            tv_address.setText("位置未知");
                        }
                    } else {
                        Log.e(TAG, "经纬度转换出错 error=" + model.error);
                    }
                } else {
                    Log.e(TAG, "经纬度转换出错 model=null");
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                Log.e(TAG, "经纬度转换出错");
                CommonKit.showErrorShort(getContext(), "网络连接失败");
            }
        });
    }

    /**
     * 开始心跳
     */
    private void startHeart() {
        stopHeart();
        if (task_sendrssi == null) {
            task_sendrssi = new TimerTask() {
                @Override
                public void run() {// 通过消息更新
                    MyBleService.get().sentHeartBeat(address);
                }
            };
            if (timer_rssi == null) {
                timer_rssi = new Timer();
            }
            timer_rssi.schedule(task_sendrssi, 1000, 5000);// 执行心跳包任务
        }
    }

    /**
     * 停止心跳
     */
    private void stopHeart() {
        if (task_sendrssi != null) {
            task_sendrssi.cancel();
            timer_rssi.cancel();
            timer_rssi = null;
            Log.i("LockFragment", "停止心跳");
            task_sendrssi = null;
        }
    }

    BroadcastReceiver dataUpdateRecevice = new BroadcastReceiver() {

        private int t = 0;
        private int q = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceMac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
            if (deviceMac == null) return;
            if (!address.equals(deviceMac)) return;
            L.e("收到数据的设备MAC：" + deviceMac);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS:
                case BLE_CONN_SUCCESS_ALLCONNECTED:
                    BleService.get().enableNotify(address);
                    CommonKit.showMsgShort(context, "设备连接成功");
                    updateBleView(View.GONE, View.VISIBLE);
                    startHeart();
                    disProgress();
                    break;

                case BLE_CONN_DIS://断开连接
                    Log.e("LockFragment", "断开连接");
                    stopHeart();
                    updateBleView(View.VISIBLE, View.GONE);
                    setLostAlarm(deviceName);//防丢报警设置
                    break;

                case ACTION_LOCK_OPEN_SUCCED:
                    CommonKit.showOkShort(context, "开锁成功");
                    MyBleService.get().getLockStatus(address);
                    break;

                case ACTION_LOCK_STARTS://锁状态FB 32 00 01 00 00 FE
                    if (b[2] == (byte) 0x01) {
                        tv_status.setText("已打开");
                    } else {
                        tv_status.setText("已关闭");
                    }
                    break;

                case ACTION_TEMP_UPDATE://温度
                    Log.d("BTTempBLEService", "温湿度 mac: " + intent.getStringExtra("mac"));
                    if (intent.getStringExtra("temp") == null) return;
                    current_temp.setText(intent.getStringExtra("temp"));
                    if (intent.getStringExtra("hum") == null) return;
                    current_hum.setText(intent.getStringExtra("hum"));
                    break;

                case BLE_CONN_RSSI_SUCCED://获取到信号强度值
//                    if (intent.getIntExtra("rssi", -100) <= -90) {
//                        if (!MyApplication.connDeviceFail) {
//                            MyApplication.connDeviceFail = true;
//                            sendMessage(IS_LOST);
//                        }
//                    } else {
                    //MyApplication.connDeviceFail = false;
                    //}
                    break;
                case BLE_CONN_RSSI_FAIL://获取信号强度失败

                    break;

                /**
                 * 收到心跳  0xFB 0x31 0x00 0x1B 0x31 0x6A 0x40 0x66 0x00 0x4B 0x37 0x00 0xFE
                 * 示例：温度做加100再乘以100然后将值转化为16进制两个字节传送给客户端
                 * 温度：26.5℃  计算完后值为：12650，转化16进制→316A →0x31 0x6A
                 */
                case ACTION_HEART:
                    String s = new StringBuilder(1).append(String.format("%02X", b[4])).toString();
                    String s1 = new StringBuilder(1).append(String.format("%02X", b[5])).toString();
                    Double parseInt = Double.valueOf(Integer.parseInt(s + s1, 16)) / 100 - 100;
                    current_temp.setText(df.format(parseInt));
                    String s2 = new StringBuilder(1).append(String.format("%02X", b[6])).toString();
                    String s3 = new StringBuilder(1).append(String.format("%02X", b[7])).toString();
                    if (s2 != null) {
                        q = Integer.parseInt(s2, 16);
                    }
                    if (s3 != null) {
                        t = Integer.parseInt(s3, 16);
                    }
                    current_hum.setText(q + "." + t);
                    String s4 = new StringBuilder(1).append(String.format("%02X", b[10])).toString();
                    int n = Integer.parseInt(s4, 16);
                    tv_electric_quantity.setText("" + n);
                    break;

                case ACTION_END_TAST://结束携行押运
                    Log.e(TAG, "结束携行押运");
                    switch (b[2]) {
                        case (byte) 0x00://成功
                            endTask();
                            break;
                        case (byte) 0x01://失败
                            CommonKit.showErrorShort(context, "结束失败");
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void updateBleView(int visible, int gone) {
        tv_status.setText("关闭");
        ble.setVisibility(visible);
        close.setVisibility(gone);
    }

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                return;
            }
            //获取地理编码结果
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
                tv_address.setText("");
                return;
            }
            tv_address.setText(result.getAddress() + result.getSematicDescription());
            //获取反向地理编码结果
        }
    };

    @Override
    public void onPause() {
        stopScanLe();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLocation(false);
        stopHeart();
        mSearch.destroy();
        context.unregisterReceiver(dataUpdateRecevice);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_lock;
    }
}
