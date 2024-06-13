package com.orb.battambang.checkupstation;

import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HearingTestController extends CheckupMenuController implements Initializable {


    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label queueNoLabel;
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
    private Button searchButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;
    @FXML
    private RadioButton yesRadioButton;
    @FXML
    private RadioButton noRadioButton;
    @FXML
    private TextArea additionalNotesTextArea;
    @FXML
    private Label warningLabel;

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

        // Create a ToggleGroup
        ToggleGroup group = new ToggleGroup();

        // Add the radio buttons to the group
        yesRadioButton.setToggleGroup(group);
        noRadioButton.setToggleGroup(group);

    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(Integer.parseInt(queueNumberTextField.getText()));
            particularsPane.setVisible(true);

            String patientQuery = "SELECT * FROM hearingTestTable WHERE queueNumber = " + queueNumber;
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(patientQuery);

                if (resultSet.next()) {
                    boolean hasHearingProblems = resultSet.getBoolean("hearingProblems");
                    if (hasHearingProblems) {
                        yesRadioButton.setSelected(true);
                    } else {
                        noRadioButton.setSelected(true);
                    }
                    additionalNotesTextArea.setText(resultSet.getString("additionalNotes"));
                } else {
                    clearRecordFields();
                }
            } catch (SQLException ex) {
                Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", true);
                clearRecordFields();
            }
        }
    }

    private void clearRecordFields() {
        yesRadioButton.setSelected(false);
        noRadioButton.setSelected(false);
        additionalNotesTextArea.setText("");
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
            exc.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
    }

    @FXML
    private void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            System.out.println(queueNumber);
            addHearingTest(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addHearingTest(int queueNumber) {
        try {

            Boolean hearingProblems = yesRadioButton.isSelected() ? true : noRadioButton.isSelected() ? false : null;
            String notes = additionalNotesTextArea.getText().isEmpty() ? "" : additionalNotesTextArea.getText();

            String insertToCreate = "INSERT OR REPLACE INTO hearingTestTable (queueNumber, hearingProblems, additionalNotes) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertToCreate)) {
                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setBoolean(2, hearingProblems);
                preparedStatement.setString(3, notes);

                preparedStatement.executeUpdate();
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
