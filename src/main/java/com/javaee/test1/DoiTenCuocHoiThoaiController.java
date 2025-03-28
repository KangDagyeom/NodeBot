/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.UUID;

/**
 * @author xinch
 */
public class DoiTenCuocHoiThoaiController {
    @FXML
    private TextField txtNewTitle;

    @FXML
    private Button btnCancel;    // Nút Hủy
    @FXML
    private Button btnConfirm;   // Nút Xác nhận

    private UserDAO userDAO = new UserDAO();

    private UUID conversationId;

    // Được gọi từ bên ngoài, gán ConversationID và load tiêu đề hiện tại (nếu muốn)
    public void setConversationId(UUID id) {
        this.conversationId = id;
        loadCurrentTitle();
    }

    // Lấy tên cuộc trò chuyện hiện tại để hiển thị
    private void loadCurrentTitle() {
        String currentTitle = userDAO.getConversationTitle(conversationId);
        if (currentTitle != null) {
            txtNewTitle.setText(currentTitle);
        }
    }

    // Sự kiện nút Hủy: Đóng cửa sổ
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    // Sự kiện nút Xác nhận: Đổi tên cuộc trò chuyện
    @FXML
    private void handleConfirm() {
        String newTitle = txtNewTitle.getText().trim();
        if (newTitle.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tên cuộc trò chuyện!", Alert.AlertType.ERROR);
            return;
        }

        boolean success = userDAO.updateConversationTitle(conversationId, newTitle);
        if (success) {
            showAlert("Thành công", "Tên cuộc trò chuyện đã được cập nhật!", Alert.AlertType.INFORMATION);
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Lỗi", "Không thể đổi tên cuộc trò chuyện. Vui lòng thử lại!", Alert.AlertType.ERROR);
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
