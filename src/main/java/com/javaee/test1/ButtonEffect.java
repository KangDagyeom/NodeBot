/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javaee.test1;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 *
 * @author culua
 */
public class ButtonEffect {
    // Hiệu ứng phóng to khi hover (có thể điều chỉnh thời gian)
    public static void applyZoomEffect(Button button, double hoverScale, double durationInSeconds) {
        // Khi di chuột vào (phóng to)
        button.setOnMouseEntered(event -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(durationInSeconds), button);
            scaleUp.setToX(hoverScale); // Phóng to đến hoverScale
            scaleUp.setToY(hoverScale);
            scaleUp.play();
        });

        // Khi di chuột ra (thu nhỏ lại kích thước gốc)
        button.setOnMouseExited(event -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(durationInSeconds), button);
            scaleDown.setToX(1.0); // Trở lại kích thước gốc (1.0)
            scaleDown.setToY(1.0);
            scaleDown.play();
        });
    }

    // Hiệu ứng khi click (thu nhỏ nhanh rồi trở lại kích thước hover)
    public static void applyClickEffect(Button button, double clickScale, double durationInSeconds) {
        // Khi nhấn chuột (thu nhỏ nhanh)
        button.setOnMousePressed(event -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(durationInSeconds / 2), button);
            scaleDown.setToX(clickScale); // Thu nhỏ (ví dụ: 0.95)
            scaleDown.setToY(clickScale);
            scaleDown.play();
        });

        // Khi thả chuột (quay lại kích thước hover)
        button.setOnMouseReleased(event -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(durationInSeconds / 2), button);
            scaleUp.setToX(1.0); // Quay lại kích thước gốc hoặc hover
            scaleUp.setToY(1.0);
            scaleUp.play();
        });
    }
}
