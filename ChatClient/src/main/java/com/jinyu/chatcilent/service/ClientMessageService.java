package com.jinyu.chatcilent.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class ClientMessageService {
    public void sendMessageToOne( String content, String senderId, String getterId){
//        实现私聊
//        封装message
        Message mes = new Message();
        mes.setSender(senderId);
        mes.setContent(content);
        mes.setGetter(getterId);
        mes.setSendTime(new Date().toString());//发送时间

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ClientConnServerThreadsManage.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
