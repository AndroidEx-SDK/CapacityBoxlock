package com.androidex.capbox.data.pojo;

/**
 * @author liyp
 * @version 1.0.0
 * @description 图片
 * @createTime 2015/12/23
 * @editTime
 * @editor
 */
public class ImageItem {
    private String path;
    private boolean isChecked;

    public ImageItem(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
