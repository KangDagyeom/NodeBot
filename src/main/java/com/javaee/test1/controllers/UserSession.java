package com.javaee.test1.controllers;

import java.util.UUID;

public class UserSession {

    private static UserSession instance;
    private UUID userId; // ➜ Thêm biến lưu trữ UserID
    private String avatar;
    private String username;
    private String subscriptionPlan;

    private UserSession() {
    }

    // Singleton: Lấy thể hiện duy nhất của UserSession
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // ➜ Cập nhật thông tin người dùng bao gồm UserID
    public void setUserInfo(UUID userId, String avatar, String username, String subscriptionPlan) {
        this.userId = userId;
        this.avatar = avatar;
        this.username = username;
        this.subscriptionPlan = subscriptionPlan;
    }

    // Getter cho userId
    public UUID getUserId() {
        return userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    // Xóa thông tin người dùng (đăng xuất)
    public void clearSession() {
        userId = null;
        avatar = null;
        username = null;
        subscriptionPlan = null;
    }

}
