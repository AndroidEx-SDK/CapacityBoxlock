package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.ui.view.TypeFaceText;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Dialog;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author liyp
 * @editTime 2017/10/10
 */

public class SettingAlarmActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.setting_distance)
    Spinner setting_distance;
    @Bind(R.id.tv_lowestTemp)
    TypeFaceText tv_lowestTemp;
    @Bind(R.id.tv_highestTemp)
    TypeFaceText tv_highestTemp;
    @Bind(R.id.tb_tempPolice)
    ToggleButton tb_tempPolice;
    @Bind(R.id.tb_humidityPolice)
    ToggleButton tb_humidityPolice;
    @Bind(R.id.tb_distancePolice)
    ToggleButton tb_distancePolice;

    private static final String TAG = "SettingAlarmActivity";
    private String police = "A";//报警开启A和关闭B
    private String dismountPolice = "A";//报警开启A和关闭B
    private int policeDiatance = 0;////报警距离：0脱距、1较近、2近、3较远、4远
    private float highestTemp = 80;  //最高温度
    private float lowestTemp = 0;  //最低温度
    private String become = "A";    //静默开启A 关闭B
    private String tempPolice = "A";    //静默开启A 关闭B
    private String humidityPolice = "A";    //静默开启A 关闭B
    private String distancePolice = "A";    //静默开启A 关闭B

    @Override
    public void initData(Bundle savedInstanceState) {
        settingDistance();//报警距离
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
            R.id.tv_finish,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_highestTemp://最高温
                Dialog.showAlertDialog(context, "请设置最高温度", new Dialog.DialogDataListener() {
                    @Override
                    public void confirm(String data) {
                        if (data != null) {
                            highestTemp = Float.parseFloat(data);
                        } else {
                            highestTemp = 80;
                        }
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
                        if (data != null) {
                            lowestTemp = Float.parseFloat(data);
                        } else {
                            lowestTemp = 0;
                        }
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
                intent.putExtra("policeDiatance", policeDiatance);
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

    //报警距离
    private void settingDistance() {
        String[] mItems2 = getResources().getStringArray(R.array.distance);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems2);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setting_distance.setAdapter(adapter1);//绑定 Adapter到控件
        setting_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                policeDiatance = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    public static void lauch(Activity activity, int requestCode) {
        CommonKit.startActivityForResult(activity, SettingAlarmActivity.class, null, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settingalarm;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_alarm://报警开关
                if (isChecked) {
                    //选中
                    police = "A";
                } else {
                    //未选中
                    police = "B";
                }
                Log.e(TAG, "police = " + police);

                break;
            case R.id.tb_alarm_demolition://防拆报警开关
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
            case R.id.tb_become://静默开关
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
            case R.id.tb_tempPolice://温度报警开关
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
            case R.id.tb_humidityPolice://湿度报警开关
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
            case R.id.tb_distancePolice://报警距离开关
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
        }
    }
}
