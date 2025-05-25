package com.jinyu.chatcilent.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.utils.Utility;

import javax.management.ObjectName;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Queue;

public class ClientConnectServerThread extends Thread{
//    全局变量事先声明
    private Socket socket;
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
//        while循环来持续接收服务端传来的信息
        while (true) {
//            等待读取
//            System.out.println("(等待读取)");

            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message mes = (Message) ois.readObject();
//            等待服务端传来message
                if (mes.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_USERS_LIST)) {
//                服务端的信息是返回在线用户列表，所以这里等待接收
                    Queue<String> onlineUsers = mes.getOnlineUsers();
                    System.out.println("=========在线用户列表=========");
                    for (int i = 0; i < onlineUsers.toArray().length; i++) {
                        System.out.println("用户：" + onlineUsers.toArray()[i]);
                    }
                } else if (mes.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
//                普通的聊天消息
                    System.out.println("\n" + mes.getSendTime() + "  " + mes.getSender() + ": " + mes.getContent());
                } else if(mes.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    System.out.println("来自" + mes.getSender() + "的文件，请输入要保存的路径：" );
                    mes.setDest(Utility.readString(50));
                    System.out.println("正在保存...");
                    FileOutputStream fos = new FileOutputStream(mes.getDest());
                    fos.write(mes.getFileBytes());
                    fos.close();
                    System.out.println("保存成功！");
                } else if(mes.getMesType().equals(MessageType.MESSAGE_SEND_TO_ALL)){
//                    服务端推送消息
                    System.out.println("\n" + mes.getSendTime() + "  " + mes.getSender() + ": " + mes.getContent());
                } else if(mes.getMesType().equals(MessageType.MESSAGE_TO_GROUP_MES)){
                    if(mes.isGroup()){
                        System.out.println("\n" + mes.getGroupName() + "  " + mes.getSendTime() + "  " + mes.getSender() + ": " + mes.getContent());
                    } else{
                        System.out.println("无此群聊＞﹏＜");
                    }
                } else {
                    System.out.println("其他类型的信息，暂时不做处理");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public Socket getSocket(){
        return socket;
    }
}
