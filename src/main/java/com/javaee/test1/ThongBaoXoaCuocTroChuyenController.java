/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.ChatHistorySession;
import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.UUID;

/**
 * @author xinch
 */
public class ThongBaoXoaCuocTroChuyenController {

    UserSession userSession = UserSession.getInstance();
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnConfirm;
    private String conversationID;
    private UserDAO chatDAO = new UserDAO(); // Vẫn giữ UserDAO như bạn yêu cầu
    private PrimaryController primaryController;

    public void setConversationID(String id) {
        this.conversationID = id;
    }

    public void setPrimaryController(PrimaryController controller) {
        this.primaryController = controller;
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleDelete() {
        // Lấy ID cuộc hội thoại hiện tại
        ChatHistorySession chatHistorySession = ChatHistorySession.getInstance();
        UUID conversationId = chatHistorySession.getConversationId();
        System.out.println(conversationId);
        if (conversationId == null) {
            showAlert("Lỗi", "Không tìm thấy cuộc trò chuyện!", Alert.AlertType.ERROR);
            return;
        }

        // Hiển thị hộp thoại xác nhận trước khi xóa
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa cuộc trò chuyện này?");
        alert.setContentText("Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (chatDAO.deleteConversation(conversationId)) {
                Platform.runLater(() -> {
                    try {

                        primaryController.loadConversations(userSession.getUserId());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


                Stage stage = (Stage) btnConfirm.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Lỗi", "Không thể xóa cuộc trò chuyện. Vui lòng thử lại!", Alert.AlertType.ERROR);
            }
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
