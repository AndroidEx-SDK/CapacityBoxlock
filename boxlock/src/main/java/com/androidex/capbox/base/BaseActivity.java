package com.androidex.capbox.base;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidex.capbox.BuildConfig;
import com.androidex.capbox.MyApplication;
import com.androidex.capbox.R;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.cache.CacheManage;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.module.UserModel;
import com.androidex.capbox.ui.activity.LoginActivity;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.Dialog;
import com.androidex.capbox.utils.DialogUtils;
import com.androidex.capbox.utils.SystemUtil;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_HEART;
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
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ISACTIVEDisConnect;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_TEMP_OUT;

/**
 * @author liyp
 * @version 1.0.0
 * @qq 1028101430
 * @description Activity基类
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    private final static String TAG = "BaseActivity";
    protected Activity context;
    private android.app.Dialog dialog;
    private android.app.Dialog mLostAlarmDialog;//防丢弹框Dialog
    protected BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            getWindow().setStatusBarColor(loadColor(R.color.starsBar_blue));
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        }

        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            ButterKnife.bind(this);
        }
        setListener();
        initBleBroadCast();
        initData(savedInstanceState);
    }

    /**
     * 初始化蓝牙广播
     */
    private void initBleBroadCast() {
        Log.e("BaseActivity", "--注册蓝牙广播");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_LOCK_STARTS);
        intentFilter.addAction(ACTION_TEMP_UPDATE);
        intentFilter.addAction(BLE_CONN_RSSI_SUCCED);
        intentFilter.addAction(BLE_CONN_RSSI_FAIL);
        intentFilter.addAction(ACTION_HEART);
        intentFilter.addAction(BLUTOOTH_OFF);//手机蓝牙关闭
        intentFilter.addAction(BLUTOOTH_ON);//手机蓝牙关闭
        intentFilter.addAction(ACTION_LOCK_OPEN_SUCCED);
        intentFilter.addAction(ACTION_TEMP_OUT);//温度超范围
        registerReceiver(baseBroad, intentFilter);
    }

    BroadcastReceiver baseBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceMac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            switch (intent.getAction()){
                case BLE_CONN_DIS://蓝牙异常断开
                    boolean isActiveDisConnect = intent.getBooleanExtra(BLECONSTANTS_ISACTIVEDisConnect, false);
                    if (!isActiveDisConnect){
                        setLostAlarm("Box"+deviceMac.substring(deviceMac.length()-2));//蓝牙异常断开弹窗
                    }
                    break;
                case ACTION_TEMP_OUT://温度超范围
                    showTempOutAlarmDialog("Box"+deviceMac.substring(deviceMac.length()-2));
                    break;
            }
        }
    };

    public abstract int getLayoutId();

    public abstract void initData(Bundle savedInstanceState);

    public abstract void setListener();

    protected void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    protected void setGone(View view) {
        view.setVisibility(View.GONE);
    }

    protected void setInVisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    protected void setClick(View view) {
        view.setOnClickListener(this);
    }

    public int loadColor(int resId) {
        return getResources().getColor(resId);
    }

    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    protected void registerEventBusSticky() {
        EventBus.getDefault().registerSticky(this);
    }

    protected void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    protected void post(Object event) {
        EventBus.getDefault().post(event);
    }

    protected void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }

    public String getToken() {
        String token = SharedPreTool.getInstance(context).getStringData(SharedPreTool.TOKEN, null);
        if (token == null) {
            CommonKit.showErrorShort(context, "账号异常");
            LoginActivity.lauch(context);
            postSticky(new Event.UserLoginEvent());
            return "";
        }
        return token;
    }

    public void setToken(String token) {
        SharedPreTool.getInstance(context).setStringData(SharedPreTool.TOKEN, token);
    }

    public String getUserName() {
        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        if (username == null) {
            CommonKit.showErrorShort(context, "账号异常");
            LoginActivity.lauch(context);
            postSticky(new Event.UserLoginEvent());
            return "";
        }
        return username;
    }

    public void setUsername(String username) {
        SharedPreTool.getInstance(context).setStringData(SharedPreTool.PHONE, username);
    }

    /**
     * 显示加载对话框
     *
     * @param msg
     */
    protected void showSpinnerDlg(String msg) {
        showSpinnerDlg(msg, true);
    }

    /**
     * 当flag为true时，显示加载对话框
     *
     * @param flag
     * @param msg
     */
    protected void showSpinnerDlg(boolean flag, String msg) {
        if (flag) {
            showSpinnerDlg(msg, true);
        }
    }

    /**
     * 设置防丢报警方式
     */
    protected void setLostAlarm(String deviceName) {
        showLostAlarmDialog(deviceName);
        SystemUtil.startVibrate(context, true);//true:循环震动，false:震动一次
    }

    /**
     * 停止显示报警dialog
     */
    protected void closeLostAlarm() {
        SystemUtil.stopVibrate(context);//停止震动
        //SystemUtil.stopPlayMediaPlayer();//停止铃声
        SystemUtil.stopPlayRaw();//停止铃声zx
        if (mLostAlarmDialog != null && mLostAlarmDialog.isShowing()) {
            mLostAlarmDialog.dismiss();
        }
    }

    /**
     * 防丢报警Dialog显示
     */
    private void showLostAlarmDialog(String deviceName) {
        //就一个确定按钮
        mLostAlarmDialog = Dialog.showRadioDialog(context, deviceName
                + getResources().getString(R.string.itemfragment_dialog_lost), new Dialog.DialogClickListener() {
            @Override
            public void confirm() {
                closeLostAlarm();
            }

            @Override
            public void cancel() {

            }
        });
    }

    /**
     * 温度超范围报警Dialog显示
     */
    private void showTempOutAlarmDialog(String deviceName) {
        //就一个确定按钮
        mLostAlarmDialog = Dialog.showRadioDialog(context, deviceName
                + getResources().getString(R.string.itemfragment_dialog_temp_out), new Dialog.DialogClickListener() {
            @Override
            public void confirm() {
                closeLostAlarm();
            }

            @Override
            public void cancel() {

            }
        });
    }

    /**
     * 显示等待框
     *
     * @param msg
     */
    protected void showProgress(String msg) {
        if (dialog == null) {
            dialog = DialogUtils.createDialog(context, msg);
        }
        TextView tv = (TextView) dialog.findViewById(R.id.msg);
        tv.setText(msg);
        if (dialog.isShowing()) {
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    protected void disProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 显示加载对话框
     *
     * @param msg
     * @param isCancel
     */
    protected void showSpinnerDlg(String msg, boolean isCancel) {
        if (dialog == null) {
            dialog = DialogUtils.createDialog(context, msg);
        }
        TextView tv = (TextView) dialog.findViewById(R.id.msg);
        tv.setText(msg);
        if (!dialog.isShowing()) {
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    /**
     * 关闭加载对话框
     */
    protected void dismissSpinnerDlg() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 关闭加载对话框
     *
     * @param flag
     */
    protected void dismissSpinnerDlg(boolean flag) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 登录后完成操作
     *
     * @param callback
     */
    protected void doAfterLogin(UserBaseActivity.CallBackAction callback) {
        if (getLoginedUser() == null) {
            LoginActivity.lauch(context, callback);
        } else {
            if (callback != null) {
                callback.action();
            }
        }
    }

    /**
     * 获取当前登录的用户
     *
     * @return
     */
    protected UserModel getLoginedUser() {
        return CacheManage.getFastCache().get(Constants.PARAM.CACHE_KEY_CUR_LOGIN_USER, UserModel.class);
    }

    protected static MyApplication getApp() {
        return MyApplication.getInstance();
    }


    @Override
    protected void onResume() {
        super.onResume();
        closeLostAlarm();
        /**
         * 二、3、打开蓝牙
         * 获取到BluetoothAdapter之后，还需要判断蓝牙是否打开。
         * 如果没打开，需要让用户打开蓝牙：
         */
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
        }

        if (!BuildConfig.DEBUG) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!BuildConfig.DEBUG) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(baseBroad);
    }
}
