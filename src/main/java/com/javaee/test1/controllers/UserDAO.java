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

        try (Connection conn = getConnect(); PreparedStatement stmt = conn.prepareStatement(query)) {

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
        System.out.println("Đang kiểm tra email: [" + email + "]");
        String query = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Số lượng kết quả: " + count);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


//2️⃣ Cập nhật Email

    public boolean updateEmail(String oldEmail, String newEmail) {
        String query = "UPDATE Users SET Email = ? WHERE Email = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
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
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
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
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
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

    // Lấy tên cuộc trò chuyện theo ConversationID
    public String getConversationTitle(UUID conversationId) {
        String query = "SELECT Title FROM ChatHistory WHERE ConversationID = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, conversationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật tên cuộc trò chuyện
    public boolean updateUserID(UUID conversationId, String newTitle) {
        String query = "UPDATE ChatHistory SET Title = ? WHERE ConversationID = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newTitle);
            ps.setObject(2, conversationId); // Xác định đúng cuộc trò chuyện cần sửa
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa cuộc trò chuyện theo conversationId
    public boolean deleteConversation(UUID conversationId) {
        String query = "DELETE FROM ChatHistory WHERE ConversationID = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, conversationId); // Định danh cuộc trò chuyện cần xóa
            return ps.executeUpdate() > 0; // Kiểm tra nếu có ít nhất một dòng bị ảnh hưởng thì trả về true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Thêm người dùng vào database
    public boolean createUser(String email, String username, String password, String avatar) {
        String sql = "INSERT INTO Users (Email, PasswordHash, Username, Avatar) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password); // Có thể thêm mã hóa mật khẩu ở đây
            pstmt.setString(3, username);
            pstmt.setString(4, avatar);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm người dùng: " + e.getMessage());
            return false;
        }
    }

    // Hàm xóa người dùng trong UserDAO
    public boolean deleteUserById(UUID userId) {
        String sql = "DELETE FROM Users WHERE UserID = ?";

        try (Connection conn = getConnect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa tài khoản: " + e.getMessage());
            return false;
        }
    }

    public User getUserById(UUID userId) {
        if (userId == null) {
            System.out.println("Lỗi: userId truyền vào là null");
            return null;
        }

        String sql = "SELECT * FROM Users WHERE UserID = ?";

        try (Connection conn = getConnect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối database.");
                return null;
            }

            pstmt.setObject(1, userId); // UUID phải dùng setObject

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getObject("UserID", UUID.class), // Cách cast đúng
                            rs.getString("Email"),
                            rs.getString("PasswordHash"),
                            rs.getString("Username"),
                            rs.getString("Avatar"),
                            rs.getString("Role"),
                            rs.getString("SubscriptionPlan"),
                            rs.getTimestamp("CreatedAt"),
                            rs.getTimestamp("LastActive"),
                            rs.getBoolean("IsActive")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
        return null; // Trả về null nếu không tìm thấy user
    }

    public boolean deleteAllConversations(UUID userId) {
        String query = "DELETE FROM ChatHistory WHERE UserID = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có dòng bị xóa
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /// xóa tất cả các cuộc hội thoại
    // Xóa tất cả cuộc trò chuyện
    public boolean deleteAllConversations() {
        String query = "DELETE FROM ChatHistory";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            return ps.executeUpdate() > 0; // Kiểm tra nếu có ít nhất một dòng bị ảnh hưởng thì trả về true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Nang cấp gói
    // Nâng cấp gói bằng username
    public boolean updateSubscriptionPlan(String username, String newPlan) {
        String query = "UPDATE Users SET SubscriptionPlan = ? WHERE Username = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newPlan);
            ps.setString(2, username);  // vì đang truyền username
            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserInfoByUsername(String username) {
        String query = "SELECT UserID,PasswordHash, Email, Avatar, Username, SubscriptionPlan FROM Users WHERE Username = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getObject("UserID", UUID.class), // Lấy UserID dạng UUID
                        rs.getString("Email"), // Các trường khác không cần thiết
                        rs.getString("PasswordHash"),
                        rs.getString("Username"),
                        rs.getString("Avatar"),
                        null,
                        rs.getString("SubscriptionPlan"),
                        null,
                        null,
                        false
                );
            } else {
                System.out.println("Không tìm thấy người dùng với username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn getUserInfoByUsername: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Không tìm thấy user
    }

    public UUID getUserIdByUsername(String username) {
        String query = "SELECT UserID FROM Users WHERE Username = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("UserID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UUID getConversationIdByTitle(String title) {
        String query = "SELECT ConversationID FROM ChatHistory WHERE Title = ?";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("ConversationID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getConversationId(UUID userId) {
        String query = "SELECT ConversationID FROM ChatHistory WHERE UserID = ? ORDER BY CreatedAt DESC LIMIT 1";
        try (Connection conn = getConnect(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("ConversationID"); // Lấy ConversationID dạng String
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không có kết quả
    }

    public void insertChatHistory(UUID userID, String title) {
        String sql = "INSERT INTO ChatHistory (UserID, Title) VALUES (?, ?)";

        try (Connection conn = getConnect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userID);
            stmt.setString(2, title);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Thêm cuộc hội thoại thành công!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addNewConversation(UUID userId, String title) {
        String sql = "INSERT INTO ChatHistory (ConversationID, UserID, Title) VALUES (?, ?, ?)";
        try (Connection conn = getConnect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setObject(2, userId);
            stmt.setString(3, title);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countConversationByUserId(UUID userId) {
        String sql = "SELECT COUNT(*) FROM ChatHistory WHERE UserID = ?";
        try (Connection conn = getConnect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
