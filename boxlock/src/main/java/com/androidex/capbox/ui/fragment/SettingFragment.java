package com.androidex.capbox.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.cache.SharedPreTool;
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

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

}
