package com.androidex.capbox.module;

/**
 * @author liyp
 * @version 1.0.0
 * @description Model基类
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class AuthCodeModel extends BaseModel{
    public String authcode;

    @Override
    public String toString() {
        return "BaseModel{" +
                "code=" + code +
                ", info='" + info + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                ", authcode='" + authcode + '\'' +
                '}';
    }
}
