package com.androidex.capbox.callback;

import android.view.View;

/**
 * @author liyp
 * @version 1.0.0
 * @description 单击回调
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public abstract class ItemClickCallBack<T> {
    /**
     * 单击事件
     *
     * @param position 位置
     * @param model    实体
     * @param tag      标签
     */
    public void onItemClick(int position, T model, int tag) {
    }

    public void onItemClick(View view, int position, T model, int tag) {
    }

    /**
     * 长按事件
     *
     * @param position 位置
     * @param model    实体
     * @param tag      标签
     */
    public void onItemLongClick(int position, T model, int tag) {
    }

    public void onItemLongClick(View view, int position, T model, int tag) {
    }
}
