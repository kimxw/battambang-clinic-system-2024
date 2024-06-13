package com.orb.battambang.checkupstation;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private Label status4Label;
    @FXML
    private Rectangle status1Rectangle;
    @FXML
    private Rectangle status2Rectangle;
    @FXML
    private Rectangle status3Rectangle;
    @FXML
    private Rectangle status4Rectangle;
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
    private Label queueNoLabel;
    @FXML
    private TextField systemTextField;
    @FXML
    private TextArea PSTextArea;
    @FXML
    private TextField durationTextField;
    @FXML
    private TextArea drugAllergiesTextArea;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any necessary data here
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

    public void updateParticularsPane(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        String bmiRecordQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
        String snellensRecordQuery = "SELECT * FROM snellensTestTable WHERE queueNumber = " + queueNumber;
        String hearingRecordQuery = "SELECT * FROM hearingTestTable WHERE queueNumber = " + queueNumber;
        String historyRecordQuery = "SELECT * FROM historyTable WHERE queueNumber = " + queueNumber;;

        try {
            Statement statement = DatabaseConnection.connection.createStatement();

            // Fetch patient details
            ResultSet patientResultSet = statement.executeQuery(patientQuery);
            if (patientResultSet.next()) {
                String name = patientResultSet.getString("name");
                int age = patientResultSet.getInt("age");
                String sex = patientResultSet.getString("sex");
                String phoneNumber = patientResultSet.getString("phoneNumber");

                queueNoLabel.setText(String.valueOf(queueNumber));
                nameLabel.setText(name);
                ageLabel.setText(String.valueOf(age));
                sexLabel.setText(sex);
                phoneNumberLabel.setText(phoneNumber);
            } else {
                queueNoLabel.setText("");
                nameLabel.setText("");
                ageLabel.setText("");
                sexLabel.setText("");
                phoneNumberLabel.setText("");
                Labels.showMessageLabel(queueSelectLabel, "Patient does not exist", false);
                status1Rectangle.setStyle("-fx-fill: #707070;");
                status1Label.setText(" Not found");
                status2Rectangle.setStyle("-fx-fill: #707070;");
                status2Label.setText(" Not found");
                status3Rectangle.setStyle("-fx-fill: #707070;");
                status3Label.setText(" Not found");
                status4Rectangle.setStyle("-fx-fill: #707070;");
                status4Label.setText(" Not found");
                return;
            }

            // update record labels
            ResultSet bmiResultSet = statement.executeQuery(bmiRecordQuery);
            if (bmiResultSet.next()) {
                status1Rectangle.setStyle("-fx-fill: #9dd895;");
                status1Label.setText(" Complete");
            } else {
                status1Rectangle.setStyle("-fx-fill: #fa8072;");
                status1Label.setText("Incomplete");
            }

            ResultSet snellensResultSet = statement.executeQuery(snellensRecordQuery);
            if (snellensResultSet.next()) {
                status2Rectangle.setStyle("-fx-fill: #9dd895;");
                status2Label.setText(" Complete");
            } else {
                status2Rectangle.setStyle("-fx-fill: #fa8072;");
                status2Label.setText("Incomplete");
            }

            ResultSet hearingResultSet = statement.executeQuery(hearingRecordQuery);
            if (hearingResultSet.next()) {
                status3Rectangle.setStyle("-fx-fill: #9dd895;");
                status3Label.setText(" Complete");
            } else {
                status3Rectangle.setStyle("-fx-fill: #fa8072;");
                status3Label.setText("Incomplete");
            }

            ResultSet historyResultSet = statement.executeQuery(historyRecordQuery);
            if (historyResultSet.next()) {
                status4Rectangle.setStyle("-fx-fill: #9dd895;");
                status4Label.setText(" Complete");
            } else {
                status4Rectangle.setStyle("-fx-fill: #fa8072;");
                status4Label.setText("Incomplete");
            }

            // Close the statement
            statement.close();
        } catch (SQLException exc) {
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
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
                        Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
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
                        Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
                    }
                }
            }
        } catch (Exception exc2) {
            Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
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
}
