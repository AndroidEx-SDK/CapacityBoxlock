package com.androidex.capbox.ui.widget.recyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * @author liyp
 * @version 1.0.0
 * @description
 * @createTime 2015/11/16
 * @editTime
 * @editor
 */
public class QTDataObserver extends RecyclerView.AdapterDataObserver {

    private QTRecyclerAdapter adapter;

    public QTDataObserver(QTRecyclerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onChanged() {
        super.onChanged();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
        adapter.notifyItemRangeChanged(positionStart + adapter.getHeaderSize(), itemCount);
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        adapter.notifyItemRangeInserted(positionStart + adapter.getHeaderSize(), itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount);
        adapter.notifyItemRangeChanged(fromPosition + adapter.getHeaderSize(), itemCount + adapter.getHeaderSize() + toPosition);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        adapter.notifyItemRangeRemoved(positionStart + adapter.getHeaderSize(), itemCount);
    }
}
