package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.androidex.boxlib.utils.Byte2HexUtil;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.adapter.ConnectDeviceListAdapter;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import java.util.List;

import butterknife.Bind;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;

/**
 * 箱体列表
 *
 * @author liyp
 * @editTime 2017/9/27
 */

public class ConnectDeviceListActivity extends BaseActivity {
    private static final String TAG = "ConnectDeviceList";
    @Bind(R.id.device_list_connected)
    ListView listconnected;
    @Bind(R.id.swipe_searchDevices)
    SwipeRefreshLayout swipe_searchDevices;

    private BleBroadCast bleBroadCast;
    private ConnectDeviceListAdapter boxListAdapter;
    private List<BluetoothDevice> allConnectDevice;

    @Override
    public void initData(Bundle savedInstanceState) {
        allConnectDevice = MyBleService.get().getAllConnectDevice();
        Loge("connected device size=" + allConnectDevice.size());
        initTitleBar();
        iniRefreshView();
        initListView();
        initBle();//蓝牙连接
    }

    private void initTitleBar() {
    }

    /**
     * 初始化两个列表的ListView
     */
    private void initListView() {
        //已绑定设备的适配器
        boxListAdapter = new ConnectDeviceListAdapter(context, allConnectDevice, new ConnectDeviceListAdapter.IClick() {

            @Override
            public void listViewItemClick(int position, View v) {
                switch (v.getId()) {
                    case R.id.rl_normal:

                        break;
                    default:
                        break;
                }
            }
        });
        //设置已绑定列表的适配器
        listconnected.setAdapter(boxListAdapter);
    }

    private void iniRefreshView() {
        swipe_searchDevices.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 模拟刷新完成
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        swipe_searchDevices.setRefreshing(false);
                        getConnectDeviceList();
                    }
                }, 1000);
            }
        });
    }

    /**
     * 蓝牙初始化
     */
    private void initBle() {
        initBleReceiver();//初始化蓝牙广播
        // 初始化蓝牙adapter
        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();//打开蓝牙
            Logd("打开蓝牙");
        }
    }

    /**
     * 注册设备连接广播
     */
    private void initBleReceiver() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(Constants.BLE.ACTION_UUID);//获取UUID
        bleBroadCast = new BleBroadCast();
        context.registerReceiver(bleBroadCast, intentFilter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 接收广播
     */
    public class BleBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://连接成功
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    getConnectDeviceList();
                    break;

                case BLE_CONN_DIS://断开连接
                    getConnectDeviceList();
                    break;

                case Constants.BLE.ACTION_UUID:
                    byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
                    if (b.length >= 20) {
                        showProgress("开始绑定...");
                        byte[] b_uuid = new byte[b.length - 4];
                        System.arraycopy(b, 4, b_uuid, 0, b.length - 4);
                        String uuid = Byte2HexUtil.byte2Hex(b_uuid).trim();
                        Loge("uuid=" + uuid);
                        if (uuid != null) {
                            showProgress("正在绑定...");
                        }
                    }
                    break;

                case Constants.BLE.BLE_CONN_FAIL:
                    disProgress();
                    CommonKit.showErrorLong(context, "连接失败");
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 获取数据
     */
    private void getConnectDeviceList() {
        allConnectDevice = MyBleService.get().getAllConnectDevice();
        boxListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bleBroadCast != null) {
            context.unregisterReceiver(bleBroadCast);
        }
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, ConnectDeviceListActivity.class, null, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_connectdevicelist;
    }
}
