package com.jinyu.ui;

import java.util.Queue;

import com.jinyu.chatclient.service.ClientMessageService;
import com.jinyu.chatclient.service.ToUserFunction;
import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.User;
import com.jinyu.chatcommon.MessageType;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.File;
import java.io.ObjectOutputStream;
import com.jinyu.chatclient.service.ClientConnServerThreadsManage;

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
    private ToUserFunction toUserFunction;

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
        // 渐变背景：左上到右下浅蓝到深蓝
        mainLayout.setStyle(
                "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #e3f0ff, #2196f3 80%, #1565c0); " +
                        "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建顶部工具栏
        HBox toolbar = createToolbar();
        // 玻璃模糊效果（已移除）
        // toolbar.setEffect(new javafx.scene.effect.GaussianBlur(18));

        // 创建聊天区域
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("-fx-font-size: 14px; " +
                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                "-fx-background-color: rgba(255, 255, 255, 0.45); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        // chatArea.setEffect(new javafx.scene.effect.GaussianBlur(10));

        // 创建在线用户列表
        onlineUsersList = new ListView<>(onlineUsers);
        onlineUsersList.setPrefWidth(200);
        onlineUsersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        onlineUsersList.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                "-fx-background-color: rgba(255, 255, 255, 0.45); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        // 给onlineUserList添加监听事件，点击后设置当前聊天对象
        onlineUsersList.setOnMouseClicked(event -> {
            String selectedUser = onlineUsersList.getSelectionModel().getSelectedItem();
            if (selectedUser != null && !selectedUser.equals(user.getUserId())) {
                setCurrentChatTarget(selectedUser);
            }
        });
        // onlineUsersList.setEffect(new javafx.scene.effect.GaussianBlur(10));

        // 添加当前聊天对象显示
        currentChatTarget = new Label("当前聊天对象: 未选择");
        currentChatTarget.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                "-fx-background-color: rgba(255, 255, 255, 0.5); " +
                "-fx-padding: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        // 创建消息输入区域
        HBox inputArea = createInputArea();
        // inputArea.setEffect(new javafx.scene.effect.GaussianBlur(8));

        // 创建群组选择区域
        groupComboBox = new ComboBox<>();
        groupComboBox.setPromptText("选择群组");
        groupComboBox.setPrefWidth(200);
        groupComboBox.setVisible(false);
        groupComboBox.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', sans-serif; " +
                "-fx-background-color: rgba(255, 255, 255, 0.5); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        // 布局设置
        HBox contentArea = new HBox(15);
        VBox chatInfo = new VBox(10);
        chatInfo.getChildren().addAll(currentChatTarget, chatArea);
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        contentArea.getChildren().addAll(chatInfo, onlineUsersList);
        HBox.setHgrow(chatInfo, Priority.ALWAYS);

        mainLayout.getChildren().addAll(toolbar, contentArea, groupComboBox, inputArea);

        // 在mainLayout顶部叠加一层高光矩形，模拟玻璃反光
        javafx.scene.shape.Rectangle highlight = new javafx.scene.shape.Rectangle(800, 80);
        highlight.setFill(
                new javafx.scene.paint.LinearGradient(0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.rgb(255, 255, 255, 0.45)),
                        new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.rgb(255, 255, 255, 0.05))));
        highlight.setArcHeight(40);
        highlight.setArcWidth(40);
        highlight.setMouseTransparent(true);
        mainLayout.getChildren().add(0, highlight);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        // 设置默认背景
        try {
            java.net.URL bgUrl = getClass().getResource("/images/default_bg.jpg");
            if (bgUrl != null) {
                File bgFile = new File(bgUrl.toURI());
                setBackground(bgFile, 0.7); // 0.7为透明度
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        primaryStage.show();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 1;");

        Button refreshButton = createStyledButton("刷新用户列表", this::reqOnlineUserList);
        Button privateChatButton = createStyledButton("私聊", () -> {
        });
        Button groupChatButton = createStyledButton("群聊", () -> {
        });
        Button createGroupButton = createStyledButton("创建群组", () -> {
        });
        Button fileButton = createStyledButton("发送文件", () -> {
        });
        Button backgroundButton = createStyledButton("选择背景", () -> {
        });
        Button logoutButton = createStyledButton("退出", () -> {
        });

        toolbar.getChildren().addAll(refreshButton, privateChatButton, groupChatButton, createGroupButton, fileButton,
                backgroundButton, logoutButton);

        return toolbar;
    }

    private HBox createInputArea() {
        HBox inputArea = new HBox(15);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setPadding(new Insets(10));
        inputArea.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        messageInput = new TextField();
        messageInput.setPromptText("输入消息...");
        messageInput.setPrefWidth(500);
        messageInput.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                "-fx-background-color: rgba(255, 255, 255, 0.5); " +
                "-fx-text-fill: black; " +
                "-fx-prompt-text-fill: rgba(0, 0, 0, 0.7); " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 2; " +
                "-fx-padding: 8 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        Button sendButton = createStyledButton("发送", () -> {
            String content = messageInput.getText().trim();
            if (content.isEmpty())
                return;
            if (currentChatUserId != null) {
                // 私聊
                new ClientMessageService().sendMessageToOne(content, user.getUserId(), currentChatUserId);
            } else if (groupComboBox.isVisible() && groupComboBox.getValue() != null) {
                // 群聊
                new ClientMessageService().sendMessageToGroup(content, user.getUserId(), groupComboBox.getValue());
            } else {
                displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "请选择聊天对象或群组"));
                return;
            }
            messageInput.clear();
        });

        inputArea.getChildren().addAll(messageInput, sendButton);
        return inputArea;
    }

    private Button createStyledButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: linear-gradient(to right, #2196f3, #1976d2); " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-padding: 8 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        button.setOnAction(e -> action.run());

        // 添加悬停效果
        button.setOnMouseEntered(
                e -> button.setStyle("-fx-background-color: linear-gradient(to right, #42a5f5, #1e88e5); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                        "-fx-background-radius: 20; -fx-border-radius: 20; " +
                        "-fx-padding: 8 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 3);"));

        button.setOnMouseExited(
                e -> button.setStyle("-fx-background-color: linear-gradient(to right, #2196f3, #1976d2); " +
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
        if (chatArea == null)
            return; // 防御
        Platform.runLater(() -> {
            String displayText;
            if (MessageType.MESSAGE_SYSTEM.equals(message.getMesType())) {
                displayText = "[系统] " + message.getContent();
            } else {
                displayText = String.format("[%s] %s: %s", message.getSendTime(), message.getSender(),
                        message.getContent());
            }
            chatArea.appendText("\n" + displayText);
        });
    }

    public void setCurrentChatTarget(String userId) {
        this.currentChatUserId = userId;
        Platform.runLater(() -> {
            currentChatTarget.setText("当前聊天对象: " + userId);
            chatArea.clear();
            chatArea.appendText("开始与 " + userId + " 的私聊\n");
        });
    }

    public void setBackground(File imageFile, double opacity) {
        try {
            Image image = new Image(imageFile.toURI().toString());
            BackgroundImage backgroundImage = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true));

            // 创建一个半透明的背景
            Background background = new Background(backgroundImage);

            // 设置主布局的背景图片
            mainLayout.setBackground(background);

            // 设置主布局的样式（移除之前的背景色）
            mainLayout.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

            // 设置聊天区域的背景透明度
            chatArea.setStyle("-fx-font-size: 14px; " +
                    "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                    "-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                    "-fx-text-fill: black; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

            // 设置用户列表的背景透明度（玻璃效果）
            onlineUsersList.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                    "-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                    "-fx-text-fill: black; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

            // 设置工具栏的背景透明度
            HBox toolbar = (HBox) mainLayout.getChildren().get(0);
            toolbar.setStyle("-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                    "-fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

            // 设置输入区域的背景透明度
            HBox inputArea = (HBox) mainLayout.getChildren().get(3);
            inputArea.setStyle("-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                    "-fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

            // 设置消息输入框的背景透明度
            TextField messageInput = (TextField) inputArea.getChildren().get(0);
            messageInput.setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                    "-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                    "-fx-text-fill: black; " +
                    "-fx-prompt-text-fill: rgba(0, 0, 0, 0.7); " +
                    "-fx-background-radius: 20; -fx-border-radius: 20; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 2; " +
                    "-fx-padding: 8 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

            // 设置当前聊天对象标签的样式
            currentChatTarget.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold; " +
                    "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                    "-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                    "-fx-padding: 5; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reqOnlineUserList() {
        if (user == null) {
            displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "用户未登录，无法获取在线用户列表！"));
            return;
        }
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_REQ_ONLINE_USERS);
        message.setSender(user.getUserId());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ClientConnServerThreadsManage.getClientConnectServerThread(user.getUserId()).getSocket()
                            .getOutputStream());
            oos.writeObject(message);
        } catch (Exception e) {
            displayMessage(new Message(MessageType.MESSAGE_SYSTEM, "获取在线用户列表失败: " + e.getMessage()));
        }
    }

    public void setToUserFunction(ToUserFunction toUserFunction) {
        this.toUserFunction = toUserFunction;
    }

    public User getUser() {
        return this.user;
    }

    public ToUserFunction getToUserFunction() {
        return this.toUserFunction;
    }

    public static void main(String[] args) {
        launch(args);
    }
}