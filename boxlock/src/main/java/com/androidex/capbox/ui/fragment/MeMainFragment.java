package com.androidex.capbox.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.ConnectDeviceListActivity;
import com.androidex.capbox.ui.activity.SettingActivity;
import com.androidex.capbox.ui.activity.TypeOfAlarmActivity;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;

import static com.androidex.boxlib.cache.SharedPreTool.IS_OPEN_LOCKSCREEN;

public class MeMainFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private static String TAG = "MeMainFragment";
    @Bind(R.id.settint_bt_user)
    TextView tv_setting;
    @Bind(R.id.setting_alarm)
    LinearLayout setting_alarm;
    @Bind(R.id.ll_connectDevice)
    LinearLayout ll_connectDevice;
    @Bind(R.id.tv_username)
    TextView tv_username;
    @Bind(R.id.setting_distance)
    Spinner setting_distance;
    @Bind(R.id.tb_alarm)
    ToggleButton tb_alarm;
@Bind(R.id.tb_lockscreen)
    ToggleButton tb_lockscreen;

    private boolean isToast = false;
    private boolean isToast_lockscreen = false;

    @Override
    public void initData() {
        initView();
        isToast = false;
        isToast_lockscreen = false;
        if (SharedPreTool.getInstance(context).getBoolData(SharedPreTool.IS_POLICE, true)) {
            tb_alarm.setChecked(true);
        } else {
            tb_alarm.setChecked(false);
        }
        if (SharedPreTool.getInstance(context).getBoolData(IS_OPEN_LOCKSCREEN, true)) {
            tb_lockscreen.setChecked(true);
        } else {
            tb_lockscreen.setChecked(false);
        }
    }

    @Override
    public void setListener() {
        tv_setting.setOnClickListener(this);
        setting_alarm.setOnClickListener(this);
        ll_connectDevice.setOnClickListener(this);
        tb_alarm.setOnCheckedChangeListener(this);
        tb_lockscreen.setOnCheckedChangeListener(this);
    }

    /**
     * 初始化
     */
    private void initView() {
        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        if (username != null) {
            tv_username.setText(username);
        }
        int rssiMaxValue = MyBleService.get().getRssiMaxValue();
        settingDistance();//报警距离
        if (rssiMaxValue >= 0) {
            setting_distance.setSelection(0);
        } else if (rssiMaxValue == -70) {
            setting_distance.setSelection(1);
        } else if (rssiMaxValue == -80) {
            setting_distance.setSelection(2);
        } else if (rssiMaxValue == -90) {
            setting_distance.setSelection(3);
        } else if (rssiMaxValue == -98) {
            setting_distance.setSelection(4);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settint_bt_user://
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_alarm://报警设置
                Intent intent_1 = new Intent(context, TypeOfAlarmActivity.class);
                startActivity(intent_1);
                break;
            case R.id.ll_connectDevice://已连接设备
                ConnectDeviceListActivity.lauch(context);
                break;
            default:
                break;
        }
    }

    //报警距离
    private void settingDistance() {
        String[] mItems2 = getResources().getStringArray(R.array.distance);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mItems2);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setting_distance.setAdapter(adapter1);//绑定 Adapter到控件
        setting_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.e(TAG, "pos=" + pos);
                switch (pos) {
                    case 0:
                        MyBleService.get().setRssiMaxValue(0);
                        break;
                    case 1:
                        MyBleService.get().setRssiMaxValue(-70);
                        break;
                    case 2:
                        MyBleService.get().setRssiMaxValue(-80);
                        break;
                    case 3:
                        MyBleService.get().setRssiMaxValue(-90);
                        break;
                    case 4:
                        MyBleService.get().setRssiMaxValue(-98);
                        break;
                    default:
                        MyBleService.get().setRssiMaxValue(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_alarm://报警开关
                Loge(TAG, "alarm开关  " + isChecked);
                if (isChecked) {
                    SharedPreTool.getInstance(context).setBoolData(SharedPreTool.IS_POLICE, true);
                } else {
                    SharedPreTool.getInstance(context).setBoolData(SharedPreTool.IS_POLICE, false);
                    if (!isToast) {
                        isToast = true;
                        return;
                    }
                }
                if (SharedPreTool.getInstance(context).getBoolData(SharedPreTool.IS_POLICE, true)) {
                    CommonKit.showOkShort(context, "打开报警开关成功");
                } else {
                    CommonKit.showOkShort(context, "关闭报警开关成功");
                }
                break;
            case R.id.tb_lockscreen://锁屏开关
                Loge(TAG, "alarm开关  " + isChecked);
                if (isChecked) {
                    SharedPreTool.getInstance(context).setBoolData(IS_OPEN_LOCKSCREEN, true);
                } else {
                    SharedPreTool.getInstance(context).setBoolData(IS_OPEN_LOCKSCREEN, false);
                    if (!isToast_lockscreen) {
                        isToast_lockscreen = true;
                        return;
                    }
                }
                if (SharedPreTool.getInstance(context).getBoolData(IS_OPEN_LOCKSCREEN, true)) {
                    CommonKit.showOkShort(context, "锁屏开关打开");
                } else {
                    CommonKit.showOkShort(context, "锁屏功能关闭");
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_memain;
    }

}
