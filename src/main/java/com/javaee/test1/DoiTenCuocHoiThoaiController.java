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
 * Controller cho cửa sổ đổi tên cuộc hội thoại
 */
public class DoiTenCuocHoiThoaiController {

    @FXML
    private TextField txtNewTitle;

    @FXML
    private Button btnCancel; // Nút Hủy
    @FXML
    private Button btnConfirm; // Nút Xác nhận

    private UserDAO chatDAO = new UserDAO(); // Vẫn giữ UserDAO như bạn yêu cầu
    private UUID userID; // Sử dụng UUID thay vì String

    public void setUserID(UUID id) {
        this.userID = id;
//        loadCurrentTitle();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleConfirm() {
        /// Kiểm tra nếu không có userId (có thể do chưa chọn hoặc lỗi logic)
        if (userID== null) {
            showAlert("Lỗi", "Không tìm thấy cuộc hội thoại để cập nhật!", Alert.AlertType.ERROR);
            return;
        }

        // Lấy tên mới từ TextField và kiểm tra nếu trống
        String newTitle = txtNewTitle.getText().trim();
        if (newTitle.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tên cuộc hội thoại mới!", Alert.AlertType.ERROR);
            return;
        }
        
        if (chatDAO.updateUserID(userID, newTitle)) {
            showAlert("Thành công", "Tên cuộc hội thoại đã được cập nhật thành công!", Alert.AlertType.INFORMATION);

            // Đóng cửa sổ sau khi đổi tên thành công
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
