package com.orb.battambang.checkupstation;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import com.orb.battambang.util.QueueManager;
import com.orb.battambang.util.Rectangles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HistoryController extends CheckupMenuController implements Initializable {

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
    private Button editButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;

    @FXML
    private Label warningLabel;
    @FXML
    private Label editWarningLabel;
    @FXML
    private Label deferLabel;
    @FXML
    private Label queueNoLabel;
    @FXML
    private TextField systemTextField;
    @FXML
    private TextArea PSTextArea;
    @FXML
    private TextField durationTextField;
    @FXML
    private TextArea drugAllergiesTextArea;

    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // for waiting list
        // Initialize the waiting list
        QueueManager waitingQueueManager = new QueueManager(waitingListView, "triageWaitingTable");
        QueueManager progressQueueManager = new QueueManager(inProgressListView, "triageProgressTable");

        // Add a listener to the text property of the queueNumberTextField
        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null && !newValue.isEmpty()) {
                    particularsPane.setVisible(false);
                    clearParticularsFields();
                }
            }
        });

        particularsPane.setVisible(false); // Initially hide the particularsPane
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(Integer.parseInt(queueNumberTextField.getText()));
            particularsPane.setVisible(true);

            String patientQuery = "SELECT * FROM historyTable WHERE queueNumber = " + queueNumber;
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(patientQuery);

                if (resultSet.next()) {
                    systemTextField.setText(resultSet.getString("bodySystem"));
                    PSTextArea.setText(resultSet.getString("PS"));
                    durationTextField.setText(resultSet.getString("duration"));
                    drugAllergiesTextArea.setText(resultSet.getString("drugAllergies"));
                } else {
                    clearFields();
                }
            } catch (SQLException ex) {
                Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", true);
                clearFields();
            }
        }
    }

    private void clearFields() {
        systemTextField.setText("");
        PSTextArea.setText("");
        durationTextField.setText("");
        drugAllergiesTextArea.setText("");
    }


    @FXML
    private void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addSymptoms(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addSymptoms(int queueNumber) {
        try {
            String system = systemTextField.getText().isEmpty() ? null : systemTextField.getText();
            String PS = PSTextArea.getText().isEmpty() ? null : PSTextArea.getText();
            String duration = durationTextField.getText().isEmpty() ? null : durationTextField.getText();
            String allergies = drugAllergiesTextArea.getText().isEmpty() ? null : drugAllergiesTextArea.getText();

            // Check if the row exists
            String checkExistQuery = "SELECT COUNT(*) FROM historyTable WHERE queueNumber = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkExistQuery)) {
                checkStatement.setInt(1, queueNumber);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    // If row exists, update the specific columns
                    String updateQuery = "UPDATE historyTable SET bodySystem = ?, PS = ?, duration = ?, drugAllergies = ? WHERE queueNumber = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, system);
                        updateStatement.setString(2, PS);
                        updateStatement.setString(3, duration);
                        updateStatement.setString(4, allergies);
                        updateStatement.setInt(5, queueNumber);

                        updateStatement.executeUpdate();
                    }
                } else {
                    // If row doesn't exist, insert a new row
                    String insertQuery = "INSERT INTO historyTable (queueNumber, bodySystem, PS, duration, drugAllergies) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setInt(1, queueNumber);
                        insertStatement.setString(2, system);
                        insertStatement.setString(3, PS);
                        insertStatement.setString(4, duration);
                        insertStatement.setString(5, allergies);

                        insertStatement.executeUpdate();
                    }
                }

                String updateStatusQuery = "UPDATE patientQueueTable SET bmiStatus = 'Complete' WHERE queueNumber = ?";
                try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                    updateStatusStatement.setInt(1, queueNumber);
                    updateStatusStatement.executeUpdate();
                }
                Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
            }
        } catch (Exception exc2) {
            Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
        }
    }

    @FXML
    private void deferButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);

        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            String updateStatusQuery = "UPDATE patientQueueTable SET historyStatus = 'Deferred' WHERE queueNumber = ?";
            try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                updateStatusStatement.setInt(1, queueNumber);
                updateStatusStatement.executeUpdate();
                Labels.showMessageLabel(deferLabel, "Defered Q" + queueNumber + " successfully", "blue");
            } catch (SQLException e1) {
                Labels.showMessageLabel(deferLabel, "Unable to defer Q" + queueNumber, false);
            }
            updateParticularsPane(queueNumber);
        }
    }

    void loadFXMLInNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            Stage currentStage = (Stage) editButton.getScene().getWindow();
            stage.initOwner(currentStage);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void editButtonOnAction(ActionEvent e) {
        try {
            EditHistoryController.queueNumber = Integer.parseInt(queueNoLabel.getText());
            loadFXMLInNewWindow("edit-history.fxml", "Edit history");
        } catch (Exception exc) {
            Labels.showMessageLabel(editWarningLabel, "Select a patient.", false);
        }

    }

    private void clearParticularsFields() {
        queueNoLabel.setText("");
        nameLabel.setText("");
        ageLabel.setText("");
        sexLabel.setText("");
        phoneNumberLabel.setText("");
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

        String deleteFromWaitingListQuery = "DELETE FROM triageWaitingTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO triageProgressTable (queueNumber) VALUES (?)";

        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
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
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
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
            movePatientToEducation(selectedPatient);
        }
    }

    private void movePatientToEducation(Integer queueNumber) {

        String deleteFromProgressListQuery = "DELETE FROM triageProgressTable WHERE queueNumber = ?";
        String insertIntoNextListQuery = "INSERT INTO educationProgressTable (queueNumber) VALUES (?)";

        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteFromProgressListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoNextListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
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
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
