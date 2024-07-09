package com.orb.battambang.login;

import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import com.orb.battambang.MainApp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class PromptConnectController implements Initializable {
    @FXML
    private ChoiceBox<String> locationChoiceBox;
    private final String[] choiceBoxOptions = new String[] {"MOPK", "TNK5", "Kbal Koh"};

    @FXML
    private Button connectButton;

    @FXML
    private Label warningLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        locationChoiceBox.getItems().addAll(choiceBoxOptions);
    }
    @FXML
    private void onConnectButton() {

        boolean success = DatabaseConnection.establishConnection(locationChoiceBox.getValue()); //check if valid connection established

        if (!success) {
            Labels.showMessageLabel(warningLabel, "Please select a location.", false);
        } else {
            Labels.showMessageLabel(warningLabel, "Connecting to database . . .", "green", 3);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {openLoginPage(); close();}));
            timeline.setCycleCount(1);
            timeline.play();
        }

    }

    private void openLoginPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);
            newUserStage.setTitle("Login");
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void close() {
        // Get the stage associated with any node in the scene
        Stage stage = (Stage) connectButton.getScene().getWindow();

        // Close the stage
        stage.close();
    }
}