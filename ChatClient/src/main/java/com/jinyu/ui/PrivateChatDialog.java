package com.jinyu.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PrivateChatDialog {
    private Stage dialogStage;
    private String selectedUserId;
    private ChatUI chatUI;

    public PrivateChatDialog(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    public void show() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("选择私聊对象");

        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593); " +
                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建标题
        Label titleLabel = new Label("选择私聊对象");
        titleLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        // 创建输入区域
        VBox inputBox = new VBox(10);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 20;");

        Label promptLabel = new Label("请输入对方用户ID：");
        promptLabel.setFont(Font.font("Microsoft YaHei", 14));
        promptLabel.setTextFill(Color.WHITE);

        TextField userIdField = new TextField();
        userIdField.setPromptText("用户ID");
        userIdField.setFont(Font.font("Microsoft YaHei", 14));
        userIdField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 8;");

        // 创建按钮区域
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = createStyledButton("确认", "#3949ab");
        Button cancelButton = createStyledButton("取消", "#5c6bc0");

        buttonBox.getChildren().addAll(confirmButton, cancelButton);

        // 添加组件到输入区域
        inputBox.getChildren().addAll(promptLabel, userIdField, buttonBox);

        // 添加组件到主布局
        mainLayout.getChildren().addAll(titleLabel, inputBox);

        // 创建场景
        Scene scene = new Scene(mainLayout, 400, 300);
        dialogStage.setScene(scene);

        // 设置按钮事件
        confirmButton.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            if (!userId.isEmpty()) {
                selectedUserId = userId;
                chatUI.setCurrentChatTarget(userId);
                dialogStage.close();
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());

        // 显示对话框
        dialogStage.showAndWait();
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