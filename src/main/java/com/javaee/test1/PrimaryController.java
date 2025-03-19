package com.javaee.test1;

import com.javaee.test1.controllers.ChatMessageDAO;
import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.models.ChatMessage;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
import java.util.UUID;

public class PrimaryController {
    ChatMessageDAO chatMessageDAO = new ChatMessageDAO();
    UserDAO userDAO = new UserDAO();
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
    private VBox conversationCon;

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
        loadConversations(UUID.fromString("882E2160-0204-F011-8D5F-B8AEEDBCAC42"));
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

            // Thêm tin nhắn user vào giao diện
            addMessageToChat(message, timestamp, true);

            // Lưu tin nhắn vào DB
            chatMessageDAO.saveMessageToDB(UUID.fromString("DFC9F96C-0304-F011-8D5F-B8AEEDBCAC42"),  // conversationId
                    UUID.fromString("882E2160-0204-F011-8D5F-B8AEEDBCAC42"),  // senderId
                    "user",  // senderType
                    message  // Nội dung tin nhắn
            );

            inputField.clear();

            // Gửi tin nhắn đến bot và nhận phản hồi
            sendResponse(message);
        }
    }

    @FXML
    private void sendResponse(String userMessage) {
        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());

        new Thread(() -> {
            String botResponse = callOllamaAPI(userMessage);

            // Lưu tin nhắn bot vào DB
            chatMessageDAO.saveMessageToDB(UUID.fromString("DFC9F96C-0304-F011-8D5F-B8AEEDBCAC42"),  // conversationId
                    UUID.fromString("882E2160-0204-F011-8D5F-B8AEEDBCAC42"),  // senderId
                    "bot",  // senderType
                    botResponse  // Nội dung tin nhắn
            );

            // Thêm tin nhắn bot vào giao diện
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

        if (isUser) {
            timestamp = new SimpleDateFormat("HH:mm").format(new Date());

            VBox messageContainer = new VBox();
            messageContainer.setMaxWidth(300);

            messageContainer.setStyle("-fx-background-color: #2f2f2f; -fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px;");


            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            messageLabel.setTextFill(Color.WHITE);
            messageLabel.setMaxWidth(280);

            TextFlow textFlow = new TextFlow(messageLabel);

            textFlow.setMaxWidth(280);


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
        } else {
            VBox messageContainer = new VBox();
            messageContainer.setMaxWidth(700);
            messageContainer.setPadding(new Insets(10));
            messageContainer.setStyle("-fx-background-color: transparent;");

            Label textLabel = new Label();
            textLabel.setWrapText(true);
            textLabel.setMaxWidth(700);
            textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
            textLabel.setText(message);
            messageContainer.getChildren().add(textLabel);
            Platform.runLater(() -> {
                chatBox.getChildren().add(messageContainer);
                scrollPane.setVvalue(1.0);
            });
        }


    }

    public void loadConversations(UUID userId) {
        ArrayList<String> conversationNames = userDAO.loadConversation(userId);
        conversationCon.getChildren().clear();
        for (String title : conversationNames) {
            Label conversationLabel = new Label(title);
            ImageView imageView1 = new ImageView(getClass().getResource("/img/Item.png").toExternalForm());
            ImageView imageView2 = new ImageView(getClass().getResource("/img/buttonpick.png").toExternalForm());

            conversationLabel.setTextFill(Color.WHITE);
            conversationLabel.setGraphic(imageView1);
            conversationLabel.setContentDisplay(ContentDisplay.LEFT);
            conversationLabel.setGraphicTextGap(10);
            conversationLabel.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");


            conversationLabel.setOnMouseEntered(event -> {
                conversationLabel.setGraphic(imageView2);
                conversationLabel.setContentDisplay(ContentDisplay.RIGHT);
            });


            conversationLabel.setOnMouseExited(event -> {
                conversationLabel.setGraphic(imageView1);
                conversationLabel.setContentDisplay(ContentDisplay.LEFT);
            });


            conversationLabel.setOnMouseClicked(event -> {
                loadChatHistory(UUID.fromString("DFC9F96C-0304-F011-8D5F-B8AEEDBCAC42"));

            });


            conversationCon.getChildren().add(conversationLabel);
        }

    }

    @FXML
    public void loadChatHistory(UUID conversationId) {
        chatBox.getChildren().clear();
        List<ChatMessage> messages = chatMessageDAO.getChatHistory(conversationId);
        for (ChatMessage msg : messages) {
            addMessageToChat(msg.getMessageText(), msg.getSentAt().toString(), msg.getSenderType().equals("user"));
        }
    }

}
