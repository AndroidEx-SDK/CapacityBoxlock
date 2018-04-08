package com.androidex.capbox.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.fragment.LockFragment;

import java.util.List;

/**
 * 已绑定设备的适配器
 *
 * @author liyp
 * @editTime 2017/9/30
 */

public class ConnectDeviceListAdapter extends BaseAdapter {
    private List<BluetoothDevice> mContentList;
    private LayoutInflater mInflater;
    private IClick mListener;
    private final Context mContext;

    public ConnectDeviceListAdapter(Context context, List<BluetoothDevice> contentList, IClick listener) {
        mContext = context;
        mContentList = contentList;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_devicelist, null);
            if (holder == null) {
                holder = new ViewHolder();
            }
            /**
             * 操作按钮层
             */
            holder.normalItemContentLayout = convertView.findViewById(R.id.rl_normal);
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.device_address = (TextView) convertView.findViewById(R.id.device_address);

            ViewGroup.LayoutParams lp = holder.normalItemContentLayout.getLayoutParams();

            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            int width2 = dm.widthPixels;//宽
            lp.width = width2;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.normalItemContentLayout.setOnClickListener(mListener);
        holder.normalItemContentLayout.setTag(position);
        final String mac = mContentList.get(position).getAddress();
        String name = mContentList.get(position).getName();
        if (name == null || name.equals("")) {
            holder.deviceName.setText("Box" + mac.substring(mac.length() - 2));
        } else {
            if (name.contains(LockFragment.boxName)) {
                if (name.trim().equals("AndroidExBox")) {
                    name = "Box" + mac.substring(mac.length() - 2);
                } else {
                    name = name.replace(LockFragment.boxName, "");
                }
            } else if (name.trim().equals("Box")) {
                name = name + mac.substring(mac.length() - 2);
            }
            holder.deviceName.setText(name);
        }
        if (mac != null) {
            holder.device_address.setText(mac);
        }
        return convertView;
    }

    public class ViewHolder {
        private View normalItemContentLayout;
        public TextView deviceName;
        public TextView device_address;
    }

    public abstract static class IClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            listViewItemClick((Integer) v.getTag(), v);
        }

        public abstract void listViewItemClick(int position, View v);

    }
}
