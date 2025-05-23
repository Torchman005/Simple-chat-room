package com.jinyu.chatserver.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;
    public ServerConnectClientThread(Socket socket, String userId){
        this.socket = socket;
        this.userId = userId;
    }
    public Socket getSocket(){
        return this.socket;
    }
    @Override
    public void run(){
        while(true){
            System.out.println(userId + "已连接并保持通信");
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
                }else if(mes.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){
//                    调用方法
                    ClientThreadsManage.removeSCCThread(mes.getSender());
                    socket.close();//这里的socket是对应这个线程的socket，一定要记住关闭socket
                    System.out.println(userId + "退出登录");
                    break;//一定要记住break！
                }else if(mes.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    ServerConnectClientThread serverConnectClientThread = ClientThreadsManage.getServerConnectClientThread(mes.getGetter());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(mes);//若要离线留言，可发送给数据库

                } else{
                    System.out.println("其他类型的信息，暂时不作处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
