package com.orb.battambang.doctor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.layout.Pane;


import static com.orb.battambang.connection.DatabaseConnection.connection;

public class ReferralController implements Initializable {

    @FXML
    private Pane pane1;
    @FXML
    private Button exitButton;
    @FXML
    private Button printButton;
    @FXML
    private Button updateButton;
    @FXML
    private TextField dateTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField ageTextField;
    @FXML
    private TextField genderTextField;
    @FXML
    private TextArea addressTextArea;
    @FXML
    private TextField diagnosisTextField;
    @FXML
    private TextField durationTextField;
    @FXML
    private TextField docNameTextField;
    @FXML
    private TextArea medicationTextArea;
    @FXML
    private TextArea notesTextArea;
    private int queueNumber = -1;

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
        initData(); // Initialize data after setting queueNumber
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Autofill the date
        dateTextField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private void initData() {
        if (queueNumber != -1) {
            // Fill patient details and doctor consultation details using queueNumber
            fillPatientDetails(queueNumber);
            fillDoctorConsultDetails(queueNumber);
            // Fill doctor referral notes if existing
            fillDoctorNotes(queueNumber);
        } else {
            // handle the case where queueNumber is not set
            //System.out.println("Queue number is not set. Data initialization skipped.");
        }
    }

    private void fillPatientDetails(int queueNumber) {
        String patientViewQuery = "SELECT name, age, sex, address FROM patientQueueTable WHERE queueNumber = " + queueNumber + ";";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(patientViewQuery);

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Integer age = resultSet.getInt("age");
                String gender = resultSet.getString("sex");
                String address = resultSet.getString("address");

                nameTextField.setText(name);
                ageTextField.setText(String.valueOf(age));
                genderTextField.setText(gender);
                addressTextArea.setText(address);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }

    private void fillDoctorConsultDetails(int queueNumber) {
        String consultViewQuery = "SELECT doctor, prescription FROM doctorConsultTable WHERE queueNumber = " + queueNumber + ";";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consultViewQuery);

            if (resultSet.next()) {
                String doctor = resultSet.getString("doctor");
                String prescription = resultSet.getString("prescription");

                docNameTextField.setText(doctor);
                medicationTextArea.setText(prescription);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }

    private void fillDoctorNotes(int queueNumber) {
        String query = "SELECT referral FROM doctorConsultTable WHERE queueNumber = " + queueNumber + ";";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                String referral = resultSet.getString("referral");
                if (referral != null && !referral.isEmpty()) {
                    String[] parts = referral.split(";");
                    if (parts.length == 3) {
                        diagnosisTextField.setText(parts[0]);
                        durationTextField.setText(parts[1]);
                        notesTextArea.setText(parts[2]);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }

    @FXML
    public void updateButtonOnAction(ActionEvent event) {
        String diagnosis = diagnosisTextField.getText();
        String duration = durationTextField.getText();
        String notes = notesTextArea.getText();
        String referral = diagnosis + ";" + duration + ";" + notes;

        String updateQuery = "UPDATE doctorConsultTable SET referral = ? WHERE queueNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

            pstmt.setString(1, referral);
            pstmt.setInt(2, queueNumber);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }

    @FXML
    public void printButtonOnAction(ActionEvent e) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null) {
            // Get the content of pane1 for printing
            Node contentToPrint = pane1;

            // Show the print dialog
            boolean proceed = printerJob.showPrintDialog(exitButton.getScene().getWindow());

            if (proceed) {
                // Print the content of pane1
                boolean printed = printerJob.printPage(contentToPrint);
                if (printed) {
                    printerJob.endJob();
                } else {
                    //System.out.println("Printing failed.");
                }
            }
        }
    }



    @FXML
    public void exitButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

}
