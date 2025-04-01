/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;


import com.javaee.test1.controllers.ChatMessageDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.UUID;

/**
 * Controller cho cửa sổ đổi tên cuộc hội thoại
 */
public class DoiTenCuocHoiThoaiController {
    @FXML
    private TextField txtNewTitle;

    @FXML
    private Button btnCancel; // Nút Hủy
    @FXML
    private Button btnConfirm; // Nút Xác nhận

    private ChatMessageDAO chatDAO = new ChatMessageDAO();
    private UUID conversationId;

    /**
     * Được gọi từ bên ngoài để gán ConversationID và tải tiêu đề hiện tại
     */
    public void setConversationId(UUID id) {
        this.conversationId = id;
//        loadCurrentTitle();
    }

    /**
     * Tải tên cuộc trò chuyện hiện tại nếu có
     */
//    private void loadCurrentTitle() {
//        if (conversationId == null) {
//            showAlert("Lỗi", "Không tìm thấy cuộc hội thoại!", Alert.AlertType.ERROR);
//            return;
//        }
//
//        String currentTitle = chatDAO.getConversationTitle(conversationId);
//        if (currentTitle != null && !currentTitle.isEmpty()) {
//            txtNewTitle.setText(currentTitle);
//        } else {
//            txtNewTitle.setPromptText("Nhập tên mới...");
//        }
//    }

    /**
     * Sự kiện khi bấm nút Hủy: Đóng cửa sổ
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Sự kiện khi bấm nút Xác nhận: Đổi tên cuộc hội thoại trong SQL
     */
    @FXML
    private void handleConfirm() {
        if (conversationId == null) {
            showAlert("Lỗi", "Không tìm thấy cuộc hội thoại!", Alert.AlertType.ERROR);
            return;
        }

        String newTitle = txtNewTitle.getText().trim();
        if (newTitle.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tên cuộc hội thoại mới!", Alert.AlertType.ERROR);
            return;
        }

        // Cập nhật tên cuộc hội thoại
        if (chatDAO.updateConversationTitle(conversationId, newTitle)) {
            showAlert("Thành công", "Tên cuộc hội thoại đã được cập nhật!", Alert.AlertType.INFORMATION);

            // Đóng cửa sổ sau khi đổi tên thành công
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Lỗi", "Không thể cập nhật tên. Vui lòng thử lại!", Alert.AlertType.ERROR);
        }
    }

    /**
     * Hiển thị thông báo lỗi hoặc thành công
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
