/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import com.javaee.test1.controllers.UserSession;
import com.javaee.test1.controllers.UserDAO;


/**
 *
 * @author xinch
 */
public class ThongBaoXoaCuocTroChuyenController {
    @FXML
    private Button huy;
    @FXML
    private Button xoa;

    private String conversationID;

    public void setConversationID(String id) {
        this.conversationID = id;
    }

    @FXML
    private void handleCancel(MouseEvent event) {
        closeWindow(event);
    }

    @FXML
    private void handleDelete(MouseEvent event) {
        if (conversationID == null || conversationID.trim().isEmpty()) {
            showAlert("Lỗi", "ID cuộc trò chuyện không hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        // ✅ Lấy userId từ UserSession
        String userId = UserSession.getInstance().getUserId().toString();
        if (userId == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin người dùng!", Alert.AlertType.ERROR);
            return;
        }

        // ✅ Xác nhận trước khi xóa
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa cuộc trò chuyện này? Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isDeleted = deleteConversationFromDatabase(conversationID, userId);

            if (isDeleted) {
                showAlert("Thành công", "Cuộc trò chuyện đã được xóa!", Alert.AlertType.INFORMATION);
                closeWindow(event);
            } else {
                showAlert("Lỗi", "Không thể xóa cuộc trò chuyện!", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean deleteConversationFromDatabase(String conversationId, String userId) {
        String query = "DELETE FROM ChatHistory WHERE ConversationID = ? AND UserID = ?";
        try (Connection connection = UserDAO.getConnect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (connection == null) {
                showAlert("Lỗi", "Không thể kết nối đến cơ sở dữ liệu!", Alert.AlertType.ERROR);
                return false;
            }

            statement.setString(1, conversationId);
            statement.setString(2, userId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu xóa thành công

        } catch (SQLException e) {
            showAlert("Lỗi", "Lỗi khi xóa cuộc trò chuyện: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
