package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;

public class AboutActivity extends BaseActivity {
    public static String TAG = "AboutActivity";
    @Bind(R.id.about_tv_versionNum)
    TextView versionNum;

    @Override
    public void initData(Bundle savedInstanceState) {
        versionNum.setText(getResources().getString(R.string.about_tv_versionNum) + CommonKit.getVersionName(context));
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
