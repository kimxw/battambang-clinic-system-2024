package com.orb.battambang.util;

import com.orb.battambang.MainApp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Labels {

    public static void showMessageLabel(Label messageLabel, String message, boolean success) {
        if (success) {
            messageLabel.setStyle("-fx-text-fill: #5f8b07;");
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

    public static void iconWithMessageDisplay(Label messageLabel, ImageView imageView, String message, String colour, String image_url) {
        Image image = new Image(MainApp.class.getResource(image_url).toExternalForm());
        messageLabel.setVisible(false);
        imageView.setVisible(false);
        imageView.setImage(image);
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + colour + " ;");
        imageView.setVisible(true);
        messageLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            imageView.setVisible(false);
            messageLabel.setVisible(false);
        }));
        timeline.setCycleCount(1);
        timeline.play();



    }
}
