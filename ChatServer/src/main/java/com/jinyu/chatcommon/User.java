package com.jinyu.chatcommon;

import java.io.Serializable;

public class User implements Serializable {
//    指定序列化版本号
    private static final long serialVersionUID = 1L;
    private String userId;
    private String pwd;

    public User(String userId, String pwd) {
        this.userId = userId;
        this.pwd = pwd;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
