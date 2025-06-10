package com.jinyu.chatclient.service;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.chatcommon.User;
import com.jinyu.chatcommon.UserType;
import com.jinyu.ui.ChatUI;

public class ToUserFunction {
//    全局变量，便于操作
    private User user = new User();
    private Socket socket;
    private ChatUI chatUI;

    public ToUserFunction() {
        // Default constructor for login screen
    }

    public ToUserFunction(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    private void displayMessage(Message message) {
        if (chatUI != null) {
            chatUI.displayMessage(message);
        } else {
            System.out.println(message.getContent());
        }
    }

    public boolean checkUser(String userId, String pwd){
//        给登录的用户账号和密码初始化，便于后续验证
        user.setUserId(userId);
        user.setPwd(pwd);
        user.setUserType(UserType.USER_LOGIN);

        boolean b = false;
        try {
            // 读取配置文件
            ClassLoader classLoader = ToUserFunction.class.getClassLoader();
            InputStream input = classLoader.getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            String host = prop.getProperty("host");
            String sport = prop.getProperty("port");
            int port = Integer.parseInt(sport);
            
            // 创建socket连接
            socket = new Socket(InetAddress.getByName(host), port);

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
                ClientConnectServerThread thread = new ClientConnectServerThread(socket, chatUI);
//                启动线程
                thread.start();
//                把这个线程存到map集合中统一管理
                ClientConnServerThreadsManage.addClientConnectServerThread(userId, thread);

            }else{
                displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "用户名或密码不正确"));
                socket.close();
            }
        } catch (Exception e) {
            displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "登录失败: " + e.getMessage()));
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
            displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "获取在线用户列表失败: " + e.getMessage()));
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
            displayMessage(new Message(MessageType.MESSAGE_SYSTEM, user.getUserId() + " 退出"));
            System.exit(0);//结束进程
        } catch (Exception e) {
            displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "退出失败: " + e.getMessage()));
        }
    }

    public boolean registerUser(String userId, String pwd) {
        boolean b = false;
        try {
            // 读取配置文件
            ClassLoader classLoader = ToUserFunction.class.getClassLoader();
            InputStream input = classLoader.getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            String host = prop.getProperty("host");
            String sport = prop.getProperty("port");
            int port = Integer.parseInt(sport);
            
            // 创建socket连接
            socket = new Socket(InetAddress.getByName(host), port);

            // 向服务端传输用户信息
            user.setUserId(userId);
            user.setPwd(pwd);
            user.setUserType(UserType.USER_REGISTER);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user); 

            // 接收服务端回复的注册信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message mes = (Message)ois.readObject();

            // 判断注册是否成功
            if(mes.getMesType().equals(MessageType.MESSAGE_REGISTER_SUCCEED)) {
                b = true;
            } else {
                displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "注册失败(用户名已存在)"));
                socket.close();
            }
        } catch (Exception e) {
            displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "注册失败: " + e.getMessage()));
        }
        return b;
    }
}
