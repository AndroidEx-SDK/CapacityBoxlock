package com.androidex.capbox.module;

/**
 * @author liyp
 * @version 1.0.0
 * @description 文件上传
 * @createTime 2015/12/23
 * @editTime
 * @editor
 */
public class FileUploadModel extends BaseModel{
    public int code;
    public String path;
    public String name;
    private int tag;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
