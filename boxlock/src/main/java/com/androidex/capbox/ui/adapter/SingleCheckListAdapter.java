package com.androidex.capbox.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.DataAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author liyp
 * @version 1.0.0
 * @description 单选列表适配器
 * @createTime 2015/11/11
 * @editTime
 * @editor
 */
public class SingleCheckListAdapter extends DataAdapter<String> {

    public SingleCheckListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_single_check_list, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_text.setText(data.get(position));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItemClick() != null) {
                    getItemClick().onItemClick(position, null, 0);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_text)
        TextView tv_text;
        @Bind(R.id.iv_checked)
        ImageView iv_checked;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
