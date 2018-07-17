package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.callback.ItemClickCallBack;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BoxDetailModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.widget.SingleCheckListDialog;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.Dialog;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.cache.SharedPreTool.HIGHEST_HUM;
import static com.androidex.boxlib.cache.SharedPreTool.HIGHEST_TEMP;
import static com.androidex.boxlib.cache.SharedPreTool.LOWEST_HUM;
import static com.androidex.boxlib.cache.SharedPreTool.LOWEST_TEMP;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_UUID;
import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;

/**
 * 箱体的最高最低温湿度设置页面
 *
 * @author liyp
 * @editTime 2017/10/10
 */

public class SettingTempActivity extends BaseActivity{
    @Bind(R.id.tv_lowestTemp)
    TextView tv_lowestTemp;
    @Bind(R.id.tv_highestTemp)
    TextView tv_highestTemp;
    @Bind(R.id.tv_lowestHum)
    TextView tv_lowestHum;
    @Bind(R.id.tv_highestHum)
    TextView tv_highestHum;

    private static final String TAG = "SettingAlarmActivity";
    private String mac;//箱体的mac
    private String uuid;
    private int highestTemp = 80;         //最高温度
    private int lowestTemp = 0;           //最低温度
    private int highestHum = 80;          //最高湿度
    private int lowestHum = 0;            //最低湿度

    @Override
    public void initData(Bundle savedInstanceState) {
        tv_lowestTemp.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(LOWEST_TEMP, "0")));
        tv_highestTemp.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(HIGHEST_TEMP, "80")));
        tv_lowestHum.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(LOWEST_HUM, "0")));
        tv_highestHum.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(HIGHEST_HUM, "80")));
        mac = getIntent().getStringExtra(EXTRA_ITEM_ADDRESS);
        uuid = getIntent().getStringExtra(EXTRA_BOX_UUID);
        if (uuid != null) {
            getBoxDetail();
        }
    }

    @Override
    public void setListener() {
    }

    @Override
    public void onClick(View view) {

    }

    @OnClick({
            R.id.ll_lowestTemp,
            R.id.ll_highestTemp,
            R.id.ll_lowestHum,
            R.id.ll_highestHum,
            R.id.tv_finish,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_highestTemp://最高温
                Dialog.showAlertDialog(context, "请设置最高温度", new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null && data != "") {
                            highestTemp = Integer.parseInt(data);
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
                            lowestTemp = Integer.parseInt(data);
                            Log.e(TAG, "设置最低温度为：lowestTemp = " + lowestTemp);
                            Log.e(TAG, "设置最低温度为：highestTemp = " + highestTemp);
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
            case R.id.ll_highestHum://最高湿度
                Dialog.showAlertDialog(context, "请设置最高温度", new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null && data != "") {
                            highestHum = Integer.parseInt(data);
                            if (highestHum <= lowestHum) {
                                CommonKit.showErrorShort(context, "最高温度不得低于最低温度");
                                highestHum = 80;
                            }
                        } else {
                            highestHum = 80;
                        }
                        SharedPreTool.getInstance(context).setStringData(HIGHEST_HUM, highestHum + "");
                        tv_highestHum.setText(String.format("%s℃", highestHum));
                        Log.e(TAG, "设置最高温度为：" + highestHum);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
            case R.id.ll_lowestHum://最低湿度
                Dialog.showAlertDialog(context, getResources().getString(R.string.setting_tv_lowtemp), new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null && data != "") {
                            lowestHum = Integer.parseInt(data);
                            if (lowestHum >= highestHum) {
                                CommonKit.showErrorShort(context, "最低温度不得高于最高温度");
                                lowestHum = 0;
                            }
                        } else {
                            lowestHum = 0;
                        }
                        SharedPreTool.getInstance(context).setStringData(LOWEST_HUM, lowestHum + "");
                        tv_lowestHum.setText(String.format("%s℃", lowestHum));
                        Log.e(TAG, "设置最低温度为：" + lowestHum);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;


            case R.id.tv_finish:
                Intent intent = new Intent();
                intent.putExtra("highestTemp", highestTemp);
                intent.putExtra("lowestTemp", lowestTemp);
                intent.putExtra("highestHum", highestHum);
                intent.putExtra("lowestHum", lowestHum);
                setResult(Activity.RESULT_OK, intent);
                CommonKit.finishActivity(this);
                break;
            default:
                break;
        }
    }

    /**
     * 过滤蓝牙连接
     *
     * @return
     */
    private boolean isConnectBle() {
        if (MyBleService.getInstance().getConnectDevice(mac) == null) {
            CommonKit.showErrorShort(context, getResources().getString(R.string.setting_tv_ble_disconnect));
            MyBleService.getInstance().connectionDevice(context, mac);
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
        NetApi.boxDetail(getToken(), getUserName(), uuid, new ResultCallBack<BoxDetailModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDetailModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            highestTemp = model.data.highestTemp;//最高温
                            lowestTemp = model.data.lowestTemp;//最低温
                            Log.d(TAG, model.toString());
                            if (highestTemp == 0) {
                                highestTemp = 80;
                            }
                            tv_highestTemp.setText(String.format("%s℃", highestTemp));
                            tv_lowestTemp.setText(String.format("%s℃", lowestTemp));
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
        CommonKit.startActivityForResult(activity, SettingTempActivity.class, bundle, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settingtemp;
    }

}
