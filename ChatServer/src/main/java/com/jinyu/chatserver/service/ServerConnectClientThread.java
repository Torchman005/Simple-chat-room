package com.jinyu.chatserver.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;
    public ServerConnectClientThread(Socket socket, String userId){
        this.socket = socket;
        this.userId = userId;
    }
    @Override
    public void run(){
        while(true){
            System.out.println("用户" + userId + "已连接，保持通信");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message mes = (Message) ois.readObject();
//                获取在线用户列表并且发给客户端
                if(mes.getMesType().equals(MessageType.MESSAGE_REQ_ONLINE_USERS)){
                    System.out.println(mes.getSender() + "请求获取在线用户列表");
                    String onlineUsers = ClientThreadsManage.getOnlineUsers();
                    Message mes2 = new Message();
                    mes2.setMesType(MessageType.MESSAGE_RET_ONLINE_USERS_LIST);
                    mes2.setContent(onlineUsers);
                    mes2.setGetter(mes.getSender());

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(mes2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
