/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.EmailService;
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
    private TextField txtOTP;
    private String sentOTP;
    private UserDAO userDAO = new UserDAO();

    //    @FXML
//    private void doiMatKhau() {
//        String userEmail = txtEmail.getText().trim();
//        String newPassword = txtMatKhauMoi.getText().trim();
//
//        if (userEmail.isEmpty() || !userEmail.contains("@")) {
//            showAlert("Lỗi", "Vui lòng nhập email hợp lệ!", Alert.AlertType.ERROR);
//            return;
//        }
//
//        if (newPassword.isEmpty()) {
//            showAlert("Lỗi", "Vui lòng nhập mật khẩu mới!", Alert.AlertType.ERROR);
//            return;
//        }
//
//        //Kiểm tra email có tồn tại trong database không
//        if (!userDAO.validateUser(userEmail, oldPassword)) {
//            showAlert("Lỗi", "Email hoặc mật khẩu không đúng!", Alert.AlertType.ERROR);
//            return;
//        }
//
//        // Đổi mật khẩu trong database
//        if (userDAO.updatePassword(userEmail, newPassword)) {
//            showAlert("Thành công", "Mật khẩu đã được cập nhật!", Alert.AlertType.INFORMATION);
//        } else {
//            showAlert("Lỗi", "Không thể đổi mật khẩu. Vui lòng thử lại!", Alert.AlertType.ERROR);
//        }
//    }
//
//    private void showAlert(String title, String content, Alert.AlertType type) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
    @FXML
    private void requestOTP() {
        String email = txtEmail.getText();
        String otp = EmailService.sendOtp(email);
        if (otp != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Gửi OTP thành công!");
            alert.show();
            sentOTP = otp;
            System.out.println("OTP đã gửi: " + otp);
        } else {
            System.out.println("Lỗi khi gửi OTP!");
        }
    }

    @FXML
    private void sendOTP() {
        String inputOtp = txtOTP.getText();
        try {
            if (inputOtp.equals(sentOTP)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("OTP chính xác!");
                alert.show();
                boolean result = userDAO.updatePassword(txtEmail.getText(), txtMatKhauMoi.getText());
                if (result) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Thông báo");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Đã cập nhật mật khẩu mới!");
                    alert1.show();
                } else {
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Thông báo");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Kiểm tra lại thông tin của bạn!");
                    alert2.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("OTP không đúng, hãy kiểm tra lại thông tin của bạn!");
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
