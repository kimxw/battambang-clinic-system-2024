package com.orb.battambang.doctor;

import com.orb.battambang.util.Labels;
import com.orb.battambang.util.MiniQueueManager;
import com.orb.battambang.util.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class AudiologistController extends SpecialistController implements Initializable {

    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;

    @FXML
    private CheckBox otoscopyClearCheckBox;

    @FXML
    private CheckBox otoscopyEarwaxCheckBox;

    @FXML
    private CheckBox otoscopyFurtherInvCheckBox;


    @FXML
    private CheckBox frequency1000CheckBox;

    @FXML
    private CheckBox frequency2000CheckBox;

    @FXML
    private CheckBox frequency4000CheckBox;

    @FXML
    private CheckBox frequency500CheckBox;


    @FXML
    private CheckBox rec1CheckBox;

    @FXML
    private CheckBox rec2CheckBox;

    @FXML
    private CheckBox rec3CheckBox;

    @FXML
    private CheckBox rec4CheckBox;

    @FXML
    private CheckBox audiologistCompleteCheckBox;

    @FXML
    private Button menuUserButton;

    protected List<Tag> tagList = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialise specialist view first
        super.initialize(url, resourceBundle);

        //mini QMs
        MiniQueueManager waitingQueueManager = new MiniQueueManager(waitingListView, "audioWaitingTable");
        MiniQueueManager progressQueueManager = new MiniQueueManager(inProgressListView, "audioProgressTable");
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        super.searchButtonOnAction(e);

        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());

            displayAudiologistFields(queueNumber);
            displayAudiologistComplete(queueNumber);

        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNoLabel.getText().equals("")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
            return;
        }

        //otoscopy results
        boolean otoscopyClear = otoscopyClearCheckBox.isSelected();
        boolean otoscopyEarwax = otoscopyEarwaxCheckBox.isSelected();
        boolean otoscopyFurtherInvestigation = otoscopyFurtherInvCheckBox.isSelected();

        //hearing screening results
        boolean hearing500Hz = frequency500CheckBox.isSelected();
        boolean hearing1000Hz = frequency1000CheckBox.isSelected();
        boolean hearing2000Hz = frequency2000CheckBox.isSelected();
        boolean hearing4000Hz = frequency4000CheckBox.isSelected();

        //recommendations
        boolean noAction = rec1CheckBox.isSelected();
        boolean visitEnt = rec2CheckBox.isSelected();
        boolean followUpEnt = rec3CheckBox.isSelected();
        boolean detailedHearingAssessment = rec4CheckBox.isSelected();

        //status
        String audioStatus = audiologistCompleteCheckBox.isSelected() ? "Complete" : "Incomplete";


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

        String upsertAudiologistTableQuery = """
                INSERT INTO audiologistTable (queueNumber, doctor,
                                                           otoscopy_clear,
                                                           otoscopy_earwax,
                                                           otoscopy_further_investigation,
                                                       
                                                           hearing_500Hz,
                                                           hearing_1000Hz,
                                                           hearing_2000Hz,
                                                           hearing_4000Hz,
                                                       
                                                           no_action,
                                                           visit_ent,
                                                           follow_up_ent,
                                                           detailed_hearing_assessment)                                                            
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    doctor = VALUES(doctor),
                    otoscopy_clear = VALUES(otoscopy_clear),
                    otoscopy_earwax = VALUES(otoscopy_earwax),
                    otoscopy_further_investigation = VALUES(otoscopy_further_investigation),
                    
                    hearing_500Hz = VALUES(hearing_500Hz),
                    hearing_1000Hz = VALUES(hearing_1000Hz),
                    hearing_2000Hz = VALUES(hearing_2000Hz),
                    hearing_4000Hz = VALUES(hearing_4000Hz),
                    
                    no_action = VALUES(no_action),
                    visit_ent = VALUES(visit_ent),
                    follow_up_ent = VALUES(follow_up_ent),
                    detailed_hearing_assessment = VALUES(detailed_hearing_assessment)
            """;

            try (PreparedStatement audiologistTableStmt = connection.prepareStatement(upsertAudiologistTableQuery)) {
                audiologistTableStmt.setInt(1, queueNumber);
                audiologistTableStmt.setString(2, menuUserButton.getText());
                audiologistTableStmt.setBoolean(3, otoscopyClear);
                audiologistTableStmt.setBoolean(4, otoscopyEarwax);
                audiologistTableStmt.setBoolean(5, otoscopyFurtherInvestigation);
                audiologistTableStmt.setBoolean(6, hearing500Hz);
                audiologistTableStmt.setBoolean(7, hearing1000Hz);
                audiologistTableStmt.setBoolean(8, hearing2000Hz);
                audiologistTableStmt.setBoolean(9, hearing4000Hz);
                audiologistTableStmt.setBoolean(10, noAction);
                audiologistTableStmt.setBoolean(11, visitEnt);
                audiologistTableStmt.setBoolean(12, followUpEnt);
                audiologistTableStmt.setBoolean(13, detailedHearingAssessment);
                audiologistTableStmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println(ex); // Handle exception appropriately
                Labels.showMessageLabel(warningLabel, "Database error occurred.", false);
                return;
            }

        // Update patientQueueTable separately if necessary
            String updatePatientQueueTableQuery = "UPDATE patientQueueTable SET audiologistStatus = ? WHERE queueNumber = ?";
            try (PreparedStatement patientQueueTableStmt = connection.prepareStatement(updatePatientQueueTableQuery)) {
                patientQueueTableStmt.setString(1, audioStatus);
                patientQueueTableStmt.setInt(2, queueNumber);
                patientQueueTableStmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println(ex); // Handle exception appropriately
                Labels.showMessageLabel(warningLabel, "Database error occurred.", false);
                return;
            }

        // Show success message if all operations succeed
            Labels.showMessageLabel(warningLabel, "Update successful.", true);

    }

    public void displayAudiologistFields(int queueNumber) {
        String patientQuery = "SELECT * FROM audiologistTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                otoscopyClearCheckBox.setSelected(resultSet.getBoolean("otoscopy_clear"));
                otoscopyEarwaxCheckBox.setSelected(resultSet.getBoolean("otoscopy_earwax"));
                otoscopyFurtherInvCheckBox.setSelected(resultSet.getBoolean("otoscopy_further_investigation"));

                frequency500CheckBox.setSelected(resultSet.getBoolean("hearing_500Hz"));
                frequency1000CheckBox.setSelected(resultSet.getBoolean("hearing_1000Hz"));
                frequency2000CheckBox.setSelected(resultSet.getBoolean("hearing_2000Hz"));
                frequency4000CheckBox.setSelected(resultSet.getBoolean("hearing_4000Hz"));

                rec1CheckBox.setSelected(resultSet.getBoolean("no_action"));
                rec2CheckBox.setSelected(resultSet.getBoolean("visit_ent"));
                rec3CheckBox.setSelected(resultSet.getBoolean("follow_up_ent"));
                rec4CheckBox.setSelected(resultSet.getBoolean("detailed_hearing_assessment"));
            } else {
                clearAudiologistFields();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearAudiologistFields();
        }
    }


    public void displayAudiologistComplete(int queueNumber) {
        String patientQuery = "SELECT audiologistStatus FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String audioStatus = resultSet.getString("audiologistStatus");

                // Toggle CheckBox based on audioStatus
                if ("Complete".equalsIgnoreCase(audioStatus)) {
                    audiologistCompleteCheckBox.setSelected(true); // Tick the CheckBox for complete status
                } else {
                    audiologistCompleteCheckBox.setSelected(false); // Untick the CheckBox for incomplete or deferred status
                }
            } else {
                // If no result found, clear CheckBox selection
                audiologistCompleteCheckBox.setSelected(false);
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }


    private void clearAudiologistFields() {
        otoscopyClearCheckBox.setSelected(false);
        otoscopyEarwaxCheckBox.setSelected(false);
        otoscopyFurtherInvCheckBox.setSelected(false);

        frequency500CheckBox.setSelected(false);
        frequency1000CheckBox.setSelected(false);
        frequency2000CheckBox.setSelected(false);
        frequency4000CheckBox.setSelected(false);

        rec1CheckBox.setSelected(false);
        rec2CheckBox.setSelected(false);
        rec3CheckBox.setSelected(false);
        rec4CheckBox.setSelected(false);
    }

    protected void clearAllFields() {
        super.clearAllFields();
        clearAudiologistFields();
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
        String nameFromWaitingListQuery = "SELECT name FROM audioWaitingTable WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM audioWaitingTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO audioProgressTable (queueNumber, name) VALUES (?, ?)";

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

        String nameFromWaitingListQuery = "SELECT name FROM audioProgressTable WHERE queueNumber = ?";
        String deleteFromProgressListQuery = "DELETE FROM audioProgressTable WHERE queueNumber = ?";
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