package com.jinyu.chatcilent.service;

import java.util.HashMap;

/*
用来管理每个用户线程的类（用map集合存储）
 */
public class ClientConnServerThreadsManage {
    private static HashMap<String, ClientConnectServerThread> threads = new HashMap<>();


//    添加用户线程
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread){
        threads.put(userId, clientConnectServerThread);
    }

//    根据userId获取用户线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return threads.get(userId);
    }
    public static void removeCCSThread(String userId){
        threads.remove(userId);
    }
}
