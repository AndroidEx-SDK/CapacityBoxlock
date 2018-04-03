package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.RLog;

import butterknife.Bind;
import butterknife.OnClick;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_CLEARFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_BECOME;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_POSSESSOR;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_SETTING;
import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;

/**
 * 配置箱体,箱体详情
 */
public class SettingFingerActivity extends BaseActivity {
    @Bind(R.id.titlebar)
    SecondTitleBar titlebar;
    @Bind(R.id.tv_possessorFinger)
    TextView tv_possessorFinger;
    @Bind(R.id.tv_becomeFinger)
    TextView tv_becomeFinger;

    private static final String TAG = SettingFingerActivity.class.getSimpleName();
    private String mac;//箱体的mac
    private String possessorFinger1 = null;//所有人指纹信息或ID
    private String possessorFinger2 = null;//所有人指纹信息或ID
    private String possessorFinger3 = null;//所有人指纹信息或ID
    private String becomeFinger1 = null;////静默模式功能的指纹
    private String becomeFinger2 = null;////静默模式功能的指纹
    private String becomeFinger3 = null;////静默模式功能的指纹
    private DataBroadcast dataBroadcast;
    private Context mContext;

    @Override
    public void initData(Bundle savedInstanceState) {
        mContext = context;
        mac = getIntent().getStringExtra(EXTRA_ITEM_ADDRESS);
        String becomeNum = getIntent().getStringExtra("becomeNum");
        String possessorNum = getIntent().getStringExtra("possessorNum");
        initBroadCast();
        initTitleBar();
        if (becomeNum != null) {
            tv_becomeFinger.setText(becomeNum);
        }
        if (possessorNum != null) {
            tv_possessorFinger.setText(possessorNum);
        }
    }

    @OnClick({
            R.id.ll_clearFinger,
            R.id.ll_possessorFinger,
            R.id.ll_becomeFinger,
    })
    public void clickEvent(View view) {
        if (MyBleService.getInstance().getConnectDevice(mac) == null) {
            CommonKit.showErrorShort(context, "蓝牙未连接");
            return;
        }
        switch (view.getId()) {
            case R.id.ll_becomeFinger://无线静默功能指纹信息
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_ITEM_ADDRESS, mac);
                FingerEnterActivity.lauch(context, bundle, REQUESTCODE_FINGER_BECOME);
                break;
            case R.id.ll_possessorFinger://所有人的指纹信息
                Bundle bundle1 = new Bundle();
                bundle1.putString(EXTRA_ITEM_ADDRESS, mac);
                FingerEnterActivity.lauch(context, bundle1, REQUESTCODE_FINGER_POSSESSOR);
                break;
            case R.id.ll_clearFinger:
                MyBleService.getInstance().clearFinger(mac);
                break;
            default:
                break;
        }
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_CLEARFINGER);//清除指纹
        dataBroadcast = new DataBroadcast();
        context.registerReceiver(dataBroadcast, intentFilter);
    }

    private void initTitleBar() {
        titlebar.getLeftBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (possessorFinger1 != null && becomeFinger1 != null) {
                    Intent intent = new Intent();
                    intent.putExtra("possessorFinger1", possessorFinger1);
                    intent.putExtra("possessorFinger2", possessorFinger2);
                    intent.putExtra("possessorFinger3", possessorFinger3);
                    intent.putExtra("becomeFinger1", becomeFinger1);
                    intent.putExtra("becomeFinger2", becomeFinger2);
                    intent.putExtra("becomeFinger3", becomeFinger3);
                    setResult(Activity.RESULT_OK, intent);
                }
                CommonKit.finishActivity(context);
            }
        });

        if (mac != null) {
            if (MyBleService.getInstance().getConnectDevice(mac) != null) {
                titlebar.getRightTv().setText("已连接");
            }
        }
        titlebar.getRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyBleService.getInstance().getConnectDevice(mac) == null) {
                    MyBleService.getInstance().connectionDevice(context, mac);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_FINGER_POSSESSOR) {//所有人指纹录取
            switch (resultCode) {
                case Activity.RESULT_OK:
                    possessorFinger1 = data.getStringExtra("possessorFinger1");
                    possessorFinger2 = data.getStringExtra("possessorFinger2");
                    possessorFinger3 = data.getStringExtra("possessorFinger3");
                    tv_possessorFinger.setText("3");
                    break;
                default:
                    CommonKit.showErrorShort(context, "取消所有人指纹录入");
                    break;
            }
        } else if (requestCode == REQUESTCODE_FINGER_BECOME) {//静默指纹录入
            switch (resultCode) {
                case Activity.RESULT_OK:
                    becomeFinger1 = data.getStringExtra("becomeFinger1");
                    becomeFinger2 = data.getStringExtra("becomeFinger2");
                    becomeFinger3 = data.getStringExtra("becomeFinger3");
                    tv_becomeFinger.setText("3");
                    break;
                default:
                    CommonKit.showErrorShort(context, "取消静默指纹录入");
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (possessorFinger1 != null && becomeFinger1 != null) {
            Intent intent = new Intent();
            intent.putExtra("possessorFinger1", possessorFinger1);
            intent.putExtra("possessorFinger2", possessorFinger2);
            intent.putExtra("possessorFinger3", possessorFinger3);
            intent.putExtra("becomeFinger1", becomeFinger1);
            intent.putExtra("becomeFinger2", becomeFinger2);
            intent.putExtra("becomeFinger3", becomeFinger3);
            setResult(Activity.RESULT_OK, intent);
        }
        CommonKit.finishActivity(context);
        super.onBackPressed();
    }

    public class DataBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceMac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            if (!mac.equals(deviceMac)) return;
            byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS:
                case BLE_CONN_SUCCESS_ALLCONNECTED:
                    RLog.d("连接成功=");
                    titlebar.getRightTv().setText("已连接");
                    CommonKit.showOkShort(mContext, "连接成功");
                    break;

                case BLE_CONN_DIS:
                    RLog.d("断开连接=");
                    titlebar.getRightTv().setText("点击连接");
                    CommonKit.showErrorShort(mContext, "蓝牙已断开");
                    break;

                case ACTION_CLEARFINGER://清除指纹
                    RLog.d("收到清除指纹 b=" + Byte2HexUtil.byte2Hex(b));
                    switch (b[2]) {
                        case (byte) 0x00://成功
                            CommonKit.showMsgShort(mContext, "清除成功");
                            break;
                        case (byte) 0x01://失败
                            CommonKit.showErrorShort(mContext, "清除失败");
                            break;
                    }
                    tv_possessorFinger.setText("0");
                    tv_becomeFinger.setText("0");
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(dataBroadcast);
    }

    @Override
    public void setListener() {

    }


    @Override
    public void onClick(View view) {

    }

    public static void lauch(Activity activity, Bundle bundle) {
        CommonKit.startActivityForResult(activity, SettingFingerActivity.class, bundle, REQUESTCODE_FINGER_SETTING);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settingfinger;
    }

}