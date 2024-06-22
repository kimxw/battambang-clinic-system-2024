package com.orb.battambang.util;


import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class Rectangles {

    public static void updateStatusRectangle(Rectangle rectangle, Label label, String message) {
        label.setText(message);
        if (message.equals("Complete")) {
            rectangle.setStyle("-fx-fill: #9dd895;");
        } else if (message.equals("Incomplete")) {
            rectangle.setStyle("-fx-fill: #fa8072;");
        } else if (message.equals("Deferred")) {
            rectangle.setStyle("-fx-fill: #46a9ff;");
        } else {
            rectangle.setStyle("-fx-fill: #707070;"); //not found
        }
    }
}
