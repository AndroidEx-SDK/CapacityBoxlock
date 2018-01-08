package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author liyp
 * @editTime 2017/10/25
 */

public class BoxSettingActivity extends BaseActivity {
    @Bind(R.id.et_name)
    EditText et_name;

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @OnClick({
            R.id.tv_confirm,
            R.id.tv_cancle,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm://配置箱体
                String name = et_name.getText().toString().trim();
                if (name != null&&!name.isEmpty()&&!name.equals("")) {
                    Intent intent = new Intent();
                    intent.putExtra("name", name);
                    setResult(Activity.RESULT_OK, intent);
                    CommonKit.finishActivity(this);
                } else {
                    CommonKit.showErrorShort(context, "昵称不可为空");
                }
                break;
            case R.id.tv_cancle://取消设置
                CommonKit.finishActivity(this);
                break;

            default:
                break;

        }
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    public static void lauch(Activity activity) {
        CommonKit.startActivityForResult(activity, BoxSettingActivity.class, null, Constants.CODE.REQUESTCODE_SET_BOX);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_boxsetting;
    }
}
