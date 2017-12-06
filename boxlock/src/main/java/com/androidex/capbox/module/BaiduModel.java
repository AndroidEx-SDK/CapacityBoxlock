package com.androidex.capbox.module;

/**
 * @author liyp
 * @version 1.0.0
 * @description {"error":0,"x":"MTEzLjU1MTgwODQwNzYx","y":"MjMuNTIxMjMyNTM1MjU0"}
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class BaiduModel {
    public int error;
    public String x;
    public String y;

    @Override
    public String toString() {
        return "BaseModel{" +
                "error=" + error +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                '}';
    }
}
