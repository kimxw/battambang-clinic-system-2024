package com.orb.battambang.checkupstation;

import com.orb.battambang.util.Labels;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.MiniQueueManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class SnellensTestController extends CheckupMenuController implements Initializable {

    public Pane editBlockPane;
    private int initialisingQueueNumber = -1;

    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label sexLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label status1Label;
    @FXML
    private Label status2Label;
    @FXML
    private Label status3Label;
    @FXML
    private Label status6Label;
    @FXML
    private Rectangle status1Rectangle;
    @FXML
    private Rectangle status2Rectangle;
    @FXML
    private Rectangle status3Rectangle;
    @FXML
    private Rectangle status6Rectangle;
    @FXML
    private Button searchButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;

    @FXML
    private Label warningLabel;
    @FXML
    private Label queueNoLabel;
    @FXML
    private TextField wpRightTextField;
    @FXML
    private TextField wpLeftTextField;
    @FXML
    private TextField npRightTextField;
    @FXML
    private TextField npLeftTextField;
    @FXML
    private TextArea additionalNotesTextArea;

    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;

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
    public void initialize(URL location, ResourceBundle resources) {
        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);

        // for waiting list
        // Initialize the waiting list
        MiniQueueManager waitingQueueManager = new MiniQueueManager(waitingListView, "triageWaitingTable");
        MiniQueueManager progressQueueManager = new MiniQueueManager(inProgressListView, "triageProgressTable");

        // Add a listener to the text property of the queueNumberTextField
        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null && !newValue.isEmpty()) {
                    particularsPane.setVisible(false);
                    clearParticularsFields();
                    clearFields();
                }
            }
        });

        particularsPane.setVisible(false);

        // Set up a listener on the Label's text property
        queueNoLabel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Check if the new value is empty
                if (newValue == null || newValue.trim().isEmpty()) {
                    // Show the pane if the text is empty
                    editBlockPane.setVisible(true);
                } else {
                    // Hide the pane if the text is not empty
                    editBlockPane.setVisible(false);
                }
            }
        });

    }

    public void postInitializationSetup() {
        if (initialisingQueueNumber == -1) {
            particularsPane.setVisible(false); // Initially hide the particularsPane
        } else {
            queueNumberTextField.setText(String.valueOf(initialisingQueueNumber));
            searchButtonOnAction(new ActionEvent());
        }
    }

    public void setInitialisingQueueNumber(int initialisingQueueNumber) {
        this.initialisingQueueNumber = initialisingQueueNumber;
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());

            updateParticularsPane(queueNumber);
            particularsPane.setVisible(true);
            displaySnellensRecords(queueNumber);
        }
    }

    private void displaySnellensRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM snellensTestTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                wpRightTextField.setText(resultSet.getString("wpRight"));
                wpLeftTextField.setText(resultSet.getString("wpLeft"));
                npRightTextField.setText(resultSet.getString("npRight"));
                npLeftTextField.setText(resultSet.getString("npLeft"));
                additionalNotesTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearFields();
        }
    }

    private void clearFields() {
        wpRightTextField.setText("");
        wpLeftTextField.setText("");
        npRightTextField.setText("");
        npLeftTextField.setText("");
        additionalNotesTextArea.setText("");
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addSnellensTest(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addSnellensTest(int queueNumber) {
        try {
            String wpRight = wpRightTextField.getText().isEmpty() ? null : wpRightTextField.getText();
            String wpLeft = wpLeftTextField.getText().isEmpty() ? null : wpLeftTextField.getText();
            String npRight = npRightTextField.getText().isEmpty() ? null : npRightTextField.getText();
            String npLeft = npLeftTextField.getText().isEmpty() ? null : npLeftTextField.getText();
            String notes = additionalNotesTextArea.getText().isEmpty() ? "" : additionalNotesTextArea.getText();

            String insertToCreate = "INSERT OR REPLACE INTO snellensTestTable (queueNumber, wpRight, wpLeft, npRight, npLeft, additionalNotes) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertToCreate)) {
                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setString(2, wpRight);
                preparedStatement.setString(3, wpLeft);
                preparedStatement.setString(4, npRight);
                preparedStatement.setString(5, npLeft);
                preparedStatement.setString(6, notes);

                preparedStatement.executeUpdate();

                String updateStatusQuery = "UPDATE patientQueueTable SET snellensStatus = 'Complete' WHERE queueNumber = ?";
                try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                    updateStatusStatement.setInt(1, queueNumber);
                    updateStatusStatement.executeUpdate();
                }

                Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
            } catch (SQLException e1) {
                Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
            }

        } catch (Exception e2) {
            Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
        }
    }

    private void clearParticularsFields() {
        queueNoLabel.setText("");
        nameLabel.setText("");
        ageLabel.setText("");
        sexLabel.setText("");
        phoneNumberLabel.setText("");
    }
}
