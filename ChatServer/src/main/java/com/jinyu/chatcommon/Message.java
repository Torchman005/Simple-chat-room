package com.jinyu.chatcommon;

import java.io.Serializable;

public class Message implements Serializable {
//    指定序列化版本号
    private static final long serialVersionUID = 1L;
    private String sender;
    private String getter;
    private String content;//发送内容
    private String sendTime;
    private String mesType;//消息类型（接口定义）

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getMesType() {
        return mesType;
    }

    public void setMesType(String mesType) {
        this.mesType = mesType;
    }
}
