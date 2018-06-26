package com.androidex.capbox.ui.activity;

/**
 * Created by cts on 17/3/31.
 */

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.RLog;

import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.Bind;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_ALL_DATA;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_DEBUG_LOG;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_SOCKET_OSPF;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_OFF;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_ON;
import static com.androidex.boxlib.utils.BleConstants.LOG.ACTION_LOG_TEST;
import static com.androidex.boxlib.utils.BleConstants.NET.ACTION_NET_TCP_RECEIVE;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_ADD_DEBUG_DEVICE;

/**
 * @author benjaminwan
 * 串口助手
 * 程序载入时自动搜索串口设备
 * n,8,1，没得选
 */
public class DebugBLEActivity extends BaseActivity {
    @Bind(R.id.titlebar)
    SecondTitleBar titlebar;
    @Bind(R.id.editTextCOMA)
    EditText editTextCOMA;
    @Bind(R.id.editTextLines)
    EditText editTextLines;
    @Bind(R.id.editTextRecDisp)
    EditText editTextRecDisp;
    @Bind(R.id.editTextTimeCOMA)
    EditText editTextTimeCOMA;
    @Bind(R.id.checkBoxAutoClear)
    CheckBox checkBoxAutoClear;
    @Bind(R.id.checkBoxAutoCOMA)
    CheckBox checkBoxAutoCOMA;
    @Bind(R.id.ButtonClear)
    Button ButtonClear;
    @Bind(R.id.ButtonSendCOMA)
    Button ButtonSendCOMA;
    @Bind(R.id.radioButtonTxt)
    RadioButton radioButtonTxt;
    @Bind(R.id.radioButtonHex)
    RadioButton radioButtonHex;
    @Bind(R.id.radioButtonTCP)
    RadioButton radioButtonTCP;
    @Bind(R.id.radioButtonUDP)
    RadioButton radioButtonUDP;
    @Bind(R.id.radioButtonDebug)
    RadioButton radioButtonDebug;

    int iRecLines = 0;//接收区行数
    private int delayTime = 500;//定时发送的间隔时间
    private String address = null;
    private boolean isHex = true;
    private boolean isDebug = true;
    private List<BluetoothDevice> allConnectDevice;
    boolean isCirculation = false;//是否自动发送
    boolean isTCP = false;//TCP协议发送
    boolean isAll = false;//全部协议发送
    private int tag = 0;

    @Override
    public void initData(Bundle savedInstanceState) {
        allConnectDevice = MyBleService.getInstance().getAllConnectDevice();
        Loge("connected device size=" + allConnectDevice.size());
        if (allConnectDevice.size() > 0) {
            address = allConnectDevice.get(0).getAddress();
        } else {
            CommonKit.showErrorShort(context, "请连接蓝牙");
        }
        initTitleBar();
        initBleBroadCast();
    }

