package com.jinyu.chatserver.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;

public class Groups {
    static HashMap<String, Queue<String>> groups = new HashMap<>();// 利用map存储群组
    // 这里需要数据库存储群组，目前先用map临时模拟存储

    public static void addGroup(String groupName, Queue<String> group) {
        groups.put(groupName, group);
    }

    public static Queue<String> getGroup(String groupName) {
        return groups.get(groupName);
    }

    public static boolean hasGroup(String groupName) {
        Iterator<String> iterator = groups.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals(groupName)) {
                return true;
            }
        }
        return false;
    }

    public static java.util.List<String> getGroupsForUser(String userId) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String groupName : groups.keySet()) {
            Queue<String> members = groups.get(groupName);
            if (members != null && members.contains(userId)) {
                result.add(groupName);
            }
        }
        return result;
    }
}
