package com.androidex.capbox.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Description: 绑定设备的信息
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/6/22
 */
@Entity(indexes = {
        @Index(value = "address DESC", unique = true)
})
public class DeviceInfo {
    @Id
    private Long id;
    @NotNull
    private String address;
    @NotNull
    private String uuid;
    @NotNull
    private String name;

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Generated(hash = 184927928)
public DeviceInfo(Long id, @NotNull String address, @NotNull String uuid,
        @NotNull String name) {
    this.id = id;
    this.address = address;
    this.uuid = uuid;
    this.name = name;
}
@Generated(hash = 2125166935)
public DeviceInfo() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getAddress() {
    return this.address;
}
public void setAddress(String address) {
    this.address = address;
}
public String getUuid() {
    return this.uuid;
}
public void setUuid(String uuid) {
    this.uuid = uuid;
}
public String getName() {
    return this.name;
}
public void setName(String name) {
    this.name = name;
}



}
