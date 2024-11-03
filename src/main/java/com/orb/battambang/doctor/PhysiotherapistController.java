package com.orb.battambang.doctor;

import com.orb.battambang.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class PhysiotherapistController extends SpecialistController implements Initializable {

    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;

    @FXML
    private TextArea inputPhysioNotesTextArea;
    @FXML
    private CheckBox physioCompleteCheckBox;

    @FXML
    private Button menuUserButton;

    protected List<Tag> tagList = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialise specialist view first
        super.initialize(url, resourceBundle);

        //mini QMs
        MiniQueueManager waitingQueueManager = new MiniQueueManager(waitingListView, "physioWaitingTable");
        MiniQueueManager progressQueueManager = new MiniQueueManager(inProgressListView, "physioProgressTable");
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        super.searchButtonOnAction(e);

        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());

            displayPhysioNotes(queueNumber);
            displayPhysioComplete(queueNumber);

        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNoLabel.getText().equals("")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
            return;
        }

        String physioNotes = inputPhysioNotesTextArea.getText();
        String physioStatus = physioCompleteCheckBox.isSelected() ? "Complete" : "Incomplete";

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

        // Check if queueNumber exists in phisiotherapistTable
        String checkPhysiotherapistQuery = "SELECT COUNT(*) FROM physiotherapistTable WHERE queueNumber = ?";
        try (PreparedStatement checkPhysiotherapistStmt = connection.prepareStatement(checkPhysiotherapistQuery)) {
            checkPhysiotherapistStmt.setInt(1, queueNumber);
            ResultSet resultSet = checkPhysiotherapistStmt.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                // Update existing record
                String updatePhysiotherapistTableQuery = "UPDATE physiotherapistTable SET physiotherapistNotes = ?, doctor = ? WHERE queueNumber = ?";
                try (PreparedStatement physiotherapistTableStmt = connection.prepareStatement(updatePhysiotherapistTableQuery)) {
                    physiotherapistTableStmt.setString(1, physioNotes);
                    physiotherapistTableStmt.setString(2, menuUserButton.getText());
                    physiotherapistTableStmt.setInt(3, queueNumber);
                    physiotherapistTableStmt.executeUpdate();
                }

            } else {
                // Insert new record
                String insertPhysiotherapistTableQuery = "INSERT INTO physiotherapistTable (queueNumber, physiotherapistNotes, doctor) VALUES (?, ?, ?)";
                try (PreparedStatement physiotherapistTableStmt = connection.prepareStatement(insertPhysiotherapistTableQuery)) {
                    physiotherapistTableStmt.setInt(1, queueNumber);
                    physiotherapistTableStmt.setString(2, physioNotes);
                    physiotherapistTableStmt.setString(3, menuUserButton.getText());
                    physiotherapistTableStmt.executeUpdate();
                }

            }

            // Update patientQueueTable
            String updatePatientQueueTableQuery = "UPDATE patientQueueTable SET physiotherapistStatus = ? WHERE queueNumber = ?";
            try (PreparedStatement patientQueueTableStmt = connection.prepareStatement(updatePatientQueueTableQuery)) {
                patientQueueTableStmt.setString(1, physioStatus);
                patientQueueTableStmt.setInt(2, queueNumber);
                patientQueueTableStmt.executeUpdate();
            }

            super.updateButtonOnAction(e); //update prescriptions + whatever else parent constructor takes care of

            // Clear warning label and show success message if all operations succeed
            Labels.showMessageLabel(warningLabel, "Update successful.", true);
        } catch (SQLException ex) {
            System.out.println(ex);
            Labels.showMessageLabel(warningLabel, "Database error occurred.", false);
        }
    }

    public void displayPhysioNotes(int queueNumber) {
        String patientQuery = "SELECT physiotherapistNotes FROM physiotherapistTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                inputPhysioNotesTextArea.setText(resultSet.getString("physiotherapistNotes"));
            } else {
                inputPhysioNotesTextArea.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            inputPhysioNotesTextArea.setText("");
        }
    }


    public void displayPhysioComplete(int queueNumber) {
        String patientQuery = "SELECT physiotherapistStatus FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String physioStatus = resultSet.getString("physiotherapistStatus");

                // Toggle CheckBox based on physioStatus
                if ("Complete".equalsIgnoreCase(physioStatus)) {
                    physioCompleteCheckBox.setSelected(true); // Tick the CheckBox for complete status
                } else {
                    physioCompleteCheckBox.setSelected(false); // Untick the CheckBox for incomplete or deferred status
                }
            } else {
                // If no result found, clear CheckBox selection
                physioCompleteCheckBox.setSelected(false);
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }


    private void clearPhysioFields() {
        inputPhysioNotesTextArea.setText("");
        physioCompleteCheckBox.setSelected(false);
    }

    protected void clearAllFields() {
        super.clearAllFields();
        clearPhysioFields();
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
        String nameFromWaitingListQuery = "SELECT name FROM physioWaitingTable WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM physioWaitingTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO physioProgressTable (queueNumber, name) VALUES (?, ?)";

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

        String nameFromWaitingListQuery = "SELECT name FROM physioProgressTable WHERE queueNumber = ?";
        String deleteFromProgressListQuery = "DELETE FROM physioProgressTable WHERE queueNumber = ?";
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