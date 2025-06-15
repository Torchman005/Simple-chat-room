package com.jinyu.chatcommon;

public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1";
    String MESSAGE_LOGIN_FAIL = "2";
    String MESSAGE_COMM_MES = "3";
    String MESSAGE_REQ_ONLINE_USERS = "4";// 请求返回在线用户列表
    String MESSAGE_RET_ONLINE_USERS_LIST = "5";// 返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";// 用户退出
    String MESSAGE_TO_GROUP_MES = "7";
    String MESSAGE_FILE_MES = "8";
    String MESSAGE_PULL_GROUP_MES = "9";
    String MESSAGE_SEND_TO_ALL = "10";
    String MESSAGE_SYSTEM = "11";
    String MESSAGE_REGISTER_SUCCEED = "12";
    String MESSAGE_REGISTER_FAIL = "13";
    String MESSAGE_REQ_GROUP_LIST = "14";// 请求群聊列表
    String MESSAGE_RET_GROUP_LIST = "15";// 返回群聊列表
}
