package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.FingerCacheUtil;
import com.androidex.capbox.utils.RLog;

import butterknife.Bind;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BECOMEFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_CARRYFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_POSSESSORFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_BECOME;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_CARRY;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_POSSESSOR;
import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;

/**
 * @author liyp
 * @editTime 2017/11/1
 */

public class FingerEnterActivity extends BaseActivity {
    private static final String TAG = "FingerEnterActivity";
    @Bind(R.id.tv_hint_printFinger)
    TextView tv_hint_printFinger;

    private FingerEnterActivity.DataBroadcast dataBroadcast;
    private Context mContext;
    private String address;
    private static int code;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    CommonKit.finishActivity(context);
                    break;
                case 1:
                    Intent intent1 = new Intent();
                    setResult(Activity.RESULT_OK, intent1);
                    CommonKit.finishActivity(context);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void initData(Bundle savedInstanceState) {
        mContext = context;
        address = getIntent().getStringExtra(EXTRA_ITEM_ADDRESS);
        initBroadCast();
        if (MyBleService.getInstance().getConnectDevice(address) != null) {
            tv_hint_printFinger.setText("请将手指放到箱体的指纹处");
            if (code == REQUESTCODE_FINGER_POSSESSOR) {
                MyBleService.getInstance().setFinger(address, 11);
            } else if (code == REQUESTCODE_FINGER_CARRY) {
                MyBleService.getInstance().setFinger(address, 12);
            } else if (code == REQUESTCODE_FINGER_BECOME) {
                MyBleService.getInstance().setFinger(address, 13);
            }
        } else {
            CommonKit.showErrorShort(context, "正在连接蓝牙，稍后再试");
            MyBleService.getInstance().connectionDevice(context, address);
        }
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_POSSESSORFINGER);//获取到所有人的指纹信息
        intentFilter.addAction(ACTION_BECOMEFINGER);//获取开启静默功能的指纹信息
        intentFilter.addAction(ACTION_CARRYFINGER);//获取到携行人的指纹信息
        dataBroadcast = new FingerEnterActivity.DataBroadcast();
        registerReceiver(dataBroadcast, intentFilter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

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
                    Log.d(TAG, "连接成功=");
                    CommonKit.showOkShort(mContext, "连接成功");
                    break;
                case BLE_CONN_DIS:
                    Log.d(TAG, "断开连接=");
                    MyBleService.getInstance().connectionDevice(context, address);
                    CommonKit.showErrorShort(mContext, "蓝牙断开，已开始重连");
                    break;
                case ACTION_POSSESSORFINGER://获取到所有人的指纹信息
                    RLog.d("获取到所有人的指纹信息 b=" + Byte2HexUtil.byte2Hex(b));
                    switch (b[1]) {
                        case (byte) 0x01:
                            RLog.d("指纹录入，第一次录入成功");
                            tv_hint_printFinger.setText("第一次录入成功");
                            break;
                        case (byte) 0x02:
                            RLog.d("指纹录入，第二次录入成功");
                            tv_hint_printFinger.setText("第二次录入成功");
                            break;
                        case (byte) 0x03:
                            RLog.d("指纹录入，第三次录入成功");
                            tv_hint_printFinger.setText("第三次录入成功");
                            handler.sendEmptyMessage(0);
                            FingerCacheUtil.addOpenFinger(context, address);//添加开锁指纹缓存
                            break;
                        case (byte) 0x00:
                            tv_hint_printFinger.setText("录入失败请重新录入");
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_BECOMEFINGER://静默功能的指纹信息
                    RLog.d("静默功能的指纹信息 b=" + Byte2HexUtil.byte2Hex(b));
                    switch (b[1]) {
                        case (byte) 0x01:
                            RLog.d("指纹录入，第一次录入成功");
                            tv_hint_printFinger.setText("第一次录入成功");
                            break;
                        case (byte) 0x02:
                            RLog.d("指纹录入，第二次录入成功");
                            tv_hint_printFinger.setText("第二次录入成功");
                            break;
                        case (byte) 0x03:
                            RLog.d("指纹录入，第三次录入成功");
                            tv_hint_printFinger.setText("第三次录入成功");
                            handler.sendEmptyMessage(1);
                            FingerCacheUtil.addBecomeFinger(context, address);//添加静默指纹缓存
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_CARRYFINGER://携行人指纹
                    RLog.d("静默功能的指纹信息 b=" + Byte2HexUtil.byte2Hex(b));
                    break;
                default:
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
    public int getLayoutId() {
        return R.layout.activity_fingerenter;
    }

    public static void lauch(Activity activity, Bundle bundle, int requestCode) {
        code = requestCode;
        CommonKit.startActivityForResult(activity, FingerEnterActivity.class, bundle, requestCode);
    }
}
