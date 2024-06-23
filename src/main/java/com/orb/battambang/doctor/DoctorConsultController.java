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

public class DoctorConsultController implements Initializable {
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
    private Label wprLabel;
    @FXML
    private Label wplLabel;
    @FXML
    private Label nprLabel;
    @FXML
    private Label nplLabel;
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
        QueueManager waitingQueueManager = new QueueManager(waitingListView, "doctorWaitingTable");
        QueueManager progressQueueManager = new QueueManager(inProgressListView, "doctorProgressTable");

        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null && !newValue.isEmpty()) {
                    clearParticularsFields();
                }
            }
        });
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            updateParticularsPane(Integer.parseInt(queueNumberTextField.getText()));
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
                Rectangles.updateStatusRectangle(status4Rectangle, status6Label, liceStatus, true);
                Rectangles.updateStatusRectangle(status5Rectangle, status6Label, dentalStatus, true);
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
                Rectangles.updateStatusRectangle(status3Rectangle, status4Label, "Not found");
                Rectangles.updateStatusRectangle(status3Rectangle, status5Label, "Not found");
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

    private void clearParticularsFields() {
        queueNoLabel.setText("");
        nameLabel.setText("");
        ageLabel.setText("");
        sexLabel.setText("");
        phoneNumberLabel.setText("");
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
