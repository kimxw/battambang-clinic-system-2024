package com.orb.battambang.util;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.AuthDatabaseConnection;
import com.orb.battambang.login.NewLoginPageController;

import com.orb.battambang.login.Staff;
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

public class MenuGallery {

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
    private final Button userButton;
    private final Button locationButton;

    private Staff staff;

    public MenuGallery(
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
            Button logoutButton,
            Button userButton,
            Button locationButton) {

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
        this.userButton = userButton;
        this.locationButton = locationButton;
        this.staff = NewLoginPageController.getStaffDetails();

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

        locationButton.setText(NewLoginPageController.getStaffDetails().getLocation());
        userButton.setText(NewLoginPageController.getStaffDetails().getFirstName() +
                " " + NewLoginPageController.getStaffDetails().getLastName() +
                " | ID: " + NewLoginPageController.getStaffDetails().getStaffID());

    }


    @FXML
    public void homeButtonOnAction(ActionEvent e) {
        if (AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("home-page.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setResizable(false);
            stage.setTitle("Home");
            stage.setScene(scene);
        } catch (Exception exc) {
            System.out.println(exc);
        }
    }

    @FXML
    public void receptionButtonOnAction(ActionEvent e) {
        if (AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }
        boolean isReception = staff.isReception();
        if (isReception) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("patient-registration.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Reception");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("z-patient-registration.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Reception");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }

    }

    @FXML
    private void triageButtonOnAction(ActionEvent e) {
        if (AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }
        boolean isTriage = staff.isTriage();
        if (isTriage) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("checkup-menu.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Triage");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("z-checkup-menu.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Triage");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    @FXML
    public void educationButtonOnAction(ActionEvent e) {
        if (AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }
        boolean isEducation = staff.isEducation();
        if (isEducation) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("education-station.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Education");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("z-education-station.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Education");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    @FXML
    public void consultButtonOnAction(ActionEvent e) {
        if (AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }
        boolean isConsultation = staff.isConsultation();
        if (isConsultation) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("doctor-consult.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Consultation");
                stage.setScene(scene);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("z-doctor-consult.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Consultation");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    @FXML
    public void pharmacyButtonOnAction(ActionEvent e) {
        if (AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }
        boolean isPharmacy = staff.isPharmacy();
        if (isPharmacy) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("medicine-dispense.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Pharmacy");
                stage.setScene(scene);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("z-medicine-dispense.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Pharmacy");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    @FXML
    public void queueManagerButtonOnAction(ActionEvent e) {
        if (AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("queue-manager.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setResizable(false);
            stage.setTitle("Queue Manager");
            stage.setScene(scene);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @FXML
    public void adminButtonOnAction(ActionEvent e) {
        boolean isAdmin = staff.isAdmin();
        if (isAdmin) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("admin.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Admin");
                stage.setScene(scene);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("z-admin.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Admin");
                stage.setScene(scene);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    @FXML
    public void logoutButtonOnAction(ActionEvent e) {
        if (!AuthDatabaseConnection.isConnectionOpen()) {
            AuthDatabaseConnection.closeDatabaseConnection();
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);

            newUserStage.setResizable(false);
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
