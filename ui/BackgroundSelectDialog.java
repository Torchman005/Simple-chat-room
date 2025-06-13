package com.jinyu.ui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BackgroundSelectDialog {
    private Stage dialogStage;
    private ChatUI chatUI;
    private Label filePathLabel;
    private File selectedFile;
    private Button selectFileButton;
    private Button applyButton;
    private Button cancelButton;
    private Slider opacitySlider;

    public BackgroundSelectDialog(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    public void show() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("选择聊天室背景");

        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593); " +
                          "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建标题
        Label titleLabel = new Label("选择聊天室背景");
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

        // 文件选择区域
        VBox fileBox = new VBox(10);
        fileBox.setAlignment(Pos.CENTER);
        
        Label fileLabel = new Label("选择背景图片：");
        fileLabel.setFont(Font.font("Microsoft YaHei", 14));
        fileLabel.setTextFill(Color.WHITE);
        
        filePathLabel = new Label("未选择图片");
        filePathLabel.setFont(Font.font("Microsoft YaHei", 14));
        filePathLabel.setTextFill(Color.WHITE);
        filePathLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                             "-fx-background-radius: 5; " +
                             "-fx-border-radius: 5; " +
                             "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                             "-fx-border-width: 1; " +
                             "-fx-padding: 8; " +
                             "-fx-min-width: 200;");
        
        selectFileButton = createStyledButton("浏览图片", "#3949ab");
        
        fileBox.getChildren().addAll(fileLabel, filePathLabel, selectFileButton);

        // 透明度调节区域
        VBox opacityBox = new VBox(10);
        opacityBox.setAlignment(Pos.CENTER);
        
        Label opacityLabel = new Label("背景透明度：");
        opacityLabel.setFont(Font.font("Microsoft YaHei", 14));
        opacityLabel.setTextFill(Color.WHITE);
        
        opacitySlider = new Slider(0.1, 1.0, 0.5);
        opacitySlider.setShowTickLabels(true);
        opacitySlider.setShowTickMarks(true);
        opacitySlider.setMajorTickUnit(0.2);
        opacitySlider.setMinorTickCount(1);
        opacitySlider.setSnapToTicks(true);
        opacitySlider.setStyle("-fx-control-inner-background: #3949ab;");
        
        opacityBox.getChildren().addAll(opacityLabel, opacitySlider);

        // 底部按钮
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        applyButton = createStyledButton("应用", "#3949ab");
        cancelButton = createStyledButton("取消", "#5c6bc0");
        buttonBox.getChildren().addAll(applyButton, cancelButton);

        // 添加所有组件到内容区域
        contentBox.getChildren().addAll(fileBox, opacityBox, buttonBox);

        // 添加组件到主布局
        mainLayout.getChildren().addAll(titleLabel, contentBox);

        // 创建场景
        Scene scene = new Scene(mainLayout, 400, 400);
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
            fileChooser.setTitle("选择背景图片");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File file = fileChooser.showOpenDialog(dialogStage);
            if (file != null) {
                selectedFile = file;
                filePathLabel.setText(file.getName());
            }
        });

        // 应用按钮事件
        applyButton.setOnAction(e -> {
            if (selectedFile == null) {
                showAlert("请选择背景图片");
                return;
            }
            
            double opacity = opacitySlider.getValue();
            chatUI.setBackground(selectedFile, opacity);
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