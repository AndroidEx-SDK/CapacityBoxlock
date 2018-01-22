package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BoxDetailModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.view.TypeFaceText;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.Dialog;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.cache.SharedPreTool.HIGHEST_TEMP;
import static com.androidex.boxlib.cache.SharedPreTool.LOWEST_TEMP;
import static com.androidex.capbox.MainActivity.username;

/**
 * 箱体的报警设置页面
 *
 * @author liyp
 * @editTime 2017/10/10
 */

public class SettingAlarmActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.tv_lowestTemp)
    TypeFaceText tv_lowestTemp;
    @Bind(R.id.tv_highestTemp)
    TypeFaceText tv_highestTemp;
    @Bind(R.id.tb_police)
    ToggleButton tb_police;
    @Bind(R.id.tb_distanceAlarm)
    ToggleButton tb_distanceAlarm;
    @Bind(R.id.tb_tamperAlarm)
    ToggleButton tb_tamperAlarm;
    @Bind(R.id.tb_tempAlarm)
    ToggleButton tb_tempAlarm;
    @Bind(R.id.tb_humAlarm)
    ToggleButton tb_humAlarm;

    private static final String TAG = "SettingAlarmActivity";
    private String mac;//箱体的mac
    private String uuid;
    private String police = "A";            //报警开启A和关闭B
    private String dismountPolice = "A";    //防拆报警开启A和关闭B
    private float highestTemp = 80;         //最高温度
    private float lowestTemp = 0;           //最低温度
    private String tempPolice = "A";        //温度报警开启A 关闭B
    private String humidityPolice = "A";    //湿度开启A 关闭B
    private String distancePolice = "A";    //距离报警开启A 关闭B
    private ServiceBean connectDevice;

    @Override
    public void initData(Bundle savedInstanceState) {
        tv_lowestTemp.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(LOWEST_TEMP, "0")));
        tv_highestTemp.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(HIGHEST_TEMP, "80")));
        mac = getIntent().getStringExtra("mac");
        uuid = getIntent().getStringExtra("uuid");
        if (mac != null) {
            connectDevice = MyBleService.get().getConnectDevice(mac);
        }
        if (SharedPreTool.getInstance(context).getObj(ServiceBean.class, mac) != null) {
            tb_police.setChecked(true ? connectDevice.isPolice() : !connectDevice.isPolice());
            tb_distanceAlarm.setChecked(true ? connectDevice.isDistanceAlarm() : !connectDevice.isDistanceAlarm());
            tb_tamperAlarm.setChecked(true ? connectDevice.isTamperAlarm() : !connectDevice.isTamperAlarm());
            tb_tempAlarm.setChecked(true ? connectDevice.isTempAlarm() : !connectDevice.isTempAlarm());
            tb_humAlarm.setChecked(true ? connectDevice.isHumAlarm() : !connectDevice.isHumAlarm());
        } else {
            if (uuid != null) {
                getBoxDetail();
            }
        }
    }

    @Override
    public void setListener() {
        tb_police.setOnCheckedChangeListener(this);
        tb_distanceAlarm.setOnCheckedChangeListener(this);
        tb_tamperAlarm.setOnCheckedChangeListener(this);
        tb_tempAlarm.setOnCheckedChangeListener(this);
        tb_humAlarm.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @OnClick({
            R.id.ll_lowestTemp,
            R.id.ll_highestTemp,
            R.id.tv_finish,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_highestTemp://最高温
                Dialog.showAlertDialog(context, "请设置最高温度", new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null && data != "") {
                            highestTemp = Float.parseFloat(data);
                            if (highestTemp <= lowestTemp) {
                                CommonKit.showErrorShort(context, "最高温度不得低于最低温度");
                                highestTemp = 80;
                            }
                        } else {
                            highestTemp = 80;
                        }
                        SharedPreTool.getInstance(context).setStringData(HIGHEST_TEMP, highestTemp + "");
                        tv_highestTemp.setText(String.format("%s℃", highestTemp));
                        Log.e(TAG, "设置最高温度为：" + highestTemp);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
            case R.id.ll_lowestTemp://最低温
                Dialog.showAlertDialog(context, getResources().getString(R.string.setting_tv_lowtemp), new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null && data != "") {
                            lowestTemp = Float.parseFloat(data);
                            if (lowestTemp >= highestTemp) {
                                CommonKit.showErrorShort(context, "最低温度不得高于最高温度");
                                lowestTemp = 0;
                            }
                        } else {
                            lowestTemp = 0;
                        }
                        SharedPreTool.getInstance(context).setStringData(LOWEST_TEMP, lowestTemp + "");
                        tv_lowestTemp.setText(String.format("%s℃", lowestTemp));
                        Log.e(TAG, "设置最低温度为：" + lowestTemp);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
            case R.id.tv_finish:
                Intent intent = new Intent();
                intent.putExtra("police", police);
                intent.putExtra("dismountPolice", dismountPolice);
                intent.putExtra("highestTemp", highestTemp);
                intent.putExtra("lowestTemp", lowestTemp);
                intent.putExtra("tempPolice", tempPolice);
                intent.putExtra("humidityPolice", humidityPolice);
                intent.putExtra("distancePolice", distancePolice);
                setResult(Activity.RESULT_OK, intent);
                CommonKit.finishActivity(this);
                break;
            default:
                break;
        }
    }

    private boolean isCloseAll = true;

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isConnectBle()) return;//判断是否连接蓝牙
        switch (compoundButton.getId()) {
            case R.id.tb_police://报警开关
                if (isChecked) {
                    //选中
                    police = "A";
                    connectDevice.setPolice(true);
                } else {
                    //未选中
                    police = "B";
                    connectDevice.setPolice(false);
                    tb_distanceAlarm.setChecked(isChecked);
                    tb_tamperAlarm.setChecked(isChecked);
                    tb_tempAlarm.setChecked(isChecked);
                    tb_humAlarm.setChecked(isChecked);
                }
                break;
            case R.id.tb_distanceAlarm://距离报警开关
                if (isChecked) {
                    distancePolice = "A";
                    Log.e(TAG, "防拆报警A");
                    tb_police.setChecked(true);
                } else {
                    distancePolice = "B";
                    Log.e(TAG, "防拆报警B");
                }
                connectDevice.setDistanceAlarm(false ? distancePolice.equals("B") : !distancePolice.equals("B"));
                break;
            case R.id.tb_tamperAlarm://防拆报警开关
                if (isChecked) {
                    dismountPolice = "A";
                    Log.e(TAG, "防拆报警A");
                    tb_police.setChecked(true);
                    connectDevice.setTamperAlarm(true);
                } else {
                    dismountPolice = "B";
                    Log.e(TAG, "防拆报警B");
                    connectDevice.setTamperAlarm(false);
                }
                break;
            case R.id.tb_tempAlarm://温度报警开关
                if (isChecked) {
                    tempPolice = "A";
                    Log.e(TAG, "温度报警开关A");
                    tb_police.setChecked(true);
                    connectDevice.setTempAlarm(true);
                } else {
                    tempPolice = "B";
                    Log.e(TAG, "温度报警开关B");
                    connectDevice.setTempAlarm(false);
                }
                break;
            case R.id.tb_humAlarm://湿度报警开关
                if (isChecked) {
                    humidityPolice = "A";
                    Log.e(TAG, "湿度报警开关A");
                    tb_police.setChecked(true);
                } else {
                    humidityPolice = "B";
                    Log.e(TAG, "湿度报警开关B");
                }
                connectDevice.setHumAlarm(false ? humidityPolice.equals("B") : !humidityPolice.equals("B"));
                break;
            default:
                break;
        }
        if (connectDevice != null) {
            Log.e(TAG, "connectDevice isn't null,存储设备对象");
            SharedPreTool.getInstance(context).saveObj(connectDevice, mac);
            ServiceBean obj = SharedPreTool.getInstance(context).getObj(ServiceBean.class, mac);
            Log.e(TAG, obj.toString());
        } else {
            Log.e(TAG, "connectDevice is null");
        }
    }

    /**
     * 过滤蓝牙连接
     *
     * @return
     */
    private boolean isConnectBle() {
        if (MyBleService.get().getConnectDevice(mac) == null) {
            connectDevice = MyBleService.get().getConnectDevice(mac);
            CommonKit.showErrorShort(context, getResources().getString(R.string.setting_tv_ble_disconnect));
            BleService.get().connectionDevice(context, mac);
            return true;
        }
        return false;
    }

    /**
     * 获取箱体详细信息
     */
    public void getBoxDetail() {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.boxDetail(getToken(), username, uuid, new ResultCallBack<BoxDetailModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDetailModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            police = model.data.police;//报警开启A和关闭B
                            dismountPolice = model.data.dismountPolice;//破拆报警的开启A和关闭B

                            highestTemp = model.data.highestTemp;//最高温
                            lowestTemp = model.data.lowestTemp;//最低温
                            Log.d(TAG, model.toString());

                            tb_police.setChecked(false ? police.equals("B") : !police.equals("B"));
                            tb_tamperAlarm.setChecked(false ? dismountPolice.equals("B") : !dismountPolice.equals("B"));
                            tb_distanceAlarm.setChecked(false ? police.equals("B") : !police.equals("B"));
                            tb_tempAlarm.setChecked(false ? police.equals("B") : !police.equals("B"));
                            tb_humAlarm.setChecked(false ? police.equals("B") : !police.equals("B"));

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


    public static void lauch(Activity activity, Bundle bundle, int requestCode) {
        CommonKit.startActivityForResult(activity, SettingAlarmActivity.class, bundle, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settingalarm;
    }

}
