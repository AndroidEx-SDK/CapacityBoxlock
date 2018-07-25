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
import android.widget.TextView;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.Event;
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
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_OFF;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLUTOOTH_ON;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;

/**
 * 悬浮在锁屏界面之上界面
 */
public class LockScreenActivity extends BaseActivity {
    @Bind(R.id.tv_time)
    TextView tv_time;
    @Bind(R.id.tv_date)
    TextView tv_date;
    @Bind(R.id.textView1)
    ZItem xitem;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private List<Fragment> list = new ArrayList();
    private TimeThread timeThread;
    private PagerAdapter pagerAdapter;
    private boolean isFirstOnEvent = false;//防止event初始化时导致的自动连接和断开等操作bug

    @Override
    public void initData(Bundle savedInstanceState) {
        isFirstOnEvent = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED   //这个在锁屏状态下
                //| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON              //这个是点亮屏幕
                //| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD            //这个是透过锁屏界面，相当与解锁，但实质没有
                //| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON              //这个是保持屏幕常亮。
        );
        registerEventBusSticky();//注册Event
        initData();
        initBleBroadCast();
        initViewPager();
        boxlist();
    }

    private void initViewPager() {
        //给viewPager添加动画
        viewPager.setOffscreenPageLimit(5);//设置预加载item个数
        viewPager.setPageTransformer(true, PagerSwitchAnimation.Instance().new MyPageTransformer());
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * 初始化蓝牙广播
     */
    private void initBleBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLUTOOTH_OFF);//手机蓝牙关闭
        intentFilter.addAction(BLUTOOTH_ON);//手机蓝牙关闭
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
     * 切换下一页
     *
     * @param event
     */
    public void onEvent(Event.NextPage event) {
        if (!isFirstOnEvent) {//初始化的时候调用这里
            RLog.e("onEvent nextpage init " + !isFirstOnEvent);
        } else {
            int currentItem = viewPager.getCurrentItem();
            RLog.e("onEvent nextpage currentItem = " + currentItem);
            if (currentItem >= list.size() - 1) {
                CommonKit.showOkToast(context, context.getResources().getString(R.string.bledevice_toast11));
            } else {
                viewPager.setCurrentItem(currentItem + 1);
            }
        }
    }

    /**
     * 切换上一页
     *
     * @param event
     */
    public void onEvent(Event.PreviousPage event) {
        if (!isFirstOnEvent) {
            RLog.e("onEvent init lastPage " + !isFirstOnEvent);
        } else {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1);
            } else {
                CommonKit.showOkToast(context, context.getResources().getString(R.string.bledevice_toast10));
            }
        }
    }

    /**
     * 获取设备列表
     */
    public void boxlist() {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorToast(context, "设备未连接网络");
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
                                    for (int i = 0; i < model.devicelist.size(); i++) {
                                        ScreenItemFragment screenItemFragment = new ScreenItemFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("item", model.devicelist.get(i));
                                        screenItemFragment.setArguments(bundle);
                                        list.add(screenItemFragment);
                                    }
                                    pagerAdapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorToast(context, "账号在其他地方登录");
                            LoginActivity.lauch(context);
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorToast(context, "获取设备列表失败");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorToast(context, model.info);
                            }
                            break;
                    }
                }
                disProgress();
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                CommonKit.showErrorToast(context, "网络连接异常");
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
            if (address == null) return;
            switch (intent.getAction()) {
                case BLUTOOTH_OFF:
                    Logd("手机蓝牙断开");
                    CommonKit.showErrorToast(context, getResources().getString(R.string.bledevice_toast9));
                    MyBleService.getInstance().disConnectDeviceALL();
                    if (list != null) {
                        pagerAdapter.notifyDataSetChanged();
                    }
                    break;
                case BLUTOOTH_ON:
                    Logd("手机蓝牙开启");
                    CommonKit.showOkToast(context, "手机蓝牙开启");
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(lockScreenReceiver);
        unregisterEventBus();
        mHandler.removeMessages(1);
        if (timeThread != null && timeThread.isAlive()) {
            timeThread.interrupt();
            timeThread = null;
        }
        super.onDestroy();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        List<Fragment> list_adapter = new ArrayList();

        public PagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list_adapter = list;
        }

        /**
         * 获得页面数量
         *
         * @return 返回实际的页面数量
         */
        @Override
        public int getCount() {
            return list_adapter.size();
        }

        /**
         * 获得指定序号的页面Fragment对象
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return list_adapter.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
            //return POSITION_UNCHANGED;
        }

        @Override
        public void notifyDataSetChanged() {
            isFirstOnEvent = true;
            super.notifyDataSetChanged();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lock_screen;
    }

}
