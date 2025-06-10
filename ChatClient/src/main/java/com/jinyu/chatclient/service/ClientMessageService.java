package com.jinyu.chatclient.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;

import java.io.ObjectOutputStream;
import java.util.Date;

public class ClientMessageService {
    public void sendMessageToOne( String content, String senderId, String getterId){
//        实现私聊
//        封装message
        Message mes = new Message();
        mes.setMesType(MessageType.MESSAGE_COMM_MES);
        mes.setSender(senderId);
        mes.setContent(content);
        mes.setGetter(getterId);
        mes.setSendTime(new Date().toString());//发送时间
        System.out.println("\n" + mes.getSendTime() + "  " + mes.getSender() + ": " + mes.getContent());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ClientConnServerThreadsManage.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToGroup(String content, String senderId, String groupName){
        Message mes = new Message();
        mes.setContent(content);
        mes.setMesType(MessageType.MESSAGE_TO_GROUP_MES);
        mes.setSender(senderId);
        mes.setGroupName(groupName);
        mes.setSendTime(new Date().toString());
        System.out.println("\n" + mes.getGroupName() + "  " + mes.getSendTime() + "  " + mes.getSender() + ": " + mes.getContent());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ClientConnServerThreadsManage.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
