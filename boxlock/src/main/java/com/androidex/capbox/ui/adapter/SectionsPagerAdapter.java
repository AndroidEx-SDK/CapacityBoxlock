package com.androidex.capbox.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.ui.fragment.ScreenItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyp
 * @editTime 2018/1/26
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    List<BoxDeviceModel.device> list = new ArrayList();
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context, List<BoxDeviceModel.device> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    /**
     * 获得页面数量
     *
     * @return 返回实际的页面数量
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * 获得指定序号的页面Fragment对象
     *
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        if (list.size() > 0) {
            ScreenItemFragment screenItemFragment = new ScreenItemFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", list.get(position));
            screenItemFragment.setArguments(bundle);
            return screenItemFragment;
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
