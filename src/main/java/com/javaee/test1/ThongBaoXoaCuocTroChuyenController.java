/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author xinch
 */
public class ThongBaoXoaCuocTroChuyenController {
//     @FXML
//    private Button huy;
//    @FXML
//    private Button xoa;
//     private String conversationTitle;
//
//    public void setConversationTitle(String title) {
//        this.conversationTitle = title;
//    }
//
//    @FXML
//    private void handleCancel(MouseEvent event) {
//        closeWindow(event);
//    }
//
//    @FXML
//    private void handleDelete(MouseEvent event) {
//        deleteConversationFromDatabase(conversationTitle);
//        closeWindow(event);
//    }
//
//    private void deleteConversationFromDatabase(String title) {
//        String query = "DELETE FROM ChatHistory WHERE conversation_title = ?";
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, title);
//            statement.executeUpdate();
//            System.out.println("Đã xóa cuộc trò chuyện: " + title);
//        } catch (SQLException e) {
//            System.out.println("Lỗi khi xóa cuộc trò chuyện: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void closeWindow(MouseEvent event) {
//        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
//        stage.close();
//    }
//    
}
