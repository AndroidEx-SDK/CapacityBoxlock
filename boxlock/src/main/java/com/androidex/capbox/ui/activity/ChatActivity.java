package com.androidex.capbox.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.capbox.MyApplication;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.db.ChatRecord;
import com.androidex.capbox.db.ChatRecordDao;
import com.androidex.capbox.db.DaoSession;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.adapter.ChatAdapter;
import com.androidex.capbox.ui.widget.ChatPopWindow;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CalendarUtil;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.RLog;
import com.androidex.capbox.utils.SoftHideKeyBoardUtil;
import com.androidex.capbox.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BOX_POLICE_CHANGE;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BOX_STARTS_CHANGE;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_END_TAST;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_LOCK_OPEN_SUCCED;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_LOCK_STARTS;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_TEMP_UPDATE;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_RSSI_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_RSSI_SUCCED;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_OFF;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_ON;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_ITEM_POSITION;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_NAME;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_UUID;
import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;
import static com.androidex.capbox.utils.Constants.EXTRA_PAGER_SIGN;
import static com.androidex.capbox.utils.Constants.VISE_COMMAND_TYPE_TEXT;

public class ChatActivity extends BaseActivity {
    @Bind(R.id.titlebar)
    SecondTitleBar titlebar;
    @Bind(R.id.lv_msgList)
    ListView lv_msgList;
    @Bind(R.id.ib_send)
    ImageButton ib_send;
    @Bind(R.id.et_msg)
    EditText et_msg;
    @Bind(R.id.rc_audio_input_toggle)
    Button rc_audio_input_toggle;
    @Bind(R.id.ib_voice)
    ImageView ib_voice;

    private String name;
    private String address;
    private String uuid;
    private int position;
    private List<ChatRecord> mChatInfoList = new ArrayList<>();
    private ChatAdapter mChatAdapter;
    private DataBroadcast dataBroadcast;
    boolean isKeyBoardActive = false;//输入法状态
    private ChatRecordDao chatRecordDao;

    @Override
    public void initData(Bundle savedInstanceState) {
        SoftHideKeyBoardUtil.assistActivity(this);
        SystemUtil.setImmerseLayout(context, titlebar);
        name = getIntent().getStringExtra(EXTRA_BOX_NAME);
        uuid = getIntent().getStringExtra(EXTRA_BOX_UUID);
        address = getIntent().getStringExtra(EXTRA_ITEM_ADDRESS);
        position = getIntent().getIntExtra(EXTRA_ITEM_POSITION, -1);
        initBroadCast();
        initTitle();
        initTitlePop();
        initView();
        mChatAdapter = new ChatAdapter(context);
        lv_msgList.setAdapter(mChatAdapter);
        initUserInfo();
        initDB();
        updataChatRecord();
    }

    private void updataChatRecord() {
        mChatInfoList.clear();
        List<ChatRecord> chatRecordList = getChatRecord();
        for (ChatRecord chatRecord : chatRecordList) {
            RLog.d("读取到的聊天数据 = " + chatRecord.toString());
            mChatInfoList.add(chatRecord);
            mChatAdapter.setListAll(mChatInfoList);
        }
    }

