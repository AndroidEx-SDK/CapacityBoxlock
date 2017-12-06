package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.adapter.BLEDeviceListAdapter;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.DialogUtils;

import java.util.ArrayList;

import static com.androidex.capbox.ui.fragment.LockFragment.boxName;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_ADD_DEVICE;

public class SearchDeviceActivity extends ListActivity implements OnClickListener {

    private final static String TAG = SearchDeviceActivity.class.getSimpleName();

    // 返回，搜索，停止
    private Button back, search;

    /**
     * 搜索BLE终端
     */
    private BLEDeviceListAdapter mDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Runnable mRunnable;
    private static final int REQUEST_ENABLE_BT = 1;// 用于蓝牙setResult
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private int DURATION = 1000 * 2;// 动画持续时间
    private Animation animation;//动画
    private android.app.Dialog dialog;
    private ArrayList<String> list_devicemac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdevice);
        list_devicemac = getIntent().getStringArrayListExtra("list_devicemac");//获取存储设备mac的集合
        if (list_devicemac == null) {
            list_devicemac = new ArrayList<>();
        }
        dialog = DialogUtils.createDialog(this, getResources().getString(R.string.device_connect));
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                scanLeDevice(false);
            }
        };

        /**
         * 一、判断软件权限及feature：
         和经典蓝牙一样，应用使用蓝牙，需要声明BLUETOOTH权限，
         如果需要扫描设备或者操作蓝牙设置，则还需要BLUETOOTH_ADMIN权限：
         <uses-permission android:name="android.permission.BLUETOOTH"/>
         <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>

         除了蓝牙权限外，如果需要BLE feature则还需要声明uses-feature：
         <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>

         按时required为true时，则应用只能在支持BLE的Android设备上安装运行；required为false时，
         Android设备均可正常安装运行，需要在代码运行时判断设备是否支持BLE feature：
         * */
        // Use this check to determine whether BLE is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        /**
         * 二、打开蓝牙的步骤：
         * 二、1、获取BluetoothAdapter
         * BluetoothAdapter是Android系统中所有蓝牙操作都需要的，它对应本地Android设备的蓝牙模块，
         * 在整个系统中BluetoothAdapter是单例的。当你获取到它的示例之后，就能进行相关的蓝牙操作了。
         * */
        // Initializes a Bluetooth adapter. For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        //注：这里通过getSystemService获取BluetoothManager，再通过BluetoothManager获取BluetoothAdapter。
        //BluetoothManager在Android4.3以上支持(API level 18)。
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        /**
         * 二、2、判断是否支持蓝牙
         *
         */
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        back = (Button) findViewById(R.id.device_btn_back);
        back.setOnClickListener(this);
        search = (Button) findViewById(R.id.device_btn_search);
        search.setOnClickListener(this);
        animation = AnimationUtils.loadAnimation(this, R.anim.scan_anim);

        /** 设置旋转动画 */
        animation.setDuration(DURATION);// 设置动画持续时间
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount((int) ((SCAN_PERIOD / DURATION) - 1));// 设置重复次数
        animation.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
    }


    /**
     * 重新onResume，更新搜索蓝牙设备
     */
    @Override
    protected void onResume() {
        super.onResume();

        /**
         * 二、3、打开蓝牙
         * 获取到BluetoothAdapter之后，还需要判断蓝牙是否打开。
         * 如果没打开，需要让用户打开蓝牙：
         */
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mDeviceListAdapter = new BLEDeviceListAdapter(this, new BLEDeviceListAdapter.IClick() {

            @Override
            public void listViewItemClick(int position, View v) {
                switch (v.getId()) {
                    case R.id.tv_connect:
                        Log.d(TAG, "开始扫描蓝牙");
                        scanLeDevice(true);//开始扫描
                        break;
                    default:

                        break;
                }
            }
        });
        setListAdapter(mDeviceListAdapter);
        scanLeDevice(true);
        search.startAnimation(animation);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            CommonKit.finishActivity(this);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        search.clearAnimation();
        search.setEnabled(false);
        mDeviceListAdapter.clear();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
        if (device == null) {
            return;
        }
        if (list_devicemac.size() > 0) {
            for (int i = 0; i < list_devicemac.size(); i++) {
                if (list_devicemac.get(i).equals(device.getAddress())) {
                    CommonKit.showErrorShort(this, "该设备已添加过");
                    continue;
                } else {
                    list_devicemac.add(device.getAddress());
                }
            }
        } else {
            list_devicemac.add(device.getAddress());
        }
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
            search.clearAnimation();
            search.setEnabled(false);
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("list_devicemac", list_devicemac);
        setResult(Activity.RESULT_OK, intent);
        CommonKit.finishActivity(SearchDeviceActivity.this);//结束之后会将结果传回BoxActivity
    }

    /**
     * 三、搜索BLE设备：
     * <p>
     * 通过调用BluetoothAdapter的startLeScan()搜索BLE设备。调用此方法时需要传入 BluetoothAdapter.LeScanCallback参数。
     * 因此你需要实现 BluetoothAdapter.LeScanCallback接口，BLE设备的搜索结果将通过这个callback返回。
     * <p>
     * 由于搜索需要尽量减少功耗，因此在实际使用时需要注意：
     * <p>
     * 1、当找到对应的设备后，立即停止扫描；
     * 2、不要循环搜索设备，为每次搜索设置适合的时间限制。避免设备不在可用范围的时候持续不停扫描，消耗电量。
     * <p>
     * 如果你只需要搜索指定UUID的外设，你可以调用 startLeScan(UUID[], BluetoothAdapter.LeScanCallback)方法。
     * 其中UUID数组指定你的应用程序所支持的GATT Services的UUID。
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(mRunnable, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //			mBluetoothAdapter.startLeScan(BTTempBLEService.uuids, mLeScanCallback);
        } else {
            mScanning = false;
            mHandler.removeCallbacks(mRunnable);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * BluetoothAdapter.LeScanCallback接口，BLE设备的搜索结果将通过这个callback返回。
     * Device scan callback.蓝牙搜索回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean flag = true;
                    if (flag) {
                        if (device.getName() != null) {
                            if (device.getName().contains(boxName)) {
                                mDeviceListAdapter.addDevice(device);
                            }
                        }
                    }
                    mDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_btn_back:
                scanLeDevice(false);
                search.clearAnimation();
                search.setEnabled(false);
                finish();
                break;
            case R.id.device_btn_search:
                scanLeDevice(false);
                search.startAnimation(animation);
                mDeviceListAdapter.clear();
                mDeviceListAdapter.notifyDataSetChanged();
                scanLeDevice(true);
                break;
            default:
                break;
        }

    }

    private void closeDialog() {
        dialog.dismiss();
    }

    /**
     * add a keylistener for progress dialog
     */
    private OnKeyListener onKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//				closeDialog(); 
