package com.javaee.test1;

import com.javaee.test1.controllers.ChatMessageDAO;
import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
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
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryController {

    ChatMessageDAO chatMessageDAO = new ChatMessageDAO();
    UserDAO userDAO = new UserDAO();
    UserSession session = UserSession.getInstance();
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
    private Label lbusername;
    @FXML
    private Label lbuserplan;
    @FXML
    private ImageView avatar;
    @FXML
    private ImageView btnSearch;
    @FXML
    private ImageView btnTranslate;
    private String saveTitle;
    private AsyncHttpClient client = new DefaultAsyncHttpClient();

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

        lbusername.setText(session.getUsername());
        lbuserplan.setText(session.getSubscriptionPlan());
        System.out.println("Username: " + session.getUsername());
        System.out.println("Avatar: " + session.getAvatar());
        System.out.println("Subscription Plan: " + session.getSubscriptionPlan());

        scrollPane.setFitToWidth(true);
        loadConversations(userDAO.getUserIdByUsername(session.getUsername()));
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
            chatMessageDAO.saveMessageToDB(userDAO.getConversationIdByTitle(saveTitle), // conversationId
                    userDAO.getUserIdByUsername(session.getUsername()), // senderId
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

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:11434/api/generate")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json.toString())).build();

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
            btnCopy.setStyle("-fx-background-color: transparent; -fx-padding: 5px;" + "-fx-background-radius: 5px;");
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

                    chatMessageDAO.saveMessageToDB(userDAO.getConversationIdByTitle(saveTitle), userDAO.getUserIdByUsername(session.getUsername()), "bot", botResponse);

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
            btnCopy.setStyle("-fx-background-color: transparent; -fx-padding: 5px;" + "-fx-background-radius: 5px;");
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
                saveTitle = conversationLabel.getText();
                loadChatHistory(userDAO.getConversationIdByTitle(saveTitle));

                System.out.println(saveTitle);
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

                Platform.runLater(() -> addMessageToChat(msg.getMessageText(), msg.getSentAt().toLocalDateTime().format(formatter), isUser, true));
            }
        });
    }

    @FXML
    private void searchAction() {
        String userInput = inputField.getText().trim(); // Lấy nội dung nhập vào

        if (!userInput.isEmpty()) {
            addMessageToChat(userInput, "", true, false); // Hiển thị tin nhắn của người dùng
            inputField.clear(); // Xóa ô nhập

            // Gửi tin nhắn đến API
            sendRequestToAPI(userInput);
        }
    }

    private void sendRequestToAPI(String query) {
        client.prepare("POST", "https://google-api-unlimited.p.rapidapi.com/search_image")
                .setHeader("x-rapidapi-key", "79e9925fedmsh5ece400febbd5d0p1e7721jsna398b5210fa7")
                .setHeader("x-rapidapi-host", "google-api-unlimited.p.rapidapi.com")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setBody("query=" + URLEncoder.encode(query, StandardCharsets.UTF_8)) // Encode query
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    String responseBody = response.getResponseBody();
                    ArrayList<String> imageUrls = extractMessageFromJson(responseBody);
                    System.out.println("Response: " + responseBody);

                    Platform.runLater(() -> {
                        if (!imageUrls.isEmpty()) {
                            VBox mainContainer = new VBox(10);
                            mainContainer.setAlignment(Pos.CENTER_LEFT);

                            HBox currentRow = new HBox(10);
                            currentRow.setAlignment(Pos.CENTER_LEFT);

                            for (int i = 0; i < imageUrls.size(); i++) {
                                String imageUrl = imageUrls.get(i);
                                ImageView imageView = new ImageView(new Image(imageUrl, true));
                                imageView.setFitWidth(200);
                                imageView.setFitHeight(200);

                                imageView.setPreserveRatio(true);

                                currentRow.getChildren().add(imageView);


                                if ((i + 1) % 3 == 0 || i == imageUrls.size() - 1) {
                                    mainContainer.getChildren().add(currentRow);
                                    currentRow = new HBox(10);
                                    currentRow.setAlignment(Pos.CENTER_LEFT);
                                }

                            }
                            StringBuilder sb = new StringBuilder();
                            for (String item : imageUrls) {
                                sb.append(item).append(" | ");
                            }

                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 3);
                            }

                            String result = sb.toString();
                            System.out.println(result);

                            addImageToChat(mainContainer, "", false, false);
                            chatMessageDAO.saveMessageToDB(userDAO.getConversationIdByTitle(saveTitle), userDAO.getUserIdByUsername(session.getUsername()), "bot", result);
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> addMessageToChat("Lỗi khi gửi yêu cầu!", "", false, false));
                    return null;
                });
    }


    private ArrayList<String> extractMessageFromJson(String json) {
        ArrayList<String> imageUrls = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            int limit = Math.min(9, jsonArray.length());

            for (int i = 0; i < limit; i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                if (item.has("preview")) {
                    JSONObject preview = item.getJSONObject("preview");
                    imageUrls.add(preview.getString("url"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            imageUrls.add("Lỗi xử lý JSON!");
        }

        return imageUrls; // Trả về danh sách 10 ảnh đầu tiên
    }

    private void addImageToChat(VBox imgContainer, String timestamp, boolean isUser, boolean isFetch) {
        if (!isFetch) {
            timestamp = new SimpleDateFormat("HH:mm").format(new Date());
        }

        if (!isUser) {
            Label messageLabel = new Label("Here is your answer: ");
            messageLabel.setWrapText(true);
            messageLabel.setTextFill(Color.WHITE);
            messageLabel.setMaxWidth(280);
            ImageView likeImageView = new ImageView(new Image(getClass().getResource("/img/Like.png").toExternalForm()));
            likeImageView.setFitWidth(16);
            likeImageView.setFitHeight(16);

            ImageView dislikeImageView = new ImageView(new Image(getClass().getResource("/img/Dislike.png").toExternalForm()));
            dislikeImageView.setFitWidth(16);
            dislikeImageView.setFitHeight(16);


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


            HBox buttonContainer = new HBox(5, btnLike, btnDislike);

            buttonContainer.setSpacing(0);
            buttonContainer.setAlignment(Pos.BOTTOM_LEFT);
            imgContainer.getChildren().add(buttonContainer);


            Platform.runLater(() -> {
                chatBox.getChildren().addAll(messageLabel, imgContainer);
                scrollPane.setVvalue(1.0);
            });
        }

    }

}
