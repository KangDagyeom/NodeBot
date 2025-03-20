package com.javaee.test1.controllers;

import com.javaee.test1.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class UserDAO {

    private static final String CONNECTION_URL = "jdbc:sqlserver://localhost:1433;databaseName=ChatSystemDB;encrypt=true;trustServerCertificate=true;username=sa;password=123";

    public static Connection getConnect() {
        try {
            return DriverManager.getConnection(CONNECTION_URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User mapResult(ResultSet rs) throws SQLException {
        return new User(UUID.fromString(rs.getString("UserID")), rs.getString("Email"), rs.getString("PasswordHash"), rs.getString("Username"), rs.getString("Avatar"), rs.getString("Role"), rs.getString("SubscriptionPlan"), rs.getTimestamp("CreatedAt"), rs.getTimestamp("LastActive"), rs.getBoolean("IsActive"));
    }

    private ArrayList<User> executeQuery(String query, Object... params) {
        ArrayList<User> list = new ArrayList<>();
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

    public ArrayList<String> loadConversation(UUID userId) {
        String query = "SELECT ConversationID, Title FROM ChatHistory WHERE UserID = ? ORDER BY CreatedAt DESC";
        ArrayList<String> conversationName = new ArrayList<>();
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID conversationId = UUID.fromString(rs.getString("ConversationID"));
                String title = rs.getString("Title");
                conversationName.add(title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversationName;
    }
}
