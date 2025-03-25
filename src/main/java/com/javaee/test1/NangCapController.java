/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;
import com.javaee.test1.controllers.UserDAO;
import javafx.scene.control.Label;
import com.javaee.test1.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 *
 * @author dokie
 */
public class NangCapController {
    @FXML
    private Label currentPlanLabel;

    @FXML
    private Button basicPlanButton, proPlanButton; // Chỉ giữ 2 nút vì giao diện bạn có 2 gói

    private User currentUser;
    private UserDAO userDAO;
    

    @FXML
    public void setData(User user, UserDAO userDAO) {
        this.currentUser = user;
        this.userDAO = userDAO;
        currentPlanLabel.setText("Gói hiện tại: " + user.getSubscriptionPlan());
    }

    @FXML
    private void chooseBasicPlan() {
        updatePlan("Miễn phí");
    }

    @FXML
    private void chooseProPlan() {
        updatePlan("Plus");
    }
    
    @FXML
    private void updatePlan(String plan) {
        userDAO.updateSubscriptionPlan(currentUser, plan);
        currentPlanLabel.setText("Gói hiện tại: " + currentUser.getSubscriptionPlan());
        System.out.println("Đã chuyển sang gói: " + plan);
    }
}

