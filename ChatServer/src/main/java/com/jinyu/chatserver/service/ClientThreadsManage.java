package com.jinyu.chatserver.service;

import java.util.HashMap;

public class ClientThreadsManage {
    private static HashMap<String, ServerConnectClientThread> threads = new HashMap<>();

    public static void addServerConnectClientThread(String userId, ServerConnectClientThread serverConnectClientThread){

        threads.put(userId, serverConnectClientThread);

    }
    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return threads.get(userId);
    }
}
