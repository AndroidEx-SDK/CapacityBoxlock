package com.androidex.capbox.base;

import java.io.Serializable;

/**
 * liyp
 */
public class BaseMessage implements Serializable {
    private byte msgType;
    private String msgContent;
    private int msgLength;

    public BaseMessage() {
    }

    public byte getMsgType() {
        return msgType;
    }

    public BaseMessage setMsgType(byte msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public BaseMessage setMsgContent(String msgContent) {
        this.msgContent = msgContent;
        return this;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public BaseMessage setMsgLength(int msgLength) {
        this.msgLength = msgLength;
        return this;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "msgType=" + msgType +
                ", msgContent='" + msgContent + '\'' +
                ", msgLength=" + msgLength +
                '}';
    }
}
