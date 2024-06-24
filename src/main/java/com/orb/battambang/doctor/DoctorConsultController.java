package com.orb.battambang.doctor;

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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DoctorConsultController extends DatabaseConnection implements Initializable {
    @FXML
    private Button switchUserButton;
    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;
    @FXML
    private TextField queueNumberTextField;
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
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label bmiLabel;
    @FXML
    private Label bmiCategoryLabel;
    @FXML
    private Rectangle bmiCategoryRectangle;
    @FXML
    private TextArea heightAndWeightTextArea;
    @FXML
    private Label headLiceLabel;
    @FXML
    private TextArea headLiceTextArea;
    @FXML
    private Label hearingProblemsLabel;
    @FXML
    private TextArea hearingTextArea;
    @FXML
    private Label wpRightLabel;
    @FXML
    private Label wpLeftLabel;
    @FXML
    private Label npRightLabel;
    @FXML
    private Label npLeftLabel;
    @FXML
    private TextArea snellensTextArea;
    @FXML
    private TextArea dentalTextArea;

    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label status1Label;
    @FXML
    private Rectangle status1Rectangle;
    @FXML
    private Label status2Label;
    @FXML
    private Rectangle status2Rectangle;
    @FXML
    private Label status3Label;
    @FXML
    private Rectangle status3Rectangle;
    @FXML
    private Label status4Label;
    @FXML
    private Rectangle status4Rectangle;
    @FXML
    private Label status5Label;
    @FXML
    private Rectangle status5Rectangle;
    @FXML
    private Label status6Label;
    @FXML
    private Rectangle status6Rectangle;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearParticularsFields();
        clearBMIFields();
        clearHeadLiceFields();
        clearHearingFields();
        clearSnellensFields();
        clearDentalFields();

        QueueManager waitingQueueManager = new QueueManager(waitingListView, "doctorWaitingTable");
        QueueManager progressQueueManager = new QueueManager(inProgressListView, "doctorProgressTable");

        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null || !newValue.isEmpty()) {
                    clearParticularsFields();
                    clearBMIFields();
                    clearHeadLiceFields();
                    clearHearingFields();
                    clearSnellensFields();
                    clearDentalFields();
                    clearHistoryFields();
                }
            }
        });
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());

            displayHeightAndWeight(queueNumber);
            displayHeadLiceRecords(queueNumber);
            displayHearingRecords(queueNumber);
            displaySnellensRecords(queueNumber);
            displayDentalRecords(queueNumber);
            updateParticularsPane(queueNumber);   // must update after loading all others!

        }
    }

    public void updateParticularsPane(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;

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

                String bmiStatus = patientResultSet.getString("bmiStatus");
                String snellensStatus = patientResultSet.getString("snellensStatus");
                String hearingStatus = patientResultSet.getString("hearingStatus");
                String liceStatus = patientResultSet.getString("liceStatus");
                String dentalStatus = patientResultSet.getString("dentalStatus");
                String historyStatus = patientResultSet.getString("historyStatus");


                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, bmiStatus, true);
                Rectangles.updateStatusRectangle(status2Rectangle, status2Label, snellensStatus, true);
                Rectangles.updateStatusRectangle(status3Rectangle, status3Label, hearingStatus, true);
                Rectangles.updateStatusRectangle(status4Rectangle, status4Label, liceStatus, true);
                Rectangles.updateStatusRectangle(status5Rectangle, status5Label, dentalStatus, true);
                Rectangles.updateStatusRectangle(status6Rectangle, status6Label, historyStatus, true);

            } else {
                nameLabel.setText("");
                ageLabel.setText("");
                sexLabel.setText("");
                phoneNumberLabel.setText("");
                Labels.showMessageLabel(queueSelectLabel, "Patient does not exist", false);
                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, "Not found");
                Rectangles.updateStatusRectangle(status2Rectangle, status2Label, "Not found");
                Rectangles.updateStatusRectangle(status3Rectangle, status3Label, "Not found");
                Rectangles.updateStatusRectangle(status4Rectangle, status4Label, "Not found");
                Rectangles.updateStatusRectangle(status5Rectangle, status5Label, "Not found");
                Rectangles.updateStatusRectangle(status6Rectangle, status6Label, "Not found");

                return;
            }

            // Close the statement
            statement.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
    }

    private void displayHeightAndWeight(int queueNumber) {
        String patientQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                weightLabel.setText(resultSet.getString("weight"));
                heightLabel.setText(resultSet.getString("height"));
                heightAndWeightTextArea.setText(resultSet.getString("additionalNotes"));
                bmiLabel.setText(String.valueOf(resultSet.getDouble("bmi")));

                String bmiCategory = resultSet.getString("bmiCategory");
                bmiCategoryLabel.setText(bmiCategory);

                if (bmiCategory.equals("Underweight")) {
                    bmiCategoryRectangle.setStyle("-fx-fill: #429ebd;");
                } else if (bmiCategory.equals("Healthy Weight")) {
                    bmiCategoryRectangle.setStyle("-fx-fill: #94b447;");
                } else if (bmiCategory.equals("Overweight")) {
                    bmiCategoryRectangle.setStyle("-fx-fill: #cf6024;");
                } else if (bmiCategory.equals("Obese")) {
                    bmiCategoryRectangle.setStyle("-fx-fill: #c4281c;");
                } else {
                    bmiCategoryRectangle.setStyle("-fx-fill: #fefefe;");
                }

            } else {
                clearBMIFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearBMIFields();
        }
    }

    private void displayHeadLiceRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM headLiceTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                boolean hadHeadLice = resultSet.getBoolean("headLice");
                if (hadHeadLice) {
                    headLiceLabel.setText("Yes");
                } else {
                    headLiceLabel.setText("No");
                }
                headLiceTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearHeadLiceFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearHeadLiceFields();
        }
    }

    private void displayHearingRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM hearingTestTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                boolean hasHearingProblems = resultSet.getBoolean("hearingProblems");
                if (hasHearingProblems) {
                    hearingProblemsLabel.setText("Yes");
                } else {
                    hearingProblemsLabel.setText("No");
                }
                hearingTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearHearingFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearHearingFields();
        }
    }

    private void displaySnellensRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM snellensTestTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                wpRightLabel.setText(resultSet.getString("wpRight"));
                wpLeftLabel.setText(resultSet.getString("wpLeft"));
                npRightLabel.setText(resultSet.getString("npRight"));
                npLeftLabel.setText(resultSet.getString("npLeft"));
                snellensTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearSnellensFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearSnellensFields();
        }
    }

    private void displayDentalRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM dentalTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                dentalTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearDentalFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearDentalFields();
        }
    }

    private void clearParticularsFields() {
        queueNoLabel.setText("");
        nameLabel.setText("");
        ageLabel.setText("");
        sexLabel.setText("");
        phoneNumberLabel.setText("");
    }

    private void clearBMIFields() {
        heightLabel.setText("");
        weightLabel.setText("");
        bmiLabel.setText("");
        bmiCategoryLabel.setText("");
        bmiCategoryRectangle.setStyle("-fx-fill: #fefefe;");
        Rectangles.clearStatusRectangle(status1Rectangle, status1Label);
    }

    private void clearHeadLiceFields() {
        headLiceLabel.setText("");
        headLiceTextArea.setText("");
        Rectangles.clearStatusRectangle(status4Rectangle, status4Label);
    }

    private void clearHearingFields() {
        hearingProblemsLabel.setText("");
        hearingTextArea.setText("");
        Rectangles.clearStatusRectangle(status3Rectangle, status3Label);
    }

    private void clearSnellensFields() {
        wpRightLabel.setText("");
        wpLeftLabel.setText("");
        npRightLabel.setText("");
        npLeftLabel.setText("");
        snellensTextArea.setText("");
        Rectangles.clearStatusRectangle(status2Rectangle, status2Label);
    }

    private void clearDentalFields() {
        dentalTextArea.setText("");
        Rectangles.clearStatusRectangle(status5Rectangle, status5Label);
    }

    private void clearHistoryFields() {
        Rectangles.clearStatusRectangle(status6Rectangle, status6Label);
    }

    @FXML
    public void switchUserButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setTitle("Consultation");
            newUserStage.setScene(scene);
            Stage stage = (Stage) switchUserButton.getScene().getWindow();
            stage.close();
            newUserStage.show();
        } catch (Exception exc) {
            exc.printStackTrace();
            exc.getCause();
        }
    }

    @FXML
    private void addButtonOnAction(ActionEvent e) {

    }

    @FXML
    private void sendButtonOnAction(ActionEvent e) {

    }
}
