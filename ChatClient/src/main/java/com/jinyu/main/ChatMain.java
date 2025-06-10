package com.jinyu.main;

import com.jinyu.ui.LoginUI;

import javafx.application.Application;

/*
程序主入口，暂且先简单写写，后面会增加业务功能
 */
public class ChatMain {
    public static void main(String[] args) {
        // 设置系统属性，解决中文乱码
        System.setProperty("file.encoding", "UTF-8");
        // 启动JavaFX应用
        Application.launch(LoginUI.class, args);
    }
}
