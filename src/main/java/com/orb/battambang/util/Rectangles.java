package com.orb.battambang.util;


import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class Rectangles {

    public static void clearStatusRectangle(Rectangle rectangle, Label label) {
        label.setText("");
        rectangle.setStyle("-fx-fill: #ffffff;");
    }

    public static void updateStatusRectangle(Rectangle rectangle, Label label, String message) {
        label.setText(message);
        if (message.equals("Complete") || message.equals("Dispensed")) {
            rectangle.setStyle("-fx-fill: #bdd9ba;");
        } else if (message.equals("Incomplete") || message.equals("Not dispensed")) {
            rectangle.setStyle("-fx-fill: #e59295;");
        } else if (message.equals("Deferred")) {
            rectangle.setStyle("-fx-fill: #bea9df;");
        } else {
            rectangle.setStyle("-fx-fill: #999999;"); //not found
        }
    }

    public static void updateStatusRectangle(Rectangle rectangle, Label label, String message, boolean excludeComplete) {
        label.setText(message);
        if (message.equals("Complete")) {
            label.setText("");
            rectangle.setStyle("-fx-fill: #ffffff;");
        } else if (message.equals("Incomplete")) {
            rectangle.setStyle("-fx-fill: #e59295;");
        } else if (message.equals("Deferred")) {
            rectangle.setStyle("-fx-fill: #bea9df;");
        } else {
            rectangle.setStyle("-fx-fill: #999999;"); //not found
        }
    }

    public static void updatePermissionRectangle(Rectangle rectangle, Label label, boolean perm, boolean admin) {
        if (perm && admin) {
            label.setText("Yes");
            label.setStyle("-fx-text-fill: #9832ce");
            rectangle.setStyle("-fx-fill: #e3abff; -fx-opacity: 0.62");
        } else if (perm) {
            label.setText("Yes");
            label.setStyle("-fx-text-fill: #5f8b07");
            rectangle.setStyle("-fx-fill: #a7cf5d; -fx-opacity: 0.62");
        } else {
            label.setText("No");
            label.setStyle("-fx-text-fill: #bf1b15");
            rectangle.setStyle("-fx-fill: #ff7773; -fx-opacity: 0.45");
        }
    }
}
