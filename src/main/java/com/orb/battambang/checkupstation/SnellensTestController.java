package com.orb.battambang.checkupstation;

import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.Text;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SnellensTestController extends CheckupMenuController implements Initializable {


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
            updateParticularsPane(queueNumber);
            particularsPane.setVisible(true);

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
                Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", true);
                clearFields();
            }
        }
    }

    private void clearFields() {
        wpRightTextField.setText("");
        wpLeftTextField.setText("");
        npRightTextField.setText("");
        npLeftTextField.setText("");
        additionalNotesTextArea.setText("");
    }

    public void updateParticularsPane(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        String bmiRecordQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
        String snellensRecordQuery = "SELECT * FROM snellensTestTable WHERE queueNumber = " + queueNumber;
        // String hearingRecordQuery = "";
        // String historyRecordQuery = "";

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

            // ResultSet hearingResultSet = statement.executeQuery(hearingRecordQuery);
            // Update status based on hearingResultSet

            // ResultSet historyResultSet = statement.executeQuery(historyRecordQuery);
            // Update status based on historyResultSet

            // Close the statement
            statement.close();
        } catch (SQLException exc) {
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
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
                Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
            } catch (SQLException e1) {
                Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
            }

        } catch (Exception e2) {
            Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
        }
    }
}
