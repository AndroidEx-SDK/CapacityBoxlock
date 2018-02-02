package com.androidex.capbox.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.callback.ItemClickCallBack;
import com.androidex.capbox.ui.adapter.SingleCheckListAdapter;

import java.util.List;

/**
 * @author liyp
 * @version 1.0.0
 * @description 单选列表对话框
 * @createTime 2015/11/11
 * @editTime
 * @editor
 */
public class SingleCheckListDialog extends Dialog {
    private TextView tv_title;
    private ListView lv_items;
    private LinearLayout ll_top;

    private SingleCheckListAdapter adapter;

    private ItemClickCallBack<String> callBack;

    public SingleCheckListDialog(Context context) {
        super(context, R.style.CustomDialgBig);
        setContentView(R.layout.dlg_single_check_list);
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);

        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_items = (ListView) findViewById(R.id.lv_items);
        ll_top = (LinearLayout) findViewById(R.id.ll_top);

        if (adapter == null) {
            adapter = new SingleCheckListAdapter(context);
        }
        adapter.setItemClick(new ItemClickCallBack<String>() {
            @Override
            public void onItemClick(int position, String model, int tag) {
                super.onItemClick(position, model, tag);
                //TODO 处理选中状态
                dismiss();
                if (callBack != null) {
                    callBack.onItemClick(position, model, tag);
                }
            }
        });
        lv_items.setAdapter(adapter);
    }

    public SingleCheckListDialog title(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
            ll_top.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText("");
            ll_top.setVisibility(View.GONE);
        }
        return this;
    }


    public SingleCheckListDialog data(List<String> data) {
        adapter.setData(data);
        return this;
    }

    public SingleCheckListDialog data(String[] data) {
        adapter.setData(data);
        return this;
    }

    public SingleCheckListDialog setItemClickCallBack(ItemClickCallBack<String> callBack) {
        this.callBack = callBack;
        return this;
    }

    public SingleCheckListDialog cancelInTouch(boolean cancel) {
        setCanceledOnTouchOutside(cancel);
        return this;
    }

    @Override
    public void show() {
        dismiss();
        super.show();
    }
}
