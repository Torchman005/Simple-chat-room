package com.jinyu.ui;

import java.util.Queue;
import java.util.Map;

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
import javafx.scene.layout.BorderPane;
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
    private HBox toolbar;
    private Map<String, java.lang.StringBuilder> sessionHistories = new java.util.HashMap<>();

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
        toolbar = createToolbar();
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
        onlineUsersList.setPrefHeight(400);
        onlineUsersList.setMinHeight(200);
        onlineUsersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // 美化：自定义CellFactory（淡蓝色卡片，透明度高，背景可见）
        onlineUsersList.setCellFactory(listView -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    boolean isSelf = user != null && item.equals(user.getUserId());
                    // 主色为淡蓝色，透明度高
                    String baseBg = "rgba(173,216,230,0.35)"; // #ADD8E6, 0.35透明度
                    String selfBg = "rgba(129,212,250,0.45)"; // #81d4fa, 0.45透明度
                    String selectedBg = "linear-gradient(to right, #42a5f5, #1976d2)";
                    String hoverBg = "rgba(144,202,249,0.55)"; // #90caf9, 0.55透明度
                    setStyle("-fx-background-color: " +
                            (isSelf ? selfBg : baseBg) + ";" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-radius: 14;" +
                            "-fx-border-color: rgba(33,150,243,0.10);" +
                            "-fx-border-width: 1.2;" +
                            "-fx-padding: 10 18;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;" +
                            (isSelf ? "-fx-text-fill: #1976d2; font-weight:bold;" : "-fx-text-fill: #1565c0;") +
                            "-fx-effect: dropshadow(gaussian, rgba(33,150,243,0.06), 6, 0, 0, 1);");
                    // 选中高亮为深蓝色渐变
                    if (isSelected()) {
                        setStyle(getStyle() + "-fx-background-color: " + selectedBg
                                + ";-fx-text-fill: white;font-weight:bold;");
                    }
                    // 悬浮高亮为更深的淡蓝色
                    setOnMouseEntered(e -> setStyle(
                            getStyle() + "-fx-background-color: " + hoverBg + ";-fx-text-fill: #1976d2;"));
                    setOnMouseExited(e -> {
                        setStyle("-fx-background-color: " +
                                (isSelf ? selfBg : baseBg) + ";" +
                                "-fx-background-radius: 14;" +
                                "-fx-border-radius: 14;" +
                                "-fx-border-color: rgba(33,150,243,0.10);" +
                                "-fx-border-width: 1.2;" +
                                "-fx-padding: 10 18;" +
                                "-fx-font-size: 15px;" +
                                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;" +
                                (isSelf ? "-fx-text-fill: #1976d2; font-weight:bold;" : "-fx-text-fill: #1565c0;") +
                                "-fx-effect: dropshadow(gaussian, rgba(33,150,243,0.06), 6, 0, 0, 1);");
                        if (isSelected()) {
                            setStyle(getStyle() + "-fx-background-color: " + selectedBg
                                    + ";-fx-text-fill: white;font-weight:bold;");
                        }
                    });
                }
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

        Scene scene = new Scene(mainLayout, 1000, 600);
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

        // 实现点击在线用户昵称直接私聊
        onlineUsersList.setOnMouseClicked(event -> {
            String selectedUser = onlineUsersList.getSelectionModel().getSelectedItem();
            if (selectedUser != null && !selectedUser.equals(user.getUserId())) {
                setCurrentChatTarget(selectedUser);
            }
        });
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
            // 弹出私聊选择框
            PrivateChatDialog dialog = new PrivateChatDialog(this);
            dialog.show();
        });
        Button groupChatButton = createStyledButton("群聊", () -> {
            // 弹出群聊列表对话框
            GroupChatDialog dialog = new GroupChatDialog(this);
            dialog.show();
        });
        Button createGroupButton = createStyledButton("创建群组", () -> {
            // 弹出创建群聊对话框
            CreateGroupDialog dialog = new CreateGroupDialog(this);
            dialog.show();
        });
        Button fileButton = createStyledButton("发送文件", () -> {
            FileSendDialog dialog = new FileSendDialog(this);
            dialog.show();
        });
        Button backgroundButton = createStyledButton("选择背景", () -> {
        });
        Button logoutButton = createStyledButton("退出", () -> {
            if (toUserFunction != null) {
                toUserFunction.logout();
            } else {
                // 兜底直接退出
                System.exit(0);
            }
            if (primaryStage != null) {
                primaryStage.close();
            }
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
                // 本地显示自己发的消息
                Message selfMsg = new Message(MessageType.MESSAGE_COMM_MES);
                selfMsg.setSender(user.getUserId());
                selfMsg.setGetter(currentChatUserId);
                selfMsg.setContent(content);
                selfMsg.setSendTime(new java.util.Date().toString());
                displayMessage(selfMsg);
            } else if (groupComboBox.isVisible() && groupComboBox.getValue() != null) {
                // 群聊
                new ClientMessageService().sendMessageToGroup(content, user.getUserId(), groupComboBox.getValue());
                // 本地显示自己发的群消息
                Message selfMsg = new Message(MessageType.MESSAGE_TO_GROUP_MES);
                selfMsg.setSender(user.getUserId());
                selfMsg.setGroupName(groupComboBox.getValue());
                selfMsg.setContent(content);
                selfMsg.setSendTime(new java.util.Date().toString());
                displayMessage(selfMsg);
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
        System.out.println("客户端刷新在线用户：" + users);
        Platform.runLater(() -> {
            onlineUsers.clear();
            if (users != null)
                onlineUsers.addAll(users);
            onlineUsersList.setItems(null); // 彻底解绑
            onlineUsersList.setItems(onlineUsers); // 重新绑定
            onlineUsersList.refresh();
            System.out.println("ListView实际显示项数：" + onlineUsersList.getItems().size());
        });
    }

    public void displayMessage(Message message) {
        if (chatArea == null)
            return;
        String sessionKey = getSessionKey(message);
        java.lang.StringBuilder history = sessionHistories.computeIfAbsent(sessionKey,
                k -> new java.lang.StringBuilder());
        String displayText;
        if (MessageType.MESSAGE_SYSTEM.equals(message.getMesType())) {
            displayText = "[系统] " + message.getContent();
        } else if (MessageType.MESSAGE_TO_GROUP_MES.equals(message.getMesType())) {
            displayText = String.format("[%s] %s@%s: %s", message.getSendTime(), message.getSender(),
                    message.getGroupName(), message.getContent());
        } else if (MessageType.MESSAGE_FILE_MES.equals(message.getMesType())) {
            String fileName = new java.io.File(message.getSrc()).getName();
            displayText = String.format("[%s] %s 发送文件: %s (%d bytes)", message.getSendTime(), message.getSender(),
                    fileName, message.getFileLen());
        } else {
            displayText = String.format("[%s] %s: %s", message.getSendTime(), message.getSender(),
                    message.getContent());
        }
        // 根据发送者决定左右显示
        String formatted = message.getSender() != null && message.getSender().equals(user.getUserId())
                ? rightAlign(displayText)
                : displayText;
        history.append("\n").append(formatted);
        // 判断当前会话是否匹配
        boolean shouldShow = false;
        if (MessageType.MESSAGE_TO_GROUP_MES.equals(message.getMesType())) {
            shouldShow = groupComboBox.isVisible() && message.getGroupName().equals(groupComboBox.getValue());
        } else if (MessageType.MESSAGE_COMM_MES.equals(message.getMesType())) {
            shouldShow = message.getSender().equals(currentChatUserId) || message.getGetter().equals(currentChatUserId);
        } else if (MessageType.MESSAGE_SYSTEM.equals(message.getMesType())) {
            shouldShow = true;
        }
        if (shouldShow) {
            javafx.application.Platform.runLater(() -> chatArea.appendText("\n" + formatted));
        }
        if (MessageType.MESSAGE_FILE_MES.equals(message.getMesType()) && message.getFileBytes() != null) {
            javafx.application.Platform.runLater(() -> saveFile(message));
        }
    }

    public void setCurrentChatTarget(String userId) {
        this.currentChatUserId = userId;
        Platform.runLater(() -> {
            currentChatTarget.setText("当前聊天对象: " + userId);
            chatArea.clear();
            String key = userId == null ? "SYS"
                    : (userId.compareTo(user.getUserId()) < 0 ? userId + "#" + user.getUserId()
                            : user.getUserId() + "#" + userId);
            chatArea.appendText(sessionHistories.getOrDefault(key, new java.lang.StringBuilder()).toString());
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
            if (toolbar != null) {
                toolbar.setStyle("-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                        "-fx-background-radius: 10; -fx-border-radius: 10; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                        "-fx-border-width: 2; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
            }

            // 设置输入区域的背景透明度
            HBox inputArea = null;
            for (javafx.scene.Node node : mainLayout.getChildren()) {
                if (node instanceof HBox && node != toolbar) {
                    inputArea = (HBox) node;
                }
            }
            if (inputArea != null) {
                inputArea.setStyle("-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                        "-fx-background-radius: 10; -fx-border-radius: 10; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                        "-fx-border-width: 2; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

                // 设置消息输入框的背景透明度
                for (javafx.scene.Node child : inputArea.getChildren()) {
                    if (child instanceof TextField) {
                        TextField messageInput = (TextField) child;
                        messageInput
                                .setStyle("-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif; " +
                                        "-fx-background-color: rgba(255, 255, 255, " + opacity + "); " +
                                        "-fx-text-fill: black; " +
                                        "-fx-prompt-text-fill: rgba(0, 0, 0, 0.7); " +
                                        "-fx-background-radius: 20; -fx-border-radius: 20; " +
                                        "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                                        "-fx-border-width: 2; " +
                                        "-fx-padding: 8 15; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
                    }
                }
            }

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
            System.out.println(currentChatTarget.getText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reqOnlineUserList() {
        if (user == null)
            return;
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

    /**
     * 创建群聊（被CreateGroupDialog调用）
     * 
     * @param groupName 群聊名称
     * @param members   群成员列表
     */
    public void createGroup(String groupName, java.util.List<String> members) {
        // 构造消息并发送到服务端
        com.jinyu.chatclient.service.Group groupService = new com.jinyu.chatclient.service.Group();
        java.util.Queue<String> groupMembers = new java.util.LinkedList<>();
        groupMembers.add(user.getUserId()); // 把自己加进去
        groupMembers.addAll(members);
        com.jinyu.chatcommon.Message mes = new com.jinyu.chatcommon.Message();
        mes.setMesType(com.jinyu.chatcommon.MessageType.MESSAGE_PULL_GROUP_MES);
        mes.setSender(user.getUserId());
        mes.setGroupName(groupName);
        mes.setGroupMembers(groupMembers);
        try {
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                    com.jinyu.chatclient.service.ClientConnServerThreadsManage
                            .getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            oos.writeObject(mes);
            displayMessage(new com.jinyu.chatcommon.Message(com.jinyu.chatcommon.MessageType.MESSAGE_SYSTEM,
                    "已发送创建群聊请求: " + groupName));
        } catch (Exception e) {
            displayMessage(new com.jinyu.chatcommon.Message(com.jinyu.chatcommon.MessageType.MESSAGE_SYSTEM,
                    "创建群聊失败: " + e.getMessage()));
        }
    }

    /**
     * 切换到群聊聊天界面
     * 
     * @param groupName 群聊名称
     */
    public void joinGroup(String groupName) {
        this.currentChatUserId = null;
        Platform.runLater(() -> {
            currentChatTarget.setText("当前群聊: " + groupName);
            chatArea.clear();
            groupComboBox.setVisible(true);
            if (!groupComboBox.getItems().contains(groupName)) {
                groupComboBox.getItems().add(groupName);
            }
            groupComboBox.setValue(groupName);
            String key = "G:" + groupName;
            chatArea.appendText(sessionHistories.getOrDefault(key, new java.lang.StringBuilder()).toString());
        });
    }

    // 主动请求群聊列表
    public void requestGroupList() {
        try {
            com.jinyu.chatcommon.Message mes = new com.jinyu.chatcommon.Message();
            mes.setMesType(com.jinyu.chatcommon.MessageType.MESSAGE_REQ_GROUP_LIST);
            mes.setSender(user.getUserId());
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                    com.jinyu.chatclient.service.ClientConnServerThreadsManage
                            .getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            oos.writeObject(mes);
        } catch (Exception e) {
            displayMessage(new com.jinyu.chatcommon.Message(com.jinyu.chatcommon.MessageType.MESSAGE_SYSTEM,
                    "请求群聊列表失败: " + e.getMessage()));
        }
    }

    // 处理服务端返回的群聊列表
    public void handleGroupListMsg(String groupListStr) {
        java.util.List<String> groupNames = new java.util.ArrayList<>();
        if (groupListStr != null && !groupListStr.isEmpty()) {
            for (String name : groupListStr.split(",")) {
                if (!name.trim().isEmpty())
                    groupNames.add(name.trim());
            }
        }
        // 分发给所有GroupChatDialog实例（这里只处理当前弹窗）
        if (com.jinyu.ui.GroupChatDialog.currentInstance != null) {
            com.jinyu.ui.GroupChatDialog.currentInstance.updateGroupList(groupNames);
        }
    }

    private String getSessionKey(Message message) {
        if (MessageType.MESSAGE_TO_GROUP_MES.equals(message.getMesType())) {
            return "G:" + message.getGroupName();
        } else if (MessageType.MESSAGE_COMM_MES.equals(message.getMesType())) {
            // 私聊：会话键统一为两人中较小-较大的拼接，确保双方一致
            String a = message.getSender();
            String b = message.getGetter();
            if (a == null || b == null)
                return "SYS";
            return a.compareTo(b) < 0 ? a + "#" + b : b + "#" + a;
        } else {
            return "SYS";
        }
    }

    // 简单右对齐：在前面补足空格到固定宽度
    private String rightAlign(String text) {
        int width = 80; // 依据字体大致列宽，可调整
        int len = text.length();
        if (len >= width)
            return text; // 太长不处理
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width - len; i++)
            sb.append(' ');
        sb.append(text);
        return sb.toString();
    }

    private void saveFile(Message message) {
        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle("保存文件 - " + message.getSrc());
        chooser.setInitialFileName(new java.io.File(message.getSrc()).getName());
        java.io.File dest = chooser.showSaveDialog(primaryStage);
        if (dest == null)
            return; // 取消
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
            fos.write(message.getFileBytes(), 0, message.getFileLen());
            displayMessage(new com.jinyu.chatcommon.Message(com.jinyu.chatcommon.MessageType.MESSAGE_SYSTEM,
                    "文件已保存到 " + dest.getAbsolutePath()));
        } catch (Exception e) {
            displayMessage(new com.jinyu.chatcommon.Message(com.jinyu.chatcommon.MessageType.MESSAGE_SYSTEM,
                    "保存文件失败: " + e.getMessage()));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}