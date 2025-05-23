package com.jinyu.chatcilent.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.chatcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ToUserFunction {
//    全局变量，便于操作
    private User user = new User();
    private Socket socket;
    public boolean checkUser(String userId, String pwd){
//        给登录的用户账号和密码初始化，便于后续验证
        user.setUserId(userId);
        user.setPwd(pwd);

        boolean b = false;
        try {
//        创建socket对象
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 6789);

//            向服务端传输用户信息，传的是对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

//            还要接受服务端回复的登录信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message mes = (Message)ois.readObject();

//            判断登录是否成功
            if(mes.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){
                b = true;

//                因为登录成功，所以新增一个通信线程
                ClientConnectServerThread thread = new ClientConnectServerThread(socket);
//                启动线程
                thread.start();
//                把这个线程存到map集合中统一管理
                ClientConnServerThreadsManage.addClientConnectServerThread(userId, thread);

            }else{
                System.out.println("(用户名或密码不正确)");
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }
    public void reqOnlineUserList(){
//        请求返回在线用户列表
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_REQ_ONLINE_USERS);
        message.setSender(user.getUserId());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ClientConnServerThreadsManage.getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    退出客户端并给服务端发送退出message的方法
    public void logout(){
        Message mes = new Message();
        mes.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
//        指定发送者
        mes.setSender(user.getUserId());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ClientConnServerThreadsManage.getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            oos.writeObject(mes);
            System.out.println("(" + user.getUserId() + "退出)");
            System.exit(0);//结束进程
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