    private void initTitleBar() {
        titlebar.getRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDeviceActivity.lauch(context, null, REQUESTCODE_ADD_DEBUG_DEVICE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_ADD_DEBUG_DEVICE:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        address = bundle.getString("address");
                        tag = bundle.getInt("tag");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setListener() {
        editTextCOMA.setOnEditorActionListener(new EditorActionEvent());
        editTextTimeCOMA.setOnEditorActionListener(new EditorActionEvent());
        editTextCOMA.setOnFocusChangeListener(new FocusChangeEvent());
        editTextTimeCOMA.setOnFocusChangeListener(new FocusChangeEvent());

        radioButtonTxt.setOnClickListener(new radioButtonClickEvent());
        radioButtonHex.setOnClickListener(new radioButtonClickEvent());
        ButtonClear.setOnClickListener(new ButtonClickEvent());
        ButtonSendCOMA.setOnClickListener(new ButtonClickEvent());
        radioButtonTCP.setOnCheckedChangeListener(new CheckBoxChangeEvent());
        radioButtonUDP.setOnCheckedChangeListener(new CheckBoxChangeEvent());
        radioButtonDebug.setOnCheckedChangeListener(new CheckBoxChangeEvent());
        checkBoxAutoCOMA.setOnCheckedChangeListener(new CheckBoxChangeEvent());

        editTextCOMA.setKeyListener(hexkeyListener);
    }

    /**
     * 初始化蓝牙广播
     */
    private void initBleBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(BLUTOOTH_OFF);//手机蓝牙关闭
        intentFilter.addAction(BLUTOOTH_ON);//手机蓝牙打开
        intentFilter.addAction(ACTION_ALL_DATA);//读取调试数据
        intentFilter.addAction(ACTION_SOCKET_OSPF);//读取蓝牙发送的透传指令
        intentFilter.addAction(ACTION_DEBUG_LOG);//调试接口的日志
        context.registerReceiver(dataUpdateRecevice, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(ACTION_NET_TCP_RECEIVE);
        intentFilter1.addAction(ACTION_LOG_TEST);
        context.registerReceiver(tcpClientReceiver, intentFilter1);
    }

    public void startSend() {
        Runnable sendRunnable = new Runnable() {
            @Override
            public void run() {
                if (getSendData() == null) {
                    checkBoxAutoCOMA.setChecked(false);
                    return;
                }
                if (address == null) return;
                while (MyBleService.getInstance().getConnectDevice(address) != null) {
                    try {
                        Thread.sleep(getDelayTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCirculation) {
                        sendData();
                    } else {
                        return;
                    }
                    RLog.e("循环发送");
                }
            }
        };
        new Thread(sendRunnable).start();
    }

    @Override
    public void onClick(View view) {

    }

    KeyListener hexkeyListener = new NumberKeyListener() {
        public int getInputType() {
            return InputType.TYPE_CLASS_TEXT;
        }

        @Override
        protected char[] getAcceptedChars() {
            return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'};
        }
    };

    //编辑框焦点转移事件
    class FocusChangeEvent implements EditText.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == editTextCOMA) {
            } else if (v == editTextTimeCOMA) {
                setDelayTime(editTextTimeCOMA);
            }
        }
    }

