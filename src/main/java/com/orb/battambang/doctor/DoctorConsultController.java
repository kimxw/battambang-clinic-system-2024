package com.orb.battambang.doctor;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.TableColumn;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class DoctorConsultController extends SpecialistController implements Initializable {

    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;

    @FXML
    protected ToggleButton tbToggleButton;
    @FXML
    protected ToggleButton optometryToggleButton;
    @FXML
    protected ToggleButton hearingToggleButton;
    @FXML
    protected ToggleButton socialToggleButton;
    @FXML
    protected ToggleButton physioToggleButton;

    @FXML
    private TextArea inputConsultNotesTextArea;
    @FXML
    private ChoiceBox<String> conditionChoiceBox;
    private String[] conditionType = {"None", "Acute", "Chronic", "Acute and Chronic"};
    @FXML
    private Button updateButton;
    @FXML
    private Button createReferralButton;
    @FXML
    private RadioButton yesRadioButton, noRadioButton;
    @FXML
    private CheckBox consultCompleteCheckBox;

    @FXML
    private Button menuUserButton;
    @FXML
    private Button menuLocationButton;

    @FXML
    private Pane editBlockPane;

    protected List<Tag> tagList = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialise specialist view first
        super.initialize(url, resourceBundle);

        //mini QMs
        MiniQueueManager waitingQueueManager = new MiniQueueManager(waitingListView, "doctorWaitingTable");
        MiniQueueManager progressQueueManager = new MiniQueueManager(inProgressListView, "doctorProgressTable");

        conditionChoiceBox.getItems().addAll(conditionType);

        // Create a ToggleGroup for referral radio buttons
        ToggleGroup group = new ToggleGroup();
        yesRadioButton.setToggleGroup(group);
        noRadioButton.setToggleGroup(group);

        //clearConsultFields(); //TODO: EXPERIEMNT
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {

        super.searchButtonOnAction(e);

        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());

            displayConsultationNotes(queueNumber);
            displayCondition(queueNumber);
            displayReferral(queueNumber);
            displayConsultComplete(queueNumber);

        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNoLabel.getText().equals("")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
            return;
        }


        String consultNotes = inputConsultNotesTextArea.getText();
        String conditionType = conditionChoiceBox.getValue();
        String prescriptionString = Prescription.convertToString(getPrescriptionItems());
        boolean referralStatus = yesRadioButton.isSelected();
        String doctorConsultStatus = consultCompleteCheckBox.isSelected() ? "Complete" : "Incomplete";

        String queueNumberText = queueNumberTextField.getText();
        if (queueNumberText.isEmpty()) {
            Labels.showMessageLabel(warningLabel, "Please fill in the queue number.", false);
            return;
        }

        int queueNumber;
        try {
            queueNumber = Integer.parseInt(queueNumberText);
        } catch (NumberFormatException ex) {
            Labels.showMessageLabel(warningLabel, "Please enter a valid queue number.", false);
            return;
        }

        // Check if queueNumber exists in patientQueueTable
        String checkQueueNumberQuery = "SELECT COUNT(*) FROM patientQueueTable WHERE queueNumber = ?";
        try (PreparedStatement checkQueueNumberStmt = connection.prepareStatement(checkQueueNumberQuery)) {
            checkQueueNumberStmt.setInt(1, queueNumber);
            ResultSet resultSet = checkQueueNumberStmt.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count == 0) {
                Labels.showMessageLabel(warningLabel, "Queue number does not exist.", false);
                return;
            }
        } catch (SQLException ex) {
            System.out.println(ex); // Handle the exception appropriately
            Labels.showMessageLabel(warningLabel, "Database error occurred.", false);
            return;
        }

        if (conditionChoiceBox.getValue().equals("")) {
            Labels.showMessageLabel(warningLabel, "Please choose a condition.", false);
            return;
        }

        if (!yesRadioButton.isSelected() && !noRadioButton.isSelected()) {
            Labels.showMessageLabel(warningLabel, "Please select a referral status.", false);
            return;
        }

        // Check if queueNumber exists in doctorConsultTable
        String checkDoctorConsultQuery = "SELECT COUNT(*) FROM doctorConsultTable WHERE queueNumber = ?";
        try (PreparedStatement checkDoctorConsultStmt = connection.prepareStatement(checkDoctorConsultQuery)) {
            checkDoctorConsultStmt.setInt(1, queueNumber);
            ResultSet resultSet = checkDoctorConsultStmt.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                // Update existing record
                String updateDoctorConsultTableQuery = "UPDATE doctorConsultTable SET consultationNotes = ?, conditionType = ?, prescription = ?, referralStatus = ?, doctor = ? WHERE queueNumber = ?";
                try (PreparedStatement doctorConsultTableStmt = connection.prepareStatement(updateDoctorConsultTableQuery)) {
                    doctorConsultTableStmt.setString(1, consultNotes);
                    doctorConsultTableStmt.setString(2, conditionType);
                    doctorConsultTableStmt.setString(3, prescriptionString);
                    doctorConsultTableStmt.setBoolean(4, referralStatus);
                    doctorConsultTableStmt.setString(5, menuUserButton.getText()); // Update doctor column with doctorLabel text
                    doctorConsultTableStmt.setInt(6, queueNumber);
                    doctorConsultTableStmt.executeUpdate();
                }
            } else {
                // Insert new record
                String insertDoctorConsultTableQuery = "INSERT INTO doctorConsultTable (queueNumber, consultationNotes, `conditionType`, prescription, referralStatus, doctor) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement doctorConsultTableStmt = connection.prepareStatement(insertDoctorConsultTableQuery)) {
                    doctorConsultTableStmt.setInt(1, queueNumber);
                    doctorConsultTableStmt.setString(2, consultNotes);
                    doctorConsultTableStmt.setString(3, conditionType);
                    doctorConsultTableStmt.setString(4, prescriptionString);
                    doctorConsultTableStmt.setBoolean(5, referralStatus);
                    doctorConsultTableStmt.setString(6, menuUserButton.getText()); // Set doctor column with doctorLabel text
                    doctorConsultTableStmt.executeUpdate();
                }
            }

            // Update patientQueueTable
            String updatePatientQueueTableQuery = "UPDATE patientQueueTable SET doctorConsultStatus = ? WHERE queueNumber = ?";
            try (PreparedStatement patientQueueTableStmt = connection.prepareStatement(updatePatientQueueTableQuery)) {
                patientQueueTableStmt.setString(1, doctorConsultStatus);
                patientQueueTableStmt.setInt(2, queueNumber);
                patientQueueTableStmt.executeUpdate();
            }

            // Clear warning label and show success message if all operations succeed
            Labels.showMessageLabel(warningLabel, "Update successful.", true);
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("UPDATE RECORD - updateButtonOnAction");
            Labels.showMessageLabel(warningLabel, "Database error occurred.", false);
        }
    }

    @FXML
    public void radioButtonOnAction(ActionEvent e) {
        if (yesRadioButton.isSelected()) {
            createReferralButton.setVisible(true);
        } else {
            createReferralButton.setVisible(false);
        }
    }

    @FXML
    public void createReferralButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().trim().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("referral.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Create Referral");
            stage.setScene(scene);

            // Get the controller instance
            ReferralController controller = loader.getController();

            // Set the queue number
            String queueNumberText = queueNumberTextField.getText().trim();
            controller.setQueueNumber(Integer.parseInt(queueNumberText));

            stage.show();

        } catch (Exception ex) {
            Labels.showMessageLabel(warningLabel, "Unexpected error occurred.", false);
            ex.printStackTrace();  // Print stack trace for debugging
        }
    }


    public void displayConsultationNotes(int queueNumber) {
        String patientQuery = "SELECT * FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                inputConsultNotesTextArea.setText(resultSet.getString("consultationNotes"));
            } else {
                inputConsultNotesTextArea.setText("");
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            inputConsultNotesTextArea.setText("");
        }
    }



    public void displayCondition(int queueNumber) {
        String patientQuery = "SELECT * FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String condition = resultSet.getString("conditionType");
                conditionChoiceBox.setValue(condition); // Set the selected value in the ChoiceBox
            } else {
                conditionChoiceBox.setValue(""); // Set an empty value if no condition is found
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }

    public void displayReferral(int queueNumber) {
        String patientQuery = "SELECT * FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                boolean referralStatus = resultSet.getBoolean("referralStatus");
                if (referralStatus) {
                    yesRadioButton.setSelected(true);
                    createReferralButton.setVisible(true);
                } else {
                    noRadioButton.setSelected(true); // Select No if referralStatus is false
                }
            } else {
                yesRadioButton.setSelected(false);
                noRadioButton.setSelected(false);
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }

    public void displayConsultComplete(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String doctorConsultStatus = resultSet.getString("doctorConsultStatus");

                // Toggle CheckBox based on doctorConsultStatus
                if ("Complete".equalsIgnoreCase(doctorConsultStatus)) {
                    consultCompleteCheckBox.setSelected(true); // Tick the CheckBox for complete status
                } else {
                    consultCompleteCheckBox.setSelected(false); // Untick the CheckBox for incomplete or deferred status
                }
            } else {
                // If no result found, clear CheckBox selection
                consultCompleteCheckBox.setSelected(false);
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }


    private void clearConsultFields() {
        inputConsultNotesTextArea.setText("");
        conditionChoiceBox.setValue("");
        yesRadioButton.setSelected(false);
        noRadioButton.setSelected(false);
        consultCompleteCheckBox.setSelected(false);
        createReferralButton.setVisible(false);
    }

    protected void clearAllFields() {
        super.clearAllFields();
        clearConsultFields();
    }

    @FXML
    private void addButtonOnAction() {
        Integer selectedPatient = waitingListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            if (!waitingListView.getItems().isEmpty()) {
                selectedPatient = waitingListView.getItems().get(0);
            }
        }

        if (selectedPatient != null) {
            movePatientToInProgress(selectedPatient);
        }
    }

    private void movePatientToInProgress(Integer queueNumber) {
        String nameFromWaitingListQuery = "SELECT name FROM doctorWaitingTable WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM doctorWaitingTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO doctorProgressTable (queueNumber, name) VALUES (?, ?)";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
            insertStatement.setString(2, name);
            insertStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            waitingListView.getItems().remove(queueNumber);
            inProgressListView.getItems().add(queueNumber);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Roll back transaction if any error occurs
                }
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    @FXML
    private void sendButtonOnAction() {
        Integer selectedPatient = inProgressListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            if (!inProgressListView.getItems().isEmpty()) {
                selectedPatient = inProgressListView.getItems().get(0);
            }
        }

        if (selectedPatient != null) {
            movePatientToPharmacy(selectedPatient);
        }
    }

    private void movePatientToPharmacy(Integer queueNumber) {

        String nameFromWaitingListQuery = "SELECT name FROM doctorProgressTable WHERE queueNumber = ?";
        String deleteFromProgressListQuery = "DELETE FROM doctorProgressTable WHERE queueNumber = ?";
        String insertIntoNextListQuery = "INSERT INTO pharmacyWaitingTable (queueNumber, name) VALUES (?, ?)";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromProgressListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoNextListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
            insertStatement.setString(2, name);
            insertStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            inProgressListView.getItems().remove(queueNumber);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Roll back transaction if any error occurs
                }
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }


}
