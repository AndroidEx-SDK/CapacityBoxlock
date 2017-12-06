package com.androidex.capbox.module;


/**
 * @author liyp
 * @version 1.0.0
 * @description Model基类
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class AlarmListModel extends BaseModel {
    public int code;
    public String id;
    public String dey_uuid;
    public String name;
    public String value;
    public String info;
    public String isSolved;
    public String time;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDey_uuid() {
        return dey_uuid;
    }

    public void setDey_uuid(String dey_uuid) {
        this.dey_uuid = dey_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIsSolved() {
        return isSolved;
    }

    public void setIsSolved(String isSolved) {
        this.isSolved = isSolved;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * {
     * code:0,
     * data:{
     * deviceMac:’FF:FF:FF:FF:FF:FF’
     * }
     * }
     *
     * @return
     */
    @Override
    public String toString() {
        return "AlarmListModel{" +
                "code=" + code +
                ", id=" + id +
                ", dey_uuid=" + dey_uuid +
                ", name=" + name +
                ", value=" + value +
                ", info=" + info +
                ", isSolved=" + isSolved +
                ", time=" + time +
                '}';
    }
}
