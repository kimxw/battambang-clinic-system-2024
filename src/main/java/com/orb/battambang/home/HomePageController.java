package com.orb.battambang.home;

import com.orb.battambang.login.Staff;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.login.NewLoginPageController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

import static com.orb.battambang.util.Rectangles.updatePermissionRectangle;

public class HomePageController implements Initializable {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label staffIDLabel;
    @FXML
    private Label primaryRoleLabel;
    @FXML
    private Label locationLabel;

    @FXML
    private Label adminPermLabel;
    @FXML
    private Rectangle adminPermRectangle;
    @FXML
    private Label receptionPermLabel;
    @FXML
    private Rectangle receptionPermRectangle;
    @FXML
    private Label triagePermLabel;
    @FXML
    private Rectangle triagePermRectangle;
    @FXML
    private Label educationPermLabel;
    @FXML
    private Rectangle educationPermRectangle;
    @FXML
    private Label consultationPermLabel;
    @FXML
    private Rectangle consultationPermRectangle;
    @FXML
    private Label pharmacyPermLabel;
    @FXML
    private Rectangle pharmacyPermRectangle;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);

        Staff staff = NewLoginPageController.getStaffDetails();
        int staffID = staff.getStaffID();
        String firstName = staff.getFirstName();
        String lastName = staff.getLastName();
        String username = staff.getUsername();
        String primaryRole = staff.getPrimaryRole();
        String location = staff.getLocation();
        boolean admin = staff.isAdmin();
        boolean reception = staff.isReception();
        boolean triage = staff.isTriage();
        boolean education = staff.isEducation();
        boolean consultation = staff.isConsultation();
        boolean pharmacy = staff.isPharmacy();

        welcomeLabel.setText("Welcome, " + firstName);
        nameLabel.setText(firstName + " " + lastName);
        staffIDLabel.setText(String.valueOf(staffID));
        primaryRoleLabel.setText(primaryRole);
        locationLabel.setText(location);

        updatePermissionRectangle(adminPermRectangle, adminPermLabel, admin, true);
        updatePermissionRectangle(receptionPermRectangle, receptionPermLabel, reception, false);
        updatePermissionRectangle(triagePermRectangle, triagePermLabel, triage, false);
        updatePermissionRectangle(educationPermRectangle, educationPermLabel, education, false);
        updatePermissionRectangle(consultationPermRectangle, consultationPermLabel, consultation, false);
        updatePermissionRectangle(pharmacyPermRectangle, pharmacyPermLabel, pharmacy, false);

    }
}