    private void initView() {
        hideVoiceInputToggle();
        showInputKeyBoard();
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        et_msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //隐藏系统软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_msg.getWindowToken(), 0);
                }
            }
        });
    }

    /**
     * 初始化数据库
     */
    public void initDB() {
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        chatRecordDao = daoSession.getChatRecordDao();
    }

    /**
     * 初始化Title
     */
    private void initTitle() {
        if (MyBleService.getInstance().getConnectDevice(address) != null) {
            titlebar.setTitle(CalendarUtil.getName(name, address) + "(已连接)");
        } else {
            titlebar.setTitle(CalendarUtil.getName(name, address) + "(断开)");
        }
    }

    /**
     * 初始化右上角弹窗
     */
    private void initTitlePop() {
        titlebar.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatPopWindow chatPopWindow = new ChatPopWindow(context, address, new ChatPopWindow.CallBack() {

                    @Override
                    public void callBack() {
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_BOX_NAME, name);
                        bundle.putString(EXTRA_BOX_UUID, uuid);
                        bundle.putString(EXTRA_ITEM_ADDRESS, address);
                        bundle.putInt(EXTRA_PAGER_SIGN, 0);//0表示从设备列表跳转过去的1表示从监控页跳转
                        bundle.putInt(EXTRA_ITEM_POSITION, position);//position选择的是第几个设备
                        BoxDetailActivity.lauch(context, bundle);
                    }
                });
                chatPopWindow.showPopupWindow(titlebar.getRightIv());
            }
        });
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_LOCK_STARTS);//锁状态
        intentFilter.addAction(ACTION_TEMP_UPDATE);//更新温度
        intentFilter.addAction(BLE_CONN_RSSI_SUCCED);
        intentFilter.addAction(BLE_CONN_RSSI_FAIL);
        intentFilter.addAction(ACTION_END_TAST);//结束携行押运
        intentFilter.addAction(ACTION_LOCK_OPEN_SUCCED);//开锁成功
        intentFilter.addAction(BLUTOOTH_OFF);//手机蓝牙关闭
        intentFilter.addAction(BLUTOOTH_ON);//手机蓝牙打开
        intentFilter.addAction(ACTION_BOX_STARTS_CHANGE);//箱体状态变更报告
        intentFilter.addAction(ACTION_BOX_POLICE_CHANGE);//箱体报警信息变更报告
        dataBroadcast = new DataBroadcast();
        registerReceiver(dataBroadcast, intentFilter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }

    @OnClick({
            R.id.ib_send,
            R.id.ib_voice,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ib_send:
                if (MyBleService.getInstance().getConnectDevice(address) == null) {
                    CommonKit.showErrorShort(context, "请先连接设备");
                    return;
                }
                if (TextUtils.isEmpty(et_msg.getText().toString())) {
                    CommonKit.showErrorShort(context, "发送消息不能为空");
                    return;
                }
                sendMessage();
                break;
            case R.id.ib_voice:
                if (isKeyBoardActive) {
                    hideVoiceInputToggle();
                    showInputKeyBoard();
                } else {
                    showVoiceInputToggle();
                    hideInputKeyBoard();
                }
                break;
        }
    }

    private void hideVoiceInputToggle() {
        et_msg.setVisibility(View.VISIBLE);
        this.ib_voice.setImageResource(R.drawable.rc_voice_toggle_selector);
        this.rc_audio_input_toggle.setVisibility(View.GONE);
        et_msg.setSelected(true);
    }

    private void showVoiceInputToggle() {
        ib_voice.setImageResource(R.drawable.rc_keyboard_selector);
        rc_audio_input_toggle.setVisibility(View.VISIBLE);
        et_msg.setVisibility(View.GONE);
        et_msg.setSelected(false);
    }

    private void hideInputKeyBoard() {
        @SuppressLint("WrongConstant")
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
        imm.hideSoftInputFromWindow(this.et_msg.getWindowToken(), 0);
        this.et_msg.clearFocus();
        this.isKeyBoardActive = true;
    }

    private void showInputKeyBoard() {
        this.et_msg.requestFocus();
        @SuppressLint("WrongConstant")
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
        imm.showSoftInput(this.et_msg, 0);
        this.isKeyBoardActive = false;
    }

    /**
     * 发送数据
     */
    public void sendMessage() {
        Date date = new Date();
        String content = et_msg.getText().toString();
        et_msg.setText("");
        insertSendDB(content, date.getTime());
    }

    /**
     * 插入发送的信息
     *
     * @param content
     * @param time
     */
    private void insertSendDB(String content, Long time) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setAddress(address)
                .setIsRead("1")
                .setIsSend("0")
                .setDeleteChat("0")
                .setMsgContent(content)
                .setNickName(name)
                .setMsgType(VISE_COMMAND_TYPE_TEXT)
                .setTime(time)
                .setUuid(uuid);
        RLog.d("msg 发送的数据 = " + chatRecord.toString());
        mChatInfoList.add(chatRecord);
        mChatAdapter.setListAll(mChatInfoList);
        sendData(content);
        chatRecordDao.insert(chatRecord);
    }

    private void insertReceiveData(String content, Long time) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setAddress(address)
                .setIsRead("1")
                .setIsSend("1")
                .setDeleteChat("0")
                .setMsgContent(content)
                .setNickName(name)
                .setMsgType(VISE_COMMAND_TYPE_TEXT)
                .setTime(time)
                .setUuid(uuid);
        RLog.d("msg 接收到的数据 = " + chatRecord.toString());
        mChatInfoList.add(chatRecord);
        mChatAdapter.setListAll(mChatInfoList);
        chatRecordDao.insert(chatRecord);
    }

    /**
     * 解析用户输入的数据
     *
     * @param data
     */
    public void sendData(String data) {
        if (data.contains("开锁") && !data.contains("不开锁")) {
            openLock();
        } else {

        }
    }

    /**
     * 收到消息
     *
     * @param data
     */
    public void receiveData(String data) {
        Date date = new Date();
        insertReceiveData(data, date.getTime());
    }

    private List<ChatRecord> getChatRecord() {
        return chatRecordDao.queryBuilder().where(ChatRecordDao.Properties.Address.eq(address), ChatRecordDao.Properties.DeleteChat.eq("0"))
                .orderAsc(ChatRecordDao.Properties.Time).list();
    }

    /**
     * 开锁
     */
    private void openLock() {
        if (MyBleService.getInstance().getConnectDevice(address) != null) {
            MyBleService.getInstance().openLock(address);
        } else {
            CommonKit.showErrorShort(context, getResources().getString(R.string.setting_tv_ble_disconnect));
        }
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
                    MyBleService.getInstance().enableNotify(address);
                    CommonKit.showOkShort(context, "连接成功");
                    break;
                case BLE_CONN_DIS:
                    CommonKit.showOkShort(context, "蓝牙断开");
                    break;
                case BLE_CONN_FAIL:
                    MyBleService.getInstance().connectionDevice(context, address);
                    CommonKit.showErrorShort(context, "连接失败，已开始重连");
                    break;
                case ACTION_LOCK_STARTS://锁状态FB 32 00 01 00 00 FE
                    if (b[4] == (byte) 0x01) {
                        receiveData("锁已打开");
                    } else {
                        receiveData("锁已关闭");
                    }
                    break;
                case ACTION_END_TAST://结束携行押运
                    switch (b[2]) {
                        case (byte) 0x00://成功
                            endTask();
                            break;
                        case (byte) 0x01://失败
                            CommonKit.showErrorShort(context, "结束失败");
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_BOX_STARTS_CHANGE://箱体状态变更报告
                    switch (b[4]) {
                        case (byte) 0x01://空闲
                            receiveData("更改空闲状态");
                            break;
                        case (byte) 0x02://配置
                            receiveData("更改为配置状态");
                            break;
                        case (byte) 0x03://携行
                            receiveData("更改为携行状态");
                            break;
                        case (byte) 0x04://开箱
                            receiveData("已开箱");
                            break;
                        case (byte) 0x05://关箱
                            receiveData("已关箱");
                            break;
                        case (byte) 0x06://APP开锁
                            receiveData("APP开锁");
                            break;
                        case (byte) 0x07://APP关锁
                            receiveData("APP关锁");
                            break;
                        case (byte) 0x08://指纹开锁
                            receiveData("指纹开锁");
                            break;
                        case (byte) 0x09://指纹关锁
                            receiveData("指纹关锁");
                            break;
                        case (byte) 0x0a://进入静默
                            receiveData("APP关锁");
                            break;
                        case (byte) 0x0b://退出静默
                            receiveData("退出静默");
                            break;
                        case (byte) 0x0c://开始充电
                            receiveData("开始充电");
                            break;
                        case (byte) 0x0d://充电完成
                            receiveData("充电完成");
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_BOX_POLICE_CHANGE://箱体报警信息变更报告
                    switch (b[4]) {
                        case (byte) 0x01://电量低
                            receiveData("电量低");
                            break;
                        case (byte) 0x02://超距（包括脱距、较近）
                            receiveData("超距报警");
                            break;
                        case (byte) 0x03://温度超范围
                            receiveData("温度超范围报警");
                            break;
                        case (byte) 0x04://湿度超范围
                            receiveData("湿度超范围报警");
                            break;
                        case (byte) 0x05://破拆
                            receiveData("破拆报警");
                            break;
                        case (byte) 0x06://通讯破解
                            receiveData("通讯破解报警");
                            break;
                        case (byte) 0x07://锁异常
                            receiveData("锁异常报警");
                            break;
                        case (byte) 0x08://异动
                            receiveData("异动报警");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            disProgress();
            initTitle();
            initTitlePop();
        }
    }

    /**
     * 结束携行
     */
    private void endTask() {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.endTask(getToken(), getUserName(), uuid, new ResultCallBack<BaseModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CommonKit.showOkShort(context, "结束成功");
                            setDeviceCaryyStarts(false);
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            } else {
                                CommonKit.showOkShort(context, "结束失败");
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                CommonKit.showErrorShort(context, "网络异常");
            }
        });
    }

    /**
     * 设置是否携行状态
     *
     * @param isflag
     */
    private void setDeviceCaryyStarts(boolean isflag) {
        ServiceBean obj = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        if (obj != null) {
            obj.setStartCarry(isflag);
            SharedPreTool.getInstance(context).saveObj(obj, address);
        } else {
            ServiceBean device = MyBleService.getInstance().getConnectDevice(address);
            if (device != null) {
                device.setStartCarry(isflag);
                SharedPreTool.getInstance(context).saveObj(device, address);
            }
        }
        Object obj1 = SharedPreTool.getInstance(context).getObj(ServiceBean.class, address);
        RLog.e("存储携行状态" + obj1.toString());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 点击输入框以外区域时隐藏输入法
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataBroadcast != null) {
            unregisterReceiver(dataBroadcast);
        }
    }

    public static void lauch(Activity activity, Bundle bundle) {
        CommonKit.startActivity(activity, ChatActivity.class, bundle, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }
}
