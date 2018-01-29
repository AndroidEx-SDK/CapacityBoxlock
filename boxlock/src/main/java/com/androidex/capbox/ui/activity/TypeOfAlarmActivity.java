package com.androidex.capbox.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 报警方式
 *
 * @author Administrator
 */
public class TypeOfAlarmActivity extends BaseActivity {
    @Bind(R.id.btn_distance)
    Button btn_distance;
    @Bind(R.id.btn_temp)
    Button btn_temp;
    @Bind(R.id.btn_lost)
    Button btn_lost;

    private int callType;
    private String[] areas;
    private RadioOnClick OnClick = new RadioOnClick(3);//不选中(>2    areas.length)

    @Override
    public void initData(Bundle savedInstanceState) {
        areas = getResources().getStringArray(R.array.areas);
        initView();
    }

    @Override
    public void setListener() {

    }

    @OnClick({
            R.id.ll_temp,
            R.id.ll_distance,
            R.id.ll_lost
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_lost:
                callType = 0;
                AlertDialog ad0 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick.getIndex(), OnClick).create();
                ad0.show();
                break;
            case R.id.ll_distance:
                callType = 1;
                AlertDialog ad1 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick.getIndex(), OnClick).create();
                ad1.show();
                break;
            case R.id.ll_temp:
                callType = 2;
                AlertDialog ad2 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick.getIndex(), OnClick).create();
                ad2.show();
                break;
            default:
                break;
        }

    }

    /**
     * 初始化控件
     */
    private void initView() {

    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 提醒方式Radio选择
     *
     * @author Administrator
     */
    class RadioOnClick implements DialogInterface.OnClickListener {
        private int index;

        public RadioOnClick(int index) {
            this.index = index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            setIndex(whichButton);
            switch (callType) {
                case 0:
                    btn_lost.setText(areas[index]);
                    break;
                case 1:
                    btn_distance.setText(areas[index]);
                    break;
                case 2:
                    btn_temp.setText(areas[index]);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_typeofalarm;
    }

}
