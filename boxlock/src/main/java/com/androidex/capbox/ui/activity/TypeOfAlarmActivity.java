package com.androidex.capbox.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;

import butterknife.Bind;
import butterknife.OnClick;

import static com.androidex.capbox.utils.Constants.SP.SP_DISTANCE_TYPE;
import static com.androidex.capbox.utils.Constants.SP.SP_LOST_TYPE;
import static com.androidex.capbox.utils.Constants.SP.SP_TEMP_TYPE;

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
            R.id.ll_lost,
            R.id.btn_lost,
            R.id.btn_distance,
            R.id.btn_temp
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_lost:
                callType = 0;
                RadioOnClick OnClick = new RadioOnClick(SharedPreTool.getInstance(this).getIntData(SP_LOST_TYPE, 0));
                AlertDialog ad0 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick.getIndex(), OnClick).create();
                ad0.show();
                break;
            case R.id.ll_distance:
                callType = 1;
                RadioOnClick OnClick1 = new RadioOnClick(SharedPreTool.getInstance(this).getIntData(SP_DISTANCE_TYPE, 0));
                AlertDialog ad1 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick1.getIndex(), OnClick1).create();
                ad1.show();
                break;
            case R.id.ll_temp:
                callType = 2;
                RadioOnClick OnClick2 = new RadioOnClick(SharedPreTool.getInstance(this).getIntData(SP_TEMP_TYPE, 0));
                AlertDialog ad2 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick2.getIndex(), OnClick2).create();
                ad2.show();
                break;
            case R.id.btn_lost:
                callType = 0;
                RadioOnClick OnClick3 = new RadioOnClick(SharedPreTool.getInstance(this).getIntData(SP_LOST_TYPE, 0));
                AlertDialog ad3 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick3.getIndex(), OnClick3).create();
                ad3.show();
                break;
            case R.id.btn_distance:
                callType = 1;
                RadioOnClick OnClick4 = new RadioOnClick(SharedPreTool.getInstance(this).getIntData(SP_DISTANCE_TYPE, 0));
                AlertDialog ad4 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick4.getIndex(), OnClick4).create();
                ad4.show();
                break;
            case R.id.btn_temp:
                callType = 2;
                RadioOnClick OnClick5 = new RadioOnClick(SharedPreTool.getInstance(this).getIntData(SP_TEMP_TYPE, 0));
                AlertDialog ad5 = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
                        .setSingleChoiceItems(areas, OnClick5.getIndex(), OnClick5).create();
                ad5.show();
                break;
            default:
                break;
        }

    }

    /**
     * 初始化控件
     */
    private void initView() {
        btn_lost.setText(areas[SharedPreTool.getInstance(this).getIntData(SP_LOST_TYPE, 0)]);
        btn_distance.setText(areas[SharedPreTool.getInstance(this).getIntData(SP_DISTANCE_TYPE, 0)]);
        btn_temp.setText(areas[SharedPreTool.getInstance(this).getIntData(SP_TEMP_TYPE, 0)]);
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
                    SharedPreTool.getInstance(context).setIntData(SP_LOST_TYPE, index);
                    btn_lost.setText(areas[index]);
                    break;
                case 1:
                    SharedPreTool.getInstance(context).setIntData(SP_DISTANCE_TYPE, index);
                    btn_distance.setText(areas[index]);
                    break;
                case 2:
                    SharedPreTool.getInstance(context).setIntData(SP_TEMP_TYPE, index);
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
