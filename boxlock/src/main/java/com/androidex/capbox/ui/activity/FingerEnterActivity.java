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

import com.androidex.boxlib.modules.ConnectedDevice;
import com.androidex.boxlib.service.BleService;
import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BECOMEFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_CARRYFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_POSSESSORFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_BECOME;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_FINGER_POSSESSOR;

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
    private String mac;
    private String possessorFinger1 = null;//所有人指纹信息或ID
    private String possessorFinger2 = null;//所有人指纹信息或ID
    private String possessorFinger3 = null;//所有人指纹信息或ID
    private String becomeFinger1 = null;////静默模式功能的指纹
    private String becomeFinger2 = null;////静默模式功能的指纹
    private String becomeFinger3 = null;////静默模式功能的指纹
    private static int code;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent();
                    intent.putExtra("possessorFinger1", possessorFinger1);
                    intent.putExtra("possessorFinger2", possessorFinger2);
                    intent.putExtra("possessorFinger3", possessorFinger3);
                    setResult(Activity.RESULT_OK, intent);
                    CommonKit.finishActivity(context);
                    break;
                case 1:
                    Intent intent1 = new Intent();
                    intent1.putExtra("becomeFinger1", becomeFinger1);
                    intent1.putExtra("becomeFinger2", becomeFinger2);
                    intent1.putExtra("becomeFinger3", becomeFinger3);
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
        mac = getIntent().getStringExtra("mac");
        initBroadCast();
        if (ConnectedDevice.get().getConnectDevice(mac) != null) {
            if (code == REQUESTCODE_FINGER_POSSESSOR) {
                MyBleService.get().getFinger(mac, "1101");
                tv_hint_printFinger.setText("请将手指放到箱体的指纹处");
            } else if (code == REQUESTCODE_FINGER_BECOME) {
                tv_hint_printFinger.setText("请将手指放到箱体的指纹处");
                MyBleService.get().getFinger(mac, "1301");
            } else {

            }
        } else {
            CommonKit.showErrorShort(context, "正在连接蓝牙，稍后再试");
            BleService.get().connectionDevice(context, mac);
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
            if (!mac.equals(deviceMac)) return;
            byte[] b = intent.getByteArrayExtra(BLECONSTANTS_ADDRESS);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS:
                case BLE_CONN_SUCCESS_ALLCONNECTED:
                    Log.d(TAG, "连接成功=");
                    BleService.get().enableNotify(mac);
                    CommonKit.showOkShort(mContext, "连接成功");
                    break;

                case BLE_CONN_DIS:
                    Log.d(TAG, "断开连接=");
                    BleService.get().connectionDevice(context, mac);
                    CommonKit.showErrorShort(mContext, "蓝牙断开，已开始重连");
                    break;
                case ACTION_POSSESSORFINGER://获取到所有人的指纹信息
                    Log.d(TAG, "获取到所有人的指纹信息b=" + Byte2HexUtil.byte2Hex(b));
                    switch (b[2]) {
                        case (byte) 0x01://识别成功
                            tv_hint_printFinger.setText("录入成功");
                            possessorFinger1 = "1";
                            possessorFinger2 = "2";
                            possessorFinger3 = "3";
                            handler.sendEmptyMessage(0);
                            break;
                        case (byte) 0x02://识别失败
                            tv_hint_printFinger.setText("");
                            MyBleService.get().getFinger(mac, "1101");
                            CommonKit.showErrorShort(context, "请重新录入");
                            tv_hint_printFinger.setText("指纹录入失败，请重新录入");
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_BECOMEFINGER://静默功能的指纹信息
                    Log.e(TAG, "静默功能的指纹信息");
                    switch (b[2]) {
                        case (byte) 0x01://识别成功
                            tv_hint_printFinger.setText("录入成功");
                            becomeFinger1 = "1";
                            becomeFinger2 = "2";
                            becomeFinger3 = "3";
                            handler.sendEmptyMessage(1);
                            break;
                        case (byte) 0x02://识别失败
                            tv_hint_printFinger.setText("");
                            MyBleService.get().getFinger(mac, "1301");
                            CommonKit.showErrorShort(context, "请重新录入");
                            tv_hint_printFinger.setText("指纹录入失败，请重新录入");
                            break;
                        default:
                            break;
                    }
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
