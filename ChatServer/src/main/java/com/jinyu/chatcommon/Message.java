package com.jinyu.chatcommon;

import java.io.Serializable;
import java.util.Queue;

public class Message implements Serializable {
//    指定序列化版本号
    private static final long serialVersionUID = 1L;
    private String sender;
    private String getter;
    private String content;//发送内容
    private Queue<String> onlineUsers;//在线用户队列

    public Queue<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(Queue<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    private String sendTime;
    private String mesType;//消息类型（接口定义）

//    和文件相关的字段
    private byte[] fileBytes;
    private int fileLen = 0;
    private String dest;//文件传输到哪里
    private String src;//文件源路径
    private Queue<String> groupMembers;
    private boolean isUser;//判断用户是否存在
    private boolean isGroup;//判断群聊是否存在

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Queue<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Queue<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private String groupName;

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public int getFileLen() {
        return fileLen;
    }

    public void setFileLen(int fileLen) {
        this.fileLen = fileLen;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

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
