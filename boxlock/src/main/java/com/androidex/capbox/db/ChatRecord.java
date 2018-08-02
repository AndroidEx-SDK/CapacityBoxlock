package com.androidex.capbox.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * @Description: 聊天记录
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/5/15
 */
@Entity(indexes = {
        @Index(value = "address, uuid, time DESC", unique = true)
})
public class ChatRecord {
    @Id
    private Long id;//数据库表条数ID。
    @NotNull
    private String nickName;//设备的ni'cheng

    private String uuid;//设备的UUID
    @NotNull
    private String address;//设备的mac地址
    @NotNull
    private Long time;//消息发生的时间
    @NotNull
    private byte msgType;//消息类型
    @NotNull
    private String msgContent;//消息内容
    @NotNull
    private String isRead;//消息是否,0未被阅读，1被阅读过了
    @NotNull
    private String isSend;//判断是否是发送的消息，0表示是自己发送的，1表示接收到的。
    @NotNull
    private String deleteChat;//是否被删除，0，未删除，1删除。被删除的消息不再先显示

    public ChatRecord() {
    }

    @Keep
    @Generated(hash = 1154287514)
    public ChatRecord(Long id, @NotNull String nickName, @NotNull String uuid,
                      @NotNull String address, @NotNull Long time, @NotNull byte msgType,
                      @NotNull String msgContent, @NotNull String isRead,
                      @NotNull String isSend, @NotNull String deleteChat) {
        this.id = id;
        this.nickName = nickName;
        this.uuid = uuid;
        this.address = address;
        this.time = time;
        this.msgType = msgType;
        this.msgContent = msgContent;
        this.isRead = isRead;
        this.isSend = isSend;
        this.deleteChat = deleteChat;
    }

    @Override
    public String toString() {
        return "ChatRecord{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", uuid='" + uuid + '\'' +
                ", address='" + address + '\'' +
                ", time=" + time +
                ", msgType='" + msgType + '\'' +
                ", msgContent='" + msgContent + '\'' +
                ", isRead='" + isRead + '\'' +
                ", isSend='" + isSend + '\'' +
                ", deleteChat='" + deleteChat + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return this.nickName;
    }

    public ChatRecord setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getUuid() {
        return this.uuid;
    }

    public ChatRecord setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getAddress() {
        return this.address;
    }

    public ChatRecord setAddress(String address) {
        this.address = address;
        return this;
    }

    public Long getTime() {
        return this.time;
    }

    public ChatRecord setTime(Long time) {
        this.time = time;
        return this;
    }

    public byte getMsgType() {
        return this.msgType;
    }

    public ChatRecord setMsgType(byte msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getMsgContent() {
        return this.msgContent;
    }

    public ChatRecord setMsgContent(String msgContent) {
        this.msgContent = msgContent;
        return this;
    }

    public String getIsRead() {
        return this.isRead;
    }

    public ChatRecord setIsRead(String isRead) {
        this.isRead = isRead;
        return this;
    }

    public String getIsSend() {
        return this.isSend;
    }

    public ChatRecord setIsSend(String isSend) {
        this.isSend = isSend;
        return this;
    }

    public String getDeleteChat() {
        return this.deleteChat;
    }

    public ChatRecord setDeleteChat(String deleteChat) {
        this.deleteChat = deleteChat;
        return this;
    }
}