//				mHandler.removeCallbacks(mRunnable_conn);
            }
            return false;
        }
    };

    /**
     * 六、连接GATT Server：
     * <p>
     * 两个设备通过BLE通信，首先需要建立GATT连接。这里我们讲的是Android设备作为client端，连接GATT Server。
     * 连接GATT Server，你需要调用BluetoothDevice的connectGatt()方法。
     * 此函数带三个参数：Context、autoConnect(boolean)和BluetoothGattCallback对象。调用示例：
     * mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
     * 函数成功，返回BluetoothGatt对象，它是GATT profile的封装。
     * 通过这个对象，我们就能进行GATT Client端的相关操作。BluetoothGattCallback用于传递一些连接状态及结果。
     * BluetoothGatt常规用到的几个操作示例:
     * connect() ：连接远程设备。
     * discoverServices() : 搜索连接设备所支持的service。
     * disconnect()：断开与远程设备的GATT连接。
     * close()：关闭GATT Client端。
     * readCharacteristic(characteristic) ：读取指定的characteristic。
     * setCharacteristicNotification(characteristic, enabled) ：设置当指定characteristic值变化时，发出通知。
     * getServices() ：获取远程设备所支持的services。
     * 等等。
     * 注：
     * 1、某些函数调用之间存在先后关系。例如首先需要connect上才能discoverServices。
     * 2、一些函数调用是异步的，需要得到的值不会立即返回，而会在BluetoothGattCallback的回调函数中返回。
     * 例如discoverServices与onServicesDiscovered回调，readCharacteristic与onCharacteristicRead回调，
     * setCharacteristicNotification与onCharacteristicChanged回调等。
     */

    public static void lauch(Activity activity, Bundle bundle) {
        CommonKit.startActivityForResult(activity, SearchDeviceActivity.class, bundle, REQUESTCODE_ADD_DEVICE);
    }

}
