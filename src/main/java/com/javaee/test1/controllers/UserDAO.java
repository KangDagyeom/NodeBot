package com.javaee.test1.controllers;

import com.javaee.test1.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        return new User(UUID.fromString(rs.getString("UserIDd")), rs.getString("Email"), rs.getString("PasswordHash"), rs.getString("Username"), rs.getString("Avatar"), rs.getString("Role"), rs.getString("SubscriptionPlan"), rs.getTimestamp("CreatedAt"), rs.getTimestamp("LastActive"), rs.getBoolean("IsActive"));
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

    // ✅ Kiểm tra mật khẩu cũ có đúng không
    public boolean isOldPasswordCorrect(String email, String oldPassword) {
        String query = "SELECT * FROM Users WHERE Email = ? AND PasswordHash = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, oldPassword);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔄 Cập nhật mật khẩu mới
    public boolean updatePassword(String email, String newPassword) {
        String query = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm kiểm tra đăng nhập
    public boolean validateUser(String usernameOrEmail, String password) {
        String query = "SELECT * FROM Users WHERE (Email = ? OR Username = ?) AND PasswordHash = ?";

        try (Connection conn = getConnect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);  // ❗ Thực tế nên hash password, ví dụ SHA-256

            ResultSet rs = stmt.executeQuery();
            return rs.next();  // Có dữ liệu nghĩa là đăng nhập đúng

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Hàm đổi tên cuộc trò chuyện
    public boolean updateConversationTitle(UUID conversationId, String newTitle) {
        String query = "UPDATE ChatHistory SET Title = ? WHERE ConversationID = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newTitle);
            ps.setObject(2, conversationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Hàm cập nhật thông tin người dùng
    public boolean updateUserInfo(UUID userId, String newUsername, String newEmail, String newAvatar) {
        String query = "UPDATE Users SET Username = ?, Email = ?, Avatar = ? WHERE UserID = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newUsername);
            ps.setString(2, newEmail);
            ps.setString(3, newAvatar);
            ps.setString(4, userId.toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// 1️⃣ Kiểm tra Email cũ có tồn tại không

    public boolean checkEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = getConnect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
//2️⃣ Cập nhật Email

    public boolean updateEmail(String oldEmail, String newEmail) {
        String query = "UPDATE Users SET Email = ? WHERE Email = ?";
        try (Connection conn = getConnect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newEmail);
            ps.setString(2, oldEmail);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //1️⃣ Kiểm tra Username có tồn tại không
    public boolean checkUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = getConnect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class HashUtil {
        public static String hashPassword(String password) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashedBytes = md.digest(password.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hashedBytes) {
                    hexString.append(String.format("%02x", b));
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // ✅ Kiểm tra mật khẩu cũ có đúng không
    public boolean isOldPasswordCorrectuser(String user, String oldPassword) {
        String query = "SELECT * FROM Users WHERE Username = ? AND PasswordHash = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, user);
            ps.setString(2, oldPassword);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔄 Cập nhật mật khẩu mới
    public boolean updatePassworduser(String user, String newPassword) {
        String query = "UPDATE Users SET PasswordHash = ? WHERE Username = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newPassword);
            ps.setString(2, user);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy username theo email (tránh chỉnh sửa username trực tiếp)
    public String getUsernameByEmail(String email) {
        String query = "SELECT Username FROM Users WHERE Email = ?";
        try (Connection conn = getConnect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
