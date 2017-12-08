package com.androidex.capbox.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.AboutActivity;
import com.androidex.capbox.ui.activity.SettingActivity;
import com.androidex.capbox.ui.activity.TypeOfAlarm;

import butterknife.Bind;

public class SettingFragment extends BaseFragment {
    private static String TAG = "SettingFragment";
    @Bind(R.id.settint_bt_user)
    TextView tv_setting;
    @Bind(R.id.setting_alarm)
    LinearLayout setting_alarm;
    @Bind(R.id.setting_about)
    LinearLayout setting_about;
    @Bind(R.id.tv_username)
    TextView tv_username;
    @Bind(R.id.setting_distance)
    Spinner setting_distance;

    @Override
    public void initData() {
        initView();
    }

    @Override
    public void setListener() {
        tv_setting.setOnClickListener(this);
        setting_alarm.setOnClickListener(this);
        setting_about.setOnClickListener(this);
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
            case R.id.settint_bt_user:
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_alarm://报警设置
                Intent intent_1 = new Intent(context, TypeOfAlarm.class);
                startActivity(intent_1);
                break;
            case R.id.setting_about://关于
                startActivity(new Intent(context, AboutActivity.class));
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
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

}
