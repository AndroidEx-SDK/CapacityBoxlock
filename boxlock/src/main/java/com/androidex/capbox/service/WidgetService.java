package com.androidex.capbox.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.R;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.RLog;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.capbox.provider.WidgetProvider.CLICK_BLE_CONNECTED;
import static com.androidex.capbox.provider.WidgetProvider.CLICK_LOCK_OPEN;
import static com.androidex.capbox.provider.WidgetProvider.CLICK_OPEN_MAINACTIVITY;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_BOX_NAME;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_BOX_UUID;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_ITEM_ADDRESS;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_ITEM_CLICK;
import static com.androidex.capbox.provider.WidgetProvider.EXTRA_ITEM_POSITION;

/**
 * 控制 桌面小部件 更新
 * Created by lyl on 2017/8/23.
 */
public class WidgetService extends RemoteViewsService {
    private static Context context;
    private static List<BoxDeviceModel.device> mWidgetItems = new ArrayList<BoxDeviceModel.device>();
    private static int mAppWidgetId;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = this;
        RLog.e("WidgetService start");
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        RLog.e("WidgetService onDestroy");
        super.onDestroy();
    }

    /*
     *  服务开始时，即调用startService()时，onStartCommand()被执行。
     *
     *  这个整形可以有四个返回值：start_sticky、start_no_sticky、START_REDELIVER_INTENT、START_STICKY_COMPATIBILITY。
     *  它们的含义分别是：
     *  1):START_STICKY：如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。随后系统会尝试重新创建service，
     *     由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null;
     *  2):START_NOT_STICKY：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务;
     *  3):START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入;
     *  4):START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            // TODO Auto-generated method stub
            RLog.e("Factory onCreate");
            boxlist();
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mWidgetItems.size();
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // TODO Auto-generated method stub
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_provider_widget);
            RLog.e("getViewAt mainPage refresh");
            BoxDeviceModel.device device = mWidgetItems.get(position);
            rv.setTextViewText(R.id.tv_name, device.getDeviceName());
            rv.setTextViewText(R.id.tv_address, device.getMac());

            rv.setOnClickFillInIntent(R.id.rl_device, getPendingIntentStartActivity(device.getDeviceName(), device.getMac(), device.getUuid(), position));
            rv.setOnClickFillInIntent(R.id.iv_connect, getIntent(CLICK_BLE_CONNECTED, position, device.getMac()));
            rv.setOnClickFillInIntent(R.id.iv_lock, getIntent(CLICK_LOCK_OPEN, position, device.getMac()));

            if (BleService.get().getConnectDevice(device.getMac()) == null) {
                rv.setImageViewResource(R.id.iv_connect, R.mipmap.starts_connect2);
            } else {
                rv.setImageViewResource(R.id.iv_connect, R.mipmap.starts_disconnect2);
            }
            return rv;
        }

        /**
         * 点击事件预触发的广播
         *
         * @param action
         * @param position
         * @param address
         * @return
         */
        private Intent getIntent(String action, int position, String address) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ITEM_CLICK, action);
            intent.putExtra(EXTRA_ITEM_POSITION, position);
            intent.putExtra(EXTRA_ITEM_ADDRESS, address);
            intent.putExtras(intent);
            return intent;
        }

        /**
         * 启动主界面
         *
         * @param name
         * @param address
         * @param uuid
         * @param position
         * @return
         */
        private Intent getPendingIntentStartActivity(String name, String address, String uuid, int position) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ITEM_CLICK, CLICK_OPEN_MAINACTIVITY);
            intent.putExtra(EXTRA_BOX_NAME, name);
            intent.putExtra(EXTRA_BOX_UUID, uuid);
            intent.putExtra(EXTRA_ITEM_ADDRESS, address);
            intent.putExtra(EXTRA_ITEM_POSITION, position);
            return intent;
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public void onDataSetChanged() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onDestroy() {
            // TODO Auto-generated method stub
            mWidgetItems.clear();
        }
    }

    /**
     * 获取设备列表
     */
    public static void boxlist() {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }

        String token = SharedPreTool.getInstance(context).getStringData(SharedPreTool.TOKEN, null);
        if (token == null) {
            CommonKit.showErrorShort(context, "账号未登录");
            RLog.e("token is null");
            return;
        }

        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        if (username == null) {
            CommonKit.showErrorShort(context, "账号未登录");
            RLog.e("username is null");
            return;
        }

        NetApi.boxlist(token, username, new ResultCallBack<BoxDeviceModel>() {

            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            mWidgetItems.clear();
                            if (model != null) {
                                if (model.devicelist != null && !model.devicelist.isEmpty()) {
                                    mWidgetItems = model.devicelist;
                                }
                            }
                            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.myListView);
                            RLog.d("刷新列表");
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "账号在其他地方登录");
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                CommonKit.showErrorShort(context, "网络连接异常");
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

}
