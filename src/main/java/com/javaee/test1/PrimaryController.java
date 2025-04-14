package com.javaee.test1;

import com.javaee.test1.controllers.*;
import com.javaee.test1.models.ChatMessage;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
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
    private ImageView imgAvatar;
    @FXML
    private ImageView btnSearch;
    @FXML
    private ImageView btnTranslate;
    @FXML
    private Button btnNewCon;
    @FXML
    private Label labelNangcap;
    @FXML
    private Label labelXoahoithoai; // Label nút xóa
    @FXML
    private Label labelLogout;
    @FXML
    private AnchorPane mainContainer;
    private String saveTitle;
    private AsyncHttpClient client = new DefaultAsyncHttpClient();
    private String currentConversationId;

    @FXML
    private Label doiten;
    @FXML
    private Label xoa;

    public void savedTitle(String currentTitle) {
        saveTitle = currentTitle;
    }

    @FXML
    public void initialize() {
        if (session.getSubscriptionPlan().equals("free")) {
            btnSearch.setVisible(false);
            btnTranslate.setVisible(false);
            btnSearch.setManaged(false);
            btnTranslate.setManaged(true);
        }

        String message = MessageHolder.getInstance().getLastMessage();
        if (message == null) {
            Platform.runLater(() -> {
                Duration delay = Duration.millis(150);
                int index = 0;

                for (Node node : mainContainer.getChildren()) {
                    // Ban đầu ẩn đi
                    node.setOpacity(0);
                    node.setTranslateY(20);

                    // Hiệu ứng trượt và mờ
                    TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
                    slide.setFromY(20);
                    slide.setToY(0);
                    slide.setInterpolator(Interpolator.EASE_OUT);

                    FadeTransition fade = new FadeTransition(Duration.millis(300), node);
                    fade.setFromValue(0);
                    fade.setToValue(1);

                    ParallelTransition transition = new ParallelTransition(slide, fade);
                    transition.setDelay(delay.multiply(index));
                    transition.play();

                    index++;
                }
            });
            loadUserInfo();
            System.out.println("Username: " + session.getUsername());
            System.out.println("Avatar: " + session.getAvatar());
            System.out.println("Subscription Plan: " + session.getSubscriptionPlan());

            scrollPane.setFitToWidth(true);
            loadConversations(userDAO.getUserIdByUsername(session.getUsername()));
            ChatHistorySession chatHistorySession = ChatHistorySession.getInstance();

            loadChatHistory(chatHistorySession.getConversationId());
            chatBox.setMinHeight(Region.USE_PREF_SIZE);
            chatBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            chatBox.setFillWidth(true);


            chatBox.heightProperty().addListener((obs, oldVal, newVal) -> {
                Platform.runLater(() -> {
                    scrollPane.setVvalue(1.0); // Cuộn xuống dòng cuối cùng
                });
            });

            labelNangcap.setOnMouseClicked(event -> openUpgradePlan());

            //gán sự kiện cho xóa hết cuộc hội thoại
            labelXoahoithoai.setOnMouseClicked(event -> deleteall());

            labelLogout.setOnMouseClicked(event -> handleLogout(event));

            doiten.setOnMouseClicked(event -> RenameConversation());

            xoa.setOnMouseClicked(event -> deleteConversation());
        } else {
            Platform.runLater(() -> {
                Duration delay = Duration.millis(150);
                int index = 0;

                for (Node node : mainContainer.getChildren()) {
                    // Ban đầu ẩn đi
                    node.setOpacity(0);
                    node.setTranslateY(20);

                    // Hiệu ứng trượt và mờ
                    TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
                    slide.setFromY(20);
                    slide.setToY(0);
                    slide.setInterpolator(Interpolator.EASE_OUT);

                    FadeTransition fade = new FadeTransition(Duration.millis(300), node);
                    fade.setFromValue(0);
                    fade.setToValue(1);

                    ParallelTransition transition = new ParallelTransition(slide, fade);
                    transition.setDelay(delay.multiply(index));
                    transition.play();

                    index++;
                }
            });
            loadUserInfo();
            System.out.println("Username: " + session.getUsername());
            System.out.println("Avatar: " + session.getAvatar());
            System.out.println("Subscription Plan: " + session.getSubscriptionPlan());

            scrollPane.setFitToWidth(true);
            loadConversations(userDAO.getUserIdByUsername(session.getUsername()));
            chatBox.setMinHeight(Region.USE_PREF_SIZE);
            chatBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            chatBox.setFillWidth(true);


            chatBox.heightProperty().addListener((obs, oldVal, newVal) -> {
                Platform.runLater(() -> {
                    scrollPane.setVvalue(1.0); // Cuộn xuống dòng cuối cùng
                });
            });

            labelNangcap.setOnMouseClicked(event -> openUpgradePlan());

            //gán sự kiện cho xóa hết cuộc hội thoại
            labelXoahoithoai.setOnMouseClicked(event -> deleteall());

            labelLogout.setOnMouseClicked(event -> handleLogout(event));

            doiten.setOnMouseClicked(event -> RenameConversation());

            xoa.setOnMouseClicked(event -> deleteConversation());
            themCuocHoiThoaiMoi();
            String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
            chatMessageDAO.saveMessageToDB(userDAO.getConversationIdByTitle(saveTitle), // conversationId
                    userDAO.getUserIdByUsername(session.getUsername()), // senderId
                    "user", // senderType
                    message // Nội dung tin nhắn
            );
            System.out.println("User đã nhập: " + message);
            addMessageToChat(message, timestamp, true, false);
            sendResponse(message);
        }

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8));


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
                textLabel.setMouseTransparent(false);
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
            conversationLabel.setCursor(Cursor.HAND);
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
                savedTitle(conversationLabel.getText());
                ChatHistorySession chatHistorySession = ChatHistorySession.getInstance();
                chatHistorySession.setChatHistoryInfo(
                        userDAO.getConversationIdByTitle(saveTitle),
                        userDAO.getUserIdByUsername(session.getUsername()),
                        saveTitle
                );
                loadChatHistory(chatHistorySession.getConversationId());
                System.out.println(saveTitle);
            });

            conversationCon.getChildren().add(conversationLabel);
            FadeTransition ft = new FadeTransition(Duration.millis(300), conversationLabel);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
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
            chatMessageDAO.saveMessageToDB(userDAO.getConversationIdByTitle(saveTitle), // conversationId
                    userDAO.getUserIdByUsername(session.getUsername()), // senderId
                    "user", // senderType
                    userInput // Nội dung tin nhắn
            );
            addMessageToChat(userInput, "", true, false); // Hiển thị tin nhắn của người dùng
            inputField.clear(); // Xóa ô nhập

            // Gửi tin nhắn đến API
            sendSearchRequestToAPI(userInput);
        }
    }

    @FXML
    private void translateAction() {
        String userInput = inputField.getText().trim(); // Lấy nội dung nhập vào

        if (!userInput.isEmpty()) {
            chatMessageDAO.saveMessageToDB(userDAO.getConversationIdByTitle(saveTitle), // conversationId
                    userDAO.getUserIdByUsername(session.getUsername()), // senderId
                    "user", // senderType
                    userInput // Nội dung tin nhắn
            );
            addMessageToChat(userInput, "", true, false); // Hiển thị tin nhắn của người dùng
            inputField.clear(); // Xóa ô nhập

            // Gửi tin nhắn đến API
            sendTranslationRequestToAPI(userInput, "vi");
        }
    }

    private void sendSearchRequestToAPI(String query) {
        client.prepare("POST", "https://google-api-unlimited.p.rapidapi.com/search_image")
                .setHeader("x-rapidapi-key", "79e9925fedmsh5ece400febbd5d0p1e7721jsna398b5210fa7")
                .setHeader("x-rapidapi-host", "google-api-unlimited.p.rapidapi.com")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setBody("query=" + URLEncoder.encode(query, StandardCharsets.UTF_8)) // Encode query
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    String responseBody = response.getResponseBody();
                    ArrayList<String> imageUrls = extractImageFromJson(responseBody);
                    System.out.println("Response: " + responseBody);

                    Platform.runLater(() -> {
                        if (!imageUrls.isEmpty()) {
                            VBox mainContainer = new VBox(10);
                            mainContainer.setAlignment(Pos.CENTER_LEFT);

                            HBox currentRow = new HBox(10);
                            currentRow.setAlignment(Pos.CENTER_LEFT);

                            for (int i = 0; i < imageUrls.size(); i++) {
                                String imageUrl = imageUrls.get(i);
                                Image image = new Image(imageUrl, true);
                                ImageView imageView = new ImageView(image);

                                double size = 260;
                                imageView.setFitWidth(size);
                                imageView.setFitHeight(size);
                                imageView.setPreserveRatio(false);


                                Rectangle clip = new Rectangle(size, size);
                                clip.setArcWidth(15);
                                clip.setArcHeight(15);
                                imageView.setClip(clip);

                                // Cắt ảnh về vùng trung tâm để tránh méo
                                double viewportSize = Math.min(image.getWidth(), image.getHeight());
                                imageView.setViewport(new Rectangle2D(
                                        (image.getWidth() - viewportSize) / 2,
                                        (image.getHeight() - viewportSize) / 2,
                                        viewportSize,
                                        viewportSize
                                ));

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

    private void sendTranslationRequestToAPI(String text, String targetLanguage) {
        client.prepare("POST", "https://google-api-unlimited.p.rapidapi.com/translate")
                .setHeader("x-rapidapi-key", "79e9925fedmsh5ece400febbd5d0p1e7721jsna398b5210fa7")
                .setHeader("x-rapidapi-host", "google-api-unlimited.p.rapidapi.com")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setBody("texte=" + URLEncoder.encode(text, StandardCharsets.UTF_8) + "&to_lang=" + targetLanguage)
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    String responseBody = response.getResponseBody();
                    String translatedText = extractMessageFromJson(responseBody);

                    System.out.println("Translated: " + translatedText);

                    Platform.runLater(() -> {
                        addMessageToChat(translatedText, "", false, false);
                        chatMessageDAO.saveMessageToDB(
                                userDAO.getConversationIdByTitle(saveTitle),
                                userDAO.getUserIdByUsername(session.getUsername()),
                                "bot",
                                translatedText
                        );
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> addMessageToChat("Lỗi khi gửi yêu cầu!", "", false, false));
                    return null;
                });
    }


    private ArrayList<String> extractImageFromJson(String json) {
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

    private String extractMessageFromJson(String json) {


        JSONObject translatedText = null;
        try {
            translatedText = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return translatedText.getString("translation");
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
            messageLabel.setAlignment(Pos.CENTER_LEFT);
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

    @FXML
    private void handleNewConversation() {
        TextInputDialog dialog = new TextInputDialog("Cuộc trò chuyện mới");
        dialog.setTitle("Nhập tên cuộc hội thoại");
        dialog.setHeaderText("Nhập tiêu đề cho cuộc trò chuyện (tối đa 50 ký tự):");
        dialog.setContentText("Tiêu đề:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            title = title.trim();
            if (title.isEmpty()) {
                title = "Cuộc trò chuyện mới";
            } else if (title.length() > 50) {
                title = title.substring(0, 50);
            }

            userDAO.insertChatHistory(userDAO.getUserIdByUsername(session.getUsername()), title);
        });


        loadConversations(userDAO.getUserIdByUsername(session.getUsername()));
    }


    @FXML
    private void themCuocHoiThoaiMoi() {
        // Tạo tiêu đề mặc định, ví dụ: "Cuộc hội thoại 1", "Cuộc hội thoại 2",...
        int stt = userDAO.countConversationByUserId(session.getUserId()) + 1;
        String newTitle = "Cuộc hội thoại " + stt;
        saveTitle = newTitle;
        userDAO.addNewConversation(session.getUserId(), newTitle);
        loadConversations(session.getUserId());

    }

    private void loadUserInfo() {
        // Lấy thông tin từ UserSession
        UserSession userSession = UserSession.getInstance();
        UUID userId = userSession.getUserId();

        if (userId == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin người dùng từ phiên đăng nhập!", Alert.AlertType.ERROR);
            return;
        }
        // Cập nhật tên và loại tài khoản từ UserSession lên giao diện
        lbusername.setText(userSession.getUsername() != null ? userSession.getUsername() : "Chưa có tên");
        lbuserplan.setText(userSession.getSubscriptionPlan() != null ? userSession.getSubscriptionPlan() : "Không xác định");

        // Hiển thị avatar từ UserSession (nếu có)
        String avatarPath = userSession.getAvatar();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    Image avatarImage = new Image(avatarFile.toURI().toString());
                    imgAvatar.setImage(avatarImage);

                    // 📌 Căn ảnh sát trái
                    imgAvatar.setPreserveRatio(true);  // Giữ tỷ lệ ảnh
                    imgAvatar.setFitWidth(55);        // Điều chỉnh chiều rộng
                    imgAvatar.setFitHeight(55);       // Điều chỉnh chiều cao
                    imgAvatar.setSmooth(true);        // Làm mịn ảnh
                    imgAvatar.setCache(true);         // Tăng hiệu suất load ảnh

                    imgAvatar.setTranslateX(500); // Di chuyển ảnh sang trái (âm là trái, dương là phải)
                    imgAvatar.setTranslateY(0);   // Di chuyển ảnh xuống dưới (âm là lên trên, dương là xuống dưới)

                    // 📌 Làm tròn avatar
                    Circle clip = new Circle(25, 25, 25); // Tạo clip hình tròn (bán kính 25px)
                    imgAvatar.setClip(clip); // Đặt hình cắt tròn vào avatar

                    // 📌 Nếu imgAvatar nằm trong HBox, căn sát trái
                    HBox.setHgrow(imgAvatar, Priority.NEVER);
                    imgAvatar.setTranslateX(-10); // Dịch ảnh về bên trái (tùy chỉnh)

                } else {
                    showAlert("Lỗi", "Không tìm thấy tệp ảnh đại diện!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể tải ảnh đại diện!", Alert.AlertType.ERROR);
            }
        }

    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //clik vào sẽ chuyển sang trang nâng cấp node
    @FXML
    private void openUpgradePlan() {
        System.out.println("Label Nâng cấp đã được click!");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Nanngcap.fxml"));
            Parent root = loader.load();
            Stage upgradeStage = new Stage();
            upgradeStage.setTitle("Nâng cấp gói");
            upgradeStage.setScene(new Scene(root));
            upgradeStage.setResizable(false);
            upgradeStage.show();

            System.out.println("Cửa sổ nâng cấp đã mở!");
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang nâng cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //=======================================
    //log out
    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("Label Đăng xuất đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/nutdangxuat.fxml")); // Đúng file cần mở
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow(); // Lấy stage hiện tại
            stage.setTitle("Đăng xuất");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Chuyển đến màn hình Đăng xuất thành công!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang Đăng xuất: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //==========
    //delete all
    @FXML
    private void deleteall() {
        System.out.println("Label Delete ALL đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/nutxacminhxoacuochoithoai.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Delete ALL");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Cửa sổ Delete ALL đã mở!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void openEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Thongtincanhan.fxml"));
            Parent root = loader.load();

            // Lấy stage hiện tại từ button avatar
            Stage stage = (Stage) imgAvatar.getScene().getWindow();

            // Cập nhật scene với root mới
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //==========
    //Delete
    @FXML
    private void deleteConversation() {
        System.out.println("Label Delete đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Thongbaoxoacuoctrochuyen.fxml"));
            Parent root = loader.load();
            ThongBaoXoaCuocTroChuyenController controller = loader.getController();
            controller.setPrimaryController(this);
            Stage stage = new Stage();
            stage.setTitle("Delete");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Cửa sổ Delete đã mở!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //==========
    //Đổi tên
    @FXML
    private void RenameConversation() {
        System.out.println("Label đổi tên đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Thongbaodoiten.fxml"));
            Parent root = loader.load();
            DoiTenCuocHoiThoaiController controller = loader.getController();
            controller.setPrimaryController(this);
            Stage stage = new Stage();
            stage.setTitle("Rename");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Cửa sổ đổi tên đã mở!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
