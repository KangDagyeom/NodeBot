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
 *
 * @author xinch
 */
public class DoiTenCuocHoiThoaiController {
 @FXML
    private TextField txtNewTitle;
    @FXML
    private Button btnDoiTen, btnHuy, btnThoat;

    private UUID conversationId; // ID cuộc hội thoại cần đổi tên
    private UserDAO userDAO = new UserDAO();

    public void setConversationId(UUID id) {
        this.conversationId = id;
    }

    // ✅ Xử lý đổi tên cuộc hội thoại
    @FXML
    private void doiTenHoiThoai() {
        String newTitle = txtNewTitle.getText().trim();
        if (newTitle.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tên mới!!", Alert.AlertType.ERROR);
            return;
        }

        boolean success = userDAO.updateConversationTitle(conversationId, newTitle);
        if (success) {
            showAlert("Thành công", "Tên cuộc trò chuyện đã được cập nhật!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Không thể đổi tên. Vui lòng thử lại!", Alert.AlertType.ERROR);
        }
    }

    // 🔄 Hủy nhập tên mới
    @FXML
    private void huyDoiTen() {
        txtNewTitle.clear();
    }

    // 🔙 Thoát khỏi giao diện
    @FXML
    private void thoatGiaoDien() {
        Stage stage = (Stage) btnThoat.getScene().getWindow();
        stage.close();
    }

    // ⚠️ Hiển thị thông báo
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
   
}