    //编辑框完成事件
    class EditorActionEvent implements EditText.OnEditorActionListener {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (v == editTextCOMA) {

            } else if (v == editTextTimeCOMA) {
                setDelayTime(editTextTimeCOMA);
            }
            return false;
        }
    }

    //----------------------------------------------------Txt、Hex模式选择
    class radioButtonClickEvent implements RadioButton.OnClickListener {
        public void onClick(View v) {
            if (v == radioButtonTxt) {
                isHex = false;
            } else if (v == radioButtonHex) {
                isHex = true;
            }
        }
    }

    //自动发送
    class CheckBoxChangeEvent implements CheckBox.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            RLog.e(buttonView.getId() + " isChecked=" + isChecked);
            if (buttonView == checkBoxAutoCOMA) {
                if (isChecked) {
                    isCirculation = true;
                    startSend();
                } else {
                    isCirculation = false;
                }
            } else if (buttonView == radioButtonTCP) {
                if (isChecked) {
                    isTCP = true;
                    editTextCOMA.setText("FB10001A9a75bca04593464d95b4266cc5e0bc2759c215ffFFFFFFFFFFFF00FE");
                } else {
                    isTCP = false;
                    editTextCOMA.setText("");
                }
            } else if (buttonView == radioButtonUDP) {
                if (isChecked) {
                    isAll = true;
                } else {
                    isAll = false;
                }
                editTextCOMA.setText("");
            } else if (buttonView == radioButtonDebug) {
                if (isChecked) {
                    isDebug = true;
                    editTextCOMA.setText("");
                } else {
                    isDebug = false;
                }
            }
        }
    }

    /**
     * 清除按钮、发送按钮
     */
    class ButtonClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            if (v == ButtonClear) {
                editTextRecDisp.setText("");
                iRecLines = 0;
                editTextLines.setText(String.valueOf(iRecLines));
            } else if (v == ButtonSendCOMA) {
                if (isTCP) {
                    if (getSendData() == null) return;
                    try {
                        if (context == null) {
                            RLog.e("context is null");
                            return;
                        } else if (MyBleService.getInstance() == null) {
                            RLog.e("MyBleService.getInstance() is null");
                            return;
                        }
                        MyBleService.getInstance().sendTCPData(context, getSendData(), null);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendData();
                }
            }
        }
    }

    private void sendData() {
        if (MyBleService.getInstance().getConnectDevice(address) == null) {
            CommonKit.showErrorShort(context, "设备未连接");
            return;
        }
        if (getSendData() == null) return;
        MyBleService.getInstance().sendData(address, Byte2HexUtil.hex2Bytes(getSendData()));
    }

    @NonNull
    private String getSendData() {
        String str = editTextCOMA.getText().toString().trim();
        if (TextUtils.isEmpty(str)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonKit.showErrorShort(context, "请输入正确的指令");
                }
            });
            return null;
        }
        return str;
    }

    //设置自动发送延时
    private void setDelayTime(TextView v) {
        String str = v.getText().toString().trim();
        if (TextUtils.isEmpty(str)) return;
        delayTime = Integer.parseInt(str);
    }

    private int getDelayTime() {
        return delayTime;
    }

    private void updateText(String str) {
        editTextRecDisp.append(str);
        iRecLines++;
        editTextLines.setText(String.valueOf(iRecLines));
        if ((iRecLines > 200) && (checkBoxAutoClear.isChecked())) {//达到200项自动清除
            editTextRecDisp.setText("");
            editTextLines.setText("0");
            iRecLines = 0;
        }
    }

    BroadcastReceiver dataUpdateRecevice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // String deviceMac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            //if (deviceMac == null) return;
            //if (!address.equals(deviceMac)) return;
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS:
                case BLE_CONN_SUCCESS_ALLCONNECTED:
                    MyBleService.getInstance().enableNotify(address);
                    disProgress();
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast3));
                    titlebar.getRightTv().setText("已连接");
                    break;
                case BLE_CONN_DIS://断开连接
                    checkBoxAutoCOMA.setChecked(false);
                    RLog.d("断开连接");
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast4));
                    titlebar.getRightTv().setText("连接设备");
                    break;
                case BLE_CONN_FAIL://连接失败
                    disProgress();
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast8));
                    break;
                case BLUTOOTH_OFF:
                    RLog.d("手机蓝牙断开");
                    CommonKit.showOkShort(context, "手机蓝牙关闭");
                    break;
                case BLUTOOTH_ON:
                    RLog.d("手机蓝牙开启");
                    CommonKit.showOkShort(context, "手机蓝牙开启");
                    break;
                case ACTION_ALL_DATA:
                    if (isAll) {
                        byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
                        if (isHex) {
                            updateText(String.format("%s\r\n", Byte2HexUtil.byte2Hex(b)));
                        } else {
                            updateText(String.format("%s\r\n", Byte2HexUtil.convertHexToString(Byte2HexUtil.byte2Hex(b))));
                        }
                    }
                    break;
                case ACTION_SOCKET_OSPF:
                    if (isTCP) {
                        byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
                        if (isHex) {
                            updateText(String.format("透传：%s\r\n", Byte2HexUtil.byte2Hex(b)));
                        } else {
                            updateText(String.format("透传：%s\r\n", Byte2HexUtil.convertHexToString(Byte2HexUtil.byte2Hex(b))));
                        }
                    }
                    break;

                case ACTION_DEBUG_LOG://调试接口的日志
                    if (isDebug) {
                        String log = intent.getStringExtra(BLECONSTANTS_DATA);
                        if (isHex) {
                            updateText(String.format("调试：%s\r\n", log));
                        } else {
                            updateText(String.format("调试：%s\r\n", Byte2HexUtil.convertHexToString(log)));
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    BroadcastReceiver tcpClientReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction) {
                case ACTION_NET_TCP_RECEIVE:
                    byte[] tcpData = intent.getByteArrayExtra("tcpClientReceiver");
                    if (tcpData.length > 0) {
                        updateText(String.format("服务器返回：%s\r\n", Byte2HexUtil.byte2Hex(tcpData)));
                    }
                    break;

                case ACTION_LOG_TEST:
                    String data = intent.getStringExtra(BLECONSTANTS_DATA);
                    updateText(String.format("发送给服务器：%s\r\n", data));
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCirculation = false;
        if (tag == 1) {
            MyBleService.getInstance().disConnectDevice(address);
        }
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, DebugBLEActivity.class, null, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_debug_ble;
    }
}
