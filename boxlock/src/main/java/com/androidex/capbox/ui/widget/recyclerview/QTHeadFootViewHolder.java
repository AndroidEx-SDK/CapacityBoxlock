package com.androidex.capbox.ui.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author liyp
 * @version 1.0.0
 * @description
 * @createTime 2015/11/16
 * @editTime
 * @editor
 */
public class QTHeadFootViewHolder extends RecyclerView.ViewHolder {

    public QTHeadFootViewHolder(View itemView) {
        super(itemView);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        itemView.setTag(this);
    }
}
