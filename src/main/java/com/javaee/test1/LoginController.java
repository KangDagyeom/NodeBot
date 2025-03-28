package com.javaee.test1;

import com.javaee.test1.controllers.ChatMessageDAO;
import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
import com.javaee.test1.models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author xinch
 */
public class LoginController {

    ChatMessageDAO chatMessageDAO = new ChatMessageDAO();
    UserDAO userDAO = new UserDAO();
    @FXML
    private TextField txtUser;
    @FXML
    private TextField txtPass;
    @FXML
    private Button buttonLogin;
    @FXML
    private Button buttonQuenMK;
    @FXML
    private Button buttonDangKy;
    @FXML
    private Button buttonGG;
    @FXML
    private Button buttonFace;
    @FXML
    private Button buttonIOS;

    @FXML
    private void handleLogin() {
        // Lấy dữ liệu người dùng nhập
        String usernameOrEmail = txtUser.getText().trim();
        String password = txtPass.getText().trim();

        // Kiểm tra ô nhập liệu có bị bỏ trống không
        if (isInputEmpty(usernameOrEmail, password)) {
            showStatus("Vui lòng nhập đầy đủ Email/Username và Password.", false);
            return;
        }
    
        // Kiểm tra thông tin đăng nhập với CSDL
        boolean loginSuccessful = userDAO.validateUser(usernameOrEmail, password);

        if (loginSuccessful) {
            showStatus("Đăng nhập thành công!", true);
            Platform.runLater(() -> {
                try {
                    User userLogged = userDAO.getUserInfoByUsername(usernameOrEmail);
                    // ➜ Lưu toàn bộ thông tin người dùng vào UserSession
                    UserSession.getInstance().setUserInfo(userLogged.getUserID(), userLogged.getAvatar(), userLogged.getUsername(), userLogged.getSubscriptionPlan(), userLogged.getEmail(), userLogged.getPasswordHash());
                    FXMLLoader fXMLLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
                    Parent root = fXMLLoader.load();
                    Scene newScene = new Scene(root, 1187, 668);

                    Stage newStage = (Stage) buttonLogin.getScene().getWindow();
                    newStage.setScene(newScene);
                    newStage.centerOnScreen();

                    newStage.setResizable(false);

                    newStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    showStatus("Lỗi khi chuyển màn hình chính ", false);
                }
            });

        } else {
            showStatus("Email/Username hoặc Password không đúng.", false);
        }
    }

    /**
     * Kiểm tra dữ liệu có bị trống không
     */
    private boolean isInputEmpty(String usernameOrEmail, String password) {
        return usernameOrEmail.isEmpty() || password.isEmpty();
    }

    /**
     * Hiển thị trạng thái đăng nhập (thành công/thất bại)
     */
    private void showStatus(String message, boolean isSuccess) {
        Alert alert = new Alert(isSuccess ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(isSuccess ? "Thành công" : "Lỗi");
        alert.setHeaderText(null);  // Không cần header
        alert.setContentText(message);
        alert.showAndWait(); // Hiện thông báo và chờ người dùng bấm OK
    }

    @FXML
    private void handleQuenMK(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("Quenmk.fxml"));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            Stage stage = (Stage) buttonQuenMK.getScene().getWindow();
            stage.setScene(newScene);
            stage.centerOnScreen();  // 🔹 Căn giữa màn hình
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Không thể mở giao diện Quên mật khẩu.", false);
        }
    }

    @FXML
    private void handleDangKy(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("DangKy.fxml"));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            Stage stage = (Stage) buttonDangKy.getScene().getWindow();
            stage.setScene(newScene);
            stage.centerOnScreen(); // 🔹 Căn giữa màn hình
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Không thể mở giao diện Đăng ký.", false);
        }
    }

}
