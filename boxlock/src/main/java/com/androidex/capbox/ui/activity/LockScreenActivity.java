package com.androidex.capbox.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.ui.adapter.BindDeviceAdapter;
import com.androidex.capbox.ui.view.ZItem;
import com.androidex.capbox.ui.view.CustomRecyclerView;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

public class LockScreenActivity extends BaseActivity {
    public static String TAG = "LockScreenActivity";
    @Bind(R.id.textView1)
    ZItem xitem;
    @Bind(R.id.qtRecyclerView)
    CustomRecyclerView qtRecyclerView;
    private BindDeviceAdapter adapter;

    @Override
    public void initData(Bundle savedInstanceState) {
        Log.e(TAG, "锁屏界面启动");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED    //这个在锁屏状态下
                //| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON                    //这个是点亮屏幕
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD                //这个是透过锁屏界面，相当与解锁，但实质没有
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);                //这个是保持屏幕常亮。
        initData();
        initRecyclerView();
        boxlist();
    }

    private void initRecyclerView() {
        adapter = new BindDeviceAdapter(context);
        qtRecyclerView.horizontalLayoutManager(context).defaultNoDivider();
        qtRecyclerView.setAdapter(adapter);
        qtRecyclerView.setOnRefreshAndLoadMoreListener(new CustomRecyclerView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                boxlist();
            }

            @Override
            public void onLoadMore(int page) {

            }
        });
    }


    public void initData() {
        xitem.setZItemListener(new ZItem.ZItemListener() {

            @Override
            public void onRight() {
                //CommonKit.finishActivity(LockScreenActivity.this);
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }

            @Override
            public void onLeft() {

            }
        });
    }

    /**
     * 获取设备列表
     */
    public void boxlist() {
        NetApi.boxlist(getToken(), getUserName(), new ResultCallBack<BoxDeviceModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            if (model != null) {
                                if (model.devicelist != null && !model.devicelist.isEmpty()) {
                                    adapter.setData(model.devicelist);
                                }
                            }
                            Logd(TAG, "刷新列表");
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
                //scanLeDeviceList(true);
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
                //scanLeDeviceList(true);
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Loge(TAG, "锁屏界面退出");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lock_screen;
    }

}
