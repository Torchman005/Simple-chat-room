package com.jinyu.chatcommon;

public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1";
    String MESSAGE_LOGIN_FAIL = "2";
    String MESSAGE_COMM_MES = "3";
    String MESSAGE_REQ_ONLINE_USERS = "4";//请求返回在线用户列表
    String MESSAGE_RET_ONLINE_USERS_LIST = "5";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//用户退出
}
