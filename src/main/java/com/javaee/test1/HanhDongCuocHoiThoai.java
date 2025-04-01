/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author xinch
 */
public class HanhDongCuocHoiThoai {
    @FXML
    private Label doiten;
    @FXML
    private Label xoa;
    
//        //====================================================================================================================================
////    //Click vào hội thoại chuyên sang đổi tên hội thoại và xóa hội thoại
//    @FXML
//    private void handleClick() {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Chọn hành động");
//        alert.setHeaderText("Bạn muốn làm gì??");
//        alert.setContentText("Chọn một trong hai hành động bên dưới:");
//
//        ButtonType renameOption = new ButtonType("Đổi tên hội thoại");
//        ButtonType deleteOption = new ButtonType("Xóa hội thoại");
//        ButtonType cancelOption = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
//        alert.getButtonTypes().setAll(renameOption, deleteOption, cancelOption);
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent()) {
//            if (result.get() == renameOption) {
//                openRenameConversation();
//            } else if (result.get() == deleteOption) {
//                openDeleteConversation();
//            }
//        }
//    }
//
//    // Mở cửa sổ đổi tên hội thoại
//    private void openRenameConversation() {
//        openWindow("/com/javaee/test1/Thongbaodoiten.fxml", "Đổi tên hội thoại");
//    }
//
//    // Mở cửa sổ xóa hội thoại
//    private void openDeleteConversation() {
//        openWindow("/com/javaee/test1/Thongbaoxoacuoctrochuyen.fxml", "Xóa hội thoại");
//    }
//
//    // Hàm dùng chung để mở cửa sổ
//    private void openWindow(String fxmlPath, String title) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//            Parent root = loader.load();
//            Stage stage = new Stage();
//            stage.setTitle(title);
//            stage.setScene(new Scene(root));
//            stage.initModality(Modality.APPLICATION_MODAL); // Đặt chế độ MODAL
//            stage.showAndWait(); // Chờ người dùng thao tác xong
//        } catch (IOException e) {
//            showError("Lỗi mở cửa sổ", "Không thể tải giao diện: " + fxmlPath);
//            e.printStackTrace();
//        }
//    }
//
//    // Hiển thị thông báo lỗi
//    private void showError(String title, String message) {
//        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//        errorAlert.setTitle(title);
//        errorAlert.setHeaderText(null);
//        errorAlert.setContentText(message);
//        errorAlert.showAndWait();
//    }
    @FXML
    private void handleRenameConversation(MouseEvent event) {
        openWindow("/com/javaee/test1/Thongbaodoiten.fxml", "Đổi tên hội thoại");
    }

    @FXML
    private void handleDeleteConversation(MouseEvent event) {
        openWindow("/com/javaee/test1/Thongbaoxoacuoctrochuyen.fxml", "Xóa hội thoại");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println("Lỗi khi mở cửa sổ: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
