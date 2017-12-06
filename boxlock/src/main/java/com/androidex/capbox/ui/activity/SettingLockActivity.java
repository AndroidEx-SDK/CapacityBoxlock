package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author liyp
 * @editTime 2017/10/10
 */

public class SettingLockActivity extends BaseActivity {
    @Bind(R.id.rule)
    Spinner rule;
    @Bind(R.id.tv_lockMode)
    TextView tv_lockMode;

    private static final String TAG = "SettingLockActivity";
    private String unlocking = "A";//开锁次数，多次有效A，一次有效B
    private String unlockingMode = "ABC";//开锁方式设定: 指纹开锁A，腕表开锁B 同时开锁 C

    @Override
    public void initData(Bundle savedInstanceState) {
        initOpenMode();//开锁规则
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    /**
     * //开锁次数设定，多次有效A 一次有效B
     * 初始化开锁规则
     */
    private void initOpenMode() {
        final String[] mItems1 = getResources().getStringArray(R.array.rule);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        rule.setAdapter(adapter);
        rule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.e(TAG, "开锁模式 = " + mItems1[pos]);
                switch (pos) {
                    case 0:
                        unlocking = "A";
                        break;
                    case 1:
                        unlocking = "B";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @OnClick({
            R.id.tv_finish,
            R.id.ll_lockMode,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_finish:
                Intent intent = new Intent();
                intent.putExtra("unlocking", unlocking);
                intent.putExtra("unlockingMode", unlockingMode);
                setResult(Activity.RESULT_OK, intent);
                CommonKit.finishActivity(this);
                break;
            case R.id.ll_lockMode:
                showMultiDialog();
                break;
            default:
                break;
        }
    }

    boolean[] selected = new boolean[]{true, true, true};//默认选中位置

    /**
     * 开锁方式设定: 指纹开锁A，腕表开锁B ,指定位置开锁C
     */
    private void showMultiDialog() {
        unlockingMode = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("开锁方式设定");
        DialogInterface.OnMultiChoiceClickListener mutiListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                selected[which] = isChecked;
            }
        };
        builder.setMultiChoiceItems(R.array.mode, selected, mutiListener);
        DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (selected[0]) {
                    unlockingMode = "A";
                }
                if (selected[1]) {
                    unlockingMode += "B";
                }
                if (selected[2]) {
                    unlockingMode += "C";
                }
                if (!TextUtils.isEmpty(unlockingMode)) {
                    tv_lockMode.setText(unlockingMode);
                } else {
                    tv_lockMode.setText("暂无选择");
                }
            }
        };
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", btnListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);// dialog弹出后，点击界面其他部分dialog消失
    }

    public static void lauch(Activity activity, int requestCode) {
        CommonKit.startActivityForResult(activity, SettingLockActivity.class, null, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settinglock;
    }
}
