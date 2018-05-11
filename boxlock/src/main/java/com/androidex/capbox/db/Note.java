package com.androidex.capbox.db;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "NOTE".
 */
@Entity(indexes = {
        @Index(value = "address, time DESC", unique = true)
})
public class Note {
    @Id
    private Long id;
    @NotNull
    private String address;
    @NotNull
    private Long time;
    @NotNull
    private String lat;
    @NotNull
    private String lon;
    private String alt;

    @Convert(converter = NoteTypeConverter.class, columnType = String.class)
    private NoteType type;

    @Generated(hash = 1272611929)
    public Note() {
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", time=" + time +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", alt='" + alt + '\'' +
                ", type=" + type +
                '}';
    }

    @Keep()
    public Note(Long id, @NotNull String address, Long time, @NotNull String lat, @NotNull String lon, String alt, NoteType type) {
        this.id = id;
        this.address = address;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.type = type;
    }

    public Note(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }
}
