package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {
    public static String TAG = "AboutActivity";
    @Bind(R.id.about_tv_versionNum)
    TextView versionNum;

    @Override
    public void initData(Bundle savedInstanceState) {
        versionNum.setText(getResources().getString(R.string.about_tv_versionNum) + CommonKit.getVersionName(context));
    }

    @OnClick({
            R.id.about_btn_back,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.about_btn_back:
                CommonKit.finishActivity(this);
                break;
        }
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, AboutActivity.class, null, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

}
