package com.androidex.capbox.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.fragment.LockFragment;
import com.androidex.capbox.utils.CalendarUtil;

import java.util.List;
import java.util.Map;

import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;

/**
 * @author liyp
 * @editTime 2017/9/30
 */

public class WatchListAdapter extends BaseAdapter {
    private List<Map<String,String>> mContentList;
    private LayoutInflater mInflater;

    public WatchListAdapter(Context context, List<Map<String,String>> contentList) {
        mContentList = contentList;
        mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_watch, null);
            if (holder==null){
                holder = new ViewHolder();
            }
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.device_address = (TextView) convertView.findViewById(R.id.device_address);
            holder.tv_connect = (TextView) convertView.findViewById(R.id.tv_connect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String address = mContentList.get(position).get(EXTRA_ITEM_ADDRESS);
        holder.deviceName.setText(CalendarUtil.getName(address));
        if (address != null) {
            holder.device_address.setText(address);
        }
        holder.tv_connect.setTag(position);
        return convertView;
    }

    public class ViewHolder {
        public TextView deviceName;
        public TextView device_address;
        public TextView tv_connect;
    }
}
