package com.orb.battambang.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Labels {

    public static void showMessageLabel(Label messageLabel, String message, boolean success) {
        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
        }
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        // Schedule a task to hide the message label after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> messageLabel.setVisible(false)));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public static void showMessageLabel(Label messageLabel, String message, String colour, int time) {
        messageLabel.setStyle("-fx-text-fill: " + colour + " ;");
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        // Schedule a task to hide the message label after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(time), event -> messageLabel.setVisible(false)));
        timeline.setCycleCount(1);
        timeline.play();
    }


    public static void showMessageLabel(Label messageLabel, String message, String colour) {
        messageLabel.setStyle("-fx-text-fill: " + colour + " ;");
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        // Schedule a task to hide the message label after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> messageLabel.setVisible(false)));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
