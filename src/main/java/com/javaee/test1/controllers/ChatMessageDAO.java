package com.javaee.test1.controllers;

import com.javaee.test1.models.ChatMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatMessageDAO {
    private static final String CONNECTION_URL = "jdbc:sqlserver://localhost:1433;databaseName=ChatSystemDB;encrypt=true;trustServerCertificate=true;username=sa;password=123";

    public static Connection getConnect() {
        try {
            return DriverManager.getConnection(CONNECTION_URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ChatMessage mapResult(ResultSet rs) throws SQLException {
        return new ChatMessage(UUID.fromString(rs.getString("MessageID")), UUID.fromString(rs.getString("ConversationID")), UUID.fromString(rs.getString("SenderID")), rs.getString("SenderType"), rs.getString("MessageText"), rs.getString("MessageType"), rs.getTimestamp("SentAt"));
    }


    private ArrayList<ChatMessage> executeQuery(String query, Object... params) {
        ArrayList<ChatMessage> list = new ArrayList<>();
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResult(rs));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thực thi truy vấn: " + query, e);
        }
        return list;
    }

    private int executeUpdate(String query, Object... params) {
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thực thi truy vấn: " + query, e);
        }
    }

    public int saveMessageToDB(UUID conversationId, UUID senderId, String senderType, String messageText) {
        String sql = "INSERT INTO ChatMessages (ConversationID, SenderID, SenderType, MessageText, SentAt) " + "VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, conversationId); // UNIQUEIDENTIFIER
            ps.setObject(2, senderId);       // UNIQUEIDENTIFIER
            ps.setString(3, senderType);     // 'user' hoặc 'bot'
            ps.setString(4, messageText);    // Nội dung tin nhắn

            return ps.executeUpdate(); // Trả về số dòng bị ảnh hưởng
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu tin nhắn vào DB: ", e);
        }
    }

    public List<ChatMessage> getChatHistory(UUID conversationId) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        String sql = "SELECT MessageID, ConversationID, SenderID, SenderType, MessageText, MessageType, SentAt " +
                "FROM ChatMessages WHERE ConversationID = ? ORDER BY SentAt";

        try (Connection conn = getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conversationId.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID messageID = UUID.fromString(rs.getString("MessageID"));
                UUID convID = UUID.fromString(rs.getString("ConversationID"));
                UUID senderID = UUID.fromString(rs.getString("SenderID"));
                String senderType = rs.getString("SenderType");  // 'user' hoặc 'bot'
                String messageText = rs.getString("MessageText");
                String messageType = rs.getString("MessageType"); // Loại tin nhắn
                Timestamp sentAt = rs.getTimestamp("SentAt");

                // Tạo đối tượng ChatMessage và thêm vào danh sách
                chatMessages.add(new ChatMessage(messageID, convID, senderID, senderType, messageText, messageType, sentAt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatMessages;
    }

    public boolean updateConversationTitle(UUID conversationId, String newTitle) {
        String query = "UPDATE ChatHistory SET Title = ? WHERE ConversationID = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newTitle);
            ps.setObject(2, conversationId);
            return ps.executeUpdate() > 0; // >0 nghĩa là update thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    

}
