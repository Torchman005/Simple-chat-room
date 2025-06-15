package com.jinyu.chatclient.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/*
该类完成文件传输
 */
public class FileClientService {
    public void sendFileToOne(String src, String senderId, String getterId) throws IOException {
        Message mes = new Message();
        mes.setMesType(MessageType.MESSAGE_FILE_MES);
        mes.setSrc(src);
        mes.setSender(senderId);
        mes.setGetter(getterId);

        // 读取文件
        FileInputStream fis = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];

        try {
            fis = new FileInputStream(src);
            fis.read(fileBytes);
            // 将文件对应的字节数组设置到message
            mes.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        System.out.println("文件发送中...");

        // 发送
        ObjectOutputStream oos = new ObjectOutputStream(
                ClientConnServerThreadsManage.getClientConnectServerThread(senderId).getSocket().getOutputStream());
        oos.writeObject(mes);
    }
}
