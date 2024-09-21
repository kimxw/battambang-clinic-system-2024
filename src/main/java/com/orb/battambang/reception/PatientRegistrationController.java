package com.orb.battambang.reception;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.Labels;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.time.LocalDate;

import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class PatientRegistrationController implements Initializable {

    private boolean isEditOperation = false;
    private Patient selectedPatientForEdit;

    @FXML
    private Label messageLabel1;
    @FXML
    private TextField inputNameTextField;
    @FXML
    private DatePicker inputDOBDatePicker;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @FXML
    private TextField inputAgeTextField;
    @FXML
    private TextField inputPhoneNumberTextField;
    @FXML
    private TextArea inputAddressTextArea;
    @FXML
    private TableView<Patient> patientTableView;
    @FXML
    private TableColumn<Patient, Integer> queueNoTableColumn;
    @FXML
    private TableColumn<Patient, String> nameTableColumn;
    @FXML
    private TableColumn<Patient, String> DOBTableColumn;
    @FXML
    private TableColumn<Patient, Integer> ageTableColumn;
    @FXML
    private TableColumn<Patient, Character> sexTableColumn;
    @FXML
    private TableColumn<Patient, String> phoneNumberTableColumn;
    @FXML
    private TableColumn<Patient, String> addressTableColumn;
    @FXML
    private RadioButton maleRadioButton;
    @FXML
    private RadioButton femaleRadioButton;

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

    ObservableList<Patient> patientObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);

        // Create a ToggleGroup
        ToggleGroup group = new ToggleGroup();
        maleRadioButton.setToggleGroup(group);
        femaleRadioButton.setToggleGroup(group);

        // Set cell value factories
        queueNoTableColumn.setCellValueFactory(new PropertyValueFactory<>("queueNo"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        DOBTableColumn.setCellValueFactory(new PropertyValueFactory<>("DOB"));
        ageTableColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        sexTableColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        phoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        String patientViewQuery = "SELECT queueNumber, name, DOB, age, sex, phoneNumber, address FROM patientQueueTable;";

        try (
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(patientViewQuery)) {

            while (resultSet.next()) {
                Integer queueNo = resultSet.getInt("QueueNumber");
                String name = resultSet.getString("Name");
                String DOB = resultSet.getString("DOB");
                Integer age = resultSet.getInt("Age");
                String sexString = resultSet.getString("Sex");
                Character sex = !sexString.isEmpty() ? sexString.charAt(0) : null;
                String phoneNumber = resultSet.getString("PhoneNumber");
                String address = resultSet.getString("Address");

                patientObservableList.add(new Patient(queueNo, name, DOB, age, sex, phoneNumber, address));
            }

            // Set items to the TableView
            patientTableView.setItems(patientObservableList);

            startPolling();

        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    private void startPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateTableView());
            }
        }, 0, 30000); // Poll every 30 seconds
    }

    private void updateTableView() {
        String query = "SELECT queueNumber, name, DOB, age, sex, phoneNumber, address FROM patientQueueTable";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            patientObservableList.clear(); // Clear the list before adding new items

            while (resultSet.next()) {
                Integer queueNo = resultSet.getInt("queueNumber");
                String name = resultSet.getString("name");
                String DOB = resultSet.getString("DOB");
                Integer age = resultSet.getInt("age");
                String sexString = resultSet.getString("sex");
                Character sex = !sexString.isEmpty() ? sexString.charAt(0) : null;
                String phoneNumber = resultSet.getString("phoneNumber");
                String address = resultSet.getString("address");

                patientObservableList.add(new Patient(queueNo, name, DOB, age, sex, phoneNumber, address));
            }

            patientTableView.setItems(patientObservableList); // Update the TableView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void computeAgeButtonOnAction(ActionEvent e) {
        if ( inputDOBDatePicker.getValue() != null ) {
            int age = LocalDate.now().getYear() - inputDOBDatePicker.getValue().getYear();
            inputAgeTextField.setText(String.valueOf(age));
        }
    }

    @FXML
    public void addButtonOnAction(ActionEvent e) {
        String inputName = inputNameTextField.getText();
        String inputDOB = null;
        try {
            inputDOB = inputDOBDatePicker.getValue().format(formatter);
        } catch (NullPointerException exc) {
            Labels.showMessageLabel(messageLabel1, "Please add all fields.", false);
        }

        String inputAge = inputAgeTextField.getText();
        String inputPhoneNumber = inputPhoneNumberTextField.getText();
        if (inputPhoneNumber.isEmpty()) {
            inputPhoneNumber = "";
        }
        Character inputSex = maleRadioButton.isSelected() ? Character.valueOf('M') : (femaleRadioButton.isSelected() ? 'F' : null); //interesting boxing phenomenon for ternery operator
        String inputAddress = inputAddressTextArea.getText();

        if (isEditOperation && selectedPatientForEdit != null) {
            // Update the existing patient record
            String updateQuery = "UPDATE patientQueueTable SET name = ?, DOB = ?, age = ?, sex = ?, phoneNumber = ?, address = ? WHERE queueNumber = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, inputName);
                statement.setString(2, inputDOB);
                statement.setInt(3, Integer.parseInt(inputAge));
                statement.setString(4, String.valueOf(inputSex));
                statement.setString(5, inputPhoneNumber);
                statement.setString(6, inputAddress);
                statement.setInt(7, selectedPatientForEdit.getQueueNo());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 1) {
                    Labels.showMessageLabel(messageLabel1, "Patient updated successfully.", true);

                    updateTableView();
                }
                clearInputFields();
                isEditOperation = false;
                selectedPatientForEdit = null;
            } catch (Exception exc) {
                Labels.showMessageLabel(messageLabel1, "Invalid fields.", false);
            }
        } else {
            // Existing code to add a new patient
            String insertFields = "INSERT INTO patientQueueTable(name, DOB, age, sex, phoneNumber, address, bmiStatus, snellensStatus, hearingStatus, liceStatus, dentalStatus, historyStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertFields, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, inputName);
                statement.setString(2, inputDOB);
                statement.setInt(3, Integer.parseInt(inputAge));
                statement.setString(4, String.valueOf(inputSex));
                statement.setString(5, inputPhoneNumber);
                statement.setString(6, inputAddress);
                statement.setString(7, "Incomplete");
                statement.setString(8, "Incomplete");
                statement.setString(9, "Incomplete");
                statement.setString(10, "Incomplete");
                statement.setString(11, "Incomplete");
                statement.setString(12, "Incomplete");
                //educationStatus is by default incomplete so no need to add here

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 1) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int queueNo = generatedKeys.getInt(1);
                        Labels.showMessageLabel(messageLabel1, "Patient added successfully.", true);

                        // Add the new patient to the ObservableList
                        patientObservableList.add(new Patient(queueNo, inputName, inputDOB, Integer.parseInt(inputAge), inputSex, inputPhoneNumber, inputAddress));

                        // Also add the new patient to the triageWaitingTable
                        String updateWaitingQueue = "INSERT INTO triageWaitingTable (queueNumber, name) VALUES (?, ?);";
                        try (PreparedStatement queueUpdateStatement = connection.prepareStatement(updateWaitingQueue)) {
                            queueUpdateStatement.setInt(1, queueNo);
                            queueUpdateStatement.setString(2, inputName);
                            queueUpdateStatement.executeUpdate();
                        }
                    }
                }
                clearInputFields();
            } catch (Exception exc) {
                Labels.showMessageLabel(messageLabel1, "Invalid fields.", false);
            }
        }
    }


    private void clearInputFields() {
        inputNameTextField.setText("");
        inputDOBDatePicker.setValue(null);
        inputAgeTextField.setText("");
        inputPhoneNumberTextField.setText("");
        maleRadioButton.setSelected(false);
        femaleRadioButton.setSelected(false);
        inputAddressTextArea.setText("");
    }



    @FXML
    public void filterButtonOnAction(ActionEvent e) {
        loadFXML("patient-filter.fxml", e);
    }

    @FXML
    public void editButtonOnAction(ActionEvent e) {
        // Get the selected patient from the TableView
        selectedPatientForEdit = patientTableView.getSelectionModel().getSelectedItem();
        if (selectedPatientForEdit != null) {
            // Populate the input fields with the selected patient's details
            inputNameTextField.setText(selectedPatientForEdit.getName());
            if (selectedPatientForEdit.getDOB() == null || selectedPatientForEdit.getDOB().equals("")) {
                inputDOBDatePicker.setValue(null);
            } else {
                inputDOBDatePicker.setValue(LocalDate.parse(selectedPatientForEdit.getDOB(), formatter));
            }
            inputAgeTextField.setText(String.valueOf(selectedPatientForEdit.getAge()));
            inputPhoneNumberTextField.setText(selectedPatientForEdit.getPhoneNumber());
            if (selectedPatientForEdit.getSex().equals('M')) {
                maleRadioButton.setSelected(true);
                femaleRadioButton.setSelected(false);
            } else if (selectedPatientForEdit.getSex().equals('F')) {
                maleRadioButton.setSelected(false);
                femaleRadioButton.setSelected(true);
            } else {
                maleRadioButton.setSelected(false);
                femaleRadioButton.setSelected(false);
            }
            inputAddressTextArea.setText(selectedPatientForEdit.getAddress());
            isEditOperation = true;
        } else {
            Labels.showMessageLabel(messageLabel1, "Please select a row.", false);
        }
    }
    @FXML
    public void deleteButtonOnAction(ActionEvent e) {
        // Get the selected item from the TableView
        Patient selectedItem = patientTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {

            try {
                String deleteQuery = "DELETE FROM patientQueueTable WHERE queueNumber = " + selectedItem.getQueueNo();
                Statement statement = connection.createStatement();
                statement.executeUpdate(deleteQuery);

                // Also delete the patient from the triageWaitingTable and triageProgressTable
                String deleteTWQuery = "DELETE FROM triageWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteTPQuery = "DELETE FROM triageProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteEWQuery = "DELETE FROM educationWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteEPQuery = "DELETE FROM educationProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteDWQuery = "DELETE FROM doctorWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteDPQuery = "DELETE FROM doctorProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deletePWQuery = "DELETE FROM pharmacyWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deletePPQuery = "DELETE FROM pharmacyProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                try (Statement deleteStatement = connection.createStatement()) {
                    deleteStatement.executeUpdate(deleteTWQuery);
                    deleteStatement.executeUpdate(deleteTPQuery);
                    deleteStatement.executeUpdate(deleteEWQuery);
                    deleteStatement.executeUpdate(deleteEPQuery);
                    deleteStatement.executeUpdate(deleteDWQuery);
                    deleteStatement.executeUpdate(deleteDPQuery);
                    deleteStatement.executeUpdate(deletePWQuery);
                    deleteStatement.executeUpdate(deletePPQuery);
                }

                Labels.showMessageLabel(messageLabel1, "Patient deleted successfully.", true);
                patientObservableList.remove(selectedItem);

                clearInputFields();
                statement.close();
            } catch (Exception ex) {
                Labels.showMessageLabel(messageLabel1, "Unexpected Error.", false);
            }
        } else {
            Labels.showMessageLabel(messageLabel1, "Please select a row.", false);
        }
    }


    void loadFXML(String fxmlFile, ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setResizable(false);
            stage.setScene(scene);
        } catch (Exception exc) {
            Labels.showMessageLabel(messageLabel1, "Unable to load page.", false);
        }
    }


}