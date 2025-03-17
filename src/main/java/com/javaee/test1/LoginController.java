/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

/**
 *
 * @author culua
 */
public class LoginController {

    @FXML
    private TextField username; // Ô nhập Username

    @FXML
    private TextField pass; // Ô nhập Password

    @FXML
    private TextField confimpass; // Ô nhập Confirm Password

    @FXML
    private Button Loginbutton; // Nút Sign up

    @FXML
    private Label resultLabel; // Nhãn thông báo kết quả

    // Regex kiểm tra định dạng email hợp lệ
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @FXML
    public void initialize() {
        // Gọi hiệu ứng cho nút Login
        ButtonEffect.applyZoomEffect(Loginbutton, 1.1, 0.3); // Hover phóng to 1.1 lần, 0.3 giây
        ButtonEffect.applyClickEffect(Loginbutton, 0.95, 0.2); // Click thu nhỏ còn 0.95, 0.2 giây
    }

    // Xử lý sự kiện khi nhấn nút "Sign up"
    @FXML
    private void handleSignUp(ActionEvent event) {
        String user = username.getText().trim();
        String password = pass.getText().trim();
        String confirmPassword = confimpass.getText().trim();

        // Kiểm tra trường trống
        if (user.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showResult("⚠️ Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // Kiểm tra định dạng email
        if (!isValidEmail(user)) {
            showResult("❌ Email không hợp lệ!");
            return;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 8) {
            showResult("❌ Mật khẩu phải có ít nhất 8 ký tự!");
            return;
        }

        // Kiểm tra mật khẩu xác nhận
        if (!password.equals(confirmPassword)) {
            showResult("❌ Mật khẩu xác nhận không khớp!");
            return;
        }

        // Nếu tất cả hợp lệ
        showResult("✅ Đăng ký thành công cho: " + user);
        clearFields();
    }

    // Kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Hiển thị thông báo
    private void showResult(String message) {
        resultLabel.setText(message);
    }

    // Xóa nội dung các ô nhập liệu
    private void clearFields() {
        username.clear();
        pass.clear();
        confimpass.clear();
    }
}
