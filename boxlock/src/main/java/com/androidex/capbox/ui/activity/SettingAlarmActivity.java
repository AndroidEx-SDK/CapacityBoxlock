package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.ui.view.TypeFaceText;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Dialog;

import butterknife.Bind;
import butterknife.OnClick;

import static com.androidex.boxlib.cache.SharedPreTool.HIGHEST_TEMP;
import static com.androidex.boxlib.cache.SharedPreTool.LOWEST_TEMP;

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
    @Bind(R.id.tb_tamperAlarm)
    ToggleButton tb_tamperAlarm;
    @Bind(R.id.tb_becomeAlarm)
    ToggleButton tb_becomeAlarm;
    @Bind(R.id.tb_tempAlarm)
    ToggleButton tb_tempAlarm;
    @Bind(R.id.tb_humAlarm)
    ToggleButton tb_humAlarm;
    @Bind(R.id.tb_distanceAlarm)
    ToggleButton tb_distanceAlarm;

    private static final String TAG = "SettingAlarmActivity";
    private String police = "A";//报警开启A和关闭B
    private String dismountPolice = "A";//报警开启A和关闭B
    private float highestTemp = 80;  //最高温度
    private float lowestTemp = 0;  //最低温度
    private String become = "A";    //静默开启A 关闭B
    private String tempPolice = "A";    //静默开启A 关闭B
    private String humidityPolice = "A";    //静默开启A 关闭B
    private String distancePolice = "A";    //静默开启A 关闭B

    @Override
    public void initData(Bundle savedInstanceState) {
        tv_lowestTemp.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(LOWEST_TEMP, "0")));
        tv_highestTemp.setText(String.format("%s℃", SharedPreTool.getInstance(context).getStringData(HIGHEST_TEMP, "80")));
    }

    @Override
    public void setListener() {
        tb_police.setOnCheckedChangeListener(this);
        tb_tamperAlarm.setOnCheckedChangeListener(this);
        tb_becomeAlarm.setOnCheckedChangeListener(this);
        tb_tempAlarm.setOnCheckedChangeListener(this);
        tb_humAlarm.setOnCheckedChangeListener(this);
        tb_distanceAlarm.setOnCheckedChangeListener(this);
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
                intent.putExtra("become", become);
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_police://报警开关
                if (isChecked) {
                    //选中
                    police = "A";
                } else {
                    //未选中
                    police = "B";
                }
                Log.e(TAG, "police = " + police);
                break;
            case R.id.tb_tamperAlarm://防拆报警开关
                if (isChecked) {
                    dismountPolice = "A";
                    //选中
                    Log.e(TAG, "防拆报警A");
                } else {
                    //未选中
                    dismountPolice = "B";
                    Log.e(TAG, "防拆报警B");
                }
                break;
            case R.id.tb_becomeAlarm://静默开关
                if (isChecked) {
                    become = "A";
                    //选中
                    Log.e(TAG, "静默开关A");
                } else {
                    become = "B";
                    Log.e(TAG, "静默开关B");
                    //未选中
                }
                break;
            case R.id.tb_tempAlarm://温度报警开关
                if (isChecked) {
                    tempPolice = "A";
                    //选中
                    Log.e(TAG, "静默开关A");
                } else {
                    tempPolice = "B";
                    Log.e(TAG, "静默开关B");
                    //未选中
                }
                break;
            case R.id.tb_humAlarm://湿度报警开关
                if (isChecked) {
                    humidityPolice = "A";
                    //选中
                    Log.e(TAG, "静默开关A");
                } else {
                    humidityPolice = "B";
                    Log.e(TAG, "静默开关B");
                    //未选中
                }
                break;
            case R.id.tb_distanceAlarm://报警距离开关
                if (isChecked) {
                    distancePolice = "A";
                    //选中
                    Log.e(TAG, "静默开关A");
                } else {
                    distancePolice = "B";
                    Log.e(TAG, "静默开关B");
                    //未选中
                }
                break;
            default:
                break;
        }
        //SharedPreTool.getInstance(context).saveObj(, mac);

    }

    public static void lauch(Activity activity, int requestCode) {
        CommonKit.startActivityForResult(activity, SettingAlarmActivity.class, null, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settingalarm;
    }

}
