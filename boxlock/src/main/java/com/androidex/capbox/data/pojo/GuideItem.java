package com.androidex.capbox.data.pojo;

/**
 * @author liyp
 * @version 1.0.0
 * @description 引导页元素
 * @createTime 2015/12/30
 * @editTime
 * @editor
 */
public class GuideItem {
    private int bgRes;      //背景图
    private boolean loadUi; //是否loadUI


    public GuideItem(int bgRes, boolean loadUi) {
        this.bgRes = bgRes;
        this.loadUi = loadUi;
    }

    public int getBgRes() {
        return bgRes;
    }

    public void setBgRes(int bgRes) {
        this.bgRes = bgRes;
    }

    public boolean isLoadUi() {
        return loadUi;
    }

    public void setLoadUi(boolean loadUi) {
        this.loadUi = loadUi;
    }
}
