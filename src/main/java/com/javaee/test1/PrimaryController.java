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
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrimaryController {

    @FXML
    private Label resultLabel;
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
    private List<String> responseChunks = new ArrayList<>();

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
    public void initialize() {
        scrollPane.setFitToWidth(true);

        // Đảm bảo VBox mở rộng theo nội dung
        chatBox.setMinHeight(Region.USE_PREF_SIZE);
        chatBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        chatBox.setFillWidth(true);

        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                scrollPane.setVvalue(1.0); // Cuộn xuống dòng cuối cùng
            });
        });
    }

    @FXML
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            String timestamp = new SimpleDateFormat("HH:mm").format(new Date());

            // Container chứa tin nhắn
            VBox messageContainer = new VBox();
            messageContainer.setMaxWidth(300);
            messageContainer.setStyle("-fx-background-color: #2f2f2f; -fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px;");


            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setTextFill(Color.WHITE);
            messageLabel.setMaxWidth(280);

            TextFlow textFlow = new TextFlow(messageLabel);

            textFlow.setMaxWidth(280);

            // Nhãn thời gian
            Label timeLabel = new Label(timestamp);
            timeLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 10px;");
            timeLabel.setAlignment(Pos.CENTER_RIGHT);

            messageContainer.getChildren().addAll(textFlow, timeLabel);
            VBox.setMargin(messageContainer, new Insets(5, 10, 5, 10));

            chatBox.getChildren().add(messageContainer);


            Platform.runLater(() -> {
                chatBox.requestLayout();
                scrollPane.setVvalue(1.0);
            });

            inputField.clear();
            sendResponse(message);
        }
    }

    @FXML
    private void sendResponse(String userMessage) {
        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());

        // Gửi yêu cầu API chạy trên một luồng khác để không bị treo giao diện
        new Thread(() -> {
            String botResponse = callOllamaAPI(userMessage);

            // Hiển thị phản hồi từ AI
            Platform.runLater(() -> addMessageToChat(botResponse, timestamp, false));
        }).start();
    }

    // Hàm gọi API Ollama
    private String callOllamaAPI(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject json = new JSONObject();
            json.put("model", "codellama:7b");
            json.put("prompt", prompt);

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:11434/api/generate")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json.toString())).build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("API Response: " + line);
                JSONObject jsonResponse = new JSONObject(line);

                if (jsonResponse.has("response")) {
                    responseChunks.add(jsonResponse.getString("response"));
                }

                if (jsonResponse.optBoolean("done", false)) {
                    String fullMessage = String.join("", responseChunks);
                    responseChunks.clear();
                    return fullMessage;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gọi API!";
        }
        return "Bot không có phản hồi!";
    }


    private void addMessageToChat(String message, String timestamp, boolean isUser) {
        VBox messageContainer = new VBox();
        messageContainer.setMaxWidth(700);
        messageContainer.setPadding(new Insets(10));
        messageContainer.setStyle("-fx-background-color: transparent;");

        // Tạo Label chứa text
        Label textLabel = new Label();
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(700);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        if (message.startsWith("```") && message.endsWith("```")) {
            // Tạo code block
            String codeContent = message.substring(3, message.length() - 3).trim();

            VBox codeContainer = new VBox();
            codeContainer.setStyle("-fx-background-color: #171717; -fx-padding: 10px; -fx-border-radius: 8px;");

            TextArea codeArea = new TextArea(codeContent);
            codeArea.setEditable(false);
            codeArea.setWrapText(false);
            codeArea.setMaxWidth(580);
            codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-background-color: #171717; -fx-text-fill: white;");

            ScrollPane scrollPane = new ScrollPane(codeArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(200);
            scrollPane.setStyle("-fx-background: #171717; -fx-padding: 10px;");

            codeContainer.getChildren().add(scrollPane);
            messageContainer.getChildren().add(codeContainer);
        } else {
            textLabel.setText(message);
            messageContainer.getChildren().add(textLabel);
        }


        // 🎯 Thêm tin nhắn vào giao diện
        Platform.runLater(() -> {
            chatBox.getChildren().add(messageContainer);
            scrollPane.setVvalue(1.0);
        });
    }


}
