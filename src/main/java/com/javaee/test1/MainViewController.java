/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author dokie
 */
public class MainViewController {

    UserDAO userDAO = new UserDAO();
    UserSession session = UserSession.getInstance();
    ChatMessageDAO chatMessageDAO = new ChatMessageDAO();
    UserSession userSession = UserSession.getInstance();
    @FXML
    private VBox chatBox;
    @FXML
    private ScrollPane scrollPane;
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
    @FXML
    private TextArea inputField;
    @FXML
    private ImageView sendButton;
    @FXML
    private Button btnthemcuochoithoai;
    private String saveTitle;

    private void loadUserInfo() {
        // Lấy thông tin từ UserSession

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
        loadUserInfo();

        loadConversations(userDAO.getUserIdByUsername(userSession.getUsername()));
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSendMessage();
            }
        });
        sendButton.setOnMouseClicked(event -> handleSendMessage());

        labelNangcap.setOnMouseClicked(event -> openUpgradePlan());

        //gán sự kiện cho xóa hết cuộc hội thoại
        labelXoahoithoai.setOnMouseClicked(event -> deleteall());

        labelLogout.setOnMouseClicked(event -> handleLogout(event));
        
        btnthemcuochoithoai.setOnAction(event -> themCuocHoiThoaiMoi());
    }

    public void loadAllData() {
        loadUserInfo();
        loadConversations(userDAO.getUserIdByUsername(userSession.getUsername()));
    }

    private void handleSendMessage() {
        String userInput = inputField.getText().trim();

        if (!userInput.isEmpty()) {
            MessageHolder.getInstance().setLastMessage(userInput);
            inputField.clear();
            openPrimaryChat();
        }
    }

    @FXML
    private void openPrimaryChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/primary.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) inputField.getScene().getWindow();

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    //==========
    //delete all
    @FXML
    private void deleteall() {
        System.out.println("Label Delete ALL đã được click!"); // Debug

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/nutxacminhxoacuochoithoai.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Delete ALL");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Cửa sổ Delete ALL đã mở!"); // Debug
        } catch (IOException e) {
            System.out.println("Lỗi khi mở trang: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //
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

            conversationLabel.setOnMouseClicked(event -> {
                saveTitle = conversationLabel.getText();
                ChatHistorySession chatHistorySession = ChatHistorySession.getInstance();
                chatHistorySession.setChatHistoryInfo(userDAO.getConversationIdByTitle(saveTitle), userDAO.getUserIdByUsername(session.getUsername()), saveTitle);
                Platform.runLater(() -> {
                    try {
                        // Chuyển sang giao diện HanhDongCuocHoiThoai.fxml
                        FXMLLoader fXMLLoader = new FXMLLoader(App.class.getResource("hanhdongcuochoithoai.fxml"));
                        Parent root = fXMLLoader.load();
                        Scene newScene = new Scene(root, 125, 120);

                        HanhDongCuocHoiThoai controller = fXMLLoader.getController();

                        // ✅ Truyền MainViewController vào HanhDongCuocHoiThoaiController
                        controller.setMainViewController(this);  // <-- thêm dòng này

                        Stage newStage = new Stage();
                        newStage.setScene(newScene);
                        newStage.centerOnScreen();
                        newStage.setTitle("Hành Động Cuộc Hội Thoại");
                        newStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/Node_logo.jpg")));
                        newStage.setResizable(false);

                        newStage.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                System.out.println(saveTitle);
            });

            conversationCon.getChildren().add(conversationLabel);
        }

    }
    
    @FXML
private void themCuocHoiThoaiMoi() {
    // Tạo tiêu đề mặc định, ví dụ: "Cuộc hội thoại 1", "Cuộc hội thoại 2",...
    int stt = userDAO.countConversationByUserId(userSession.getUserId()) + 1;
    String newTitle = "Cuộc hội thoại " + stt;

    boolean success = userDAO.addNewConversation(userSession.getUserId(), newTitle);

    if (success) {
        showAlert("Thành công", "Đã thêm cuộc hội thoại mới!", Alert.AlertType.INFORMATION);
        loadConversations(userSession.getUserId());
    } else {
        showAlert("Lỗi", "Không thể thêm cuộc hội thoại mới!", Alert.AlertType.ERROR);
    }
}
}
