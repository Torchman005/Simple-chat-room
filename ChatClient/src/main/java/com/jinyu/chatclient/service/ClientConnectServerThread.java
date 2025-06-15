package com.jinyu.chatclient.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.ui.ChatUI;
import com.jinyu.utils.Utility;

import javax.management.ObjectName;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Queue;

public class ClientConnectServerThread extends Thread {
    // 全局变量事先声明
    private Socket socket;
    private ChatUI chatUI;

    public ClientConnectServerThread(Socket socket, ChatUI chatUI) {
        this.socket = socket;
        this.chatUI = chatUI;
    }

    @Override
    public void run() {
        // while循环来持续接收服务端传来的信息
        while (true) {
            // 等待读取
            // System.out.println("(等待读取)");

            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message mes = (Message) ois.readObject();
                // 等待服务端传来message
                if (mes.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_USERS_LIST)) {
                    // 服务端的信息是返回在线用户列表，所以这里等待接收
                    Queue<String> onlineUsers = mes.getOnlineUsers();
                    chatUI.updateOnlineUsers(onlineUsers);
                } else if (mes.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    // 普通的聊天消息
                    chatUI.displayMessage(mes);
                } else if (mes.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    chatUI.displayMessage(mes);
                    // 文件保存逻辑将在UI中处理
                } else if (mes.getMesType().equals(MessageType.MESSAGE_SEND_TO_ALL)) {
                    // 服务端推送消息
                    chatUI.displayMessage(mes);
                } else if (mes.getMesType().equals(MessageType.MESSAGE_TO_GROUP_MES)) {
                    if (mes.isGroup()) {
                        chatUI.displayMessage(mes);
                    } else {
                        chatUI.displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "无此群聊＞﹏＜"));
                    }
                } else if (mes.getMesType().equals(MessageType.MESSAGE_RET_GROUP_LIST)) {
                    chatUI.handleGroupListMsg(mes.getContent());
                } else {
                    chatUI.displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "其他类型的信息，暂时不做处理"));
                }

            } catch (Exception e) {
                chatUI.displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "接收消息错误: " + e.getMessage()));
                break;
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
