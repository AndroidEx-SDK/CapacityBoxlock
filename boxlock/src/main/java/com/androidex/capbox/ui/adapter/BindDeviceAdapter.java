package com.androidex.capbox.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.boxlib.service.BleService;
import com.androidex.capbox.R;
import com.androidex.capbox.base.RecyclerAdapter;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.utils.CommonKit;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * @author liyp
 * @version 1.0.0
 * @description 用户参与人次适配器
 * @createTime 2015/12/7
 * @editTime
 * @editor
 */
public class BindDeviceAdapter extends RecyclerAdapter<BoxDeviceModel.device, BindDeviceAdapter.ViewHolder> {
    public static String TAG = "BindDeviceAdapter";

    public BindDeviceAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_binddevice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BoxDeviceModel.device item = getDataSource().get(position);
        // UILKit.loadHead(item.head, holder.iv_head);
        holder.tv_name.setText(item.boxName);
        holder.iv_lock.setImageResource(R.mipmap.lock_close);
        if (BleService.get().getConnectDevice(item.getMac()) == null) {
            holder.iv_connect.setImageResource(R.mipmap.starts_connect);
        } else {
            holder.iv_connect.setImageResource(R.mipmap.starts_disconnect);
        }
        holder.iv_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BleService.get().getConnectDevice(item.getMac()) == null) {
                    EventBus.getDefault().postSticky(new Event.BleConnected(item.getMac()));
                } else {
                    EventBus.getDefault().postSticky(new Event.BleDisConnected(item.getMac()));
                }
            }
        });
        holder.iv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BleService.get().getConnectDevice(item.getMac()) == null) {
                    CommonKit.showErrorShort(context, context.getResources().getString(R.string.bledevice_toast7));
                } else {
                    BleService.get().openLock(item.getMac());
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_lock)
        ImageView iv_lock;
        @Bind(R.id.iv_connect)
        ImageView iv_connect;
        @Bind(R.id.tv_name)
        TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
