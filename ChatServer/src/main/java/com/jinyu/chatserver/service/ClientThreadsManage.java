package com.jinyu.chatserver.service;

import java.util.HashMap;
import java.util.Queue;

public class ClientThreadsManage {
    private static HashMap<String, ServerConnectClientThread> threads = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getThreads() {
        return threads;
    }

    public static void addServerConnectClientThread(String userId, ServerConnectClientThread serverConnectClientThread){

        threads.put(userId, serverConnectClientThread);

    }
    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return threads.get(userId);
    }
    public static Queue<String> getOnlineUsers(){
        return OnlineUsers.onlineUsers;
    }
    public static void removeSCCThread(String userId){
        threads.remove(userId);
    }
}
