package com.jinyu.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GroupChatDialog {
    public static GroupChatDialog currentInstance = null;
    private Stage dialogStage;
    private ChatUI chatUI;
    private TextField groupNameField;
    private ListView<String> groupList;
    private ObservableList<String> groups;
    private Button joinButton;
    private Button cancelButton;

    public GroupChatDialog(ChatUI chatUI) {
        this.chatUI = chatUI;
        this.groups = FXCollections.observableArrayList();
        // TODO: 从服务器获取群聊列表
        // 这里先添加一些测试数据
        groups.addAll("测试群1", "测试群2", "测试群3");
    }

    public void show() {
        // 弹窗显示前，主动请求群聊列表
        chatUI.requestGroupList();
        currentInstance = this;
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("选择群聊");

        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593); " +
                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建标题
        Label titleLabel = new Label("选择群聊");
        titleLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        // 创建内容区域
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 20;");

        // 群聊输入区域
        VBox inputBox = new VBox(10);
        inputBox.setAlignment(Pos.CENTER);

        Label groupNameLabel = new Label("群聊名称：");
        groupNameLabel.setFont(Font.font("Microsoft YaHei", 14));
        groupNameLabel.setTextFill(Color.WHITE);

        groupNameField = new TextField();
        groupNameField.setPromptText("请输入要加入的群聊名称");
        groupNameField.setFont(Font.font("Microsoft YaHei", 14));
        groupNameField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 8;");

        inputBox.getChildren().addAll(groupNameLabel, groupNameField);

        // 群聊列表区域
        VBox listBox = new VBox(10);
        listBox.setAlignment(Pos.CENTER);

        Label listLabel = new Label("现有群聊：");
        listLabel.setFont(Font.font("Microsoft YaHei", 14));
        listLabel.setTextFill(Color.WHITE);

        groupList = new ListView<>(groups);
        groupList.setPrefHeight(200);
        groupList.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1;");

        // 双击列表项时自动填充到输入框
        groupList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selectedGroup = groupList.getSelectionModel().getSelectedItem();
                if (selectedGroup != null) {
                    groupNameField.setText(selectedGroup);
                }
            }
        });

        listBox.getChildren().addAll(listLabel, groupList);

        // 底部按钮
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        joinButton = createStyledButton("加入群聊", "#3949ab");
        cancelButton = createStyledButton("取消", "#5c6bc0");
        buttonBox.getChildren().addAll(joinButton, cancelButton);

        // 添加所有组件到内容区域
        contentBox.getChildren().addAll(inputBox, listBox, buttonBox);

        // 添加组件到主布局
        mainLayout.getChildren().addAll(titleLabel, contentBox);

        // 创建场景
        Scene scene = new Scene(mainLayout, 400, 600);
        dialogStage.setScene(scene);

        // 设置事件处理
        setupEventHandlers();

        // 显示对话框
        dialogStage.showAndWait();
    }

    private void setupEventHandlers() {
        // 加入群聊按钮事件
        joinButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                showAlert("请输入群聊名称");
                return;
            }
            // 调用进入群聊的方法
            chatUI.joinGroup(groupName);
            dialogStage.close();
        });

        // 取消按钮事件
        cancelButton.setOnAction(e -> dialogStage.close());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 8 20; " +
                "-fx-cursor: hand;");

        // 添加悬停效果
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + color + "dd; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 8 20; " +
                "-fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 8 20; " +
                "-fx-cursor: hand;"));
        return button;
    }

    // 新增：更新群聊列表
    public void updateGroupList(java.util.List<String> groupNames) {
        groups.clear();
        groups.addAll(groupNames);
        if (groupList != null) {
            groupList.setItems(groups);
            groupList.refresh();
        }
    }
}