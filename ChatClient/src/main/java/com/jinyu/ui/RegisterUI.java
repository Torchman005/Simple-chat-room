package com.jinyu.ui;

import com.jinyu.chatclient.service.ToUserFunction;
import com.jinyu.chatcommon.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterUI extends Application {
    private TextField userIdField;
    private PasswordField pwdField;
    private PasswordField confirmPwdField;
    private Button registerButton;
    private Button backButton;
    private Label statusLabel;
    private Stage primaryStage;
    private ToUserFunction toUserFunction;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.toUserFunction = new ToUserFunction();
        
        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593); " +
                          "-fx-font-family: 'Microsoft YaHei', 'SimHei', 'PingFang SC', sans-serif;");

        // 创建标题
        Label titleLabel = new Label("Simple-Chat-Room  Register");
        titleLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        // 创建表单容器
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-border-width: 1; " +
                        "-fx-padding: 30;");

        // 创建输入字段
        userIdField = createStyledTextField("用户ID");
        pwdField = createStyledPasswordField("密码");
        confirmPwdField = createStyledPasswordField("确认密码");

        // 创建按钮
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        registerButton = createStyledButton("注册", "#3949ab");
        backButton = createStyledButton("返回登录", "#5c6bc0");
        
        buttonBox.getChildren().addAll(registerButton, backButton);

        // 创建状态标签
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Microsoft YaHei", 14));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setStyle("-fx-padding: 10;");

        // 添加组件到表单
        formBox.getChildren().addAll(
            createInputGroup("用户ID", userIdField),
            createInputGroup("密码", pwdField),
            createInputGroup("确认密码", confirmPwdField),
            buttonBox,
            statusLabel
        );

        // 添加组件到主布局
        mainLayout.getChildren().addAll(titleLabel, formBox);

        // 创建场景
        Scene scene = new Scene(mainLayout, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // 设置舞台
        primaryStage.setTitle("聊天系统注册");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // 添加事件处理
        setupEventHandlers();
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setFont(Font.font("Microsoft YaHei", 14));
        field.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                      "-fx-text-fill: white; " +
                      "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                      "-fx-background-radius: 5; " +
                      "-fx-border-radius: 5; " +
                      "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                      "-fx-border-width: 1; " +
                      "-fx-padding: 10;");
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setFont(Font.font("Microsoft YaHei", 14));
        field.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                      "-fx-text-fill: white; " +
                      "-fx-prompt-text-fill: rgba(255, 255, 255, 0.7); " +
                      "-fx-background-radius: 5; " +
                      "-fx-border-radius: 5; " +
                      "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                      "-fx-border-width: 1; " +
                      "-fx-padding: 10;");
        return field;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-background-radius: 5; " +
                       "-fx-border-radius: 5; " +
                       "-fx-padding: 10 30; " +
                       "-fx-cursor: hand;");
        
        // 添加悬停效果
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + color + "dd; " +
                                                    "-fx-text-fill: white; " +
                                                    "-fx-background-radius: 5; " +
                                                    "-fx-border-radius: 5; " +
                                                    "-fx-padding: 10 30; " +
                                                    "-fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; " +
                                                   "-fx-text-fill: white; " +
                                                   "-fx-background-radius: 5; " +
                                                   "-fx-border-radius: 5; " +
                                                   "-fx-padding: 10 30; " +
                                                   "-fx-cursor: hand;"));
        return button;
    }

    private VBox createInputGroup(String label, javafx.scene.control.TextInputControl input) {
        VBox group = new VBox(5);
        Label inputLabel = new Label(label);
        inputLabel.setFont(Font.font("Microsoft YaHei", 14));
        inputLabel.setTextFill(Color.WHITE);
        group.getChildren().addAll(inputLabel, input);
        return group;
    }

    private void setupEventHandlers() {
        registerButton.setOnAction(e -> {
            String userId = userIdField.getText();
            String pwd = pwdField.getText();
            String confirmPwd = confirmPwdField.getText();
            
            if (userId.isEmpty() || pwd.isEmpty() || confirmPwd.isEmpty()) {
                statusLabel.setText("请填写所有字段");
                return;
            }

            if (!pwd.equals(confirmPwd)) {
                statusLabel.setText("两次输入的密码不一致");
                return;
            }

            User user = new User();
            user.setUserId(userId);
            user.setPwd(pwd);

            Task<Boolean> registerTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return toUserFunction.registerUser(user);
                }
            };

            registerTask.setOnSucceeded(event -> {
                boolean success = registerTask.getValue();
                if (success) {
                    statusLabel.setText("注册成功！");
                    // 延迟2秒后返回登录界面
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(() -> {
                                LoginUI loginUI = new LoginUI();
                                try {
                                    loginUI.start(new Stage());
                                    primaryStage.close();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                } else {
                    statusLabel.setText("注册失败：用户ID已存在");
                }
            });

            registerTask.setOnFailed(event -> {
                statusLabel.setText("注册失败：" + registerTask.getException().getMessage());
            });

            new Thread(registerTask).start();
        });

        backButton.setOnAction(e -> {
            LoginUI loginUI = new LoginUI();
            try {
                loginUI.start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
} 