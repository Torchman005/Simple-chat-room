package com.jinyu.ui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FileSendDialog {
    private Stage dialogStage;
    private ChatUI chatUI;
    private TextField userIdField;
    private Label filePathLabel;
    private File selectedFile;
    private Button selectFileButton;
    private Button sendButton;
    private Button cancelButton;

    public FileSendDialog(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    public void show() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("发送文件");

        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593); " +
                "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建标题
        Label titleLabel = new Label("发送文件");
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

        // 用户ID输入区域
        VBox userIdBox = new VBox(5);
        userIdBox.setAlignment(Pos.CENTER);

        Label userIdLabel = new Label("接收者ID：");
        userIdLabel.setFont(Font.font("Microsoft YaHei", 14));
        userIdLabel.setTextFill(Color.WHITE);

        userIdField = new TextField();
        userIdField.setPromptText("请输入接收者用户ID");
        userIdField.setFont(Font.font("Microsoft YaHei", 14));
        userIdField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 8;");

        userIdBox.getChildren().addAll(userIdLabel, userIdField);

        // 文件选择区域
        VBox fileBox = new VBox(10);
        fileBox.setAlignment(Pos.CENTER);

        Label fileLabel = new Label("选择文件：");
        fileLabel.setFont(Font.font("Microsoft YaHei", 14));
        fileLabel.setTextFill(Color.WHITE);

        filePathLabel = new Label("未选择文件");
        filePathLabel.setFont(Font.font("Microsoft YaHei", 14));
        filePathLabel.setTextFill(Color.WHITE);
        filePathLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-padding: 8; " +
                "-fx-min-width: 200;");

        selectFileButton = createStyledButton("浏览文件", "#3949ab");

        fileBox.getChildren().addAll(fileLabel, filePathLabel, selectFileButton);

        // 底部按钮
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        sendButton = createStyledButton("发送", "#3949ab");
        cancelButton = createStyledButton("取消", "#5c6bc0");
        buttonBox.getChildren().addAll(sendButton, cancelButton);

        // 添加所有组件到内容区域
        contentBox.getChildren().addAll(userIdBox, fileBox, buttonBox);

        // 添加组件到主布局
        mainLayout.getChildren().addAll(titleLabel, contentBox);

        // 创建场景
        Scene scene = new Scene(mainLayout, 400, 500);
        dialogStage.setScene(scene);

        // 设置事件处理
        setupEventHandlers();

        // 显示对话框
        dialogStage.showAndWait();
    }

    private void setupEventHandlers() {
        // 选择文件按钮事件
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择要发送的文件");
            File file = fileChooser.showOpenDialog(dialogStage);
            if (file != null) {
                selectedFile = file;
                filePathLabel.setText(file.getName());
            }
        });

        // 发送按钮事件
        sendButton.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            if (userId.isEmpty()) {
                showAlert("请输入接收者用户ID");
                return;
            }
            if (selectedFile == null) {
                showAlert("请选择要发送的文件");
                return;
            }
            try {
                new com.jinyu.chatclient.service.FileClientService().sendFileToOne(selectedFile.getAbsolutePath(),
                        chatUI.getUser().getUserId(), userId);
                chatUI.displayMessage(new com.jinyu.chatcommon.Message(com.jinyu.chatcommon.MessageType.MESSAGE_SYSTEM,
                        "文件已发送: " + selectedFile.getName()));
            } catch (Exception ex) {
                chatUI.displayMessage(new com.jinyu.chatcommon.Message(com.jinyu.chatcommon.MessageType.MESSAGE_SYSTEM,
                        "发送文件失败: " + ex.getMessage()));
            }
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