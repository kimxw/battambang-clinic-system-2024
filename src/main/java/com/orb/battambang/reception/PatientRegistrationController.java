package com.orb.battambang.reception;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.TableViewUpdater;
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
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class PatientRegistrationController implements Initializable {

    private boolean isEditOperation = false;
    private Patient selectedPatientForEdit;

    @FXML
    private Label messageLabel1;
    @FXML
    private Button switchUserButton;
    @FXML
    private Button triageButton;
    @FXML
    private TextField inputNameTextField;
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
    private TableColumn<Patient, Integer> ageTableColumn;
    @FXML
    private TableColumn<Patient, Character> sexTableColumn;
    @FXML
    private TableColumn<Patient, String> phoneNumberTableColumn;
    @FXML
    private TableColumn<Patient, String> addressTableColumn;
    @FXML
    private ChoiceBox<Character> inputSexChoiceBox;
    private final Character[] choiceBoxOptions = new Character[] {'M', 'F'};

    ObservableList<Patient> patientObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputSexChoiceBox.getItems().addAll(choiceBoxOptions);

        // Set cell value factories
        queueNoTableColumn.setCellValueFactory(new PropertyValueFactory<>("queueNo"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageTableColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        sexTableColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        phoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        String patientViewQuery = "SELECT queueNumber, name, age, sex, phoneNumber, address FROM patientQueueTable;";

        try (
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(patientViewQuery)) {

            while (resultSet.next()) {
                Integer queueNo = resultSet.getInt("QueueNumber");
                String name = resultSet.getString("Name");
                Integer age = resultSet.getInt("Age");
                String sexString = resultSet.getString("Sex");
                Character sex = !sexString.isEmpty() ? sexString.charAt(0) : null;
                String phoneNumber = resultSet.getString("PhoneNumber");
                String address = resultSet.getString("Address");

                patientObservableList.add(new Patient(queueNo, name, age, sex, phoneNumber, address));
            }

            // Set items to the TableView
            patientTableView.setItems(patientObservableList);

        } catch (Exception exc) {
            exc.printStackTrace();
        }

        // Start polling to update the TableView
        new TableViewUpdater(patientObservableList, patientTableView);
    }

    @FXML
    public void switchUserButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);

            newUserStage.setTitle("Login");
            newUserStage.setScene(scene);
            Stage stage = (Stage) switchUserButton.getScene().getWindow();
            stage.close();
            newUserStage.show();
        } catch (Exception exc) {
            Labels.showMessageLabel(messageLabel1, "Unable to load page.", false);
        }
    }

    @FXML
    private void triageButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("checkup-menu.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    @FXML
    public void addButtonOnAction(ActionEvent e) {
        String inputName = inputNameTextField.getText();
        String inputAge = inputAgeTextField.getText();
        String inputPhoneNumber = inputPhoneNumberTextField.getText();
        if (inputPhoneNumber.isEmpty()) {
            inputPhoneNumber = "";
        }
        Character inputSex = inputSexChoiceBox.getValue();
        String inputAddress = inputAddressTextArea.getText();

        if (isEditOperation && selectedPatientForEdit != null) {
            // Update the existing patient record
            String updateQuery = "UPDATE patientQueueTable SET name = ?, age = ?, sex = ?, phoneNumber = ?, address = ? WHERE queueNumber = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, inputName);
                statement.setInt(2, Integer.parseInt(inputAge));
                statement.setString(3, String.valueOf(inputSex));
                statement.setString(4, inputPhoneNumber);
                statement.setString(5, inputAddress);
                statement.setInt(6, selectedPatientForEdit.getQueueNo());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 1) {
                    Labels.showMessageLabel(messageLabel1, "Patient updated successfully.", true);

                    // Update the patient in the ObservableList
                    selectedPatientForEdit.setName(inputName);
                    selectedPatientForEdit.setAge(Integer.parseInt(inputAge));
                    selectedPatientForEdit.setSex(inputSex);
                    selectedPatientForEdit.setPhoneNumber(inputPhoneNumber);
                    selectedPatientForEdit.setAddress(inputAddress);

                    // Refresh the TableView to show updated data
                    patientTableView.refresh();
                }
                clearInputFields();
                isEditOperation = false;
                selectedPatientForEdit = null;
            } catch (Exception exc) {
                Labels.showMessageLabel(messageLabel1, "Invalid fields.", false);
            }
        } else {
            // Existing code to add a new patient
            String insertFields = "INSERT INTO patientQueueTable(name, age, sex, phoneNumber, address, bmiStatus, snellensStatus, hearingStatus, liceStatus, dentalStatus, historyStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertFields, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, inputName);
                statement.setInt(2, Integer.parseInt(inputAge));
                statement.setString(3, String.valueOf(inputSex));
                statement.setString(4, inputPhoneNumber);
                statement.setString(5, inputAddress);
                statement.setString(6, "Incomplete");
                statement.setString(7, "Incomplete");
                statement.setString(8, "Incomplete");
                statement.setString(9, "Incomplete");
                statement.setString(10, "Incomplete");
                statement.setString(11, "Incomplete");
                //educationStatus is by default incomplete so no need to add here

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 1) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int queueNo = generatedKeys.getInt(1);
                        Labels.showMessageLabel(messageLabel1, "Patient added successfully.", true);

                        // Add the new patient to the ObservableList
                        patientObservableList.add(new Patient(queueNo, inputName, Integer.parseInt(inputAge), inputSex, inputPhoneNumber, inputAddress));

                        // Also add the new patient to the triageWaitingTable
                        String updateWaitingQueue = "INSERT INTO triageWaitingTable (queueNumber) VALUES (?);";
                        try (PreparedStatement queueUpdateStatement = connection.prepareStatement(updateWaitingQueue)) {
                            queueUpdateStatement.setInt(1, queueNo);
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
        inputAgeTextField.setText("");
        inputPhoneNumberTextField.setText("");
        inputSexChoiceBox.getSelectionModel().clearSelection();
        inputAddressTextArea.setText("");
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("patient-search.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception exc) {
            Labels.showMessageLabel(messageLabel1, "Unexpected error.", false);
        }
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
            inputAgeTextField.setText(String.valueOf(selectedPatientForEdit.getAge()));
            inputPhoneNumberTextField.setText(selectedPatientForEdit.getPhoneNumber());
            inputSexChoiceBox.setValue(selectedPatientForEdit.getSex());
            inputAddressTextArea.setText(selectedPatientForEdit.getAddress());
            isEditOperation = true;
        } else {
            Labels.showMessageLabel(messageLabel1, "Please select a patient to edit.", false);
        }
    }
    @FXML
    public void deleteButtonOnAction(ActionEvent e) {
        // Get the selected item from the TableView
        Patient selectedItem = patientTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            patientObservableList.remove(selectedItem);

            try {
                String deleteQuery = "DELETE FROM patientQueueTable WHERE queueNumber = " + selectedItem.getQueueNo();
                Statement statement = connection.createStatement();
                statement.executeUpdate(deleteQuery);

                // Also delete the patient from the triageWaitingTable and triageProgressTable
                String deleteWaitingQuery = "DELETE FROM triageWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteProgressQuery = "DELETE FROM triageProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                try (Statement deleteStatement = connection.createStatement()) {
                    deleteStatement.executeUpdate(deleteWaitingQuery);
                    deleteStatement.executeUpdate(deleteProgressQuery);
                }

                Labels.showMessageLabel(messageLabel1, "Patient deleted successfully.", true);
            } catch (Exception ex) {
                Labels.showMessageLabel(messageLabel1, "Unexpected Error.", false);
            }
        } else {
            Labels.showMessageLabel(messageLabel1, "Please select a patient to delete.", false);
        }
    }


    void loadFXML(String fxmlFile, ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception exc) {
            Labels.showMessageLabel(messageLabel1, "Unable to load page.", false);
        }
    }

}