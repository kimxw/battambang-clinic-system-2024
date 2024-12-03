package com.orb.battambang.reception;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.Labels;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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
    private TextField inputFaceIDTextArea;

    @FXML
    private TextField queueSearchTextField;
    @FXML
    private TextField nameSearchTextField;
    @FXML
    private TextField phoneNumberSearchTextField;
    @FXML
    private TextField faceIDSearchTextField;

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
    private TableColumn<Patient, String> faceIDTableColumn;
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
    public ImageView connectionImageView;
    @FXML
    public Label connectionStatus;

    ObservableList<Patient> patientObservableList = FXCollections.observableArrayList();
    FilteredList<Patient> filteredList = new FilteredList<>(patientObservableList);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPhysiotherapistButton, menuAudiologistButton, menuPharmacyButton, menuQueueManagerButton,
                menuAdminButton, menuLogoutButton, menuUserButton, menuLocationButton, connectionImageView, connectionStatus);

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
        faceIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("faceID"));

        String patientViewQuery = "SELECT queueNumber, name, DOB, age, sex, phoneNumber, address, faceID FROM patientQueueTable;";

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
                String faceID = resultSet.getString("faceID");

                patientObservableList.add(new Patient(queueNo, name, DOB, age, sex, phoneNumber, address, faceID));
            }

            // Set items to the TableView
            patientTableView.setItems(patientObservableList);

            // Filter by queue number
            queueSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(patientSearchModel -> {
                    String searchQueue = newValue.trim();
                    String searchName = nameSearchTextField.getText().trim().toLowerCase();
                    String searchPhone = phoneNumberSearchTextField.getText().trim();
                    String searchFaceID = faceIDSearchTextField.getText().trim();
                    boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                    boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().contains(searchPhone);
                    boolean matchFaceID = searchFaceID.isEmpty() || patientSearchModel.getFaceID().contains(searchFaceID);
                    return matchQueue && matchName && matchPhone && matchFaceID;
                });
            });

            // Filter by name
            nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(patientSearchModel -> {
                    String searchQueue = queueSearchTextField.getText().trim();
                    String searchName = newValue.trim().toLowerCase();
                    String searchPhone = phoneNumberSearchTextField.getText().trim();
                    String searchFaceID = faceIDSearchTextField.getText().trim();
                    boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                    boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().contains(searchPhone);
                    boolean matchFaceID = searchFaceID.isEmpty() || patientSearchModel.getFaceID().contains(searchFaceID);
                    return matchQueue && matchName && matchPhone && matchFaceID;
                });
            });

            // Filter by phone number
            phoneNumberSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(patientSearchModel -> {
                    String searchQueue = queueSearchTextField.getText().trim();
                    String searchName = nameSearchTextField.getText().trim().toLowerCase();
                    String searchPhone = newValue.trim();
                    String searchFaceID = faceIDSearchTextField.getText().trim();
                    boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                    boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().contains(searchPhone);
                    boolean matchFaceID = searchFaceID.isEmpty() || patientSearchModel.getFaceID().contains(searchFaceID);
                    return matchQueue && matchName && matchPhone && matchFaceID;
                });
            });

            // Filter by faceID
            faceIDSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(patientSearchModel -> {
                    String searchQueue = queueSearchTextField.getText().trim();
                    String searchName = nameSearchTextField.getText().trim().toLowerCase();
                    String searchPhone = phoneNumberSearchTextField.getText().trim();
                    String searchFaceID = newValue.trim();
                    boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                    boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().contains(searchPhone);
                    boolean matchFaceID = searchFaceID.isEmpty() || patientSearchModel.getFaceID().contains(searchFaceID);
                    return matchQueue && matchName && matchPhone && matchFaceID;
                });
            });

            SortedList<Patient> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(patientTableView.comparatorProperty());
            patientTableView.setItems(sortedList);

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
        String query = "SELECT queueNumber, name, DOB, age, sex, phoneNumber, address, faceID FROM patientQueueTable";
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
                String faceID = resultSet.getString("faceID");

                patientObservableList.add(new Patient(queueNo, name, DOB, age, sex, phoneNumber, address, faceID));
            }

            // Ensure the filter and sort are re-applied

            filteredList.setPredicate(patientSearchModel -> {
                String searchQueue = queueSearchTextField.getText().trim();
                String searchName = nameSearchTextField.getText().trim().toLowerCase();
                String searchPhone = phoneNumberSearchTextField.getText().trim();
                String searchFaceID = faceIDSearchTextField.getText().trim();
                boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().contains(searchPhone);
                boolean matchFaceID = searchFaceID.isEmpty() || patientSearchModel.getFaceID().contains(searchFaceID);
                return matchQueue && matchName && matchPhone && matchFaceID;
            });

            SortedList<Patient> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(patientTableView.comparatorProperty());

            patientTableView.setItems(sortedList); // Update the TableView

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
        String inputFaceID = inputFaceIDTextArea.getText() ;

        if (isEditOperation && selectedPatientForEdit != null) {
            // Update the existing patient record
            String updateQuery = "UPDATE patientQueueTable SET name = ?, DOB = ?, age = ?, sex = ?, phoneNumber = ?, address = ?, faceID = ? WHERE queueNumber = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, inputName);
                statement.setString(2, inputDOB);
                statement.setInt(3, Integer.parseInt(inputAge));
                statement.setString(4, String.valueOf(inputSex));
                statement.setString(5, inputPhoneNumber);
                statement.setString(6, inputAddress);
                statement.setString(7, inputFaceID);
                statement.setInt(8, selectedPatientForEdit.getQueueNo());

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
            String insertFields = "INSERT INTO patientQueueTable(name, DOB, age, sex, phoneNumber, address, faceID, bmiStatus, snellensStatus, hearingStatus, liceStatus, dentalStatus, historyStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertFields, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, inputName);
                statement.setString(2, inputDOB);
                statement.setInt(3, Integer.parseInt(inputAge));
                statement.setString(4, String.valueOf(inputSex));
                statement.setString(5, inputPhoneNumber);
                statement.setString(6, inputAddress);
                statement.setString(7, inputFaceID);
                statement.setString(8, "Incomplete");
                statement.setString(9, "Incomplete");
                statement.setString(10, "Incomplete");
                statement.setString(11, "Incomplete");
                statement.setString(12, "Incomplete");
                statement.setString(13, "Incomplete");
                //educationStatus is by default incomplete so no need to add here

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 1) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int queueNo = generatedKeys.getInt(1);
                        Labels.showMessageLabel(messageLabel1, "Patient added successfully.", true);

                        // Add the new patient to the ObservableList
                        patientObservableList.add(new Patient(queueNo, inputName, inputDOB, Integer.parseInt(inputAge), inputSex, inputPhoneNumber, inputAddress, inputFaceID));

                        // Also add the new patient to the triageWaitingTable
                        String updateWaitingQueue = "INSERT INTO triageWaitingTable (queueNumber, name) VALUES (?, ?);";
                        try (PreparedStatement queueUpdateStatement = connection.prepareStatement(updateWaitingQueue)) {
                            queueUpdateStatement.setInt(1, queueNo);
                            queueUpdateStatement.setString(2, inputName);
                            queueUpdateStatement.executeUpdate();
                        }

                        // Create tags for the patient
                        String createTags = "INSERT INTO patientTagTable (queueNumber, tag_T, tag_O, tag_H, tag_P, tag_S) VALUES (?, FALSE, FALSE, FALSE, FALSE, FALSE);";
                        try (PreparedStatement createTagsStatement = connection.prepareStatement(createTags)) {
                            createTagsStatement.setInt(1, queueNo);
                            createTagsStatement.executeUpdate(); // Execute the query to insert the tags
                        }
                    }
                }


                clearInputFields();
            } catch (Exception exc) {
                exc.printStackTrace();
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
        inputFaceIDTextArea.setText("");
    }


