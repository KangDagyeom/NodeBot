/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 *
 * @author xinch
 */
public class NutDangXuatController {
    
    @FXML
    private Button exit;
    @FXML
    private Button Logout;

    
    
     @FXML
    private void handleLogoutAndExit(MouseEvent event) {
        System.out.println("Đăng xuất và thoát ứng dụng!"); // Debug
        Platform.exit(); // Thoát toàn bộ ứng dụng
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("Label Đăng xuất đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/login.fxml")); // Chuyển về màn hình đăng nhập
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow(); // Lấy stage hiện tại
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Chuyển về màn hình đăng nhập thành công!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
