package com.jinyu.chatserver.service;

import com.jinyu.chatcommon.Message;

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
//                还未完成，后期会用message
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
