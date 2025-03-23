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
public class testcase {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        
        // Giả lập một UUID để test
        UUID testUserId = UUID.randomUUID();

        // Test hàm updateUserInfo
        System.out.println("UserID test: " + testUserId);
        boolean result = userDAO.updateUserInfo(testUserId, "TestUser", "test@example.com", "avatar.png");

        System.out.println("Kết quả cập nhật: " + result);
    }
}
