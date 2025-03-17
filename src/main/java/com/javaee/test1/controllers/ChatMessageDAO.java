package com.javaee.test1.controllers;

import com.javaee.test1.models.ChatMessage;

import java.sql.*;
import java.util.ArrayList;
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
        return new ChatMessage(
                UUID.fromString(rs.getString("MessageID")),
                UUID.fromString(rs.getString("SenderID")),
                UUID.fromString(rs.getString("ReceiverID")),
                rs.getString("MessageText"),
                rs.getBoolean("HasAttachment"),
                rs.getString("AttachmentFileName"),
                rs.getString("AttachmentFilePath"),
                rs.getTimestamp("SentAt")
        );
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


}
