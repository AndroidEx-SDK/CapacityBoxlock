package com.androidex.capbox.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.fragment.LockFragment;
import com.androidex.capbox.ui.view.TypeFaceText;

import java.util.List;

import static com.androidex.capbox.R.id.tv_status;

/**
 * 已绑定设备的适配器
 *
 * @author liyp
 * @editTime 2017/9/30
 */

public class ConnectDeviceListAdapter extends BaseAdapter {
    private static final String TAG = "BoxListAdapter";
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

    /**
     * 根据位置从数据集中移除一条数据 并更新listview
     *
     * @param position
     */
    public void removeItem(int position) {
        if (position >= 0 && position < mContentList.size()) {
            mContentList.remove(position);
            notifyDataSetChanged();
        }
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
            convertView = mInflater.inflate(R.layout.listitem_device, null);
            if (holder == null) {
                holder = new ViewHolder();
            }
            /**
             * 操作按钮层
             */
            holder.normalItemContentLayout = convertView.findViewById(R.id.rl_normal);
            holder.deviceName = (TypeFaceText) convertView.findViewById(R.id.device_name);
            holder.device_address = (TypeFaceText) convertView.findViewById(R.id.device_address);
            holder.iv_online = (ImageView) convertView.findViewById(R.id.iv_online);
            holder.tv_status = (TypeFaceText) convertView.findViewById(tv_status);
            holder.modify = (TypeFaceText) convertView.findViewById(R.id.tv_modify);

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

        // 设置监听事件
        holder.modify.setOnClickListener(new View.OnClickListener() {//修改

            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    public class ViewHolder {
        private View normalItemContentLayout;
        public TypeFaceText deviceName;
        public TypeFaceText device_address;
        public ImageView iv_online;
        public TypeFaceText tv_status;
        private TextView modify;
    }

    public abstract static class IClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            listViewItemClick((Integer) v.getTag(), v);
        }

        public abstract void listViewItemClick(int position, View v);

    }
}
