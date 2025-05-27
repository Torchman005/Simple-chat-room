package com.jinyu.chatserver.service;

import com.jinyu.cfg.GetPath;
import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.chatcommon.User;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ChatServer {
    ServerSocket ss = null;
    public ChatServer() throws Exception{

        try {



            //             启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();
//

            String path = GetPath.getPath();
            FileInputStream input = new FileInputStream(path);
//            ClassLoader classLoader = ChatServer.class.getClassLoader();
//            InputStream input = classLoader.getResourceAsStream(path);
            Properties prop = new Properties();
            prop.load(input);
            String sport = prop.getProperty("port");
            int port = Integer.parseInt(sport);


            System.out.println("服务端在" + port + "端口监听");
//            System.out.println("服务端在2323端口监听");
            ss = new ServerSocket(port);

            while(true){
                Socket socket = ss.accept();// 监听客户端的连接，若没有则阻塞

//                对象输入输出流读取用户对象
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User user = (User) ois.readObject();
//                准备一个Message对象，用来回复客户端
                Message message = new Message();
//                然后再验证是否能够成功登录
                /**
                 * 注：这部分业务逻辑需要负责登录逻辑的人完善，本人只是写了个样例,我这里
                 * 先假设用户名是千早爱音，密码是mygo
                 */
                if(user.getUserId().equals("qianzaoaiyin") && user.getPwd().equals("mygo")){
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
//                    加入线程
                    ServerConnectClientThread thread = new ServerConnectClientThread(socket, user.getUserId());
                    thread.start();
                    ClientThreadsManage.addServerConnectClientThread(user.getUserId(), thread);

//                    将用户Id加入在线用户队列
                    OnlineUsers.addOnlineUsers(user.getUserId());

//                    然后把message传给客户端
                    oos.writeObject(message);
                }else{
//                    登录失败
                    System.out.println("用户账号或密码不正确");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ss.close();
        }

    }
}
