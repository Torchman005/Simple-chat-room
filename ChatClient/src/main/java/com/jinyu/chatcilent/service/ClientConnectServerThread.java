package com.jinyu.chatcilent.service;

import com.jinyu.chatcommon.Message;

import javax.management.ObjectName;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
//    全局变量事先声明
    private Socket socket;
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
//        while循环来持续接收服务端传来的信息
        System.out.println("等待读取");
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message mes = (Message) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Socket getSocket(){
        return socket;
    }
}
