package com.androidex.capbox.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.view.TypeFaceText;
import com.androidex.capbox.ui.fragment.LockFragment;

import java.util.List;
import java.util.Map;

/**
 * @author liyp
 * @editTime 2017/9/30
 */

public class WatchListAdapter extends BaseAdapter {

    private static final String TAG = "WatchListAdapter";
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
        Log.i(TAG, "getView");
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_watch, null);
            if (holder==null){
                holder = new ViewHolder();
            }
            holder.deviceName = (TypeFaceText) convertView.findViewById(R.id.device_name);
            holder.device_address = (TypeFaceText) convertView.findViewById(R.id.device_address);
            holder.tv_connect = (TypeFaceText) convertView.findViewById(R.id.tv_connect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = mContentList.get(position).get("name");
        if (name.contains(LockFragment.boxName)){
            name=name.replace(LockFragment.boxName,"");
            holder.deviceName.setText(name);
        }
        holder.device_address.setText(mContentList.get(position).get("mac"));
        holder.tv_connect.setTag(position);
        return convertView;
    }

    public class ViewHolder {
        public TypeFaceText deviceName;
        public TypeFaceText device_address;
        public TypeFaceText tv_connect;
    }
}
