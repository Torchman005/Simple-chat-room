package com.jinyu.chatserver.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class OnlineUsers {
    static Queue<String> onlineUsers = new LinkedList<>();// 存储在线用户id

    public static void addOnlineUsers(String userId) {
        onlineUsers.add(userId);
        System.out.println(userId + " 上线了");
    }

    public static Queue<String> getOnlineUsers() {
        return onlineUsers;

    }

    public static void deleteUser(String userId) {
        onlineUsers.remove(userId);
    }

    public static boolean hasUser(String getterId) {
        for (String onlineUser : onlineUsers) {
            if (getterId.equals(onlineUser)) {
                return true;
            }
        }
        return false;
    }
}
