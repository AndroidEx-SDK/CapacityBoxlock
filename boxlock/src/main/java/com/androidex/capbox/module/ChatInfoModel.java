package com.androidex.capbox.module;


import com.androidex.capbox.base.BaseMessage;

import java.io.Serializable;

/**
 * liyp
 */
public class ChatInfoModel implements Serializable {

    private int chatId;
    private FriendInfoModel FriendInfoModel;
    private BaseMessage message;
    private boolean isSend;
    private String sendTime;
    private String receiveTime;

    public int getChatId() {
        return chatId;
    }

    public ChatInfoModel setChatId(int chatId) {
        this.chatId = chatId;
        return this;
    }

    public FriendInfoModel getFriendInfo() {
        return FriendInfoModel;
    }

    public ChatInfoModel setFriendInfo(FriendInfoModel FriendInfoModel) {
        this.FriendInfoModel = FriendInfoModel;
        return this;
    }

    public BaseMessage getMessage() {
        return message;
    }

    public ChatInfoModel setMessage(BaseMessage message) {
        this.message = message;
        return this;
    }

    public boolean isSend() {
        return isSend;
    }

    public ChatInfoModel setSend(boolean send) {
        isSend = send;
        return this;
    }

    public String getSendTime() {
        return sendTime;
    }

    public ChatInfoModel setSendTime(String sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public ChatInfoModel setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
        return this;
    }

    @Override
    public String toString() {
        return "ChatInfoModel{" +
                "chatId=" + chatId +
                ", FriendInfoModel=" + FriendInfoModel +
                ", message=" + message +
                ", isSend=" + isSend +
                ", sendTime='" + sendTime + '\'' +
                ", receiveTime='" + receiveTime + '\'' +
                '}';
    }
}