//    @FXML
//    public void filterButtonOnAction(ActionEvent e) {
//        loadFXML("patient-filter.fxml", e);
//    }

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
            inputFaceIDTextArea.setText(selectedPatientForEdit.getFaceID());
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
                String deletePhWQuery = "DELETE FROM physioWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deletePhPQuery = "DELETE FROM physioProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteAWQuery = "DELETE FROM audioWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deleteAPQuery = "DELETE FROM audioProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deletePWQuery = "DELETE FROM pharmacyWaitingTable WHERE queueNumber = " + selectedItem.getQueueNo();
                String deletePPQuery = "DELETE FROM pharmacyProgressTable WHERE queueNumber = " + selectedItem.getQueueNo();
                try (Statement deleteStatement = connection.createStatement()) {
                    deleteStatement.executeUpdate(deleteTWQuery);
                    deleteStatement.executeUpdate(deleteTPQuery);
                    deleteStatement.executeUpdate(deleteEWQuery);
                    deleteStatement.executeUpdate(deleteEPQuery);
                    deleteStatement.executeUpdate(deleteDWQuery);
                    deleteStatement.executeUpdate(deleteDPQuery);
                    deleteStatement.executeUpdate(deletePhWQuery);
                    deleteStatement.executeUpdate(deletePhPQuery);
                    deleteStatement.executeUpdate(deleteAWQuery);
                    deleteStatement.executeUpdate(deleteAPQuery);
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

    @FXML
    public void newQueueNumberForExistingOnAction(ActionEvent e) {
        Patient selectedItem = patientTableView.getSelectionModel().getSelectedItem();
        String inputName = selectedItem.getName();
        String inputDOB = selectedItem.getDOB();
        Integer inputAge = selectedItem.getAge();
        String inputPhoneNumber = selectedItem.getPhoneNumber();
        if (inputPhoneNumber == null) {
            inputPhoneNumber = "";
        }
        Character inputSex = selectedItem.getSex();
        String inputAddress = selectedItem.getAddress();
        String inputFaceID = selectedItem.getFaceID() ;

        String insertFields = "INSERT INTO patientQueueTable(name, DOB, age, sex, phoneNumber, address, faceID, bmiStatus, snellensStatus, hearingStatus, liceStatus, dentalStatus, historyStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertFields, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, inputName);
            statement.setString(2, inputDOB);
            statement.setInt(3, inputAge);
            statement.setString(4, String.valueOf(inputSex));
            statement.setString(5, inputPhoneNumber);
            statement.setString(6, inputAddress);
            statement.setString(7, inputFaceID);
            statement.setString(8, "Incomplete");
            statement.setString(9, "Incomplete");
            statement.setString(10, "Incomplete");
            statement.setString(11, "Incomplete");
            statement.setString(12, "Incomplete");
            statement.setString(13, "Incomplete");
            //educationStatus is by default incomplete so no need to add here

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 1) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int queueNo = generatedKeys.getInt(1);
                    Labels.showMessageLabel(messageLabel1, "Patient added successfully.", true);

                    // Add the new patient to the ObservableList
                    patientObservableList.add(new Patient(queueNo, inputName, inputDOB, inputAge, inputSex, inputPhoneNumber, inputAddress, inputFaceID));

                    // Also add the new patient to the triageWaitingTable
                    String updateWaitingQueue = "INSERT INTO triageWaitingTable (queueNumber, name) VALUES (?, ?);";
                    try (PreparedStatement queueUpdateStatement = connection.prepareStatement(updateWaitingQueue)) {
                        queueUpdateStatement.setInt(1, queueNo);
                        queueUpdateStatement.setString(2, inputName);
                        queueUpdateStatement.executeUpdate();
                    }

                    // Create tags for the patient
                    String createTags = "INSERT INTO patientTagTable (queueNumber, tag_T, tag_O, tag_H, tag_P, tag_S) VALUES (?, FALSE, FALSE, FALSE, FALSE, FALSE);";
                    try (PreparedStatement createTagsStatement = connection.prepareStatement(createTags)) {
                        createTagsStatement.setInt(1, queueNo);
                        createTagsStatement.executeUpdate(); // Execute the query to insert the tags
                    }
                }
            }


            clearInputFields();
        } catch (Exception exc) {
            exc.printStackTrace();
            Labels.showMessageLabel(messageLabel1, "Invalid fields.", false);
        }

    }


}