package com.jinyu.ui;

import java.util.ArrayList;
import java.util.List;

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

public class CreateGroupDialog {
    private Stage dialogStage;
    private ChatUI chatUI;
    private TextField groupNameField;
    private TextField userIdField;
    private ListView<String> selectedUsersList;
    private ObservableList<String> selectedUsers;
    private Button addUserButton;
    private Button removeUserButton;
    private Button createButton;
    private Button cancelButton;

    public CreateGroupDialog(ChatUI chatUI) {
        this.chatUI = chatUI;
        this.selectedUsers = FXCollections.observableArrayList();
    }

    public void show() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("创建群聊");

        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593); " +
                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建标题
        Label titleLabel = new Label("创建群聊");
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

        // 群名称输入
        VBox groupNameBox = new VBox(5);
        Label groupNameLabel = new Label("群名称：");
        groupNameLabel.setFont(Font.font("Microsoft YaHei", 14));
        groupNameLabel.setTextFill(Color.WHITE);

        groupNameField = new TextField();
        groupNameField.setPromptText("请输入群名称");
        groupNameField.setFont(Font.font("Microsoft YaHei", 14));
        groupNameField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 8;");
        groupNameBox.getChildren().addAll(groupNameLabel, groupNameField);

        // 用户ID输入和添加
        HBox userInputBox = new HBox(10);
        userInputBox.setAlignment(Pos.CENTER_LEFT);

        Label userIdLabel = new Label("用户ID：");
        userIdLabel.setFont(Font.font("Microsoft YaHei", 14));
        userIdLabel.setTextFill(Color.WHITE);

        userIdField = new TextField();
        userIdField.setPromptText("请输入要添加的用户ID");
        userIdField.setFont(Font.font("Microsoft YaHei", 14));
        userIdField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 8;");

        addUserButton = createStyledButton("添加", "#3949ab");
        userInputBox.getChildren().addAll(userIdLabel, userIdField, addUserButton);

        // 已选用户列表
        VBox selectedUsersBox = new VBox(5);
        Label selectedUsersLabel = new Label("已选用户：");
        selectedUsersLabel.setFont(Font.font("Microsoft YaHei", 14));
        selectedUsersLabel.setTextFill(Color.WHITE);

        selectedUsersList = new ListView<>(selectedUsers);
        selectedUsersList.setPrefHeight(150);
        selectedUsersList.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1;");

        removeUserButton = createStyledButton("移除选中用户", "#5c6bc0");
        selectedUsersBox.getChildren().addAll(selectedUsersLabel, selectedUsersList, removeUserButton);

        // 底部按钮
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        createButton = createStyledButton("创建群聊", "#3949ab");
        cancelButton = createStyledButton("取消", "#5c6bc0");
        buttonBox.getChildren().addAll(createButton, cancelButton);

        // 添加所有组件到内容区域
        contentBox.getChildren().addAll(groupNameBox, userInputBox, selectedUsersBox, buttonBox);

        // 添加组件到主布局
        mainLayout.getChildren().addAll(titleLabel, contentBox);

        // 创建场景
        Scene scene = new Scene(mainLayout, 500, 600);
        dialogStage.setScene(scene);

        // 设置事件处理
        setupEventHandlers();

        // 显示对话框
        dialogStage.showAndWait();
    }

    private void setupEventHandlers() {
        // 添加用户按钮事件
        addUserButton.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            if (!userId.isEmpty() && !selectedUsers.contains(userId)) {
                selectedUsers.add(userId);
                userIdField.clear();
            }
        });

        // 移除用户按钮事件
        removeUserButton.setOnAction(e -> {
            String selectedUser = selectedUsersList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                selectedUsers.remove(selectedUser);
            }
        });

        // 创建群聊按钮事件
        createButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                showAlert("请输入群名称");
                return;
            }
            if (selectedUsers.isEmpty()) {
                showAlert("请至少添加一个群成员");
                return;
            }

            // 调用创建群聊的方法
            List<String> members = new ArrayList<>(selectedUsers);
            chatUI.createGroup(groupName, members);

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
}