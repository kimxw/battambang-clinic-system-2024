package com.orb.battambang.doctor;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.nio.file.StandardWatchEventKinds;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


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
    private Button typesOpenButton;
    @FXML
    private Button typesCloseButton;
    @FXML
    private CheckBox mongKolCheckBox;
    @FXML
    private CheckBox poipetCheckBox;
    @FXML
    private CheckBox sevaCheckBox;
    @FXML
    private CheckBox wsCheckBox;
    @FXML
    private CheckBox optometristCheckbox;
    @FXML
    private CheckBox dentistCheckBox;
    @FXML
    private CheckBox bongBongdolCheckBox;

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
    private Map<CheckBox, String> checkBoxHashMap = new HashMap<>();

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;

        this.checkBoxHashMap.put(mongKolCheckBox, "MongKol Borey Hospital");
        this.checkBoxHashMap.put(poipetCheckBox, "Poipet Referral Hospital");
        this.checkBoxHashMap.put(sevaCheckBox, "SEVA");
        this.checkBoxHashMap.put(wsCheckBox, "WS Audiology");
        this.checkBoxHashMap.put(optometristCheckbox, "Optometrist");
        this.checkBoxHashMap.put(dentistCheckBox, "Dentist");
        this.checkBoxHashMap.put(bongBongdolCheckBox, "Bong Bondol");

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

    private String generateTypesAsString() {
        StringBuilder types = new StringBuilder();

        for (Map.Entry<CheckBox, String> entry : checkBoxHashMap.entrySet()) {
            if (entry.getKey().isSelected()) {
                types.append(entry.getValue()).append("; ");
            }
        }

        return types.toString();
    }

    private void markReferralTypes(String typesAsString) {
        for (Map.Entry<CheckBox, String> entry : this.checkBoxHashMap.entrySet()) {
            if (typesAsString == null) {
                return;
            }
            if (typesAsString.contains(entry.getValue())) {
                entry.getKey().setSelected(true);
            }
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
        String consultViewQuery = "SELECT doctor, referralTypes FROM doctorConsultTable WHERE queueNumber = " + queueNumber + ";";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consultViewQuery);

            if (resultSet.next()) {
                String doctor = resultSet.getString("doctor");
                //String prescription = resultSet.getString("prescription");
                String referralTypes = resultSet.getString("referralTypes");

                markReferralTypes(referralTypes);

                docNameTextField.setText(doctor);
                //medicationTextArea.setText(prescription);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }

    private void fillDoctorNotes(int queueNumber) {
        String query = "SELECT referral, referralTypes FROM doctorConsultTable WHERE queueNumber = " + queueNumber + ";";

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
                String referralTypes = resultSet.getString("referralTypes");
                markReferralTypes(referralTypes);
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
        String referralTypes = generateTypesAsString();

        String updateQuery = "UPDATE doctorConsultTable SET referral = ?, referralTypes = ? WHERE queueNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

            pstmt.setString(1, referral);
            pstmt.setString(2, referralTypes);
            pstmt.setInt(3, queueNumber);
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
            boolean proceed = printerJob.showPrintDialog(dateTextField.getScene().getWindow());

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


}
