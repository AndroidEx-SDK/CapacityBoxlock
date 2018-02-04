package com.androidex.capbox.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidex.capbox.callback.ItemClickCallBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liyp
 * @version 1.0.0
 * @description Adapter基类
 * @createTime 2015/7/28
 * @editTime
 * @editor
 */
public abstract class DataAdapter<T> extends BaseAdapter {
    protected List<T> data = new ArrayList<T>();
    private ItemClickCallBack<T> itemClick;
    protected Context context;

    public DataAdapter(Context context) {
        this.context = context;
    }

    public DataAdapter(Context context, ItemClickCallBack<T> itemClick) {
        this(context);
        this.itemClick = itemClick;
    }

    public DataAdapter(Context context, List<T> data) {
        this.context = context;
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setData(List<T> data) {
        if (data != null) {
            this.data.clear();
            this.data.addAll(data);
        } else {
            this.data.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setData(T[] data) {
        if (data != null && data.length > 0) {
            setData(Arrays.asList(data));
        }
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addData(List<T> data) {
        if (data != null && data.size() > 0) {
            if (this.data == null) {
                this.data = new ArrayList<T>();
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addData(T[] data) {
        addData(Arrays.asList(data));
    }

    /**
     * 删除元素
     *
     * @param element
     */
    public void removeElement(T element) {
        if (data.contains(element)) {
            data.remove(element);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除元素
     *
     * @param position
     */
    public void removeElement(int position) {
        if (data != null && data.size() > position) {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除元素
     *
     * @param elements
     */
    public void removeElements(List<T> elements) {
        if (data != null && elements != null && elements.size() > 0
                && data.size() >= elements.size()) {

            for (T element : elements) {
                if (data.contains(element)) {
                    data.remove(element);
                }
            }

            notifyDataSetChanged();
        }
    }

    /**
     * 删除元素
     *
     * @param elements
     */
    public void removeElements(T[] elements) {
        if (elements != null && elements.length > 0) {
            removeElements(Arrays.asList(elements));
        }
    }

    /**
     * 更新元素
     *
     * @param element
     * @param position
     */
    public void updateElement(T element, int position) {
        if (position >= 0 && data.size() > position) {
            data.remove(position);
            data.add(position, element);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加元素
     *
     * @param element
     */
    public void addElement(T element) {
        if (element != null) {
            if (this.data == null) {
                this.data = new ArrayList<T>();
            }
            data.add(element);
            notifyDataSetChanged();
        }
    }

    /**
     * 清除数据源
     */
    public void clearData() {
        if (this.data != null) {
            this.data.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 设置控件可见
     *
     * @param view
     */
    protected void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 设置控件不可见
     *
     * @param view
     */
    protected void setGone(View view) {
        view.setVisibility(View.GONE);
    }

    /**
     * 设置控件不可见
     *
     * @param view
     */
    protected void setInvisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    /**
     * 获取图片资源
     *
     * @param resId
     * @return
     */
    protected Drawable getDrawable(int resId) {
        return context.getResources().getDrawable(resId);
    }

    /**
     * 获取字符串资源
     *
     * @param resId
     * @return
     */
    protected String getString(int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * 获取颜色资源
     *
     * @param resId
     * @return
     */
    protected int getColor(int resId) {
        return context.getResources().getColor(resId);
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getDataSource() {
        return data;
    }

    /**
     * 设置监听
     *
     * @param itemClick
     */
    public void setItemClick(ItemClickCallBack<T> itemClick) {
        this.itemClick = itemClick;
    }

    public ItemClickCallBack getItemClick() {
        return itemClick;
    }

    /**
     * 获取数据源大小
     *
     * @return
     */
    public int getSize() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView,
                                 ViewGroup parent);
}