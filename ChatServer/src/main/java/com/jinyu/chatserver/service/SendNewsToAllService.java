package com.jinyu.chatserver.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.utils.Utility;

import java.io.ObjectOutputStream;
import java.util.*;
// 服务端推送消息到全体在线用户

public class SendNewsToAllService implements Runnable{
    @Override
    public void run(){
//        多次推送
        while (true){
            System.out.println("(Enter 'exit' to exit)Enter message to send");
            String news = Utility.readString(100);
            if(news.equals("exit")){
                break;
            }
//        构建消息
            Message mes = new Message();
            mes.setSender("服务器");
            mes.setContent(news);
            mes.setMesType(MessageType.MESSAGE_SEND_TO_ALL);
            mes.setSendTime(new Date().toString());
            System.out.println("Send to all:" + news);
//        遍历当前所有通信线程，发送message
            HashMap<String, ServerConnectClientThread> threads = ClientThreadsManage.getThreads();
            Set<String> keySet = threads.keySet();
            Iterator<String> iterator = keySet.iterator();
            while(iterator.hasNext()){
                String onLineUserId = iterator.next().toString();
                ServerConnectClientThread serverConnectClientThread = threads.get(onLineUserId);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
