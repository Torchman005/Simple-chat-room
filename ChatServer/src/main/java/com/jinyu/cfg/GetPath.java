package com.jinyu.cfg;

import java.io.File;

public class GetPath {
    public static String getPath(){
//        获取配置文件路径
        String path = GetPath.class.getResource("").getPath();
        String result = null;
        String marker = "/Simple";

        int index = path.indexOf(marker);
        if (index != -1) {
            result = path.substring(0, index);
        }
        File file = new File(result + "/Simple-Chat-Room/ChatServer/src/main/java/com/jinyu/cfg/config.properties");
        String absolutePath = file.getAbsolutePath();
        return absolutePath;
    }
}
