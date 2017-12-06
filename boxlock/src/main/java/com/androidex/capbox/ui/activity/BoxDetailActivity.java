package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidex.boxlib.modules.ConnectedDevice;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.module.BoxDetailModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.Dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BOX_MAC;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_END_TAST;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_ONEKEYCONFIG;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_RECOVER;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_START_CARRYESCORT;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_SETTING;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_OPEN_MONITOR;

/**
 * 配置箱体,箱体详情
 */
public class BoxDetailActivity extends BaseActivity {
    private static final String TAG = BoxDetailActivity.class.getSimpleName();
    @Bind(R.id.tv_carryNum)
    TextView tv_carryNum;
    @Bind(R.id.tv_heartbeatRate)
    TextView tv_heartbeatRate;
    @Bind(R.id.tv_locationRate)
    TextView tv_locationRate;
    @Bind(R.id.titlebar)
    SecondTitleBar titlebar;
    @Bind(R.id.tv_mac)
    TextView tv_mac;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_boxConfig)
    TextView tv_boxConfig;
    @Bind(R.id.tv_startCarryScort)
    TextView tv_startCarryScort;
    @Bind(R.id.tv_connect_starts)
    TextView tv_connect_starts;

    private String username;//所有人的电话
    private String name;//箱体的名字
    private String mac;//箱体的mac
    private String uuid;//箱体的UUID
    private String police = "A";//报警开启A和关闭B
    private String possessorFinger1 = null;//所有人指纹信息或ID
    private String possessorFinger2 = null;//所有人指纹信息或ID
    private String possessorFinger3 = null;//所有人指纹信息或ID
    private String becomeFinger1 = null;////静默模式功能的指纹
    private String becomeFinger2 = null;////静默模式功能的指纹
    private String becomeFinger3 = null;////静默模式功能的指纹
    private String unlocking = "A";//开锁次数，多次有效A，一次有效B
    private String unlockingMode = "C";//开锁方式设定: 指纹开锁A，腕表开锁B 同时开锁 C
    private String dismountPolice = "A";  //破拆报警的开启A和关闭B
    private float highestTemp = 80;  //最高温度
    private float lowestTemp = 0;  //最低温度
    private int carryPersonNum = 1;  //携行人员人数跟腕表数量对应
    private int policeDiatance = 0;  //报警距离：0脱距、1较近、2近、3较远、4远
    private int heartbeatRate = 60;  //心跳更新频率60秒
    private int locationRate = 120;   //定位更新频率为60秒
    private String become = "A";    //静默开启A 关闭B
    private String tempPolice = "A";    //温度报警开关
    private String humidityPolice = "A";    //湿度报警开关
    private String distancePolice = "A";    //脱距报警开关
    private ArrayList<Map<String, String>> mapArrayList = new ArrayList<>();//添加的设备集合
    private ArrayList<String> list_devicemac = new ArrayList<>();//添加的设备集合的MAC
    private DataBroadcast dataBroadcast;
    private Context mContext;
    private int status;

    @Override
    public void initData(Bundle savedInstanceState) {
        mContext = context;
        username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        name = getIntent().getStringExtra("name");
        uuid = getIntent().getStringExtra("uuid");
        mac = getIntent().getStringExtra("mac");
        initBroadCast();
        initTitleBar();

        if (ConnectedDevice.get().getConnectDevice(mac) == null) {
            if (mac != null) {
                BleService.get().connectionDevice(context, mac);
            } else {
                Log.d(TAG, "mac is null");
            }
        } else {
            Log.d(TAG, "已经连接");
            BleService.get().enableNotify(mac);
            tv_connect_starts.setText("已连接");
            CommonKit.showOkShort(mContext, "设备已连接");
        }
        tv_mac.setText(mac);
        if (name != null) {
            tv_name.setText(name);
        }
        if (uuid != null) {
            getBoxDetail();
        }
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_RECOVER);//恢复出厂
        intentFilter.addAction(ACTION_ONEKEYCONFIG);//一键配置
        intentFilter.addAction(ACTION_BOX_MAC);//发送mac给腕表
        intentFilter.addAction(ACTION_START_CARRYESCORT);//启动携行押运
        intentFilter.addAction(ACTION_END_TAST);//结束携行押运
        dataBroadcast = new DataBroadcast();
        context.registerReceiver(dataBroadcast, intentFilter);
    }

    private void initTitleBar() {
        titlebar.getLeftBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonKit.finishActivity(context);
            }
        });
        titlebar.getRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("mac", mac);
                bundle.putString("uuid", uuid);
                bundle.putString("name", name);
                bundle.putInt("status", status);
                LockActivity.lauch(context, bundle);
            }
        });
    }

    /**
     * 获取箱体详细信息
     */
    public void getBoxDetail() {
        NetApi.boxDetail(getToken(), username, uuid, new ResultCallBack<BoxDetailModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDetailModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            status = model.data.deviceStatus;
                            unlocking = model.data.unlocking;//一次有效多次有效
                            unlockingMode = model.data.unlockingMode;//一次有效多次有效
                            carryPersonNum = model.data.carryPersonNum;//携行人员数量
                            if (carryPersonNum == 0) {
                                carryPersonNum = 1;
                            }
                            police = model.data.police;//报警开启A和关闭B
                            policeDiatance = model.data.policeDiatance;////报警距离：0脱距、1较近、2近、3较远、4远
                            heartbeatRate = model.data.heartbeatRate;//心跳更新频率60秒
                            if (heartbeatRate <= 20) {
                                heartbeatRate = 60;
                            }
                            locationRate = model.data.locationRate;//定位更新频率60秒
                            if (locationRate <= 30) {
                                locationRate = 60;
                            }
                            highestTemp = model.data.highestTemp;//最高温
                            lowestTemp = model.data.lowestTemp;//最低温
                            dismountPolice = model.data.dismountPolice;//破拆报警的开启A和关闭B
                            become = model.data.become;//静默开启A 关闭B
                            tv_carryNum.setText(String.format("%d人", carryPersonNum));
                            tv_heartbeatRate.setText(String.format("%ds/次", heartbeatRate));
                            tv_locationRate.setText(String.format("%ds/次", locationRate));
                            if (status == 2) {
                                tv_boxConfig.setEnabled(false);
                                tv_startCarryScort.setText("结束携行押运");
                            } else {
                                tv_boxConfig.setEnabled(true);
                                tv_startCarryScort.setText("启动携行押运");
                            }
                            becomeFinger1 = model.data.becomeFinger1;
                            becomeFinger2 = model.data.becomeFinger2;
                            becomeFinger3 = model.data.becomeFinger3;
                            possessorFinger1 = model.data.possessorFinger1;
                            possessorFinger2 = model.data.possessorFinger2;
                            possessorFinger3 = model.data.possessorFinger3;
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

    @OnClick({
            R.id.tv_boxConfig,
            R.id.rl_boxset,
            R.id.oneKeyConfig,
            R.id.setting_carryPersonNum,
            R.id.setting_Location,
            R.id.ll_heartbeatRate,
            R.id.ll_settingFinger,
            R.id.setting_factory_settings,
            R.id.tv_startCarryScort,
            R.id.ll_settingAlarm,
            R.id.ll_settinglock,
            R.id.tv_connect_starts,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_boxConfig://配置箱体
                if (uuid == null || uuid.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_uuid_commit));
                    return;
                } else if (name == null || name.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_boxname_commit));
                    return;
                } else if (possessorFinger1 == null || possessorFinger1.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_possessorFinger_commit));
                    return;
                } else if (possessorFinger2 == null || possessorFinger2.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_possessorFinger_commit));
                    return;
                } else if (possessorFinger3 == null || possessorFinger3.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_possessorFinger_commit));
                    return;
                } else if (becomeFinger1 == null || becomeFinger1.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_becomeFinger_commit));
                    return;
                } else if (becomeFinger2 == null || becomeFinger2.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_becomeFinger_commit));
                    return;
                } else if (becomeFinger3 == null || becomeFinger3.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_becomeFinger_commit));
                    return;
                } else if (unlocking == null || unlocking.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_unlocking_commit));
                    return;
                } else if (unlockingMode == null || unlockingMode.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_unlockingmode_commit));
                    return;
                } else if (carryPersonNum == 0) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_carryPersonNum_commit));
                    return;
                } else if (police == null || police.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_police_commit));
                } else if (heartbeatRate <= 30 || heartbeatRate > 120) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_heartbeatRate_commit));
                    return;
                } else if (locationRate < 60 || locationRate > 300) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_locationRate_commit));
                    return;
                } else if (dismountPolice == null || dismountPolice.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_dismountPolice_commit));
                    return;
                } else if (become == null || become.equals("")) {
                    CommonKit.showErrorShort(context, getString(R.string.hint_become_commit));
                    return;
                } else {
                    boxConfig();
                }
                break;
            case R.id.rl_boxset:
                BoxSettingActivity.lauch(context);
                break;
            case R.id.oneKeyConfig://一键配置
                MyBleService.get().getDevice().startBoxConfig(mac);
                break;
            case R.id.setting_carryPersonNum://携行设备
                Bundle bundle = new Bundle();
                bundle.putString("uuid", uuid);
                WatchListActivity.lauch(context, bundle);
                break;
            case R.id.ll_heartbeatRate://心跳更新频率
                Dialog.showAlertDialog(context, "请设置心跳更新频率", new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null) {
                            heartbeatRate = Integer.parseInt(data);
                        } else {
                            heartbeatRate = 60;
                        }
                        tv_heartbeatRate.setText(String.format("%ds/次", heartbeatRate));
                        Log.e(TAG, "设置的定位更新频率为：" + heartbeatRate);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
            case R.id.setting_Location://定位更新频率
                Dialog.showAlertDialog(context, "请设置定位更新频率", new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null) {
                            locationRate = Integer.parseInt(data);
                        } else {
                            locationRate = 60;
                        }
                        tv_locationRate.setText(String.format("%ds/次", locationRate));
                        Log.e(TAG, "设置的定位更新频率为：" + locationRate);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
            case R.id.ll_settingFinger://设置指纹
                Bundle bundle1 = new Bundle();
                if (becomeFinger1 != null) {
                    bundle1.putString("becomeNum", "3");
                }
                if (possessorFinger1 != null) {
                    bundle1.putString("possessorNum", "3");
                }
                bundle1.putString("mac", mac);
                SettingFingerActivity.lauch(context, bundle1);
                break;
            case R.id.setting_factory_settings://恢复出厂
                MyBleService.get().getDevice().recover(mac);
                break;
            case R.id.tv_startCarryScort://启动/结束携行押运
                if (status == 2) {
                    MyBleService.get().getDevice().endTask(mac);
                } else {
                    MyBleService.get().getDevice().startEscort(mac);
                }
                break;
            case R.id.ll_settingAlarm://报警设置
                SettingAlarmActivity.lauch(context, Constants.CODE.REQUESTCODE_SET_ALARM);
                break;
            case R.id.ll_settinglock:
                SettingLockActivity.lauch(context, Constants.CODE.REQUESTCODE_SET_LOCK);
                break;
            case R.id.tv_connect_starts:
                BleService.get().connectionDevice(context, mac);
                break;
            default:
                break;
        }
    }

    /**
     * 配置箱体
     */
    private void boxConfig() {
        Log.e(TAG, "开始配置");
        NetApi.boxConfig(getToken(), username, uuid, name,
                possessorFinger1, possessorFinger2, possessorFinger3,
                becomeFinger1, becomeFinger2, becomeFinger3,
                unlocking, unlockingMode, carryPersonNum, police,
                policeDiatance, heartbeatRate, locationRate, highestTemp, lowestTemp,
                dismountPolice, become, tempPolice, humidityPolice, distancePolice,
                new ResultCallBack<BaseModel>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.e(TAG, "onStart");
                        showSpinnerDlg(true, getString(R.string.label_config_ing));
                    }

                    @Override
                    public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                        super.onSuccess(statusCode, headers, model);
                        dismissSpinnerDlg(true);
                        Log.e(TAG, "onSuccess");
                        if (model != null) {
                            switch (model.code) {
                                case Constants.API.API_OK:
                                    CommonKit.showOkShort(context, "配置成功");
                                    break;
                                case Constants.API.API_FAIL:
                                    if (model.info != null) {
                                        CommonKit.showErrorShort(context, model.info);
                                    } else {
                                        CommonKit.showOkShort(context, "配置失败");
                                    }
                                    break;
                                case Constants.API.API_NOPERMMISION:
                                    if (model.info != null) {
                                        CommonKit.showErrorShort(context, model.info);
                                    } else {
                                        CommonKit.showOkShort(context, "配置失败");
                                    }
                                    break;
                                default:
                                    if (model.info != null) {
                                        CommonKit.showErrorShort(context, model.info);
                                    } else {
                                        CommonKit.showOkShort(context, "配置失败");
                                    }
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Request request, Exception e) {
                        super.onFailure(statusCode, request, e);
                        dismissSpinnerDlg(true);
                        CommonKit.showErrorShort(context, "网络连接失败");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CODE.REQUESTCODE_ADD_DEVICE) {//添加设备回调
            switch (resultCode) {
                case Activity.RESULT_OK:
                    list_devicemac = data.getStringArrayListExtra("list_devicemac");
                    for (int i = 0; i < list_devicemac.size(); i++) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("mac", list_devicemac.get(i));
                        map.put("deviceType", "B");
                        mapArrayList.add(map);
                    }
                    if (list_devicemac != null) {
                        tv_carryNum.setText(list_devicemac.size() + "");
                        carryPersonNum = list_devicemac.size() + 1;
                    } else {
                        carryPersonNum = 1;
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    CommonKit.showErrorShort(context, "未添加新设备");
                    break;
            }
        } else if (requestCode == Constants.CODE.REQUESTCODE_SET_ALARM) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    police = data.getStringExtra("police");//报警开关
                    policeDiatance = data.getIntExtra("policeDiatance", 0);//报警距离
                    dismountPolice = data.getStringExtra("dismountPolice");//报警距离
                    highestTemp = data.getFloatExtra("highestTemp", 80);   //最高温
                    lowestTemp = data.getFloatExtra("lowestTemp", 0);     //最低温
                    become = data.getStringExtra("become");             //静默开关
                    tempPolice = data.getStringExtra("tempPolice");     //温度报警开关
                    humidityPolice = data.getStringExtra("humidityPolice");//湿度报警开关
                    distancePolice = data.getStringExtra("distancePolice");//距离报警开关
                    Log.d(TAG, "police=" + police + " policeDiatance=" + policeDiatance +
                            " dismountPolice" + dismountPolice + " highestTemp=" +
                            highestTemp + "lowestTemp=" + lowestTemp +
                            "tempPolice=" + tempPolice + " humidityPolice=" + humidityPolice + "distancePolice=" + distancePolice);
                    break;
                default:
                    CommonKit.showErrorShort(context, "取消配置");
                    break;
            }
        } else if (requestCode == Constants.CODE.REQUESTCODE_SET_LOCK) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    unlockingMode = data.getStringExtra("unlockingMode");//报警开关
                    unlocking = data.getStringExtra("unlocking");//报警距离
                    Log.d(TAG, " unlockingMode=" + unlockingMode + " unlocking=" + unlocking);
                    break;
                default:
                    CommonKit.showErrorShort(context, "取消配置");
                    break;
            }
        } else if (requestCode == Constants.CODE.REQUESTCODE_SET_BOX) {//箱体设置
            switch (resultCode) {
                case Activity.RESULT_OK:
                    name = data.getStringExtra("name");//箱体昵称
                    Log.d(TAG, " name=" + name);
                    tv_name.setText(name);
                    break;
                default:
                    CommonKit.showErrorShort(context, "取消修改");
                    break;
            }
        } else if (requestCode == REQUESTCODE_OPEN_MONITOR) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (mac != null) {
                        BleService.get().connectionDevice(context, mac);
                    }
                    break;
                default:
                    break;
            }
        } else if (requestCode == REQUESTCODE_FINGER_SETTING) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    possessorFinger1 = data.getStringExtra("possessorFinger1");
                    possessorFinger2 = data.getStringExtra("possessorFinger2");
                    possessorFinger3 = data.getStringExtra("possessorFinger3");
                    becomeFinger1 = data.getStringExtra("becomeFinger1");
                    becomeFinger2 = data.getStringExtra("becomeFinger2");
                    becomeFinger3 = data.getStringExtra("becomeFinger3");
                    if (possessorFinger1 != null && becomeFinger1 != null) {
                        CommonKit.showOkShort(context, "指纹录入成功");
                    } else {
                        CommonKit.showOkShort(context, "指纹录入失败");
                    }
                    break;
                default:
                    CommonKit.showOkShort(context, "指纹录入取消");
                    break;
            }
        }
    }

    public class DataBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceMac = intent.getStringExtra("deviceMac");
            if (!mac.equals(deviceMac)) return;
            byte[] b = intent.getByteArrayExtra("data");
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS:
                case BLE_CONN_SUCCESS_ALLCONNECTED:
                    Log.d(TAG, "连接成功=");
                    BleService.get().enableNotify(mac);
                    tv_connect_starts.setText("已连接");
                    CommonKit.showOkShort(mContext, "连接成功");
                    break;

                case BLE_CONN_DIS:
                    tv_connect_starts.setText("点击连接");
                    Log.d(TAG, "断开连接=");
                    setLostAlarm("");//防丢报警设置
                    break;
                case ACTION_RECOVER://恢复出厂
                    Log.e(TAG, "恢复出厂");
                    switch (b[2]) {
                        case (byte) 0x00://成功
                            CommonKit.showMsgShort(mContext, "恢复出厂成功");
                            break;
                        case (byte) 0x01://失败
                            CommonKit.showErrorShort(mContext, "恢复出厂失败");
                            break;
                    }
                    break;
                case ACTION_ONEKEYCONFIG://一键配置
                    Log.e(TAG, "一键配置");
                    switch (b[2]) {
                        case (byte) 0x00://成功
                            CommonKit.showMsgShort(mContext, "配置成功");
                            break;
                        case (byte) 0x01://失败
                            CommonKit.showErrorShort(mContext, "配置失败");
                            break;
                    }
                    break;
                case ACTION_BOX_MAC://发送mac
                    Log.e(TAG, "发送Mac给腕表");
                    switch (b[2]) {
                        case (byte) 0x00://成功
                            CommonKit.showMsgShort(mContext, "发送Mac给腕表成功");
                            break;
                        case (byte) 0x01://失败
                            CommonKit.showErrorShort(mContext, "发送Mac给腕表失败");
                            break;
                    }
                    break;
                case ACTION_START_CARRYESCORT://启动携行押运
                    Log.e(TAG, "启动携行押运");
                    switch (b[2]) {
                        case (byte) 0x00://成功
                            CommonKit.showMsgShort(mContext, "启动成功,箱体不可配置");
                            startEscort();
                            tv_boxConfig.setEnabled(false);
                            break;
                        case (byte) 0x01://失败
                            CommonKit.showErrorShort(mContext, "启动失败");
                            break;
                    }
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
    }

    /**
     * 启动携行押运
     */
    public void startEscort() {
        NetApi.startEscort(getToken(), username, uuid, new ResultCallBack<BaseModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            Log.e(TAG, "启动携行押运成功");
                            tv_startCarryScort.setText("结束携行押运");
                            getBoxDetail();
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
                CommonKit.showErrorShort(mContext, "网络连接失败");
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
                            getBoxDetail();
                            CommonKit.showOkShort(context, "结束成功");
                            tv_startCarryScort.setText("启动携行押运");
                            tv_boxConfig.setEnabled(true);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(dataBroadcast);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    public static void lauch(Activity activity, Bundle bundle) {
        CommonKit.startActivity(activity, BoxDetailActivity.class, bundle, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_boxdetail;
    }

}