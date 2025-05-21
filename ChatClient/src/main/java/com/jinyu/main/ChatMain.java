package com.jinyu.main;

import com.jinyu.chatcilent.service.ToUserFunction;

import java.util.Scanner;

/*
程序主入口，暂且先简单写写，后面会增加业务功能
 */
public class ChatMain {
//    控制是否登录循环
    private boolean loop = true;
    private ToUserFunction toUserFunction = new ToUserFunction();
    private int count = 0;//用来记录登录次数，若三次登录账号密码错误则自动退出程序

    public static void main(String[] args) throws InterruptedException {
        new ChatMain().login();
        Thread.sleep(1000);
        System.out.println("退出系统...");
    }

    public void login(){
        //    接收用户键盘输入
        Scanner sc = new Scanner(System.in);
        while (loop){

            System.out.println("请输入用户名：");
            String userId = sc.next();
            System.out.println("请输入密码：");
            String pwd = sc.next();
            if(toUserFunction.checkUser(userId, pwd)) {
                System.out.println("登陆成功，欢迎！");
//                因ui还未做出，现先用用户输入数字来实现功能
                /*
                1:获取在线用户列表
                2:群发消息
                3:私聊消息
                4:发送文件
                9:退出
                 */
                int key = sc.nextInt();
                switch (key){
                    case 1:
                        toUserFunction.reqOnlineUserList();
                        break;
                    case 2:
                        System.out.println("群发消息");
                        break;
                    case 3:
                        System.out.println("私聊消息");
                        break;
                    case 4:
                        System.out.println("发送文件");
                        break;
                    case 9:
                        System.out.println("退出");
                        break;
                }

                System.out.println("新功能仍在开发中，敬请期待！[]~(￣▽￣)~*");
                break;
            }else{
                System.out.println("登陆失败,请检查用户名或密码是否正确.");
                count++;
            }
//            判断失败是否超过三次
            if(count==3){
                break;
            }
        }
    }

}
