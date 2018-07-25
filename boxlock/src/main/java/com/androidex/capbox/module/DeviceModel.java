package com.androidex.capbox.module;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/7/24
 */
public class DeviceModel implements Parcelable {
    private String address = "address";
    private String uuid = "uuid";
    private String name = "name";

    public DeviceModel(String address, String uuid, String name) {
        this.address = address;
        this.uuid = uuid;
        this.name = name;
    }

    protected DeviceModel(Parcel in) {
        address = in.readString();
        uuid = in.readString();
        name = in.readString();
    }

    public static final Creator<DeviceModel> CREATOR = new Creator<DeviceModel>() {
        @Override
        public DeviceModel createFromParcel(Parcel in) {
            return new DeviceModel(in);
        }

        @Override
        public DeviceModel[] newArray(int size) {
            return new DeviceModel[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(uuid);
        dest.writeString(name);
    }
}
