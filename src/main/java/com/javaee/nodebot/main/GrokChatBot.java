package com.javaee.nodebot.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.javaee.nodebot.config.APIKEY;
import com.javaee.nodebot.config.ApiConfig;

public class GrokChatBot extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private final Gson gson = new Gson(); // Thêm Gson instance

    public GrokChatBot() {
        // Thiết lập giao diện
        setTitle("Grok-2 Chatbot");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khu vực hiển thị chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Ô nhập liệu và nút gửi
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // Thêm panel vào frame
        add(panel);

        // Xử lý sự kiện nút gửi
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText().trim();
                if (!userInput.isEmpty()) {
                    chatArea.append("You: " + userInput + "\n");
                    inputField.setText("");
                    sendToGrok(userInput);
                }
            }
        });

        // Xử lý Enter để gửi
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });
    }

    private void sendToGrok(String message) {
        new Thread(() -> {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(ApiConfig.ENDPOINT);
                post.setHeader("Content-Type", "application/json");
                post.setHeader("X-RapidAPI-Key", new APIKEY().getKey()); // Sử dụng API key RapidAPI
                post.setHeader("X-RapidAPI-Host", "grok-2-by-xai.p.rapidapi.com");

                // Tạo payload JSON cho Grok-2 API trên RapidAPI
                JsonObject payload = new JsonObject();
                payload.addProperty("model", ApiConfig.MODEL);
                JsonObject messageObj = new JsonObject();
                messageObj.addProperty("role", "user");
                messageObj.addProperty("content", message);
                JsonObject[] messages = {JsonParser.parseString(messageObj.toString()).getAsJsonObject()};
                payload.add("messages", JsonParser.parseString(gson.toJson(messages)).getAsJsonArray());
                payload.addProperty("temperature", ApiConfig.TEMPERATURE);
                payload.addProperty("max_tokens", 2048); // Thêm nếu cần giới hạn độ dài phản hồi

                StringEntity entity = new StringEntity(payload.toString(), "UTF-8");
                post.setEntity(entity);

                // Gửi yêu cầu và nhận phản hồi
                var response = httpClient.execute(post);
                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("JSON Response: " + jsonResponse); // In ra để kiểm tra

                JsonObject jsonObj = JsonParser.parseString(jsonResponse).getAsJsonObject();

                // Kiểm tra lỗi từ API
                if (jsonObj.has("error")) {
                    String errorMessage = jsonObj.get("error").getAsString();
                    SwingUtilities.invokeLater(() -> {
                        chatArea.append("Error: " + errorMessage + "\n");
                    });
                    return;
                }

                // Kiểm tra và lấy "choices" (giả sử cấu trúc giống Grok)
                JsonArray choices = jsonObj.getAsJsonArray("choices");
                if (choices == null || choices.size() == 0) {
                    SwingUtilities.invokeLater(() -> {
                        chatArea.append("Error: No response from Grok-2 or invalid response format.\n");
                    });
                    return;
                }

                // Lấy phản hồi
                JsonObject choice = choices.get(0).getAsJsonObject();
                JsonObject messageResponse = choice.getAsJsonObject("message");
                String reply = messageResponse.get("content").getAsString();

                // Hiển thị phản hồi trên giao diện
                SwingUtilities.invokeLater(() -> {
                    chatArea.append("Grok-2: " + reply + "\n");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    chatArea.append("Error: " + ex.getMessage() + "\n");
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GrokChatBot().setVisible(true);
        });
    }
}
