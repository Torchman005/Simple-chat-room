package com.jinyu.chatclient.service;

import java.util.HashMap;

/*
用来管理每个用户线程的类（用map集合存储）
 */
public class ClientConnServerThreadsManage {
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();


//    添加用户线程
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread thread) {
        hm.put(userId, thread);
    }

//    根据userId获取用户线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId) {
        return hm.get(userId);
    }

    public static void removeCCSThread(String userId){
        hm.remove(userId);
    }
}
