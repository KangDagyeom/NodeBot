/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import com.javaee.test1.controllers.UserDAO;
//import javafx.scene.control.Label;
//import com.javaee.test1.controllers.ChatMessageDAO;
//import com.javaee.test1.controllers.UserDAO;
//import com.javaee.test1.models.ChatMessage;
//import javafx.animation.Animation;
//import javafx.animation.KeyFrame;
//import javafx.animation.ScaleTransition;
//import javafx.animation.Timeline;
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.*;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.text.TextFlow;
//import javafx.util.Duration;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
////đổi ten theo controller của từng cái 
//import com.javaee.test1.DoiThongTinCaNhan;
//import com.javaee.test1.DoiTenCuocHoiThoaiController;
////import com.javaee.test1.controllers.DeleteConversationController;
//import com.javaee.test1.NangCapController;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.javaee.test1.controllers.UserDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

// Import controller theo từng chức năng


/**
 *
 * @author dokie
 */
public class MainViewController {
    UserDAO userDAO = new UserDAO();
    private List<String> historyList = new ArrayList<>();

    @FXML
    private Button txtNode;
    @FXML
    private Button txtClickcuochoithoai;
    @FXML
    private Label labelNangcap;
    @FXML
    private Button buttonCuoctrochuyenmoi;
    @FXML
    private Label labelLichsutrochuyen;
    // có để khi bấm vào cuộc trò chuyện mới sẽ xóa hết tin nhắn đang nhắn trong TextArea
    @FXML
    private TextArea textAreaChat;
    @FXML
    private Label labelXoahoithoai; // Label nút xóa
    @FXML
    private Label labelLogout;

    //gán sự kiện cho từng nút
    @FXML
    private void initialize() {
        // Gán sự kiện click cho button
        txtNode.setOnAction(event -> openEditProfile());
        //gán sự kien click cuoc hội thoại
        txtClickcuochoithoai.setOnAction(event -> handleClick());
        //gán sự kiện bấm vào label nâng cấp chuyển sang trang nâng cấp gói
        labelNangcap.setOnMouseClicked(event -> openUpgradePlan());
        //GÁN SỰ KIỆN CUỘC TRÒ CHUYỆN MỚI
        buttonCuoctrochuyenmoi.setOnAction(event -> startNewConversation());
        //gán sự kiện cho xóa hết cuộc hội thoại
        labelXoahoithoai.setOnMouseClicked(event -> {
            xoaTatCaLichSu();
        });
        labelLogout.setOnMouseClicked(event -> handleLogout(event));
    }

    //===========================================================================
    //bấm vào button avatar thì chuyển sang trang  trỉnh sửa thông tin cá nhân
    @FXML
    private void openEditProfile() {      
        try {
            // Load FXML của trang chỉnh sửa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Thongtincanhan.fxml"));//đổi đường dẫn thì nó mới load sang trang 
            Parent root = loader.load();

            // Lấy controller của EditProfile để truyền dữ liệu nếu cần
            DoiThongTinCaNhan controller = loader.getController();
            // Giả sử bạn có user hiện tại, bạn có thể truyền sang
            // controller.setCurrentUser(currentUser);

            // Tạo stage mới hiển thị
            Stage stage = new Stage();
            stage.setTitle("Chỉnh sửa thông tin");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //====================================================================================================================================
    //Click vào hội thoại chuyên sang đổi tên hội thoại và xóa hội thoại
    @FXML
    private void handleClick() {
        try {
            // ======= Giao diện ĐỔI TÊN ========
            FXMLLoader renameLoader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Thongbaodoiten.fxml"));
            Parent renameRoot = renameLoader.load();
            // Lấy controller của RenameConversation
            DoiTenCuocHoiThoaiController renameController = renameLoader.getController();
            // Có thể gọi hàm hoặc set dữ liệu ở đây nếu muốn, ví dụ:
            // renameController.setConversationData(conversation);

            Stage renameStage = new Stage();
            renameStage.setTitle("Đổi tên hội thoại");
            renameStage.setScene(new Scene(renameRoot));
            renameStage.show();

            // ======= Giao diện XÓA HỘI THOẠI ========
            FXMLLoader deleteLoader = new FXMLLoader(getClass().getResource("/com/javaee/test1/DeleteConversation.fxml"));
            Parent deleteRoot = deleteLoader.load();
            // Lấy controller của DeleteConversation
//            DeleteConversationController deleteController = deleteLoader.getController();
            // Có thể gọi hàm hoặc set dữ liệu ở đây nếu muốn, ví dụ:
            // deleteController.setConversationData(conversation);

            Stage deleteStage = new Stage();
            deleteStage.setTitle("Xóa hội thoại");
            deleteStage.setScene(new Scene(deleteRoot));
            deleteStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //clik vào sẽ chuyển sang trang nâng cấp node
    @FXML
    private void openUpgradePlan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Nanngcap.fxml"));//doi link dan vao controller cua nang cap goi
            Parent root = loader.load();

            // Lấy controller nếu cần tương tác
            NangCapController controller = loader.getController();
            // controller.setupData(...); // nếu có truyền dữ liệu

            Stage stage = new Stage();
            stage.setTitle("Nâng cấp gói");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=======================================================================================================
    //CUỘC TRÒ CHUYỆN MỚI VÀ LƯU LỊCH SỬ CUỘC TRÒ CHUYỆN CŨ VÀO LABEL
    @FXML
    private void startNewConversation() {
        // Lưu nội dung cuộc trò chuyện hiện tại
        String currentConversation = getCurrentConversation(); // Viết hàm này để lấy nội dung đang chat
        if (!currentConversation.isEmpty()) {
            historyList.add(currentConversation);
        }

        // Clear nội dung chat (giả sử bạn có text area hoặc list message)
        clearChat();

        // Hiển thị lịch sử
        showHistory();

        // Có thể reload lại giao diện nếu muốn:
        // reloadScene();
    }

    @FXML
    private void showHistory() {
        StringBuilder sb = new StringBuilder();
        for (String convo : historyList) {
            sb.append(convo).append("\n");
        }
        labelLichsutrochuyen.setText(sb.toString());
    }

    @FXML
    private String getCurrentConversation() {
        // Lấy nội dung chat hiện tại (tùy vào bạn hiển thị message như nào)
        // Ví dụ:
        return "Cuộc trò chuyện " + (historyList.size() + 1);
    }

    @FXML
    private void clearChat() {

        textAreaChat.clear(); // Xóa sạch nội dung text area
    }

    @FXML
    private void reloadScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/views/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) buttonCuoctrochuyenmoi.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=======================================
    @FXML
    private void xoaTatCaLichSu() {
        labelLichsutrochuyen.setText(""); // Xóa hết nội dung
        System.out.println("Đã xóa tất cả lịch sử cuộc trò chuyện!");
    }

    //log out
    @FXML
    private void handleLogout(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText("Bạn có chắc muốn đăng xuất không?");
        alert.setContentText("Chọn OK để đăng xuất.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Đóng cửa sổ hiện tại
            ((Stage) labelLogout.getScene().getWindow()).close();

            // Mở lại màn hình đăng nhập
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Login.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Đăng nhập");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
