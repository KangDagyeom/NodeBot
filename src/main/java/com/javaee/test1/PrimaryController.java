package com.javaee.test1;

import com.javaee.test1.controllers.ChatMessageDAO;
import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.models.ChatMessage;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        loadConversations(UUID.fromString("6626B948-A305-F011-8D62-B8AEEDBCAC42"));
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
            addMessageToChat(message, timestamp, true, false);

            // Lưu tin nhắn vào DB
            chatMessageDAO.saveMessageToDB(UUID.fromString("6926B948-A305-F011-8D62-B8AEEDBCAC42"), // conversationId
                    UUID.fromString("6626B948-A305-F011-8D62-B8AEEDBCAC42"), // senderId
                    "user", // senderType
                    message // Nội dung tin nhắn
            );

            inputField.clear();

            // Gửi tin nhắn đến bot và nhận phản hồi
            sendResponse(message);
        }
    }

    @FXML
    private void sendResponse(String userMessage) {

        new Thread(() -> {
            callOllamaAPI(userMessage);
        }).start();
    }

    private void callOllamaAPI(String prompt) {
        responseChunks.clear();
        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject json = new JSONObject();
            json.put("model", "codellama:7b");
            json.put("prompt", prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));

            VBox botMessageContainer = new VBox();
            botMessageContainer.setMaxWidth(700);
            botMessageContainer.setPadding(new Insets(10));
            botMessageContainer.setStyle("-fx-background-color: transparent;");

            Label botLabel = new Label();
            botLabel.setWrapText(true);
            botLabel.setMaxWidth(700);
            botLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

            ImageView copyImageView = new ImageView(new Image(getClass().getResource("/img/btnCopy.png").toExternalForm()));
            copyImageView.setFitWidth(16);
            copyImageView.setFitHeight(16);

            ImageView likeImageView = new ImageView(new Image(getClass().getResource("/img/Like.png").toExternalForm()));
            likeImageView.setFitWidth(16);
            likeImageView.setFitHeight(16);

            ImageView dislikeImageView = new ImageView(new Image(getClass().getResource("/img/Dislike.png").toExternalForm()));
            dislikeImageView.setFitWidth(16);
            dislikeImageView.setFitHeight(16);

            Button btnCopy = new Button();
            btnCopy.setGraphic(copyImageView);
            btnCopy.setCursor(Cursor.HAND);
            btnCopy.setStyle("-fx-background-color: transparent; -fx-margin:0; -fx-padding:5px;");
            btnCopy.setStyle(
                    "-fx-background-color: transparent; -fx-padding: 5px;" +
                            "-fx-background-radius: 5px;"
            );
            btnCopy.setOnMouseEntered(e -> btnCopy.setStyle("-fx-background-color: #2c2c2c; -fx-padding: 5px; -fx-background-radius: 10px;"));
            btnCopy.setOnMouseExited(e -> btnCopy.setStyle("-fx-background-color: transparent; -fx-padding: 5px;"));

            Button btnLike = new Button();
            btnLike.setGraphic(likeImageView);
            btnLike.setCursor(Cursor.HAND);
            btnLike.setStyle("-fx-background-color: transparent; -fx-margin:0; -fx-padding:5px;");

            Button btnDislike = new Button();
            btnDislike.setGraphic(dislikeImageView);
            btnDislike.setCursor(Cursor.HAND);
            btnDislike.setStyle("-fx-background-color: transparent; -fx-margin:0; -fx-padding:5px;");
            btnLike.setOnMouseEntered(e -> btnLike.setStyle("-fx-background-color: #2c2c2c; -fx-padding: 5px; -fx-background-radius: 10px;"));
            btnLike.setOnMouseExited(e -> btnLike.setStyle("-fx-background-color: transparent; -fx-padding: 5px; "));

            btnDislike.setOnMouseEntered(e -> btnDislike.setStyle("-fx-background-color: #2c2c2c; -fx-padding: 5px; -fx-background-radius: 10px;"));
            btnDislike.setOnMouseExited(e -> btnDislike.setStyle("-fx-background-color: transparent; -fx-padding: 5px; "));

            HBox buttonContainer = new HBox(5, btnCopy, btnLike, btnDislike);

            buttonContainer.setSpacing(0);
            buttonContainer.setAlignment(Pos.BOTTOM_LEFT);

            botMessageContainer.getChildren().addAll(botLabel, buttonContainer);

            Platform.runLater(() -> {
                chatBox.getChildren().add(botMessageContainer);
                scrollPane.setVvalue(1.0);
            });

            StringBuilder responseText = new StringBuilder();
            List<String> buffer = new ArrayList<>();

            // Tạo Timeline cập nhật giao diện mỗi 50ms
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                if (!buffer.isEmpty()) {
                    responseText.append(String.join("", buffer));
                    botLabel.setText(responseText.toString());
                    responseChunks.addAll(buffer);
                    buffer.clear();
                }
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject jsonResponse = new JSONObject(line);
                System.out.println("Bot API response: " + jsonResponse);
                if (jsonResponse.has("response")) {
                    String chunk = jsonResponse.getString("response");
                    buffer.add(chunk);
                }

                if (jsonResponse.optBoolean("done", false)) {
                    timeline.stop(); // Dừng Timeline khi nhận xong dữ liệu
                    String botResponse = String.join("", responseChunks);
                    btnCopy.setOnAction(event -> {
                        ImageView copiedImageView = new ImageView(new Image(getClass().getResource("/img/btnCopied.png").toExternalForm()));
                        copiedImageView.setFitWidth(20);
                        copiedImageView.setFitHeight(20);
                        btnCopy.setGraphic(copiedImageView);

                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(botResponse);
                        clipboard.setContent(content);
                        System.out.println("Copied: " + botResponse);

                        // Tạo hiệu ứng đợi 3 giây
                        PauseTransition pause = new PauseTransition(Duration.seconds(3));
                        pause.setOnFinished(e -> {


                            copyImageView.setFitWidth(16);
                            copyImageView.setFitHeight(16);
                            btnCopy.setGraphic(copyImageView);
                        });
                        pause.play();
                    });

                    // 🔹 Xử lý code block
                    Platform.runLater(() -> processBotResponse(botResponse, botMessageContainer));

                    chatMessageDAO.saveMessageToDB(UUID.fromString("6926B948-A305-F011-8D62-B8AEEDBCAC42"),
                            UUID.fromString("6626B948-A305-F011-8D62-B8AEEDBCAC42"),
                            "bot",
                            botResponse);

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> addMessageToChat("Lỗi khi gọi API!", "", false, true));
        }
    }

    private void processBotResponse(String message, VBox botMessageContainer) {
        String regex = "```(.*?)```";  // Regex tìm code block
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message);

        int lastEnd = 0;

        while (matcher.find()) {

            String codeBlock = matcher.group(1).trim();

            TextArea codeArea = new TextArea(codeBlock);
            codeArea.setWrapText(true);
            codeArea.setMaxWidth(700);
            codeArea.setEditable(false);
            codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-background-color: #282c34; -fx-text-fill: #ffffff;");
            botMessageContainer.getChildren().add(codeArea);

            lastEnd = matcher.end();
        }

    }

    private void addMessageToChat(String message, String timestamp, boolean isUser, boolean isFetch) {
        if (!isFetch) {
            timestamp = new SimpleDateFormat("HH:mm").format(new Date());
        }
        if (isUser) {

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

            Platform.runLater(() -> {
                chatBox.getChildren().add(messageContainer);
                chatBox.requestLayout();
                scrollPane.setVvalue(1.0);
            });
        }
        if (!isUser) {
            VBox messageContainer = new VBox();
            messageContainer.setMaxWidth(700);
            messageContainer.setPadding(new Insets(10));
            messageContainer.setStyle("-fx-background-color: transparent;");

            if (message.contains("```")) {
                // Xử lý nội dung code block
                StringBuilder formattedMessage = new StringBuilder();
                boolean isCodeBlock = false;

                for (String line : message.split("\n")) {
                    if (line.trim().startsWith("```") && line.trim().endsWith("```")) {
                        isCodeBlock = !isCodeBlock; // Bật/tắt chế độ code block
                        continue; // Bỏ qua dòng ``` trong hiển thị
                    }

                    if (isCodeBlock) {
                        formattedMessage.append(line).append("\n");
                    }
                }

                TextArea codeArea = new TextArea(formattedMessage.toString().trim());
                codeArea.setEditable(false);
                codeArea.setWrapText(true);
                codeArea.setMaxWidth(680);
                codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13px; -fx-background-color: #2e2e2e; -fx-text-fill: #00ff00;");
                messageContainer.getChildren().add(codeArea);

            } else {
                // Xử lý tin nhắn bình thường
                Label textLabel = new Label();
                textLabel.setWrapText(true);
                textLabel.setMaxWidth(700);
                textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
                textLabel.setText(message);
                messageContainer.getChildren().add(textLabel);
            }
            ImageView copyImageView = new ImageView(new Image(getClass().getResource("/img/btnCopy.png").toExternalForm()));
            copyImageView.setFitWidth(16);
            copyImageView.setFitHeight(16);

            ImageView likeImageView = new ImageView(new Image(getClass().getResource("/img/Like.png").toExternalForm()));
            likeImageView.setFitWidth(16);
            likeImageView.setFitHeight(16);

            ImageView dislikeImageView = new ImageView(new Image(getClass().getResource("/img/Dislike.png").toExternalForm()));
            dislikeImageView.setFitWidth(16);
            dislikeImageView.setFitHeight(16);

            Button btnCopy = new Button();
            btnCopy.setGraphic(copyImageView);
            btnCopy.setCursor(Cursor.HAND);
            btnCopy.setStyle("-fx-background-color: transparent; -fx-margin:0; -fx-padding:5px;");
            btnCopy.setStyle(
                    "-fx-background-color: transparent; -fx-padding: 5px;" +
                            "-fx-background-radius: 5px;"
            );
            btnCopy.setOnMouseEntered(e -> btnCopy.setStyle("-fx-background-color: #2c2c2c; -fx-padding: 5px; -fx-background-radius: 10px;"));
            btnCopy.setOnMouseExited(e -> btnCopy.setStyle("-fx-background-color: transparent; -fx-padding: 5px;"));

            Button btnLike = new Button();
            btnLike.setGraphic(likeImageView);
            btnLike.setCursor(Cursor.HAND);
            btnLike.setStyle("-fx-background-color: transparent; -fx-margin:0; -fx-padding:5px;");

            Button btnDislike = new Button();
            btnDislike.setGraphic(dislikeImageView);
            btnDislike.setCursor(Cursor.HAND);
            btnDislike.setStyle("-fx-background-color: transparent; -fx-margin:0; -fx-padding:5px;");
            btnLike.setOnMouseEntered(e -> btnLike.setStyle("-fx-background-color: #2c2c2c; -fx-padding: 5px; -fx-background-radius: 10px;"));
            btnLike.setOnMouseExited(e -> btnLike.setStyle("-fx-background-color: transparent; -fx-padding: 5px; "));

            btnDislike.setOnMouseEntered(e -> btnDislike.setStyle("-fx-background-color: #2c2c2c; -fx-padding: 5px; -fx-background-radius: 10px;"));
            btnDislike.setOnMouseExited(e -> btnDislike.setStyle("-fx-background-color: transparent; -fx-padding: 5px; "));


            HBox buttonContainer = new HBox(5, btnCopy, btnLike, btnDislike);

            buttonContainer.setSpacing(0);
            buttonContainer.setAlignment(Pos.BOTTOM_LEFT);
            messageContainer.getChildren().add(buttonContainer);
            btnCopy.setOnAction(event -> {
                ImageView copiedImageView = new ImageView(new Image(getClass().getResource("/img/btnCopied.png").toExternalForm()));
                copiedImageView.setFitWidth(20);
                copiedImageView.setFitHeight(20);
                btnCopy.setGraphic(copiedImageView);

                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(message);
                clipboard.setContent(content);
                System.out.println("Copied: " + message);

                // Tạo hiệu ứng đợi 3 giây
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(e -> {


                    copyImageView.setFitWidth(16);
                    copyImageView.setFitHeight(16);
                    btnCopy.setGraphic(copyImageView);
                });
                pause.play();
            });

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
                loadChatHistory(UUID.fromString("6926B948-A305-F011-8D62-B8AEEDBCAC42"));

            });

            conversationCon.getChildren().add(conversationLabel);
        }

    }

    @FXML
    public void loadChatHistory(UUID conversationId) {
        chatBox.getChildren().clear();

        List<ChatMessage> messages = chatMessageDAO.getChatHistory(conversationId);
        messages.sort(Comparator.comparing(ChatMessage::getSentAt));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        CompletableFuture.runAsync(() -> {
            for (ChatMessage msg : messages) {
                boolean isUser = msg.getSenderType().equalsIgnoreCase("user");

                try {
                    // Đợi một chút để đảm bảo JavaFX vẽ UI theo thứ tự user → bot
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> addMessageToChat(
                        msg.getMessageText(),
                        msg.getSentAt().toLocalDateTime().format(formatter),
                        isUser,
                        true
                ));
            }
        });
    }

}
