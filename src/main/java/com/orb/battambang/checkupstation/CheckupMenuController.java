package com.orb.battambang.checkupstation;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;

import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Rectangles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class CheckupMenuController extends DatabaseConnection implements Initializable {

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
    private Button switchUserButton;
    @FXML
    private Button heightAndWeightButton;
    @FXML
    private Button snellensTestButton;
    @FXML
    private Button hearingTestButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;


    private FXMLLoader fxmlLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //fxmlLoader = new FXMLLoader();

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

    void loadFXML(String fxmlFile, ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @FXML
    public void switchUserButtonOnAction(ActionEvent e) {
        loadFXML("login-page.fxml", e);
    }

    @FXML
    public void heightAndWeightButtonOnAction(ActionEvent e) {
        loadFXML("height-and-weight.fxml", e);
    }

    @FXML
    public void snellensTestButtonOnAction(ActionEvent e) {
        loadFXML("snellens-test.fxml", e);
    }

    @FXML
    public void hearingTestButtonOnAction(ActionEvent e) {
        loadFXML("hearing-test.fxml", e);
    }

    @FXML
    public void historyButtonOnAction(ActionEvent e) {
        loadFXML("history.fxml", e);
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            updateParticularsPane(Integer.parseInt(queueNumberTextField.getText()));
            particularsPane.setVisible(true);
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


                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, bmiStatus);
                Rectangles.updateStatusRectangle(status2Rectangle, status2Label, snellensStatus);
                Rectangles.updateStatusRectangle(status3Rectangle, status3Label, hearingStatus);
                Rectangles.updateStatusRectangle(status4Rectangle, status4Label, historyStatus);

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
}
