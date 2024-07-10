package com.orb.battambang.util;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SideMenu {
    private final AnchorPane sliderAnchorPane;
    private final Label menuLabel;
    private final Label menuBackLabel;
    private final Button homeButton;
    private final Button receptionButton;
    private final Button triageButton;
    private final Button educationButton;
    private final Button consultationButton;
    private final Button pharmacyButton;
    private final Button queueManagerButton;
    private final Button adminButton;
    private final Button logoutButton;

    public SideMenu(
            AnchorPane sliderAnchorPane,
            Label menuLabel,
            Label menuBackLabel,
            Button homeButton,
            Button receptionButton,
            Button triageButton,
            Button educationButton,
            Button consultationButton,
            Button pharmacyButton,
            Button queueManagerButton,
            Button adminButton,
            Button logoutButton) {

        this.sliderAnchorPane = sliderAnchorPane;
        this.menuLabel = menuLabel;
        this.menuBackLabel = menuBackLabel;
        this.homeButton = homeButton;
        this.receptionButton = receptionButton;
        this.triageButton = triageButton;
        this.educationButton = educationButton;
        this.consultationButton = consultationButton;
        this.pharmacyButton = pharmacyButton;
        this.queueManagerButton = queueManagerButton;
        this.adminButton = adminButton;
        this.logoutButton = logoutButton;

        setUpMenu();

    }

    private void setUpMenu() {
        sliderAnchorPane.setTranslateX(-200);
        menuLabel.setVisible(true);
        menuBackLabel.setVisible(false);

        menuLabel.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(sliderAnchorPane);

            slide.setToX(0);
            slide.play();

            sliderAnchorPane.setTranslateX(-200);

            slide.setOnFinished((ActionEvent e) -> {
                menuLabel.setVisible(false);
                menuBackLabel.setVisible(true);

            });
        });

        menuBackLabel.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(sliderAnchorPane);

            slide.setToX(-200);
            slide.play();

            sliderAnchorPane.setTranslateX(0);

            slide.setOnFinished((ActionEvent e) -> {
                menuLabel.setVisible(true);
                menuBackLabel.setVisible(false);

            });
        });

        homeButton.setOnAction(this :: homeButtonOnAction);
        receptionButton.setOnAction(this :: receptionButtonOnAction);
        triageButton.setOnAction(this :: triageButtonOnAction);
        educationButton.setOnAction(this :: educationButtonOnAction);
        consultationButton.setOnAction(this :: consultButtonOnAction);
        pharmacyButton.setOnAction(this :: pharmacyButtonOnAction);
        queueManagerButton.setOnAction(this :: queueManagerButtonOnAction);
        adminButton.setOnAction(this :: adminButtonOnAction);
        logoutButton.setOnAction(this :: logoutButtonOnAction);

    }

    //menu items
    @FXML
    public void homeButtonOnAction(ActionEvent e) {

    }

    @FXML
    public void receptionButtonOnAction(ActionEvent e) {

    }

    @FXML
    private void triageButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("checkup-menu.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @FXML
    public void educationButtonOnAction(ActionEvent e) {

    }

    @FXML
    public void consultButtonOnAction(ActionEvent e) {

    }

    @FXML
    public void pharmacyButtonOnAction(ActionEvent e) {

    }

    @FXML
    public void queueManagerButtonOnAction(ActionEvent e) {

    }

    @FXML
    public void adminButtonOnAction(ActionEvent e) {

    }

    @FXML
    public void logoutButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);

            newUserStage.setTitle("Login");
            newUserStage.setScene(scene);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            newUserStage.show();
        } catch (Exception exc) {
            System.out.println(exc);
        }
    }
}
