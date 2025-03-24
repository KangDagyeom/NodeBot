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
    private TextField txtEmailMoi;
    @FXML
    private TextField txtEmailCu;
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
    private User user;

    public void setUserId(UUID id) {
        this.userId = id;
        loadUserInfo();
    }

    // ✅ Load thông tin người dùng khi mở form
    private void loadUserInfo() {

        if (user != null) {
            txtEmailMoi.setText(user.getEmail());
            txtUsername.setText(user.getUsername());
        }
    }

    // ✅ Cập nhật Email khi bấm "Lưu và Xác Minh"
    @FXML
    private void luuVaXacMinhEmail() {
        String oldEmail = txtEmailCu.getText().trim();
        String newEmail = txtEmailMoi.getText().trim();

        // Kiểm tra email hợp lệ
        if (newEmail.isEmpty() || !newEmail.contains("@")) {
            showAlert("Lỗi", "Vui lòng nhập địa chỉ email mới hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        if (oldEmail.isEmpty() || !oldEmail.contains("@")) {
            showAlert("Lỗi", "Vui lòng nhập địa chỉ email cũ hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        // Kiểm tra email cũ có đúng không
        if (!userDAO.checkEmailExists(oldEmail)) {
            showAlert("Lỗi", "Email cũ không tồn tại trong hệ thống!", Alert.AlertType.ERROR);
            return;
        }

        // Cập nhật email trong database
        if (userDAO.updateEmail(oldEmail, newEmail)) {
            showAlert("Thành công", "Email đã được cập nhật!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Không thể cập nhật email. Vui lòng thử lại!", Alert.AlertType.ERROR);
        }
    }

    // ✅ Cập nhật Username và Mật khẩu khi bấm "Lưu Thay Đổi"
    @FXML
    private void luuThayDoi() {
        String user = txtUsername.getText().trim();
        String currentPassword = txtCurrentPassword.getText().trim();
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (user.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập username", Alert.AlertType.ERROR);
            return;
        }

//    // Lấy username theo email từ database
//    String username = userDAO.getUsernameByEmail(user);
//    if (username == null) {
//        showAlert("Lỗi", "Email không tồn tại!", Alert.AlertType.ERROR);
//        return;
//    }

        // Kiểm tra xác nhận mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Lỗi", "Mật khẩu mới không khớp!", Alert.AlertType.ERROR);
            return;
        }

        // Kiểm tra mật khẩu cũ có đúng không
        if (!userDAO.isOldPasswordCorrectuser(user, currentPassword)) {
            showAlert("Lỗi", "Mật khẩu hiện tại không đúng!", Alert.AlertType.ERROR);
            return;
        }

        // Cập nhật mật khẩu
        if (userDAO.updatePassworduser(user, newPassword)) {
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
