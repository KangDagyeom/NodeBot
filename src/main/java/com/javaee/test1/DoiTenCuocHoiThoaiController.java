/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.ChatHistorySession;
import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.UUID;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 * Controller cho cửa sổ đổi tên cuộc hội thoại
 */
public class DoiTenCuocHoiThoaiController {

    @FXML
    private TextField txtTitle;

    @FXML
    private Button btnCancel; // Nút Hủy
    @FXML
    private Button btnConfirm; // Nút Xác nhận

    private UserDAO chatDAO = new UserDAO(); // Vẫn giữ UserDAO như bạn yêu cầu
    UserSession userSession = UserSession.getInstance();
    @FXML
    private VBox chatBox; // Đây là VBox chứa các hội thoại

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleConfirm() {

        // Lấy tên mới từ TextField và kiểm tra nếu trống
        String newTitle = txtTitle.getText().trim();
        if (newTitle.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tên cuộc hội thoại mới!", Alert.AlertType.ERROR);
            return;
        }
        ChatHistorySession chatHistorySession = ChatHistorySession.getInstance();
        if (chatDAO.updateUserID(chatHistorySession.getConversationId(), newTitle)) {
            showAlert("Thành công", "Tên cuộc hội thoại đã được cập nhật thành công!", Alert.AlertType.INFORMATION);
            
          
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Lỗi", "Không thể cập nhật tên cuộc hội thoại. Vui lòng thử lại!", Alert.AlertType.ERROR);
        }
    }
   





    /**
     * Hiển thị thông báo lỗi hoặc thành công
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
