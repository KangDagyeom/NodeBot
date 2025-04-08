/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @author dokie
 */
public class XoaTatCaCuocHoiThoai {

    UserSession userSession = UserSession.getInstance();
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;
    private UserDAO chatDAO = new UserDAO(); // Vẫn giữ UserDAO như bạn yêu cầu

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    //    @FXML
//    private void handleDelete() {
//        // Hiển thị hộp thoại xác nhận trước khi xóa tất cả cuộc trò chuyện
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Xác nhận xóa");
//        alert.setHeaderText("Bạn có chắc chắn muốn xóa tất cả cuộc trò chuyện?");
//        alert.setContentText("Hành động này không thể hoàn tác!");
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            if (chatDAO.deleteAllConversations()) {
//                showAlert("Thành công", "Tất cả cuộc trò chuyện đã được xóa!", Alert.AlertType.INFORMATION);
//
//                // Đóng cửa sổ sau khi xóa thành công
//                Stage stage = (Stage) btnConfirm.getScene().getWindow();
//                stage.close();
//            } else {
//                showAlert("Lỗi", "Không thể xóa tất cả cuộc trò chuyện. Vui lòng thử lại!", Alert.AlertType.ERROR);
//            }
//        }
//    }
    @FXML
    private void handleDelete() {
        // Xóa tất cả cuộc trò chuyện mà không cần xác nhận
        if (chatDAO.deleteAllConversations()) {
            showAlert("Thành công", "Tất cả cuộc trò chuyện đã được xóa!", Alert.AlertType.INFORMATION);

            // Đóng cửa sổ sau khi xóa thành công
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Lỗi", "Không thể xóa tất cả cuộc trò chuyện. Vui lòng thử lại!", Alert.AlertType.ERROR);
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
