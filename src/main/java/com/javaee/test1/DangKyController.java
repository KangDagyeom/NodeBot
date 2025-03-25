/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.ChatMessageDAO;
import com.javaee.test1.controllers.UserDAO;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author xinch
 */
public class DangKyController {

    ChatMessageDAO chatMessageDAO = new ChatMessageDAO();
    UserDAO userDAO = new UserDAO();
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtUser;
    @FXML
    private TextField txtPass;
    @FXML
    private Button buttonSigh;
    @FXML
    private Button buttonLogin;
    @FXML
    private Button buttonGG;
    @FXML
    private Button buttonIOS;
    @FXML
    private Button buttonFace;
    @FXML
    private ImageView imageAvatar; // ImageView để hiển thị ảnh

    private String avatarPath = ""; // Lưu đường dẫn ảnh đã chọn

    @FXML
    private void initialize() {
        buttonSigh.setOnAction(event -> handleSignUp());

        // Khi người dùng bấm vào imageAvatar, mở hộp thoại chọn file
        imageAvatar.setOnMouseClicked(event -> selectAvatar());
    }

    @FXML
    private void handleSignUp() {
        String email = txtEmail.getText().trim();
        String username = txtUser.getText().trim();
        String password = txtPass.getText().trim();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        boolean success = userDAO.createUser(email, username, password, avatarPath); // Lưu đường dẫn ảnh

        if (success) {
            showAlert(AlertType.INFORMATION, "Thành công", "Tạo tài khoản thành công!");
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Lỗi", "Email đã tồn tại hoặc có lỗi hệ thống.");
        }
    }

    private void selectAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");

        // Chỉ cho phép chọn file ảnh
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            avatarPath = file.getAbsolutePath(); // Lưu đường dẫn ảnh

            // Hiển thị ảnh trong ImageView
            Image avatarImage = new Image(file.toURI().toString());
            imageAvatar.setImage(avatarImage);
        }
    }

    private void clearFields() {
        txtEmail.clear();
        txtUser.clear();
        txtPass.clear();

    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            Stage stage = (Stage) buttonLogin.getScene().getWindow();
            stage.setScene(newScene);
            stage.centerOnScreen(); // Căn giữa màn hình
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể mở giao diện Đăng nhập.");
        }
    }

}
