package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.DeviceWatchModel;
import com.androidex.capbox.ui.adapter.WatchListAdapter;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

public class WatchListActivity extends BaseActivity {
    @Bind(R.id.list)
    ListView listconnected;
    @Bind(R.id.secondtitlebar)
    SecondTitleBar secondtitlebar;
    @Bind(R.id.swipe_watchlist)
    SwipeRefreshLayout swipe_watchlist;

    private static final String TAG = "WatchListActivity";
    private String uuid;
    List<Map<String, String>> mylist = new ArrayList<>();
    private WatchListAdapter watchListAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_watch_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        uuid = getIntent().getStringExtra("uuid");
        initTitleBar();
        initListView();
        iniRefreshView();
        watchlist();
    }

    private void initTitleBar() {
        secondtitlebar.setRightRes(R.drawable.device_search);
        //搜索
        secondtitlebar.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDeviceActivity.lauch(context, null);
            }
        });
    }

    @Override
    public void setListener() {

    }

    private void initListView() {
        watchListAdapter = new WatchListAdapter(context, mylist);
        //添加并且显示
        listconnected.setAdapter(watchListAdapter);
        listconnected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                Bundle bundle = new Bundle();
                bundle.putString("mac", mylist.get(position).get("mac"));
                bundle.putString("uuid", uuid);
                WatchDetialActivity.lauch(context, bundle);
            }
        });
    }

    private void iniRefreshView() {
        swipe_watchlist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 模拟刷新完成
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        watchlist();
                        swipe_watchlist.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    /**
     * 获取设备列表
     */
    public void watchlist() {
        NetApi.watchlist(getToken(), getUserName(), uuid, new ResultCallBack<DeviceWatchModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, DeviceWatchModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            for (DeviceWatchModel.device device : model.devicelist) {
                                Map<String, String> map = new HashMap<>();
                                map.put("name", "AndroidExWatch");
                                map.put("mac", device.mac);
                                mylist.add(map);
                            }
                            if (model.devicelist.size() > 0) {
                                L.e(TAG + "刷新列表");
                                watchListAdapter.notifyDataSetChanged();
                            } else {
                                L.e(TAG + "刷新列表无数据");
                                CommonKit.showErrorShort(context, "该箱体未绑定腕表");
                            }
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "未绑定腕表");
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
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

    public static void lauch(Activity activity, Bundle bundle) {
        CommonKit.startActivityForResult(activity, WatchListActivity.class, bundle, Constants.CODE.REQUESTCODE_ADD_DEVICE);
    }


    @Override
    public void onClick(View v) {

    }
}
