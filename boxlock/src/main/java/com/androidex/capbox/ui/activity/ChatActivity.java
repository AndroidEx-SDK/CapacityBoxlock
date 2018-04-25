package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.module.ActionItem;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.view.TitlePopup;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CalendarUtil;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.RLog;

import butterknife.Bind;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_ITEM_POSITION;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_NAME;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_UUID;
import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;
import static com.androidex.capbox.utils.Constants.EXTRA_PAGER_SIGN;

public class ChatActivity extends BaseActivity {
    @Bind(R.id.titlebar)
    SecondTitleBar titlebar;
    @Bind(R.id.lv_msgList)
    ListView lv_msgList;
    @Bind(R.id.ib_send)
    ImageButton ib_send;
    @Bind(R.id.et_msg)
    EditText et_msg;

    private String name;
    private String address;
    private TitlePopup titlePopup;
    private String uuid;
    private int position;

    @Override
    public void initData(Bundle savedInstanceState) {
        name = getIntent().getStringExtra(EXTRA_BOX_NAME);
        uuid = getIntent().getStringExtra(EXTRA_BOX_UUID);
        address = getIntent().getStringExtra(EXTRA_ITEM_ADDRESS);
        position = getIntent().getIntExtra(EXTRA_ITEM_POSITION, -1);
        initBroadCast();
        initTitle();
        initTitlePop();
    }

    private void initTitle() {
        if (MyBleService.getInstance().getConnectDevice(address) != null) {
            titlebar.setTitle(CalendarUtil.getName(name, address) + "(已连接)");
        } else {
            titlebar.setTitle(CalendarUtil.getName(name, address) + "(断开)");
        }
    }

    private void initTitlePop() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(context, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 给标题栏弹窗添加子类
        if (MyBleService.getInstance().getConnectDevice(address) == null) {
            titlePopup.addAction(new ActionItem(context, "连接", R.drawable.connectlist));
        } else {
            titlePopup.addAction(new ActionItem(context, "断开", R.drawable.connectlist));
        }
        titlePopup.addAction(new ActionItem(context, "配置", R.drawable.setting));
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                switch (position) {
                    case 0:
                        if (MyBleService.getInstance().getConnectDevice(address) == null) {
                            MyBleService.getInstance().connectionDevice(context, address);
                            showProgress("开始连接...");
                        } else {
                            RLog.d("已经连接");
                            MyBleService.getInstance().disConnectDevice(address);
                        }
                        break;
                    case 1:
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_BOX_NAME, name);
                        bundle.putString(EXTRA_BOX_UUID, uuid);
                        bundle.putString(EXTRA_ITEM_ADDRESS, address);
                        bundle.putInt(EXTRA_PAGER_SIGN, 0);//0表示从设备列表跳转过去的1表示从监控页跳转
                        bundle.putInt(EXTRA_ITEM_POSITION, position);//position选择的是第几个设备
                        BoxDetailActivity.lauch(context, bundle);
                        break;
                    default:
                        break;
                }
            }
        });
        titlebar.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titlePopup.show(view);
            }
        });
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        DataBroadcast dataBroadcast = new DataBroadcast();
        registerReceiver(dataBroadcast, intentFilter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

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
                default:
                    break;
            }
            disProgress();
            initTitle();
            initTitlePop();
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
