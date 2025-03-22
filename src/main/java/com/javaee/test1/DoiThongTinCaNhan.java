/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.UUID;

/**
 * @author xinch
 */
public class DoiThongTinCaNhan {
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtCurrentPassword;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Button btnLuuThayDoi;
    @FXML
    private Button btnLuuVaXacMinh;

    private UserDAO userDAO = new UserDAO();
    private UUID userId;

    public void setUserId(UUID id) {
        this.userId = id;
        loadUserInfo();
    }

    // ✅ Load thông tin người dùng khi mở form
    private void loadUserInfo() {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            txtEmail.setText(user.getEmail());
            txtUsername.setText(user.getUsername());
        }
    }

    // ✅ Cập nhật Email khi bấm "Lưu và Xác Minh"
    @FXML
    private void luuVaXacMinhEmail() {
        String email = txtEmail.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            showAlert("Lỗi", "Vui lòng nhập địa chỉ email hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        if (userDAO.updateEmail(userId, email)) {
            showAlert("Thành công", "Email đã được cập nhật!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Không thể cập nhật email!", Alert.AlertType.ERROR);
        }
    }

    // ✅ Cập nhật Username và Mật khẩu khi bấm "Lưu Thay Đổi"
    @FXML
    private void luuThayDoi() {
        String username = txtUsername.getText().trim();
        String currentPassword = txtCurrentPassword.getText().trim();
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();
        System.out.println(currentPassword);
        if (username.isEmpty()) {
            showAlert("Lỗi", "Tên người dùng không được để trống!", Alert.AlertType.ERROR);
            return;
        }

        // 🔹 Nếu đổi mật khẩu, kiểm tra mật khẩu cũ
        if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (!userDAO.isOldPasswordCorrect(txtEmail.getText().trim(), currentPassword)) {
                showAlert("Lỗi", "Mật khẩu hiện tại không đúng!", Alert.AlertType.ERROR);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showAlert("Lỗi", "Mật khẩu mới không khớp!", Alert.AlertType.ERROR);
                return;
            }
            userDAO.updatePassword(txtEmail.getText().trim(), newPassword);
        }

        // 🔹 Cập nhật Username
        if (userDAO.updateUsername(userId, username)) {
            showAlert("Thành công", "Thông tin cá nhân đã được cập nhật!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Cập nhật không thành công!", Alert.AlertType.ERROR);
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
