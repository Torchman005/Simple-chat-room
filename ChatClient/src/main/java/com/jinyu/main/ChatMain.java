package com.jinyu.main;

import com.jinyu.chatcilent.service.ClientMessageService;
import com.jinyu.chatcilent.service.FileClientService;
import com.jinyu.chatcilent.service.ToUserFunction;
import com.jinyu.utils.Utility;

import java.io.IOException;
import java.util.Scanner;

/*
程序主入口，暂且先简单写写，后面会增加业务功能
 */
public class ChatMain {
//    控制是否登录循环
    private boolean loop = true;
    private boolean status = false;
    private String userId;//设个全局变量，以便后面使用
    private ToUserFunction toUserFunction = new ToUserFunction();
    private int count = 0;//用来记录登录次数，若三次登录账号密码错误则自动退出程序
    private ClientMessageService clientMessageService = new ClientMessageService();// 对象用户私聊/群聊
    private FileClientService fileClientService = new FileClientService();//文件传输对象

    public static void main(String[] args) throws InterruptedException {
        new ChatMain().login();
        System.out.println("(退出)");
    }

    public void login() throws InterruptedException {
        while (!status) {
            System.out.println("请输入用户名：");
            userId = Utility.readString(20);
            System.out.println("请输入密码：");
            String pwd = Utility.readString(20);
            if (toUserFunction.checkUser(userId, pwd)) {
                System.out.println("(登陆成功，欢迎!)");
                status = true;
            } else {
                System.out.println("(登陆失败,请检查用户名或密码是否正确)");
                count++;
            }
//            判断失败是否超过三次
            if (count == 3) {
                return;
            }
        }
        while (loop){
//                因ui还未做出，现先用用户输入数字来实现功能
                /*
                1:获取在线用户列表
                2:群发消息
                3:私聊消息
                4:发送文件
                9:退出
                 */
                int key = Utility.readInt();
                switch (key){
                    case 1:
                        toUserFunction.reqOnlineUserList();
                        break;
                    case 2:
                        System.out.println("(群聊)");
                        break;
                    case 3:
                        System.out.println("选择你要发送的用户：");
                        String getterId = Utility.readString(10);
                        while(true) {
                        System.out.println("(输入exit退出)请输入发送的信息：");
                        String content = Utility.readString(50);
                        if(content.equals("exit")){
                            break;
                        }
                        clientMessageService.sendMessageToOne(content, userId, getterId);
                    }
                        break;
                    case 4:
                        System.out.println("请输入你要发送的文件路径(C:/xx 或 C:\\xx)：");
                        String src = Utility.readString(50);
                        System.out.println("你要发给谁(在线)：");
                        String getter = Utility.readString(20);
                        try {
                            fileClientService.sendFileToOne(src, userId, getter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        System.out.println("拉群");
                        break;
                    case 9:
                        toUserFunction.logout();
                        System.out.println("(系统已安全退出！)");
//                        需要管理这个客户端的线程退出
                        break;
                    default:
                        System.out.println("新功能仍在开发中，敬请期待！[]~(￣▽￣)~*");
                        break;
                }
        }
    }
}
