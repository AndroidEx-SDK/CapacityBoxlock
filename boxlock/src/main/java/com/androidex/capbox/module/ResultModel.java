package com.androidex.capbox.module;

/**
 * Created by cts on 17/10/14.
 */

public class ResultModel extends BaseModel {
    public Data data;

    public class Data {
    }


    @Override
    public String toString() {
        return "LoginModel{" +
                "code=" + code +
                ", data=" + data + '\'' +
                ", info='" + info + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                '}';
    }

}
