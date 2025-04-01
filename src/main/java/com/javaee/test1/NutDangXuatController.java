/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.event.ActionEvent;

/**
 * @author xinch
 */
public class NutDangXuatController {

    @FXML
    private Button exit;
    @FXML
    private Button logout;

    @FXML
    private void handleLogoutAndExit(ActionEvent event) {
        System.exit(0); // Thoát hoàn toàn ứng dụng
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javaee/test1/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Đăng nhập");
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
