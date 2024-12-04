package com.orb.battambang.doctor;

import com.orb.battambang.util.Labels;
import com.orb.battambang.util.MiniQueueManager;
import com.orb.battambang.util.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
    private CheckBox leftOtoscopyClearCheckBox;

    @FXML
    private CheckBox leftOtoscopyEarwaxCheckBox;

    @FXML
    private CheckBox leftOtoscopyFurtherInvCheckBox;
    @FXML
    private CheckBox rightOtoscopyClearCheckBox;

    @FXML
    private CheckBox rightOtoscopyEarwaxCheckBox;

    @FXML
    private CheckBox rightOtoscopyFurtherInvCheckBox;


    @FXML
    private ChoiceBox<String> left500ChoiceBox;
    @FXML
    private ChoiceBox<String> left1000ChoiceBox;
    @FXML
    private ChoiceBox<String> left2000ChoiceBox;
    @FXML
    private ChoiceBox<String> left4000ChoiceBox;
    @FXML
    private ChoiceBox<String> right500ChoiceBox;
    @FXML
    private ChoiceBox<String> right1000ChoiceBox;
    @FXML
    private ChoiceBox<String> right2000ChoiceBox;
    @FXML
    private ChoiceBox<String> right4000ChoiceBox;


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

        String[] choiceBoxItems = new String[]{
                "NIL", "20 dB", "30 dB", "40 dB", "50 dB", "60 dB",
                "70 dB", "80 dB", "90 dB", "100 dB", "110 dB", "120 dB"};
        left500ChoiceBox.getItems().addAll(choiceBoxItems);
        left1000ChoiceBox.getItems().addAll(choiceBoxItems);
        left2000ChoiceBox.getItems().addAll(choiceBoxItems);
        left4000ChoiceBox.getItems().addAll(choiceBoxItems);
        right500ChoiceBox.getItems().addAll(choiceBoxItems);
        right1000ChoiceBox.getItems().addAll(choiceBoxItems);
        right2000ChoiceBox.getItems().addAll(choiceBoxItems);
        right4000ChoiceBox.getItems().addAll(choiceBoxItems);
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

        //left ear otoscopy results
        boolean leftOtoscopyClear = leftOtoscopyClearCheckBox.isSelected();
        boolean leftOtoscopyEarwax = leftOtoscopyEarwaxCheckBox.isSelected();
        boolean leftOtoscopyFurtherInvestigation = leftOtoscopyFurtherInvCheckBox.isSelected();

        //right ear otoscopy results
        boolean rightOtoscopyClear = rightOtoscopyClearCheckBox.isSelected();
        boolean rightOtoscopyEarwax = rightOtoscopyEarwaxCheckBox.isSelected();
        boolean rightOtoscopyFurtherInvestigation = rightOtoscopyFurtherInvCheckBox.isSelected();

        //left ear hearing screening results
        String left500Hz = left500ChoiceBox.getValue();
        String left1000Hz = left1000ChoiceBox.getValue();
        String left2000Hz = left2000ChoiceBox.getValue();
        String left4000Hz = left4000ChoiceBox.getValue();

        //right ear hearing screening results
        String right500Hz = right500ChoiceBox.getValue();
        String right1000Hz = right1000ChoiceBox.getValue();
        String right2000Hz = right2000ChoiceBox.getValue();
        String right4000Hz = right4000ChoiceBox.getValue();

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
                                                           left_otoscopy_clear,
                                                           left_otoscopy_earwax,
                                                           left_otoscopy_further_investigation,
                                                           
                                                           right_otoscopy_clear,
                                                           right_otoscopy_earwax,
                                                           right_otoscopy_further_investigation,
                                                       
                                                           left_hearing_500Hz,
                                                           left_hearing_1000Hz,
                                                           left_hearing_2000Hz,
                                                           left_hearing_4000Hz,
                                                           
                                                           right_hearing_500Hz,
                                                           right_hearing_1000Hz,
                                                           right_hearing_2000Hz,
                                                           right_hearing_4000Hz,
                                                       
                                                           no_action,
                                                           visit_ent,
                                                           follow_up_ent,
                                                           detailed_hearing_assessment)                                                            
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    doctor = VALUES(doctor),
                    left_otoscopy_clear = VALUES(left_otoscopy_clear),
                    left_otoscopy_earwax = VALUES(left_otoscopy_earwax),
                    left_otoscopy_further_investigation = VALUES(left_otoscopy_further_investigation),
                    
                    right_otoscopy_clear = VALUES(right_otoscopy_clear),
                    right_otoscopy_earwax = VALUES(right_otoscopy_earwax),
                    right_otoscopy_further_investigation = VALUES(right_otoscopy_further_investigation),
                    
                    left_hearing_500Hz = VALUES(left_hearing_500Hz),
                    left_hearing_1000Hz = VALUES(left_hearing_1000Hz),
                    left_hearing_2000Hz = VALUES(left_hearing_2000Hz),
                    left_hearing_4000Hz = VALUES(left_hearing_4000Hz),
                    
                    right_hearing_500Hz = VALUES(right_hearing_500Hz),
                    right_hearing_1000Hz = VALUES(right_hearing_1000Hz),
                    right_hearing_2000Hz = VALUES(right_hearing_2000Hz),
                    right_hearing_4000Hz = VALUES(right_hearing_4000Hz),
                    
                    no_action = VALUES(no_action),
                    visit_ent = VALUES(visit_ent),
                    follow_up_ent = VALUES(follow_up_ent),
                    detailed_hearing_assessment = VALUES(detailed_hearing_assessment)
            """;

            try (PreparedStatement audiologistTableStmt = connection.prepareStatement(upsertAudiologistTableQuery)) {
                audiologistTableStmt.setInt(1, queueNumber);
                audiologistTableStmt.setString(2, menuUserButton.getText());
                audiologistTableStmt.setBoolean(3, leftOtoscopyClear);
                audiologistTableStmt.setBoolean(4, leftOtoscopyEarwax);
                audiologistTableStmt.setBoolean(5, leftOtoscopyFurtherInvestigation);
                audiologistTableStmt.setBoolean(6, rightOtoscopyClear);
                audiologistTableStmt.setBoolean(7, rightOtoscopyEarwax);
                audiologistTableStmt.setBoolean(8, rightOtoscopyFurtherInvestigation);
                audiologistTableStmt.setString(9, left500Hz);
                audiologistTableStmt.setString(10, left1000Hz);
                audiologistTableStmt.setString(11, left2000Hz);
                audiologistTableStmt.setString(12, left4000Hz);
                audiologistTableStmt.setString(13, right500Hz);
                audiologistTableStmt.setString(14, right1000Hz);
                audiologistTableStmt.setString(15, right2000Hz);
                audiologistTableStmt.setString(16, right4000Hz);
                audiologistTableStmt.setBoolean(17, noAction);
                audiologistTableStmt.setBoolean(18, visitEnt);
                audiologistTableStmt.setBoolean(19, followUpEnt);
                audiologistTableStmt.setBoolean(20, detailedHearingAssessment);
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

        super.updateButtonOnAction(e); //update prescriptions + whatever else parent constructor takes care of

        // Show success message if all operations succeed
            Labels.showMessageLabel(warningLabel, "Update successful.", true);

    }

    public void displayAudiologistFields(int queueNumber) {
        String patientQuery = "SELECT * FROM audiologistTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                leftOtoscopyClearCheckBox.setSelected(resultSet.getBoolean("left_otoscopy_clear"));
                leftOtoscopyEarwaxCheckBox.setSelected(resultSet.getBoolean("left_otoscopy_earwax"));
                leftOtoscopyFurtherInvCheckBox.setSelected(resultSet.getBoolean("left_otoscopy_further_investigation"));

                rightOtoscopyClearCheckBox.setSelected(resultSet.getBoolean("right_otoscopy_clear"));
                rightOtoscopyEarwaxCheckBox.setSelected(resultSet.getBoolean("right_otoscopy_earwax"));
                rightOtoscopyFurtherInvCheckBox.setSelected(resultSet.getBoolean("right_otoscopy_further_investigation"));

                left500ChoiceBox.setValue(resultSet.getString("left_hearing_500Hz"));
                left1000ChoiceBox.setValue(resultSet.getString("left_hearing_1000Hz"));
                left2000ChoiceBox.setValue(resultSet.getString("left_hearing_2000Hz"));
                left4000ChoiceBox.setValue(resultSet.getString("left_hearing_4000Hz"));

                right500ChoiceBox.setValue(resultSet.getString("right_hearing_500Hz"));
                right1000ChoiceBox.setValue(resultSet.getString("right_hearing_1000Hz"));
                right2000ChoiceBox.setValue(resultSet.getString("right_hearing_2000Hz"));
                right4000ChoiceBox.setValue(resultSet.getString("right_hearing_4000Hz"));

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
        leftOtoscopyClearCheckBox.setSelected(false);
        leftOtoscopyEarwaxCheckBox.setSelected(false);
        leftOtoscopyFurtherInvCheckBox.setSelected(false);

        rightOtoscopyClearCheckBox.setSelected(false);
        rightOtoscopyEarwaxCheckBox.setSelected(false);
        rightOtoscopyFurtherInvCheckBox.setSelected(false);

        left500ChoiceBox.setValue(null);
        left1000ChoiceBox.setValue(null);
        left2000ChoiceBox.setValue(null);
        left4000ChoiceBox.setValue(null);

        right500ChoiceBox.setValue(null);
        right1000ChoiceBox.setValue(null);
        right2000ChoiceBox.setValue(null);
        right4000ChoiceBox.setValue(null);

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