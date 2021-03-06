package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.FingerCacheUtil;
import com.androidex.capbox.utils.RLog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_CLEARFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_OWNER_BECOME;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_OWNER_OPEN;
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
    @Bind(R.id.ll_clearFinger)
    LinearLayout ll_clearFinger;
    @Bind(R.id.ll_clearBecomeFinger)
    LinearLayout ll_clearBecomeFinger;

    private String address;//箱体的mac
    private DataBroadcast dataBroadcast;
    private Context mContext;
    private boolean isClear = false;
    private int fingerTag;

    @Override
    public void initData(Bundle savedInstanceState) {
        mContext = context;
        address = getIntent().getStringExtra(EXTRA_ITEM_ADDRESS);
        initBroadCast();
        initTitleBar();
        tv_possessorFinger.setText(FingerCacheUtil.isHasOpenFinger(context, address) ? "3" : "0");
        tv_becomeFinger.setText(FingerCacheUtil.isHasBecomeFinger(context, address) ? "3" : "0");
    }

    /**
     * 启动超时检测
     */
    private void startTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isClear) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            disProgress();
                            CommonKit.showMsgShort(mContext, "清除超时");
                            ll_clearFinger.setClickable(true);
                            ll_clearBecomeFinger.setClickable(true);
                        }
                    });
                }
            }
        };
        new Timer().schedule(task, 5000);
    }

    @OnClick({
            R.id.ll_possessorFinger,
            R.id.ll_becomeFinger,
            R.id.ll_clearFinger,
            R.id.ll_clearBecomeFinger,
    })
    public void clickEvent(View view) {
        if (MyBleService.getInstance().getConnectDevice(address) == null) {
            CommonKit.showErrorShort(context, "蓝牙未连接");
            return;
        }
        switch (view.getId()) {
            case R.id.ll_possessorFinger://录入指纹信息
                if (FingerCacheUtil.isHasOpenFinger(context, address)) {
                    CommonKit.showErrorShort(context, "指纹已经录入");
                } else {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(EXTRA_ITEM_ADDRESS, address);
                    FingerEnterActivity.lauch(context, bundle1, REQUESTCODE_FINGER_OWNER_OPEN);
                }
                break;
            case R.id.ll_becomeFinger://无线静默功能指纹信息
                if (FingerCacheUtil.isHasBecomeFinger(context, address)) {
                    CommonKit.showErrorShort(context, "指纹已经录入");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_ITEM_ADDRESS, address);
                    FingerEnterActivity.lauch(context, bundle, REQUESTCODE_FINGER_OWNER_BECOME);
                }
                break;
            case R.id.ll_clearFinger: {
                isClear = true;
                fingerTag = 0;
                ll_clearBecomeFinger.setClickable(false);
                showProgress("正在清除...");
                MyBleService.getInstance().clearFinger(address, 01);
                startTimer();
            }
            break;
            case R.id.ll_clearBecomeFinger: {
                isClear = true;
                fingerTag = 1;
                ll_clearFinger.setClickable(false);
                showProgress("正在清除...");
                MyBleService.getInstance().clearFinger(address, 02);
                startTimer();
            }
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
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                CommonKit.finishActivity(context);
            }
        });

        if (address != null) {
            if (MyBleService.getInstance().getConnectDevice(address) != null) {
                titlebar.getRightTv().setText("已连接");
            }
        }
        titlebar.getRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyBleService.getInstance().getConnectDevice(address) == null) {
                    MyBleService.getInstance().connectionDevice(context, address);
                    showProgress("正在连接...");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_FINGER_OWNER_OPEN) {//所有人指纹录取
            switch (resultCode) {
                case Activity.RESULT_OK:
                    tv_possessorFinger.setText(FingerCacheUtil.isHasOpenFinger(context, address) ? "3" : "0");
                    CommonKit.showErrorShort(context, "录入成功");
                    break;
                default:
                    CommonKit.showErrorShort(context, "取消所有人指纹录入");
                    break;
            }
        } else if (requestCode == REQUESTCODE_FINGER_OWNER_BECOME) {//静默指纹录入
            switch (resultCode) {
                case Activity.RESULT_OK:
                    tv_becomeFinger.setText(FingerCacheUtil.isHasBecomeFinger(context, address) ? "3" : "0");
                    CommonKit.showOkShort(context, "录入成功");
                    break;
                default:
                    CommonKit.showErrorShort(context, "取消静默指纹录入");
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class DataBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceMac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            if (!address.equals(deviceMac)) return;
            byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS:
                case BLE_CONN_SUCCESS_ALLCONNECTED:
                    RLog.d("连接成功=");
                    disProgress();
                    titlebar.getRightTv().setText("已连接");
                    CommonKit.showOkShort(mContext, "连接成功");
                    break;
                case BLE_CONN_FAIL:
                    RLog.d("连接失败");
                    showProgress("连接失败，正在重连...");
                    if (MyBleService.getInstance().getConnectDevice(address) == null) {
                        MyBleService.getInstance().connectionDevice(context, address);
                    }
                    break;
                case BLE_CONN_DIS:
                    RLog.d("断开连接=");
                    titlebar.getRightTv().setText("点击连接");
                    CommonKit.showErrorShort(mContext, "蓝牙已断开");
                    break;
                case ACTION_CLEARFINGER://清除指纹
                    RLog.d("收到清除指纹 b=" + Byte2HexUtil.byte2Hex(b));
                    isClear = false;
                    ll_clearFinger.setClickable(true);
                    ll_clearBecomeFinger.setClickable(true);
                    disProgress();
                    switch (b[1]) {
                        case (byte) 0x01://成功
                            CommonKit.showMsgShort(mContext, "清除成功");
                            if (fingerTag == 0) {
                                FingerCacheUtil.clearOpenFingerCache(context, address);
                            } else {
                                FingerCacheUtil.clearBecomeFingerCache(context, address);
                            }
                            break;
                        case (byte) 0x00://失败
                            CommonKit.showErrorShort(mContext, "清除失败");
                            break;
                        default:
                            CommonKit.showErrorShort(mContext, "清除失败");
                            break;
                    }
                    tv_possessorFinger.setText(FingerCacheUtil.isHasOpenFinger(context, address) ? "3" : "0");
                    tv_becomeFinger.setText(FingerCacheUtil.isHasBecomeFinger(context, address) ? "3" : "0");
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