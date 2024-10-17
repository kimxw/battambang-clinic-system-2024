package com.orb.battambang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("prompt-connect.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 354, 351);

        Image logo = new Image(getClass().getResourceAsStream("/icons/logo.png"));
        stage.getIcons().add(logo);

        stage.setResizable(false);
        stage.setTitle("Welcome");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}