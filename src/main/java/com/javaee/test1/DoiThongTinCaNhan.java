/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
import com.javaee.test1.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
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
    @FXML
    private Button btnXoaNguoiDungNew;
    @FXML
    private ImageView imgAvatar;
    @FXML
    private Label txtTenND;
    @FXML
    private Label txtTaiKhoanND;

    //    private UserDAO userDAO = new UserDAO();
//    private UUID userId;
//    private User user;
//
//    public void setUserId(UUID id) {
//        this.userId = id;
//        this.user = userDAO.getUserById(id); // Lấy user từ database nếu chưa có
//        loadUserInfo();
//    }
//
//    // ✅ Load thông tin người dùng khi mở form
//    private void loadUserInfo() {
//
//        if (user != null) {
//            txtEmailMoi.setText(user.getEmail());
//            txtUsername.setText(user.getUsername());
//        }
//    }
    private UUID userId;
    private User user;
    private UserDAO userDAO = new UserDAO();

    // ✅ Gán userId từ user (nếu có), nếu không thì lấy từ database
    public void setUserId(UUID id) {
        if (id == null) {
            showAlert("Lỗi", "ID người dùng không hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        this.userId = id;
        this.user = userDAO.getUserById(id);

        if (this.user == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin người dùng!", Alert.AlertType.ERROR);
        } else {
            loadUserInfo();
        }
    }

    @FXML
    public void initialize() {
        loadUserInfo(); // Gọi loadUserInfo() khi view được hiển thị
    }

    private void loadUserInfo() {
        // Lấy thông tin từ UserSession
        UserSession userSession = UserSession.getInstance();
        UUID userId = userSession.getUserId();

        if (userId == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin người dùng từ phiên đăng nhập!!", Alert.AlertType.ERROR);
            return;
        }
        // Cập nhật tên và loại tài khoản từ UserSession lên giao diện
        txtTenND.setText(userSession.getUsername() != null ? userSession.getUsername() : "Chưa có tên");
        txtTaiKhoanND.setText(userSession.getSubscriptionPlan() != null ? userSession.getSubscriptionPlan() : "Không xác định");

        // Hiển thị avatar từ UserSession (nếu có)
        String avatarPath = userSession.getAvatar();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    Image avatarImage = new Image(avatarFile.toURI().toString());
                    imgAvatar.setImage(avatarImage);

                    // 📌 Căn ảnh sát trái
                    imgAvatar.setPreserveRatio(true);  // Giữ tỷ lệ ảnh
                    imgAvatar.setFitWidth(55);        // Điều chỉnh chiều rộng
                    imgAvatar.setFitHeight(55);       // Điều chỉnh chiều cao
                    imgAvatar.setSmooth(true);        // Làm mịn ảnh
                    imgAvatar.setCache(true);         // Tăng hiệu suất load ảnh

                    imgAvatar.setTranslateX(500); // Di chuyển ảnh sang trái (âm là trái, dương là phải)
                    imgAvatar.setTranslateY(0);   // Di chuyển ảnh xuống dưới (âm là lên trên, dương là xuống dưới)

                    // 📌 Làm tròn avatar
                    Circle clip = new Circle(25, 25, 25); // Tạo clip hình tròn (bán kính 25px)
                    imgAvatar.setClip(clip); // Đặt hình cắt tròn vào avatar

                    // 📌 Nếu imgAvatar nằm trong HBox, căn sát trái
                    HBox.setHgrow(imgAvatar, Priority.NEVER);
                    imgAvatar.setTranslateX(-10); // Dịch ảnh về bên trái (tùy chỉnh)

                } else {
                    showAlert("Lỗi", "Không tìm thấy tệp ảnh đại diện!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể tải ảnh đại diện!", Alert.AlertType.ERROR);
            }
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

    @FXML
    private void xoaNguoiDung() {
        UUID userId = UserSession.getInstance().getUserId(); // ➜ Lấy UserID từ UserSession

        if (userId == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin tài khoản!", Alert.AlertType.ERROR);
            return;
        }

        // Xác nhận trước khi xóa
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa tài khoản này? Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userDAO.deleteUserById(userId);

            if (success) {
                showAlert("Thành công", "Tài khoản của bạn đã bị xóa!", Alert.AlertType.INFORMATION);

                // ➜ Xóa session và chuyển hướng về màn hình đăng nhập
                UserSession.getInstance().clearSession();
                redirectToLogin();
            } else {
                showAlert("Lỗi", "Không thể xóa tài khoản!", Alert.AlertType.ERROR);
            }
        }
    }
// Chuyển hướng về màn hình đăng nhập

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnXoaNguoiDungNew.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể chuyển về màn hình đăng nhập!", Alert.AlertType.ERROR);
        }
    }

}
