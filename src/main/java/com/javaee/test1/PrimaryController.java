package com.javaee.test1;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
    public void initialize() {
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        // Sự kiện tự điều chỉnh kích thước khi nhập văn bản
        inputField.textProperty().addListener((obs, oldText, newText) -> adjustInputHeight());

        // Sự kiện khi nhấn Enter sẽ gửi tin nhắn
        inputField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    event.consume(); // Ngăn xuống dòng mặc định
                    sendMessage();
                    break;
                default:
                    break;
            }
        });
    }

    private void adjustInputHeight() {
        double minHeight = 40; // Chiều cao tối thiểu
        double maxHeight = 120; // Chiều cao tối đa
        double newHeight = inputField.getText().split("\n").length * 20 + minHeight;

        // Giới hạn chiều cao tối đa
        if (newHeight > maxHeight) newHeight = maxHeight;

        inputField.setPrefHeight(newHeight);
    }

    @FXML
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            // Tạo nhãn tin nhắn
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-background-color: #E1FFC7; -fx-padding: 8px; -fx-background-radius: 10;");

            // Thêm tin nhắn vào chatBox
            chatBox.getChildren().add(messageLabel);

            // Xóa nội dung nhập vào và đặt lại chiều cao
            inputField.clear();
            adjustInputHeight();

            // Cuộn xuống tin nhắn mới nhất
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        }
    }
}
