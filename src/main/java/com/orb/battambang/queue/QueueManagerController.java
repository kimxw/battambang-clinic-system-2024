package com.orb.battambang.queue;

import com.orb.battambang.login.NewLoginPageController;
import com.orb.battambang.util.MenuGallery;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class QueueManagerController implements Initializable {

    @FXML
    private AnchorPane sliderAnchorPane;
    @FXML
    private Label menuLabel;
    @FXML
    private Label menuBackLabel;
    @FXML
    private Button menuHomeButton;
    @FXML
    private Button menuReceptionButton;
    @FXML
    private Button menuTriageButton;
    @FXML
    private Button menuEducationButton;
    @FXML
    private Button menuConsultationButton;
    @FXML
    private Button menuPharmacyButton;
    @FXML
    private Button menuQueueManagerButton;
    @FXML
    private Button menuAdminButton;
    @FXML
    private Button menuLogoutButton;
    @FXML
    private Button menuUserButton;
    @FXML
    private Button menuLocationButton;

    @FXML
    private Pane actionsPane;
    @FXML
    private Button moreActionsOpenButton;
    @FXML
    private Button moreActionsCloseButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);

        setUpAnchorPane();


    }

    private void setUpAnchorPane() {

        actionsPane.setTranslateY(-44);
        moreActionsOpenButton.setVisible(true);
        moreActionsCloseButton.setVisible(false);

        moreActionsOpenButton.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(actionsPane);

            slide.setToY(0);
            slide.play();

            sliderAnchorPane.setTranslateY(44);

            slide.setOnFinished((ActionEvent e) -> {
                moreActionsOpenButton.setVisible(false);
                moreActionsCloseButton.setVisible(true);

            });
        });

        moreActionsCloseButton.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(actionsPane);

            slide.setToY(-44);
            slide.play();

            actionsPane.setTranslateY(0);

            slide.setOnFinished((ActionEvent e) -> {
                moreActionsOpenButton.setVisible(true);
                moreActionsCloseButton.setVisible(false);

            });
        });
    }
}

