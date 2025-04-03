/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1.controllers;

import java.util.UUID;

/**
 *
 * @author xinch
 */
public class ChatHistorySession {
    private static ChatHistorySession instance;
    private UUID conversationId; // ➜ Lưu trữ ConversationID
    private UUID userId; // ➜ Lưu trữ UserID
    private String title; // ➜ Lưu trữ Tiêu đề cuộc hội thoại
 

    public ChatHistorySession() {
    }

    // Singleton: Lấy thể hiện duy nhất của ChatHistorySession
    public static ChatHistorySession getInstance() {
        if (instance == null) {
            instance = new ChatHistorySession();
        }
        return instance;
    }

    // Cập nhật thông tin của cuộc hội thoại
    public void setChatHistoryInfo(UUID conversationId, UUID userId, String title) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.title = title;
        
    }

    // Getter cho ConversationID
    public UUID getConversationId() {
        return conversationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

  

    // Xóa thông tin cuộc hội thoại (ví dụ khi người dùng đăng xuất)
    public void clearSession() {
        conversationId = null;
        userId = null;
        title = null;
       
    }
}
