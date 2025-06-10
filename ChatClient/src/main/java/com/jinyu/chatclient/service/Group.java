package com.jinyu.chatclient.service;

import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Queue;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.utils.Utility;

public class Group {
    public void pullGroup(String senderId){
//        利用map存储群聊的用户
        Queue<String> groupMembers = new LinkedList<>();
        Message mes = new Message();

        System.out.println("群聊名？");
        String groupName = Utility.readString(20);
        mes.setGroupName(groupName);
        groupMembers.add(senderId);
        System.out.println("(输入ok结束输入)你要邀请谁进群？");
        while (true) {
            String userId = Utility.readString(20);
            if(userId.equals("ok")){
                break;
            }else {
                groupMembers.add(userId);
            }
        }
        mes.setMesType(MessageType.MESSAGE_PULL_GROUP_MES);
        mes.setGroupMembers(groupMembers);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ClientConnServerThreadsManage.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
