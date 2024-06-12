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
import org.controlsfx.control.action.Action;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HeightAndWeightController extends CheckupMenuController implements Initializable {

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
    private TextField heightTextField;
    @FXML
    private TextField weightTextField;
    @FXML
    private TextArea additionalNotesTextArea;
    @FXML
    private Label bmiLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label warningLabel;
    @FXML
    private Rectangle categoryRectangle;
    @FXML
    private Button searchButton;
    @FXML
    private Button updateButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;


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

            String patientQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(patientQuery);

                if (resultSet.next()) {
                    weightTextField.setText(resultSet.getString("weight"));
                    heightTextField.setText(resultSet.getString("height"));
                    additionalNotesTextArea.setText(resultSet.getString("additionalNotes"));
                    bmiButtonOnAction(new ActionEvent());
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
        weightTextField.setText("");
        heightTextField.setText("");
        additionalNotesTextArea.setText("");
        bmiLabel.setText("");
        categoryLabel.setText("");
        categoryRectangle.setStyle("-fx-fill: #dddddd;");
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
            addHeightAndWeight(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addHeightAndWeight(int queueNumber) {
        try {
            String heightStr = heightTextField.getText();
            String weightStr = weightTextField.getText();
            String notes = additionalNotesTextArea.getText().isEmpty() ? "" : additionalNotesTextArea.getText();

            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            double bmi = Double.parseDouble(bmiLabel.getText());
            String bmiCategory = categoryLabel.getText();

            String insertToCreate = "INSERT OR REPLACE INTO heightAndWeightTable(queueNumber, height, weight, bmi, bmiCategory, additionalNotes) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertToCreate)) {
                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setDouble(2, height);
                preparedStatement.setDouble(3, weight);
                preparedStatement.setDouble(4, bmi);
                preparedStatement.setString(5, bmiCategory);
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


    @FXML
    private void bmiButtonOnAction(ActionEvent e) {
        String height = heightTextField.getText();
        String weight = weightTextField.getText();
        if (height.isEmpty() || weight.isEmpty()) {
            Labels.showMessageLabel(warningLabel, "Input height and weight.", false);
        } else {
            double bmi = BMICalculator.calculateBMI(Double.parseDouble(weight), Double.parseDouble(height));

            String ageSexQuery = "SELECT age, sex FROM patientQueueTable WHERE queueNumber = " + queueNoLabel.getText();
            int age = 0;
            String sex = "";

            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(ageSexQuery);
                if (resultSet.next()) {
                    age = resultSet.getInt("age");
                    sex = resultSet.getString("sex");
                }
            } catch (SQLException exc) {
                Labels.showMessageLabel(warningLabel, "Error fetching patient data.", false);
                return;
            }

            String bmiCategory = BMICalculator.determineBMICategory(bmi, age, sex);
            if (bmiCategory.equals("Underweight")) {
                categoryRectangle.setStyle("-fx-fill: #429ebd;");
            } else if (bmiCategory.equals("Healthy Weight")) {
                categoryRectangle.setStyle("-fx-fill: #94b447;");
            } else if (bmiCategory.equals("Overweight")) {
                categoryRectangle.setStyle("-fx-fill: #cf6024;");
            } else if (bmiCategory.equals("Obese")) {
                categoryRectangle.setStyle("-fx-fill: #c4281c;");
            } else {
                categoryRectangle.setStyle("-fx-fill: #6b6b6b;");
            }
            bmiLabel.setText("" + bmi);
            categoryLabel.setText(bmiCategory);
        }
    }


}
