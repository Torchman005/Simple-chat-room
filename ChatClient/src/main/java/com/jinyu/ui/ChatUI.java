package com.jinyu.ui;

import java.util.Queue;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatUI extends Application {
    private User user;
    private TextArea chatArea;
    private ListView<String> onlineUsersList;
    private TextField messageInput;
    private ComboBox<String> groupComboBox;
    private VBox mainLayout;
    private Stage primaryStage;
    private ObservableList<String> onlineUsers = FXCollections.observableArrayList();
    private Label currentChatTarget;
    private String currentChatUserId;

    public void setUser(User user) {
        this.user = user;
        if (primaryStage != null) {
            primaryStage.setTitle("聊天系统 - " + user.getUserId());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createMainUI();
    }

    private void createMainUI() {
        primaryStage.setTitle("聊天系统");

        // 创建主布局
        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593); " +
                          "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建顶部工具栏
        HBox toolbar = createToolbar();
        
        // 创建聊天区域
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("-fx-font-size: 14px; " +
                         "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                         "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                         "-fx-text-fill: white; " +
                         "-fx-background-radius: 10; " +
                         "-fx-border-radius: 10; " +
                         "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                         "-fx-border-width: 1;");

        // 创建在线用户列表
        onlineUsersList = new ListView<>(onlineUsers);
        onlineUsersList.setPrefWidth(200);
        onlineUsersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        onlineUsersList.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                               "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                               "-fx-text-fill: white; " +
                               "-fx-background-radius: 10; " +
                               "-fx-border-radius: 10; " +
                               "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                               "-fx-border-width: 1;");

        // 添加当前聊天对象显示
        currentChatTarget = new Label("当前聊天对象: 未选择");
        currentChatTarget.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                                 "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建消息输入区域
        HBox inputArea = createInputArea();

        // 创建群组选择区域
        groupComboBox = new ComboBox<>();
        groupComboBox.setPromptText("选择群组");
        groupComboBox.setPrefWidth(200);
        groupComboBox.setVisible(false);
        groupComboBox.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', sans-serif; " +
                             "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; " +
                             "-fx-background-radius: 10; -fx-border-radius: 10; " +
                             "-fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1;");

        // 布局设置
        HBox contentArea = new HBox(15);
        VBox chatInfo = new VBox(10);
        chatInfo.getChildren().addAll(currentChatTarget, chatArea);
        VBox.setVgrow(chatArea, Priority.ALWAYS);
        
        contentArea.getChildren().addAll(chatInfo, onlineUsersList);
        HBox.setHgrow(chatInfo, Priority.ALWAYS);

        mainLayout.getChildren().addAll(toolbar, contentArea, groupComboBox, inputArea);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                        "-fx-background-radius: 10; -fx-border-radius: 10; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 1;");

        Button refreshButton = createStyledButton("刷新用户列表", () -> {});
        Button privateChatButton = createStyledButton("私聊", () -> {});
        Button groupChatButton = createStyledButton("群聊", () -> {});
        Button createGroupButton = createStyledButton("创建群组", () -> {});
        Button fileButton = createStyledButton("发送文件", () -> {});
        Button logoutButton = createStyledButton("退出", () -> {});

        toolbar.getChildren().addAll(refreshButton, privateChatButton, groupChatButton, createGroupButton, fileButton, logoutButton);
        return toolbar;
    }

    private HBox createInputArea() {
        HBox inputArea = new HBox(15);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setPadding(new Insets(10));
        inputArea.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 10; -fx-border-radius: 10; " +
                          "-fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 1;");

        messageInput = new TextField();
        messageInput.setPromptText("输入消息...");
        messageInput.setPrefWidth(500);
        messageInput.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                            "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                            "-fx-text-fill: white; " +
                            "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                            "-fx-background-radius: 20; -fx-border-radius: 20; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.2); -fx-border-width: 1; " +
                            "-fx-padding: 8 15;");

        Button sendButton = createStyledButton("发送", () -> {});

        inputArea.getChildren().addAll(messageInput, sendButton);
        return inputArea;
    }

    private Button createStyledButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: linear-gradient(to right, #3949ab, #5c6bc0); " +
                       "-fx-text-fill: white; -fx-font-weight: bold; " +
                       "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                       "-fx-background-radius: 20; -fx-border-radius: 20; " +
                       "-fx-padding: 8 20; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        
        button.setOnAction(e -> action.run());
        
        // 添加悬停效果
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: linear-gradient(to right, #5c6bc0, #7986cb); " +
                                                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                                                    "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                                                    "-fx-background-radius: 20; -fx-border-radius: 20; " +
                                                    "-fx-padding: 8 20; " +
                                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 3);"));
        
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: linear-gradient(to right, #3949ab, #5c6bc0); " +
                                                   "-fx-text-fill: white; -fx-font-weight: bold; " +
                                                   "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                                                   "-fx-background-radius: 20; -fx-border-radius: 20; " +
                                                   "-fx-padding: 8 20; " +
                                                   "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"));
        
        return button;
    }

    public void updateOnlineUsers(Queue<String> users) {
        Platform.runLater(() -> {
            onlineUsers.clear();
            onlineUsers.addAll(users);
        });
    }

    public void displayMessage(Message message) {
        Platform.runLater(() -> {
            String displayText = message.getContent();
            if (!displayText.isEmpty()) {
                chatArea.appendText("\n" + displayText);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
} 