package com.jinyu.test;

import com.jinyu.chatclient.service.ToUserFunction;
import com.jinyu.chatclient.service.ClientMessageService;
import com.jinyu.chatclient.service.FileClientService;
import com.jinyu.chatclient.service.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ChatTest {
    private ToUserFunction toUserFunction;
    private ClientMessageService clientMessageService;
    private FileClientService fileClientService;
    private Group group;

    @BeforeEach
    void setUp() {
        toUserFunction = new ToUserFunction();
        clientMessageService = new ClientMessageService();
        fileClientService = new FileClientService();
        group = new Group();
    }

    @Test
    @DisplayName("测试用户登录功能")
    void testUserLogin() {
        // 测试正确的用户名和密码
        assertTrue(toUserFunction.checkUser("testUser", "testPass"), "登录应该成功");
        
        // 测试错误的用户名和密码
        assertFalse(toUserFunction.checkUser("wrongUser", "wrongPass"), "登录应该失败");
    }

    @Test
    @DisplayName("测试在线用户列表请求")
    void testOnlineUserList() {
        // 假设已经登录成功
        toUserFunction.checkUser("testUser", "testPass");
        
        // 测试请求在线用户列表
        assertDoesNotThrow(() -> toUserFunction.reqOnlineUserList(), 
            "请求在线用户列表不应该抛出异常");
    }

    @Test
    @DisplayName("测试私聊消息发送")
    void testPrivateMessage() {
        String sender = "testUser";
        String receiver = "otherUser";
        String message = "Hello, this is a test message";
        
        assertDoesNotThrow(() -> 
            clientMessageService.sendMessageToOne(message, sender, receiver),
            "发送私聊消息不应该抛出异常");
    }

    @Test
    @DisplayName("测试群聊消息发送")
    void testGroupMessage() {
        String sender = "testUser";
        String groupName = "testGroup";
        String message = "Hello group!";
        
        assertDoesNotThrow(() -> 
            clientMessageService.sendMessageToGroup(message, sender, groupName),
            "发送群聊消息不应该抛出异常");
    }

    @Test
    @DisplayName("测试文件发送")
    void testFileSending() {
        String sender = "testUser";
        String receiver = "otherUser";
        String filePath = "test.txt";
        
        assertDoesNotThrow(() -> 
            fileClientService.sendFileToOne(filePath, sender, receiver),
            "发送文件不应该抛出异常");
    }

    @Test
    @DisplayName("测试群组创建")
    void testGroupCreation() {
        String userId = "testUser";
        
        assertDoesNotThrow(() -> 
            group.pullGroup(userId),
            "创建群组不应该抛出异常");
    }

    @Test
    @DisplayName("测试用户退出")
    void testUserLogout() {
        // 假设已经登录成功
        toUserFunction.checkUser("testUser", "testPass");
        
        assertDoesNotThrow(() -> 
            toUserFunction.logout(),
            "用户退出不应该抛出异常");
    }
}
