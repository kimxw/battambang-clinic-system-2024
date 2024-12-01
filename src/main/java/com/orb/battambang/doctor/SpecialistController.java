package com.orb.battambang.doctor;

import com.orb.battambang.MainApp;
import com.orb.battambang.pharmacy.Medicine;
import com.orb.battambang.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class SpecialistController implements Initializable {
    @FXML
    private Button switchUserButton;
    @FXML
    protected TextField queueNumberTextField;
    @FXML
    protected Label queueNoLabel;
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
    private Label bloodPressureLabel;
    @FXML
    private Label temperatureLabel;
    @FXML
    private Label historySystemLabel;
    @FXML
    private Label truncalRotationLabel;
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
    protected Label queueSelectLabel;
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
    private Label status7Label;
    @FXML
    private Rectangle status7Rectangle;
    @FXML
    private Label status8Label;
    @FXML
    private Rectangle status8Rectangle;
    @FXML
    private ImageView TXTImageView;
    @FXML
    protected ToggleButton tbToggleButton;
    @FXML
    protected ToggleButton optometryToggleButton;
    @FXML
    protected ToggleButton hearingToggleButton;
    @FXML
    protected ToggleButton socialToggleButton;
    @FXML
    protected ToggleButton physioToggleButton;

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
    protected Label warningLabel;

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
    private Button menuPhysiotherapistButton;
    @FXML
    private Button menuAudiologistButton;
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

    protected List<Tag> tagList = new ArrayList<>();

    @FXML
    public ImageView connectionImageView;
    @FXML
    public Label connectionStatus;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPhysiotherapistButton, menuAudiologistButton, menuPharmacyButton, menuQueueManagerButton,
                menuAdminButton, menuLogoutButton, menuUserButton, menuLocationButton, connectionImageView, connectionStatus);

        clearAllFields();

        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null || !newValue.isEmpty()) {
                    clearAllFields();
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

        // Initialize columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosageInstructions"));

        nameColumn.setCellFactory(new WrappedTextCellFactory<>());
        quantityColumn.setCellFactory(new WrappedTextCellFactory<>());
        unitsColumn.setCellFactory(new WrappedTextCellFactory<>());
        dosageColumn.setCellFactory(new WrappedTextCellFactory<>());

    }

    private void initialiseTags() {
        Tag Ttag = new Tag(tbToggleButton);
        Tag Otag = new Tag(optometryToggleButton);
        Tag Htag = new Tag(hearingToggleButton);
        Tag Stag = new Tag(socialToggleButton);
        Tag Ptag = new Tag(physioToggleButton);

        tagList.add(Ttag);
        tagList.add(Otag);
        tagList.add(Htag);
        tagList.add(Stag);
        tagList.add(Ptag);
    }

    //-------------------------------------------------------
    //Displaying Patient Records Area
    //-------------------------------------------------------

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
                String vitalsStatus = patientResultSet.getString("vitalSignsStatus");
                String snellensStatus = patientResultSet.getString("snellensStatus");
                String hearingStatus = patientResultSet.getString("hearingStatus");
                String liceStatus = patientResultSet.getString("liceStatus");
                String dentalStatus = patientResultSet.getString("dentalStatus");
                String scoliosisStatus = patientResultSet.getString("scoliosisStatus");
                String historyStatus = patientResultSet.getString("historyStatus");


                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, bmiStatus, true);
                Rectangles.updateStatusRectangle(status2Rectangle, status2Label, snellensStatus, true);
                Rectangles.updateStatusRectangle(status3Rectangle, status3Label, hearingStatus, true);
                Rectangles.updateStatusRectangle(status4Rectangle, status4Label, liceStatus, true);
                Rectangles.updateStatusRectangle(status5Rectangle, status5Label, dentalStatus, true);
                Rectangles.updateStatusRectangle(status6Rectangle, status6Label, historyStatus, true);
                Rectangles.updateStatusRectangle(status7Rectangle, status7Label, vitalsStatus, true);
                Rectangles.updateStatusRectangle(status8Rectangle, status8Label, scoliosisStatus, true);

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
                Rectangles.updateStatusRectangle(status7Rectangle, status7Label, "Not found");
                Rectangles.updateStatusRectangle(status8Rectangle, status8Label, "Not found");

                return;
            }

            // Close the statement
            statement.close();
        } catch (SQLException exc) {
            System.out.println(exc);
            //System.out.println("updateParticularsPane");
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
            ex.printStackTrace();
        }
    }

    private void displayVitals(int queueNumber) {
        String patientQuery = "SELECT * FROM vitalSignsTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                bloodPressureLabel.setText(resultSet.getString("bloodPressure"));
                temperatureLabel.setText(resultSet.getString("temperature"));
            } else {
                clearVitalsFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearVitalsFields();
            ex.printStackTrace();
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

    private void displayScoliosisRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM scoliosisTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                truncalRotationLabel.setText(resultSet.getString("angleOfTruncalRotation"));
            } else {
                clearScoliosisFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearScoliosisFields();
            ex.printStackTrace();
        }
    }

    private void displayHistorySystem(int queueNumber) {
        String patientQuery = "SELECT bodySystem FROM historyTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                historySystemLabel.setText(resultSet.getString("bodySystem"));
            } else {
                clearHistoryFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearHistoryFields();
        }
    }

    @FXML
    public void historyRecordsButtonOnAction(ActionEvent e) {
        if (queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            HistoryRecordsController.queueNumber = Integer.parseInt(queueNoLabel.getText());
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("history-records-view.fxml"));
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
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("full-records-view.fxml"));
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

    //-------------------------------------------------------
    //Resetting Patient Records Area
    //-------------------------------------------------------

    protected void clearAllFields() {
        clearParticularsFields();
        clearBMIFields();
        clearVitalsFields();
        clearHeadLiceFields();
        clearHearingFields();
        clearSnellensFields();
        clearDentalFields();
        clearHistoryFields();
        clearScoliosisFields();
        clearPrescriptionFields();
    }

    private void resetToggleButtons() {
        tbToggleButton.setSelected(false);
        optometryToggleButton.setSelected(false);
        hearingToggleButton.setSelected(false);
        socialToggleButton.setSelected(false);
        physioToggleButton.setSelected(false);
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

    private void clearVitalsFields() {
        bloodPressureLabel.setText("");
        temperatureLabel.setText("");
        Rectangles.clearStatusRectangle(status7Rectangle, status7Label);
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

    private void clearScoliosisFields() {
        truncalRotationLabel.setText("");
        Rectangles.clearStatusRectangle(status8Rectangle, status8Label);
    }

    private void clearHistoryFields() {
        historySystemLabel.setText("");
        Rectangles.clearStatusRectangle(status6Rectangle, status6Label);
    }

    private void clearPrescriptionFields(){
        prescriptionTableView.setItems(FXCollections.observableArrayList());
    }

    //-------------------------------------------------------
    //Displaying Prescription
    //-------------------------------------------------------

    public void displayPrescription(int queueNumber) {
        String patientQuery = "SELECT prescription FROM patientPrescriptionTable WHERE queueNumber = " + queueNumber;
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

    protected ObservableList<Prescription.PrescriptionEntry>  getPrescriptionItems() {
        return prescriptionTableView.getItems();
    }

    //-------------------------------------------------------
    //On Action and direct helpers
    //-------------------------------------------------------
    @FXML
    protected void tagToggleOnAction(ActionEvent e) {
        if(queueNoLabel.getText().isEmpty()) {
            resetToggleButtons();
            return;
        }
        updatePostToggle(Integer.parseInt(queueNoLabel.getText()));
    }

    protected void updatePreToggle(int queueNumber) {
        boolean tb = false;
        boolean opto = false;
        boolean hearing = false;
        boolean social = false;
        boolean physio = false;

        String updateQuery = "SELECT * FROM patientTagTable WHERE queueNumber = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, queueNumber); // Set the queueNumber parameter

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    tb = resultSet.getBoolean("tag_T");
                    opto = resultSet.getBoolean("tag_O");
                    hearing = resultSet.getBoolean("tag_H");
                    social = resultSet.getBoolean("tag_S");
                    physio = resultSet.getBoolean("tag_P");
                } else {
                    Labels.showMessageLabel(queueSelectLabel, "Unable to fetch tags / patient does not exist", false);
                }
            }
        } catch (SQLException e) {
            Labels.showMessageLabel(queueSelectLabel, "Unable to fetch tags / patient does not exist", false);
            throw new RuntimeException(e);
        }

        tbToggleButton.setSelected(tb);
        optometryToggleButton.setSelected(opto);
        hearingToggleButton.setSelected(hearing);
        socialToggleButton.setSelected(social);
        physioToggleButton.setSelected(physio);

    }

    private void updatePostToggle(int queueNumber) {
        boolean tb = tbToggleButton.isSelected();
        boolean opto = optometryToggleButton.isSelected();
        boolean hearing = hearingToggleButton.isSelected();
        boolean social = socialToggleButton.isSelected();
        boolean physio = physioToggleButton.isSelected();

        String updateQuery = "UPDATE patientTagTable SET tag_T = ?, tag_O = ?, tag_H = ?, tag_P = ?, tag_S = ? WHERE queueNumber = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setBoolean(1, tb);
            updateStatement.setBoolean(2, opto);
            updateStatement.setBoolean(3, hearing);
            updateStatement.setBoolean(4, social);
            updateStatement.setBoolean(5, physio);
            updateStatement.setInt(6, queueNumber);

            updateStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a valid queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());

            displayHeightAndWeight(queueNumber);
            displayVitals(queueNumber);
            displayHeadLiceRecords(queueNumber);
            displayHearingRecords(queueNumber);
            displaySnellensRecords(queueNumber);
            displayDentalRecords(queueNumber);
            displayHistorySystem(queueNumber);
            displayScoliosisRecords(queueNumber);
            displayPrescription(queueNumber);
            updatePreToggle(queueNumber); //for tags
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
            controller.setSpecialistController(this);

        } catch (Exception ex) {
            Labels.showMessageLabel(warningLabel, "Unexpected error occured.", false);
        }
    }

    protected void updateButtonOnAction(ActionEvent e) {
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
        PrescriptionController.upsertPrescriptionInDatabase(queueNumber, Prescription.convertToString(getPrescriptionItems()));
    }


    //-------------------------------------------------------
    //Others
    //-------------------------------------------------------

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
    public void editBlockPaneOnMouseClicked(MouseEvent e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a patient first.", ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null); // No header text
        alert.showAndWait();
    }

    @FXML
    public void onEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchButtonOnAction(new ActionEvent());
        }
    }
}
