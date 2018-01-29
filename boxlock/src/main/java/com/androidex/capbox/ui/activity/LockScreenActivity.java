package com.androidex.capbox.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.fragment.ScreenItemFragment;
import com.androidex.capbox.ui.view.ZItem;
import com.androidex.capbox.utils.CalendarUtil;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.PagerSwitchAnimation;
import com.androidex.capbox.utils.RLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.cache.SharedPreTool.IS_BIND_NUM;
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
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_IN;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_OUT;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_TEMP_OUT;

public class LockScreenActivity extends BaseActivity {
    @Bind(R.id.tv_time)
    TextView tv_time;
    @Bind(R.id.tv_date)
    TextView tv_date;
    @Bind(R.id.textView1)
    ZItem xitem;
    @Bind(R.id.rl_lockscreen)
    RelativeLayout rl_lockscreen;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private List<Fragment> list = new ArrayList();
    private TimeThread timeThread;
    private PagerAdapter pagerAdapter;
    private boolean isfisrst = true;
    private boolean isfisrst_next = true;

    @Override
    public void initData(Bundle savedInstanceState) {
        RLog.e("锁屏界面启动");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED   //这个在锁屏状态下
                //| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON                    //这个是点亮屏幕
                //| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD                //这个是透过锁屏界面，相当与解锁，但实质没有
                //| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON                  //这个是保持屏幕常亮。
        );
        registerEventBusSticky();//注册Event
        initData();
        initBleBroadCast();
        initViewPager();
        boxlist();
    }

    private void initViewPager() {
        //给viewPager添加动画
        viewPager.setOffscreenPageLimit(1);//设置预加载item个数
        viewPager.setPageTransformer(true, PagerSwitchAnimation.Instance().new MyPageTransformer());
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), list);
        RLog.e("--viewpager set adapter" + "" + list.size());
        viewPager.setAdapter(pagerAdapter);
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
        intentFilter.addAction(ACTION_LOCK_STARTS);
        intentFilter.addAction(ACTION_TEMP_UPDATE);
        intentFilter.addAction(BLE_CONN_RSSI_SUCCED);
        intentFilter.addAction(BLE_CONN_RSSI_FAIL);
        intentFilter.addAction(ACTION_HEART);
        intentFilter.addAction(BLUTOOTH_OFF);//手机蓝牙关闭
        intentFilter.addAction(BLUTOOTH_ON);//手机蓝牙关闭
        intentFilter.addAction(ACTION_LOCK_OPEN_SUCCED);
        intentFilter.addAction(ACTION_TEMP_OUT);//温度超范围
        intentFilter.addAction(ACTION_RSSI_OUT);//信号值超出范围内
        intentFilter.addAction(ACTION_RSSI_IN);//信号值回到范围内
        registerReceiver(lockScreenReceiver, intentFilter);
    }

    public void initData() {
        timeThread = new TimeThread();
        timeThread.start();
        xitem.setZItemListener(new ZItem.ZItemListener() {

            @Override
            public void onRight() {
                CommonKit.finishActivity(LockScreenActivity.this);
//                Intent home = new Intent(Intent.ACTION_MAIN);
//                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                home.addCategory(Intent.CATEGORY_HOME);
//                startActivity(home);
            }

            @Override
            public void onLeft() {

            }
        });
    }

    /**
     * 更新状态
     *
     * @param event
     */
    public void onEvent(Event.BoxBindChange event) {
        //刷新数据
        boxlist();
    }

    /**
     * 解除绑定时触发
     *
     * @param event
     */
    public void onEvent(Event.BoxRelieveBind event) {
        boxlist();
    }

    /**
     * 蓝牙连接
     *
     * @param event
     */
    public void onEvent(Event.BleConnected event) {
        RLog.e("onEvent connect " + event.getAddress());
        BleService.get().connectionDevice(context, event.getAddress());
    }

    /**
     * 蓝牙断开
     *
     * @param event
     */
    public void onEvent(Event.BleDisConnected event) {
        RLog.e("onEvent disconnect " + event.getAddress());
        ServiceBean device = MyBleService.get().getConnectDevice(event.getAddress());
        if (device != null) {
            device.setActiveDisConnect(true);
        }
        MyBleService.get().disConnectDevice(event.getAddress());
    }

    /**
     * 切换下一页
     *
     * @param event
     */
    public void onEvent(Event.NextPage event) {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem >= list.size() - 1) {
            if (!isfisrst_next) {
                CommonKit.showOkShort(context, context.getResources().getString(R.string.bledevice_toast11));
            } else {
                isfisrst_next = false;
            }
        } else {
            viewPager.setCurrentItem(currentItem + 1);
        }
    }

    /**
     * 切换上一页
     *
     * @param event
     */
    public void onEvent(Event.PreviousPage event) {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1);
        } else {
            if (!isfisrst) {
                CommonKit.showOkShort(context, context.getResources().getString(R.string.bledevice_toast10));
            } else {
                isfisrst = false;
            }
        }
    }

    @OnClick({
            R.id.rl_lockscreen
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_lockscreen:
                Logd("锁屏界面主页面被点击");
                break;
        }
    }

    /**
     * 获取设备列表
     */
    public void boxlist() {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.boxlist(getToken(), getUserName(), new ResultCallBack<BoxDeviceModel>() {

            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            if (model != null) {
                                list.clear();
                                if (model.devicelist != null && !model.devicelist.isEmpty()) {
                                    //list = model.devicelist;
                                    for (int i = 0; i < model.devicelist.size(); i++) {
                                        RLog.e("fragment is has values");
                                        ScreenItemFragment screenItemFragment = new ScreenItemFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("item", model.devicelist.get(i));
                                        screenItemFragment.setArguments(bundle);
                                        list.add(screenItemFragment);
                                    }
                                    pagerAdapter.notifyDataSetChanged();
                                    RLog.e("adapter set notifyDataSetChanged list.size=" + list.size());
                                }
                            }
                            Logd("锁屏 刷新列表");
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "账号在其他地方登录");
                            LoginActivity.lauch(context);
                            showProgress("刷新失败");
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            showProgress("刷新失败");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
                disProgress();
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                disProgress();
                if (context != null) {
                    showProgress("刷新列表失败");
                }
                disProgress();
                CommonKit.showErrorShort(context, "网络连接异常");
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

    BroadcastReceiver lockScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            String address = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
            if (address == null) return;
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS://重复连接
                case BLE_CONN_SUCCESS_ALLCONNECTED://重复连接
                    BleService.get().enableNotify(address);
                    disProgress();
                    if (list != null) {
                        pagerAdapter.notifyDataSetChanged();
                    }
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast3));
                    break;
                case BLE_CONN_DIS://蓝牙断开
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast4));
                    if (list != null) {
                        pagerAdapter.notifyDataSetChanged();
                    }
                    break;
                case BLE_CONN_FAIL://连接失败
                    disProgress();
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast8));
                    break;
                case BLUTOOTH_OFF:
                    Logd("手机蓝牙断开");
                    CommonKit.showOkShort(context, getResources().getString(R.string.bledevice_toast9));
                    MyBleService.get().disConnectDeviceALL();
                    if (list != null) {
                        pagerAdapter.notifyDataSetChanged();
                    }
                    break;
                case BLUTOOTH_ON:
                    Logd("手机蓝牙开启");
                    CommonKit.showOkShort(context, "手机蓝牙开启");
                    break;
                case ACTION_LOCK_OPEN_SUCCED:
                    CommonKit.showOkShort(context, "开锁成功");
                    MyBleService.get().getLockStatus(address);
                    break;
                case ACTION_LOCK_STARTS://锁状态FB 32 00 01 00 00 FE
                    if (b[2] == (byte) 0x01) {
                        //tv_status.setText("已打开");
                    } else {
                        //tv_status.setText("已关闭");
                    }
                    break;

                case ACTION_TEMP_OUT://温度超范围
                    break;

                case ACTION_RSSI_OUT:
                    break;
                case ACTION_RSSI_IN:

                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void setListener() {

    }

    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tv_time.setText(CalendarUtil.getInstance().getNowRecordTime()); //更新时间
                    tv_date.setText(CalendarUtil.getInstance().getNowTime("MM月dd日") + " " + CalendarUtil.getInstance().getWeek()); //更新时间
                    break;
                default:
                    break;
            }
        }
    };

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Message msg = Message.obtain();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(lockScreenReceiver);
        unregisterEventBus();
        mHandler.removeMessages(1);
        if (timeThread != null && timeThread.isAlive()) {
            timeThread.interrupt();
            timeThread = null;
        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        List<Fragment> list = new ArrayList();

        public PagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        /**
         * 获得页面数量
         *
         * @return 返回实际的页面数量
         */
        @Override
        public int getCount() {
            RLog.e("get bind count = " + SharedPreTool.getInstance(context).getIntData(IS_BIND_NUM, 0));
            RLog.e("get list size = " + list.size());
            return list.size();
        }

        /**
         * 获得指定序号的页面Fragment对象
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            RLog.e("adapter getItem");
            if (list.size() > 0) {
                return list.get(position);
            }
            RLog.e("fragment is null ");
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            // if (object.getClass().getName().equals(ScreenItemFragment.class.getName())) {
            RLog.e("切换 fargment=" + object.getClass().getName());
            return POSITION_NONE;
            //}
            //RLog.e("父类的 fargment=" + object.getClass().getName());
            //return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            RLog.e("notify  is start " + getCount());
            super.notifyDataSetChanged();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lock_screen;
    }


}
