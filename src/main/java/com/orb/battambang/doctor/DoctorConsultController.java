package com.orb.battambang.doctor;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import static com.orb.battambang.connection.DatabaseConnection.connection;

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
    @FXML
    private ImageView TXTImageView;

    @FXML
    private TextArea inputConsultNotesTextArea;
    @FXML
    private TableView<Prescription.PrescriptionEntry> prescriptionTableView;
    @FXML
    private TableColumn<Prescription.PrescriptionEntry, String> nameColumn;
    @FXML
    private TableColumn<Prescription.PrescriptionEntry, String> quantityColumn;
    @FXML
    private TableColumn<Prescription.PrescriptionEntry, String> unitsColumn;
    @FXML
    private TableColumn<Prescription.PrescriptionEntry, String> dosageColumn;
    @FXML
    private ChoiceBox<String> conditionChoiceBox;
    private String[] condition = {"None", "Acute", "Chronic", "Acute and Chronic"};
    @FXML
    private Button updateButton;
    @FXML
    private Button createReferralButton;
    @FXML
    private Button addMedicationButton;
    @FXML
    private RadioButton yesRadioButton, noRadioButton;
    @FXML
    private CheckBox consultCompleteCheckBox;
    @FXML
    private Label warningLabel;

    @FXML
    private AnchorPane sliderAnchorPane;
    @FXML
    private Label menuLabel;
    @FXML
    private Label menuBackLabel;
    @FXML
    private Button menuHomeButton;
    @FXML
    private Button menuReceptionButton;
    @FXML
    private Button menuTriageButton;
    @FXML
    private Button menuEducationButton;
    @FXML
    private Button menuConsultationButton;
    @FXML
    private Button menuPharmacyButton;
    @FXML
    private Button menuQueueManagerButton;
    @FXML
    private Button menuAdminButton;
    @FXML
    private Button menuLogoutButton;
    @FXML
    private Button menuUserButton;
    @FXML
    private Button menuLocationButton;

    @FXML
    private Pane editBlockPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);

        clearParticularsFields();
        clearBMIFields();
        clearHeadLiceFields();
        clearHearingFields();
        clearSnellensFields();
        clearDentalFields();
        clearConsultFields();

        MiniQueueManager waitingQueueManager = new MiniQueueManager(waitingListView, "doctorWaitingTable");
        MiniQueueManager progressQueueManager = new MiniQueueManager(inProgressListView, "doctorProgressTable");

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
                    clearConsultFields();
                }
            }
        });

        // EDIT BLOCK PANE, QUEUENOLABEL LISTENER
        queueNoLabel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Check if the new value is empty
                if (newValue == null || newValue.trim().isEmpty()) {
                    // Show the pane if the text is empty
                    editBlockPane.setVisible(true);
                } else {
                    // Hide the pane if the text is not empty
                    editBlockPane.setVisible(false);
                }
            }
        });

        conditionChoiceBox.getItems().addAll(condition);

        // Initialize columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosageInstructions"));

        nameColumn.setCellFactory(new WrappedTextCellFactory<>());
        quantityColumn.setCellFactory(new WrappedTextCellFactory<>());
        unitsColumn.setCellFactory(new WrappedTextCellFactory<>());
        dosageColumn.setCellFactory(new WrappedTextCellFactory<>());

        // Create a ToggleGroup for referral radio buttons
        ToggleGroup group = new ToggleGroup();
        yesRadioButton.setToggleGroup(group);
        noRadioButton.setToggleGroup(group);
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNoLabel.getText().equals("")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
            return;
        }

        String consultNotes = inputConsultNotesTextArea.getText();
        String condition = conditionChoiceBox.getValue();
        String prescriptionString = Prescription.convertToString(prescriptionTableView.getItems());
        boolean referralStatus = yesRadioButton.isSelected();
        String doctorConsultStatus = consultCompleteCheckBox.isSelected() ? "Complete" : "Incomplete";

        String queueNumberText = queueNumberTextField.getText();
        if (queueNumberText.isEmpty()) {
            Labels.showMessageLabel(warningLabel, "Please fill in the queue number.", false);
            return;
        }

        int queueNumber;
        try {
            queueNumber = Integer.parseInt(queueNumberText);
        } catch (NumberFormatException ex) {
            Labels.showMessageLabel(warningLabel, "Please enter a valid queue number.", false);
            return;
        }

        // Check if queueNumber exists in patientQueueTable
        String checkQueueNumberQuery = "SELECT COUNT(*) FROM patientQueueTable WHERE queueNumber = ?";
        try (PreparedStatement checkQueueNumberStmt = connection.prepareStatement(checkQueueNumberQuery)) {
            checkQueueNumberStmt.setInt(1, queueNumber);
            ResultSet resultSet = checkQueueNumberStmt.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count == 0) {
                Labels.showMessageLabel(warningLabel, "Queue number does not exist.", false);
                return;
            }
        } catch (SQLException ex) {
            System.out.println(ex); // Handle the exception appropriately
            System.out.println("updateButtonOnAction");
            Labels.showMessageLabel(warningLabel, "Database error occurred.", false);
            return;
        }


        if (conditionChoiceBox.getValue().equals("")) {
            Labels.showMessageLabel(warningLabel, "Please choose a condition.", false);
            return;
        }

        if (!yesRadioButton.isSelected() && !noRadioButton.isSelected()) {
            Labels.showMessageLabel(warningLabel, "Please select a referral status.", false);
            return;
        }

        // Check if queueNumber exists in doctorConsultTable
        String checkDoctorConsultQuery = "SELECT COUNT(*) FROM doctorConsultTable WHERE queueNumber = ?";
        try (PreparedStatement checkDoctorConsultStmt = connection.prepareStatement(checkDoctorConsultQuery)) {
            checkDoctorConsultStmt.setInt(1, queueNumber);
            ResultSet resultSet = checkDoctorConsultStmt.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                // Update existing record
                String updateDoctorConsultTableQuery = "UPDATE doctorConsultTable SET consultationNotes = ?, condition = ?, prescription = ?, referralStatus = ?, doctor = ? WHERE queueNumber = ?";
                try (PreparedStatement doctorConsultTableStmt = connection.prepareStatement(updateDoctorConsultTableQuery)) {
                    doctorConsultTableStmt.setString(1, consultNotes);
                    doctorConsultTableStmt.setString(2, condition);
                    doctorConsultTableStmt.setString(3, prescriptionString);
                    doctorConsultTableStmt.setBoolean(4, referralStatus);
                    doctorConsultTableStmt.setString(5, menuUserButton.getText()); // Update doctor column with doctorLabel text
                    doctorConsultTableStmt.setInt(6, queueNumber);
                    doctorConsultTableStmt.executeUpdate();
                }
            } else {
                // Insert new record
                String insertDoctorConsultTableQuery = "INSERT INTO doctorConsultTable (queueNumber, consultationNotes, condition, prescription, referralStatus, doctor) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement doctorConsultTableStmt = connection.prepareStatement(insertDoctorConsultTableQuery)) {
                    doctorConsultTableStmt.setInt(1, queueNumber);
                    doctorConsultTableStmt.setString(2, consultNotes);
                    doctorConsultTableStmt.setString(3, condition);
                    doctorConsultTableStmt.setString(4, prescriptionString);
                    doctorConsultTableStmt.setBoolean(5, referralStatus);
                    doctorConsultTableStmt.setString(6, menuUserButton.getText()); // Set doctor column with doctorLabel text
                    doctorConsultTableStmt.executeUpdate();
                }
            }

            // Update patientQueueTable
            String updatePatientQueueTableQuery = "UPDATE patientQueueTable SET doctorConsultStatus = ? WHERE queueNumber = ?";
            try (PreparedStatement patientQueueTableStmt = connection.prepareStatement(updatePatientQueueTableQuery)) {
                patientQueueTableStmt.setString(1, doctorConsultStatus);
                patientQueueTableStmt.setInt(2, queueNumber);
                patientQueueTableStmt.executeUpdate();
            }

            // Clear warning label and show success message if all operations succeed
            Labels.showMessageLabel(warningLabel, "Update successful.", true);
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println("updateButtonOnAction2");
            Labels.showMessageLabel(warningLabel, "Database error occurred.", false);
        }
    }


    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());

            displayHeightAndWeight(queueNumber);
            displayHeadLiceRecords(queueNumber);
            displayHearingRecords(queueNumber);
            displaySnellensRecords(queueNumber);
            displayDentalRecords(queueNumber);
            displayConsultationNotes(queueNumber);
            displayPrescription(queueNumber);
            displayCondition(queueNumber);
            displayReferral(queueNumber);
            displayConsultComplete(queueNumber);
            updateParticularsPane(queueNumber);   // must update after loading all others!

        }
    }

    @FXML
    public void editPrescriptionButtonOnAction(ActionEvent e) {

        if (queueNoLabel.getText().equals("")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("prescription.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Prescription");
            stage.setScene(scene);
            stage.show();

            // Set the queue number in the PrescriptionController
            String queueNumberText = queueNumberTextField.getText();
            PrescriptionController controller = loader.getController(); // Get the controller instance
            controller.setQueueNumber(Integer.parseInt(queueNumberText));
            // Pass instance of DoctorConsultController to PrescriptionController
            controller.setDoctorConsultController(this);

        } catch (Exception ex) {
            Labels.showMessageLabel(warningLabel, "Unexpected error occured.", false);
        }
    }

    @FXML
    public void radioButtonOnAction(ActionEvent e) {
        if (yesRadioButton.isSelected()) {
            createReferralButton.setVisible(true);
        } else {
            createReferralButton.setVisible(false);
        }
    }

    @FXML
    public void createReferralButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().trim().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("referral.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Create Referral");
            stage.setScene(scene);

            // Get the controller instance
            ReferralController controller = loader.getController();

            // Set the queue number
            String queueNumberText = queueNumberTextField.getText().trim();
            controller.setQueueNumber(Integer.parseInt(queueNumberText));

            stage.show();

        } catch (Exception ex) {
            Labels.showMessageLabel(warningLabel, "Unexpected error occurred.", false);
            ex.printStackTrace();  // Print stack trace for debugging
        }
    }


    public void displayConsultationNotes(int queueNumber) {
        String patientQuery = "SELECT * FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                inputConsultNotesTextArea.setText(resultSet.getString("consultationNotes"));
            } else {
                inputConsultNotesTextArea.setText("");
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            inputConsultNotesTextArea.setText("");
        }
    }

    public void displayPrescription(int queueNumber) {
        String patientQuery = "SELECT prescription FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String prescriptionText = resultSet.getString("prescription");

                if (prescriptionText != null && !prescriptionText.isEmpty()) {
                    // Convert prescriptionText to ObservableList<Prescription.PrescriptionEntry>
                    ObservableList<Prescription.PrescriptionEntry> prescriptionList = Prescription.convertToObservableList(prescriptionText);

                    // Display prescriptionList in TableView
                    prescriptionTableView.setItems(prescriptionList);
                } else {
                    // If prescriptionText is null or empty, clear the TableView
                    prescriptionTableView.setItems(FXCollections.observableArrayList());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex); // Handle SQLException properly in your application
        }
    }

    //overloaded
    public void displayPrescription(ObservableList<Prescription.PrescriptionEntry> updatedPrescriptionList) {
        prescriptionTableView.setItems(updatedPrescriptionList);
    }

    public void displayCondition(int queueNumber) {
        String patientQuery = "SELECT * FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String condition = resultSet.getString("condition");
                conditionChoiceBox.setValue(condition); // Set the selected value in the ChoiceBox
            } else {
                conditionChoiceBox.setValue(""); // Set an empty value if no condition is found
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }

    public void displayReferral(int queueNumber) {
        String patientQuery = "SELECT * FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                boolean referralStatus = resultSet.getBoolean("referralStatus");
                if (referralStatus) {
                    yesRadioButton.setSelected(true);
                    createReferralButton.setVisible(true);
                } else {
                    noRadioButton.setSelected(true); // Select No if referralStatus is false
                }
            } else {
                yesRadioButton.setSelected(false);
                noRadioButton.setSelected(false);
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }

    public void displayConsultComplete(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String doctorConsultStatus = resultSet.getString("doctorConsultStatus");

                // Toggle CheckBox based on doctorConsultStatus
                if ("Complete".equalsIgnoreCase(doctorConsultStatus)) {
                    consultCompleteCheckBox.setSelected(true); // Tick the CheckBox for complete status
                } else {
                    consultCompleteCheckBox.setSelected(false); // Untick the CheckBox for incomplete or deferred status
                }
            } else {
                // If no result found, clear CheckBox selection
                consultCompleteCheckBox.setSelected(false);
            }
        } catch (SQLException ex) {
            // Handle SQLException, optionally show a message or log the error
            System.out.println(ex);
        }
    }

    public void updateParticularsPane(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;

        try {
            Statement statement = connection.createStatement();

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
            System.out.println(exc);
            System.out.println("updateParticularsPane");
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

    private void clearConsultFields() {
        inputConsultNotesTextArea.setText("");
        prescriptionTableView.setItems(FXCollections.observableArrayList());
        conditionChoiceBox.setValue("");
        yesRadioButton.setSelected(false);
        noRadioButton.setSelected(false);
        consultCompleteCheckBox.setSelected(false);
        createReferralButton.setVisible(false);
    }


    @FXML
    public void historyRecordsButtonOnAction(ActionEvent e) {
        if (queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            HistoryRecordsController.queueNumber = Integer.parseInt(queueNoLabel.getText());
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("history-records.fxml"));
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Patient History");
                stage.setScene(new Scene(root));

                // Set the new window's owner to the primary stage
                Stage primaryStage = (Stage) queueNumberTextField.getScene().getWindow(); // Assuming you have a reference to a node in the primary stage
                stage.initOwner(primaryStage);

                // Show the new window
                stage.show();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    @FXML
    public void TXTButtonOnAction() {
        Image pressed = new Image(MainApp.class.getResource("/icons/txt-button-pressed.png").toExternalForm());
        TXTImageView.setImage(pressed);
    }

    @FXML
    public void TXTButtonOnRelease() {
        Image unpressed = new Image(MainApp.class.getResource("/icons/txt-button-unpressed.png").toExternalForm());
        TXTImageView.setImage(unpressed);

        if (queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            RecordsViewController.queueNumber = Integer.parseInt(queueNoLabel.getText());
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("records-view.fxml"));
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Records.txt");
                stage.setScene(new Scene(root));

                // Set the new window's owner to the primary stage
                Stage primaryStage = (Stage) queueNumberTextField.getScene().getWindow(); // Assuming you have a reference to a node in the primary stage
                stage.initOwner(primaryStage);

                // Show the new window
                stage.show();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

    }

    @FXML
    public void switchUserButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setResizable(false);
            newUserStage.setTitle("Consultation");
            newUserStage.setScene(scene);
            Stage stage = (Stage) switchUserButton.getScene().getWindow();
            stage.close();
            newUserStage.show();
        } catch (Exception exc) {
            System.out.println(exc);
            exc.getCause();
        }
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
        String nameFromWaitingListQuery = "SELECT name FROM doctorWaitingTable WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM doctorWaitingTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO doctorProgressTable (queueNumber, name) VALUES (?, ?)";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
            insertStatement.setString(2, name);
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
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
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
            movePatientToPharmacy(selectedPatient);
        }
    }

    private void movePatientToPharmacy(Integer queueNumber) {

        String nameFromWaitingListQuery = "SELECT name FROM doctorProgressTable WHERE queueNumber = ?";
        String deleteFromProgressListQuery = "DELETE FROM doctorProgressTable WHERE queueNumber = ?";
        String insertIntoNextListQuery = "INSERT INTO pharmacyWaitingTable (queueNumber, name) VALUES (?, ?)";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromProgressListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoNextListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
            insertStatement.setString(2, name);
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
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    @FXML
    public void editBlockPaneOnMouseClicked(MouseEvent e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a patient first.", ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null); // No header text
        alert.showAndWait();
    }
}
