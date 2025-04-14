/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import com.javaee.test1.controllers.UserDAO;
import com.javaee.test1.controllers.UserSession;
import com.javaee.test1.models.User;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * @author dokie
 */
public class NangCapController {
    @FXML
    private Label currentPlanLabel;

    @FXML
    private Button basicPlanButton;
    @FXML
    private Button proPlanButton;
    @FXML
    private AnchorPane mainContainer;
    private User currentUser;
    private UserDAO userDAO;
    private UserSession userSessịon;

    public void setUserSession(UserSession userSession) {
        this.userSessịon = userSession;
        this.userDAO = new UserDAO(); // ← KHỞI TẠO ở đây là đủ
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Duration delay = Duration.millis(150);
            int index = 0;

            for (Node node : mainContainer.getChildren()) {
                // Ban đầu ẩn đi
                node.setOpacity(0);
                node.setTranslateY(20);

                // Hiệu ứng trượt và mờ
                TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
                slide.setFromY(20);
                slide.setToY(0);
                slide.setInterpolator(Interpolator.EASE_OUT);

                FadeTransition fade = new FadeTransition(Duration.millis(300), node);
                fade.setFromValue(0);
                fade.setToValue(1);

                ParallelTransition transition = new ParallelTransition(slide, fade);
                transition.setDelay(delay.multiply(index));
                transition.play();

                index++;
            }
        });
    }


    public void switchToPaymentView(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/Payment.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.centerOnScreen();
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToMainView(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/mainview.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.centerOnScreen();
            newStage.show();

            // Đóng stage cũ
            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

