package com.androidex.capbox.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class ScreenItemFragment extends BaseFragment {
    @Bind(R.id.iv_last)
    ImageView iv_last;
    @Bind(R.id.iv_next)
    ImageView iv_next;
    @Bind(R.id.iv_lock)
    ImageView iv_lock;
    @Bind(R.id.iv_connect)
    ImageView iv_connect;
    @Bind(R.id.tv_name)
    TextView tv_name;

    BoxDeviceModel.device item;

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        item = (BoxDeviceModel.device) bundle.getSerializable("item");
        if (item == null) return;
        initView();
    }

    private void initView() {
        if (item.boxName.equals("Box")) {
            item.boxName = item.boxName + item.getMac().substring(item.getMac().length() - 2);
        } else if (item.boxName.contains("AndroidExBox")) {
            item.boxName = "Box" + item.getMac().substring(item.getMac().length() - 2);
        }
        tv_name.setText(item.boxName);
        iv_lock.setImageResource(R.mipmap.lock_close);
        if (BleService.get().getConnectDevice(item.getMac()) == null) {
            iv_connect.setImageResource(R.mipmap.starts_connect);
        } else {
            iv_connect.setImageResource(R.mipmap.starts_disconnect);
        }
        iv_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BleService.get().getConnectDevice(item.getMac()) == null) {
                    CommonKit.showOkShort(context, context.getResources().getString(R.string.bledevice_toast12));
                    EventBus.getDefault().postSticky(new Event.BleConnected(item.getMac()));
                } else {
                    EventBus.getDefault().postSticky(new Event.BleDisConnected(item.getMac()));
                }
            }
        });
        iv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BleService.get().getConnectDevice(item.getMac()) == null) {
                    CommonKit.showErrorShort(context, context.getResources().getString(R.string.bledevice_toast7));
                } else {
                    BleService.get().openLock(item.getMac());
                }
            }
        });
        iv_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new Event.PreviousPage());
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new Event.NextPage());
            }
        });
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_binddevice;
    }
}
