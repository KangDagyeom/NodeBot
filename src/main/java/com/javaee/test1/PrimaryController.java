package com.javaee.test1;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrimaryController {

    @FXML
    private Label resultLabel;

    @FXML
    private void handleMouseEnter(MouseEvent event) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), resultLabel);
        scaleUp.setToX(1.2); // Phóng to 20%
        scaleUp.setToY(1.2);
        scaleUp.play();
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), resultLabel);
        scaleDown.setToX(1); // Trả về kích thước ban đầu
        scaleDown.setToY(1);
        scaleDown.play();
    }

    @FXML
    private VBox chatBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextArea inputField;
    @FXML
    private Button sendButton;

    @FXML
    private ScrollPane scrollPane2;


    @FXML
    public void initialize() {
        // Đảm bảo VBox mở rộng tự động theo nội dung
        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0); // Luôn cuộn xuống dưới cùng khi có tin nhắn mới
        });
    }


    private void adjustInputHeight() {
        double minHeight = 80; // Chiều cao tối thiểu
        double maxHeight = 120; // Chiều cao tối đa
        double newHeight = inputField.getText().split("\n").length * 20 + minHeight;

        // Giới hạn chiều cao tối đa
        if (newHeight > maxHeight) newHeight = maxHeight;

        inputField.setPrefHeight(newHeight);
    }

    @FXML
    private void sendMessage() {
        String message = inputField.getText().trim();

        if (!message.isEmpty()) { // Kiểm tra chặn spam

            // Lấy thời gian hiện tại
            String timestamp = new SimpleDateFormat("HH:mm").format(new Date());

            // Tạo VBox chứa tin nhắn + thời gian
            VBox messageContainer = new VBox();
            messageContainer.setMaxWidth(300);
            messageContainer.setStyle("-fx-background-color: #2f2f2f; " + "-fx-padding: 10px; " + "-fx-border-radius: 10px; " + "-fx-background-radius: 10px;");

            // Label tin nhắn
            Label messageLabel = new Label(message);
            messageLabel.setMaxHeight(Region.USE_PREF_SIZE);
            scrollPane.vvalueProperty().bind(chatBox.heightProperty());
            messageLabel.setWrapText(true);


            messageLabel.setTextFill(Color.WHITE);


            // Label thời gian
            Label timeLabel = new Label(timestamp);
            timeLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 10px;"); // Màu xám
            timeLabel.setAlignment(Pos.CENTER_RIGHT);

            // Thêm vào VBox
            messageContainer.getChildren().addAll(messageLabel, timeLabel);
            VBox.setMargin(messageContainer, new Insets(5, 10, 5, 10));

            // Thêm vào chatBox
            chatBox.getChildren().add(messageContainer);
            chatBox.layout();

            Platform.runLater(() -> {
                scrollPane.vvalueProperty().unbind();
                scrollPane.setVvalue(1.0);
            });


            // Xóa input
            inputField.clear();


        }

    }


}
