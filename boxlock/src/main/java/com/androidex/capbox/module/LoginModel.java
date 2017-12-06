package com.androidex.capbox.module;

/**
 * @author liyp
 * @version 1.0.0
 * @description Model基类
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class LoginModel extends BaseModel{

    public String token;

    @Override
    public String toString() {
        return "LoginModel{" +
                "code=" + code +
                ", token='" + token + '\'' +
                ", info='" + info + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                '}';
    }
}
