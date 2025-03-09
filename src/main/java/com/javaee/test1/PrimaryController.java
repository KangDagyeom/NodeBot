package com.javaee.test1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML
    private TextField inputField;

    @FXML
    private Button submitButton;

    @FXML
    private Label resultLabel;

    @FXML
    private void handleSubmit(ActionEvent event) {
        String inputText = inputField.getText().trim();
        if (!inputText.isEmpty()) {
            resultLabel.setText(inputText);
            inputField.clear();
        } else {
            resultLabel.setText("Vui lòng nhập văn bản!");
        }
    }
}
