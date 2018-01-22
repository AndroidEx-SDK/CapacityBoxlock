package com.androidex.capbox.base;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.cache.CacheManage;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.module.UserModel;
import com.androidex.capbox.ui.activity.LoginActivity;
import com.androidex.capbox.utils.BuildConfig;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
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
import static com.androidex.capbox.utils.Constants.BASE.ACTION_TEMP_OUT;

/**
 * @author liyp
 * @version 1.0.0
 * @description Fragment基类
 * @createTime 2015/9/19
 * @editTime
 * @editor
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "BaseFragment";
    protected View rootView;
    protected LayoutInflater layoutInflater;
    protected Activity context;
    private android.app.Dialog dialog;
    private android.app.Dialog mLostAlarmDialog;//防丢弹框Dialog
    protected BluetoothAdapter mBtAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
        if (rootView == null) {
            context = getActivity();
            rootView = inflater.inflate(getLayoutId(), null);
            ButterKnife.bind(this, rootView);
        } else {
            ViewGroup viewGroup = (ViewGroup) rootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(rootView);
            }
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            getActivity().getWindow().setStatusBarColor(getColor(R.color.starsBar_blue));
            ViewGroup mContentView = (ViewGroup) getActivity().findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        }
        setListener();
        initBleBroadCast();
        initData();
    }

    /**
     * 初始化蓝牙广播
     */
    private void initBleBroadCast() {
        Log.e("BaseFragment", "--注册蓝牙广播");
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
        context.registerReceiver(baseBroad, intentFilter);
    }

    BroadcastReceiver baseBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceMac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            switch (intent.getAction()) {
                case BLE_CONN_DIS://蓝牙异常断开
                    // Log.e(TAG, "蓝牙异常断开");
                    // setLostAlarm("Box" + deviceMac.substring(deviceMac.length() - 2));//蓝牙异常断开弹窗
                    break;
                case ACTION_TEMP_OUT://温度超范围
                    //showTempOutAlarmDialog("Box" + deviceMac.substring(deviceMac.length() - 2));
                    break;
            }
        }
    };

    public abstract void initData();

    public abstract void setListener();

    public abstract int getLayoutId();

    protected void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    protected void setGone(View view) {
        view.setVisibility(View.GONE);
    }

    protected void setInvisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    protected int getColor(int resId) {
        return context.getResources().getColor(resId);
    }

    protected Drawable getDrawable(int resId) {
        return context.getResources().getDrawable(resId);
    }

    protected void setClick(View view) {
        view.setOnClickListener(this);
    }

    protected View getRootView() {
        return rootView;
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

    protected void Logd(String tag, String msg) {
        Log.d(tag, msg);
    }

    protected void Loge(String tag, String msg) {
        Log.e(tag, msg);
    }

    protected String getToken() {
        String token = SharedPreTool.getInstance(context).getStringData(SharedPreTool.TOKEN, null);
        return token;
    }

    protected void setToken(String token) {
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
     * 停止显示报警dialog
     */
    protected void closeLostAlarm() {
        SystemUtil.stopVibrate(context);//停止震动
        //SystemUtil.stopPlayMediaPlayer();//停止铃声
        SystemUtil.stopPlayRaw();//停止铃声
        if (mLostAlarmDialog != null && mLostAlarmDialog.isShowing()) {
            mLostAlarmDialog.dismiss();
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

    /**
     * 显示等待框
     *
     * @param msg
     */
    protected void showProgress(String msg) {
        disProgress();
        if (dialog == null) {
            dialog = DialogUtils.createDialog(context, msg);
        }
        TextView tv = (TextView) dialog.findViewById(R.id.msg);
        tv.setText(msg);
        if (!dialog.isShowing()) {
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    protected void disProgress() {
        if (dialog == null) {
            return;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onResume() {
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
    public void onPause() {
        super.onPause();
        if (!BuildConfig.DEBUG) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (baseBroad != null && context != null)
            context.unregisterReceiver(baseBroad);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
