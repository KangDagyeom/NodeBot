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
/// /đổi ten theo controller của từng cái
//import com.javaee.test1.DoiThongTinCaNhan;
//import com.javaee.test1.DoiTenCuocHoiThoaiController;
/// /import com.javaee.test1.controllers.DeleteConversationController;
//import com.javaee.test1.NangCapController;

import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Import controller theo từng chức năng

/**
 * @author dokie
 */
public class MainViewController {

    UserDAO userDAO = new UserDAO();
    UserSession session = UserSession.getInstance();
    private List<String> historyList = new ArrayList<>();
    @FXML
    private Label labelNangcap;
    @FXML
    private Label labelXoahoithoai; // Label nút xóa
    @FXML
    private Label labelLogout;
    @FXML
    private Label labelLichsutrochuyen;
    @FXML
    private VBox conversationCon;
    @FXML
    private Label txtTenND;
    @FXML
    private Label txtTaiKhoanND;
    @FXML
    private ImageView imgAvatar;
    //gán sự kiện cho từng nút
    private String saveTitle;

    private void loadUserInfo() {
        // Lấy thông tin từ UserSession
        UserSession userSession = UserSession.getInstance();
        UUID userId = userSession.getUserId();

        if (userId == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin người dùng từ phiên đăng nhập!", Alert.AlertType.ERROR);
            return;
        }
        // Cập nhật tên và loại tài khoản từ UserSession lên giao diện
        txtTenND.setText(userSession.getUsername() != null ? userSession.getUsername() : "Chưa có tên");
        txtTaiKhoanND.setText(userSession.getSubscriptionPlan() != null ? userSession.getSubscriptionPlan() : "Không xác định");

        // Hiển thị avatar từ UserSession (nếu có)
        String avatarPath = userSession.getAvatar();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    Image avatarImage = new Image(avatarFile.toURI().toString());
                    imgAvatar.setImage(avatarImage);

                    // 📌 Căn ảnh sát trái
                    imgAvatar.setPreserveRatio(true);  // Giữ tỷ lệ ảnh
                    imgAvatar.setFitWidth(55);        // Điều chỉnh chiều rộng
                    imgAvatar.setFitHeight(55);       // Điều chỉnh chiều cao
                    imgAvatar.setSmooth(true);        // Làm mịn ảnh
                    imgAvatar.setCache(true);         // Tăng hiệu suất load ảnh

                    imgAvatar.setTranslateX(500); // Di chuyển ảnh sang trái (âm là trái, dương là phải)
                    imgAvatar.setTranslateY(0);   // Di chuyển ảnh xuống dưới (âm là lên trên, dương là xuống dưới)

                    // 📌 Làm tròn avatar
                    Circle clip = new Circle(25, 25, 25); // Tạo clip hình tròn (bán kính 25px)
                    imgAvatar.setClip(clip); // Đặt hình cắt tròn vào avatar

                    // 📌 Nếu imgAvatar nằm trong HBox, căn sát trái
                    HBox.setHgrow(imgAvatar, Priority.NEVER);
                    imgAvatar.setTranslateX(-10); // Dịch ảnh về bên trái (tùy chỉnh)

                } else {
                    showAlert("Lỗi", "Không tìm thấy tệp ảnh đại diện!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể tải ảnh đại diện!", Alert.AlertType.ERROR);
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

    @FXML
    private void initialize() {
        loadUserInfo(); // Gọi loadUserInfo() khi view được hiển thị
        //gán sự kiện bấm vào label nâng cấp chuyển sang trang nâng cấp gói
        labelNangcap.setOnMouseClicked(event -> openUpgradePlan());

        //gán sự kiện cho xóa hết cuộc hội thoại
        labelXoahoithoai.setOnMouseClicked(event -> deleteAllConversations());

        labelLogout.setOnMouseClicked(event -> handleLogout(event));
    }

    //===========================================================================
    //bấm vào button avatar thì chuyển sang trang  trỉnh sửa thông tin cá nhân
    @FXML
    private void openEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Thongtincanhan.fxml"));
            Parent root = loader.load();

            // Lấy stage hiện tại từ button avatar
            Stage stage = (Stage) imgAvatar.getScene().getWindow();

            // Cập nhật scene với root mới
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //====================================================================================================================================
//    //Click vào hội thoại chuyên sang đổi tên hội thoại và xóa hội thoại
    @FXML
    private void handleClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Chọn hành động");
        alert.setHeaderText("Bạn muốn làm gì??");
        alert.setContentText("Chọn một trong hai hành động bên dưới:");

        ButtonType renameOption = new ButtonType("Đổi tên hội thoại");
        ButtonType deleteOption = new ButtonType("Xóa hội thoại");
        ButtonType cancelOption = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(renameOption, deleteOption, cancelOption);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == renameOption) {
                openRenameConversation();
            } else if (result.get() == deleteOption) {
                openDeleteConversation();
            }
        }
    }

    // Mở cửa sổ đổi tên hội thoại
    private void openRenameConversation() {
        openWindow("/com/javaee/test1/Thongbaodoiten.fxml", "Đổi tên hội thoại");
    }

    // Mở cửa sổ xóa hội thoại
    private void openDeleteConversation() {
        openWindow("/com/javaee/test1/Thongbaoxoacuoctrochuyen.fxml", "Xóa hội thoại");
    }

    // Hàm dùng chung để mở cửa sổ
    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Đặt chế độ MODAL
            stage.showAndWait(); // Chờ người dùng thao tác xong
        } catch (IOException e) {
            showError("Lỗi mở cửa sổ", "Không thể tải giao diện: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Hiển thị thông báo lỗi
    private void showError(String title, String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    //clik vào sẽ chuyển sang trang nâng cấp node
    @FXML
    private void openUpgradePlan() {
        System.out.println("Label Nâng cấp đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Nanngcap.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nâng cấp gói");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Cửa sổ nâng cấp đã mở!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang nâng cấp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //=======================================
    //log out
    @FXML

    private void handleLogout(MouseEvent event) {
        System.out.println("Label Đăng xuất đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/nutdangxuat.fxml")); // Đúng file cần mở
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow(); // Lấy stage hiện tại
            stage.setTitle("Đăng xuất");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Chuyển đến màn hình Đăng xuất thành công!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang Đăng xuất: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //delete all
    @FXML
    private void deleteAllConversations() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc muốn xóa tất cả cuộc hội thoại không?");
        alert.setContentText("Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            historyList.clear(); // Xóa hết danh sách hội thoại
            System.out.println("Tất cả hội thoại đã bị xóa!");

            // Cập nhật lại giao diện
            updateUIAfterDelete();
        }
    }
// Hàm cập nhật giao diện sau khi xóa hội thoại

    private void updateUIAfterDelete() {
        labelLichsutrochuyen.setText("Không có cuộc hội thoại nào"); // Cập nhật label
    }

    /// / thêm cuộc trò chuyện
//    @FXML
//    private void startNewConversation() {
//        // Lấy nội dung cuộc trò chuyện hiện tại
//        String currentConversation = getCurrentConversation();
//
//        // Kiểm tra xem cuộc trò chuyện có nội dung không trước khi lưu vào lịch sử
//        if (currentConversation != null && !currentConversation.trim().isEmpty()) {
//            historyList.add(currentConversation);
//        }
//
//        // Hiển thị lịch sử cuộc trò chuyện
//        showHistory();
//    }
//
//    @FXML
//    private void showHistory() {
//        if (historyList.isEmpty()) {
//            labelLichsutrochuyen.setText("Không có cuộc hội thoại nào");
//        } else {
//            StringBuilder sb = new StringBuilder();
//            for (String convo : historyList) {
//                sb.append(convo).append("\n");
//            }
//            labelLichsutrochuyen.setText(sb.toString());
//        }
//    }
    @FXML
    private void startNewConversation() {
        // Lấy nội dung cuộc trò chuyện hiện tại
        String currentConversation = getCurrentConversation();

        // Kiểm tra xem cuộc trò chuyện có nội dung không trước khi lưu vào lịch sử
        if (currentConversation != null && !currentConversation.trim().isEmpty()) {
            historyList.add(0, currentConversation); // Thêm vào đầu danh sách thay vì cuối
        }

        // Hiển thị lịch sử cuộc trò chuyện
        showHistory();
    }

    @FXML
    private void showHistory() {
        if (historyList.isEmpty()) {
            labelLichsutrochuyen.setText("Không có cuộc hội thoại nào");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String convo : historyList) {
                sb.append(convo).append("\n");
            }
            labelLichsutrochuyen.setText(sb.toString()); // Hiển thị theo thứ tự mới nhất ở trên
        }
    }

    //==================================================================================================
    @FXML
    private String getCurrentConversation() {
        // Lấy nội dung chat hiện tại (tùy vào bạn hiển thị message như nào)
        // Ví dụ:
        return "Cuộc trò chuyện " + (historyList.size() + 1);
    }

    public void loadConversations(UUID userId) {
        ArrayList<String> conversationNames = userDAO.loadConversation(userId);
        conversationCon.getChildren().clear();
        for (String title : conversationNames) {
            Label conversationLabel = new Label(title);
            ImageView imageView1 = new ImageView(getClass().getResource("/img/Item.png").toExternalForm());
            ImageView imageView2 = new ImageView(getClass().getResource("/img/buttonpick.png").toExternalForm());

            conversationLabel.setTextFill(Color.WHITE);
            conversationLabel.setGraphic(imageView1);
            conversationLabel.setContentDisplay(ContentDisplay.LEFT);
            conversationLabel.setGraphicTextGap(10);
            conversationLabel.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");

            conversationLabel.setOnMouseEntered(event -> {
                conversationLabel.setGraphic(imageView2);
                conversationLabel.setContentDisplay(ContentDisplay.RIGHT);
            });

            conversationLabel.setOnMouseExited(event -> {
                conversationLabel.setGraphic(imageView1);
                conversationLabel.setContentDisplay(ContentDisplay.LEFT);
            });

//            conversationLabel.setOnMouseClicked(event -> {
//                saveTitle = conversationLabel.getText();
//                loadChatHistory(userDAO.getConversationIdByTitle(saveTitle));
//
//                System.out.println(saveTitle);
//            });
            conversationCon.getChildren().add(conversationLabel);
        }

    }
}
