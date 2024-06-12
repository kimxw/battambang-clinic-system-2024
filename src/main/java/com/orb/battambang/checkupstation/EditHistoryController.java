package com.orb.battambang.checkupstation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditHistoryController extends CheckupMenuController implements Initializable {
    @FXML
    private Button saveAndExitButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any necessary data here
    }

    @FXML
    public void saveAndExitButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) saveAndExitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
