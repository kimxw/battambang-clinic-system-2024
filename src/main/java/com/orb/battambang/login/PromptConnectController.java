package com.orb.battambang.login;

import com.orb.battambang.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class PromptConnectController implements Initializable {
//    @FXML
//    private ChoiceBox<String> locationChoiceBox;
//    private final String[] choiceBoxOptions = new String[] {"MOPK", "TNK5", "Kbal Koh"};

    @FXML
    private Pane goPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //locationChoiceBox.getItems().addAll(choiceBoxOptions);
    }
//    private void onConnectButton() {
//
//        boolean success = DatabaseConnection.establishConnection(locationChoiceBox.getValue()); //check if valid connection established
//
//        if (!success) {
//            Labels.showMessageLabel(warningLabel, "Please select a location.", false);
//        } else {
//            Labels.showMessageLabel(warningLabel, "Connecting to database . . .", "green", 3);
//            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {openLoginPage(); close();}));
//            timeline.setCycleCount(1);
//            timeline.play();
//        }
//
//    }

    @FXML
    private void goOnMouseClicked(MouseEvent e) {
        openLoginPage();
    }

    private void openLoginPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);
            newUserStage.setResizable(false);
            newUserStage.setTitle("Login");
            newUserStage.setScene(scene);
            newUserStage.show();
            close();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void close() {
        Stage stage = (Stage) goPane.getScene().getWindow();
        stage.close();
    }

}