/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * @author xinch
 */
public class QuenMatKhauController {

    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtMatKhauMoi;
    @FXML
    private TextField txtMatKhaucu;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void doiMatKhau() {
        String userEmail = txtEmail.getText().trim();
        String newPassword = txtMatKhauMoi.getText().trim();
        String oldPassword = txtMatKhaucu.getText().trim();
        if (userEmail.isEmpty() || !userEmail.contains("@")) {
            showAlert("Lỗi", "Vui lòng nhập email hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        if (newPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập mật khẩu mới!", Alert.AlertType.ERROR);
            return;
        }

        //Kiểm tra email có tồn tại trong database không
        if (!userDAO.validateUser(userEmail, oldPassword)) {
            showAlert("Lỗi", "Email hoặc mật khẩu không đúng!", Alert.AlertType.ERROR);
            return;
        }

        // Đổi mật khẩu trong database
        if (userDAO.updatePassword(userEmail, newPassword)) {
            showAlert("Thành công", "Mật khẩu đã được cập nhật!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Không thể đổi mật khẩu. Vui lòng thử lại!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
