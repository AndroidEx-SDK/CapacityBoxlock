package com.androidex.capbox.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.fragment.LockFragment;
import com.androidex.capbox.utils.CalendarUtil;

import java.util.ArrayList;

import static com.androidex.capbox.utils.Constants.boxName;

public class BLEDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;
    private IClick mListener;
    private int current_position = -1;
    private String current_hint = "";

    public BLEDeviceListAdapter(Context context, IClick listener) {
        super();
        mLeDevices = new ArrayList<>();
        mInflator = LayoutInflater.from(context);
        mListener = listener;
    }

    public void removeDevice(int position) {
        this.mLeDevices.remove(position);
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setTextHint(int position, String hint) {
        current_hint = hint;
        current_position = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflator.inflate(R.layout.list_search_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = view.findViewById(R.id.device_address);
            viewHolder.tv_hint = view.findViewById(R.id.tv_hint);
            viewHolder.deviceName = view.findViewById(R.id.device_name);
            viewHolder.deviceBtn = view.findViewById(R.id.tv_connect);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (current_position == position) {
            viewHolder.tv_hint.setText(current_hint);
            viewHolder.deviceBtn.setText("断开连接");
        } else {
            viewHolder.deviceBtn.setText("绑定");
            viewHolder.tv_hint.setText("");
        }
        viewHolder.deviceBtn.setOnClickListener(mListener);
        viewHolder.deviceBtn.setTag(position);
        BluetoothDevice device = mLeDevices.get(position);
//        String deviceName = CalendarUtil.getName(device.getAddress());
        String deviceName = device.getName();
        viewHolder.deviceName.setText(deviceName);
        viewHolder.deviceAddress.setText(device.getAddress());
        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceBtn;
        TextView tv_hint;
    }

    public abstract static class IClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            listViewItemClick((Integer) v.getTag(), v);
        }

        public abstract void listViewItemClick(int position, View v);
    }
}
