package com.orb.battambang.admin;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.AuthDatabaseConnection;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.login.Staff;
import com.orb.battambang.pharmacy.UpdateInventoryController;
import com.orb.battambang.reception.Patient;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.Labels;
import com.orb.battambang.util.PatientTableViewUpdater;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class AdminController implements Initializable {

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
    private TextField staffIDSearchTextField;
    @FXML
    private TextField firstNameSearchTextField;
    @FXML
    private TextField lastNameSearchTextField;
    @FXML
    private TextField usernameSearchTextField;

    @FXML
    private TextField staffIDUpdateTextField;
    @FXML
    private TextField firstNameUpdateTextField;
    @FXML
    private TextField lastNameUpdateTextField;
    @FXML
    private TextField usernameUpdateTextField;
    @FXML
    private ChoiceBox<String> primaryRoleChoiceBox;

    @FXML
    private CheckBox adminCheckBox;
    @FXML
    private CheckBox receptionCheckBox;
    @FXML
    private CheckBox triageCheckBox;
    @FXML
    private CheckBox educationCheckBox;
    @FXML
    private CheckBox consultationCheckBox;
    @FXML
    private CheckBox pharmacyCheckBox;

    @FXML
    private TableView<Staff> staffTableView;
    @FXML
    private TableColumn<Staff, Integer> staffIDTableColumn;
    @FXML
    private TableColumn<Staff, String> firstNameTableColumn;
    @FXML
    private TableColumn<Staff, String> lastNameTableColumn;
    @FXML
    private TableColumn<Staff, String> usernameTableColumn;
    @FXML
    private TableColumn<Staff, String> primaryRoleTableColumn;

    private final ObservableList<Staff> staffObservableList = FXCollections.observableArrayList();
    private final FilteredList<Staff> filteredList = new FilteredList<>(staffObservableList);

    @FXML
    private Label warningLabel;
    @FXML
    private Label exportWarningLabel;

    private final String[] roles = {"Admin", "Reception", "Triage", "Education", "Consultation", "Pharmacy"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AuthDatabaseConnection.establishConnection();

        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);

        initializeTableColumns();
        initializeStaffList();
        setupListeners();
        startPolling();
        clearUpdateFields();

        primaryRoleChoiceBox.getItems().addAll(roles);

        staffIDUpdateTextField.setEditable(false);

    }

    private void initializeTableColumns() {
        staffIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        primaryRoleTableColumn.setCellValueFactory(new PropertyValueFactory<>("primaryRole"));
    }

    private void initializeStaffList() {
        String staffViewQuery = "SELECT * FROM staffTable;";

        if(!AuthDatabaseConnection.isConnectionOpen()) {
            return;
        }

        try (Statement statement = AuthDatabaseConnection.connection.createStatement();
             ResultSet resultSet = statement.executeQuery(staffViewQuery)) {

            while (resultSet.next()) {
                int staffID = resultSet.getInt("staffID");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                String primaryRole = resultSet.getString("primaryRole");
                boolean admin = resultSet.getBoolean("admin");
                boolean reception = resultSet.getBoolean("reception");
                boolean triage = resultSet.getBoolean("triage");
                boolean education = resultSet.getBoolean("education");
                boolean consultation = resultSet.getBoolean("consultation");
                boolean pharmacy = resultSet.getBoolean("pharmacy");

                staffObservableList.add(new Staff(staffID, firstName, lastName, username, primaryRole, admin, reception, triage, education, consultation, pharmacy));
            }

            SortedList<Staff> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(staffTableView.comparatorProperty());
            staffTableView.setItems(sortedList);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void setupListeners() {
        ChangeListener<String> listener = (observable, oldValue, newValue) -> updateFilter();

        staffIDSearchTextField.textProperty().addListener(listener);
        firstNameSearchTextField.textProperty().addListener(listener);
        lastNameSearchTextField.textProperty().addListener(listener);
        usernameSearchTextField.textProperty().addListener(listener);

        // Listener for row selection
        staffTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                staffIDUpdateTextField.setText(Integer.toString(newValue.getStaffID()));
                firstNameUpdateTextField.setText(newValue.getFirstName());
                lastNameUpdateTextField.setText(newValue.getLastName());
                usernameUpdateTextField.setText(newValue.getUsername());
                primaryRoleChoiceBox.setValue(newValue.getPrimaryRole());

                adminCheckBox.setSelected(newValue.isAdmin());
                receptionCheckBox.setSelected(newValue.isReception());
                triageCheckBox.setSelected(newValue.isTriage());
                educationCheckBox.setSelected(newValue.isEducation());
                consultationCheckBox.setSelected(newValue.isConsultation());
                pharmacyCheckBox.setSelected(newValue.isPharmacy());
            }
        });
    }

    private void updateFilter() {
        filteredList.setPredicate(staff -> filterStaff(staff,
                staffIDSearchTextField.getText().trim(),
                firstNameSearchTextField.getText().trim().toLowerCase(),
                lastNameSearchTextField.getText().trim().toLowerCase(),
                usernameSearchTextField.getText().trim().toLowerCase()
        ));
    }

    private boolean filterStaff(Staff staff, String staffID, String firstName, String lastName, String username) {
        boolean matchesID = staffID.isEmpty() || Integer.toString(staff.getStaffID()).contains(staffID);
        boolean matchesFirstName = firstName.isEmpty() || staff.getFirstName().toLowerCase().contains(firstName);
        boolean matchesLastName = lastName.isEmpty() || staff.getLastName().toLowerCase().contains(lastName);
        boolean matchesUsername = username.isEmpty() || staff.getUsername().toLowerCase().contains(username);

        return matchesID && matchesFirstName && matchesLastName && matchesUsername;
    }

    private void startPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(AdminController.this::updateTableView);
            }
        }, 0, 30000); // Poll every 30 seconds
    }

    private void updateTableView() {

        if(!AuthDatabaseConnection.isConnectionOpen()) {
            return;
        }

        String query = "SELECT * FROM staffTable";
        try (Statement statement = AuthDatabaseConnection.connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            staffObservableList.clear(); // Clear the list before adding new items

            while (resultSet.next()) {
                int staffID = resultSet.getInt("staffID");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                String primaryRole = resultSet.getString("primaryRole");
                boolean admin = resultSet.getBoolean("admin");
                boolean reception = resultSet.getBoolean("reception");
                boolean triage = resultSet.getBoolean("triage");
                boolean education = resultSet.getBoolean("education");
                boolean consultation = resultSet.getBoolean("consultation");
                boolean pharmacy = resultSet.getBoolean("pharmacy");

                staffObservableList.add(new Staff(staffID, firstName, lastName, username, primaryRole, admin, reception, triage, education, consultation, pharmacy));
            }

            // Reapply the filter after updating the list
            filteredList.setPredicate(staff -> filterStaff(staff, staffIDSearchTextField.getText().trim(),
                                                                  firstNameSearchTextField.getText().trim().toLowerCase(),
                                                                  lastNameSearchTextField.getText().trim().toLowerCase(),
                                                                  usernameSearchTextField.getText().trim().toLowerCase()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearUpdateFields() {
        staffIDUpdateTextField.setText("");
        firstNameSearchTextField.setText("");
        lastNameSearchTextField.setText("");
        usernameSearchTextField.setText("");
        primaryRoleChoiceBox.setValue(null);

        adminCheckBox.setSelected(false);
        receptionCheckBox.setSelected(false);
        triageCheckBox.setSelected(false);
        educationCheckBox.setSelected(false);
        consultationCheckBox.setSelected(false);
        pharmacyCheckBox.setSelected(false);

    }


    @FXML
    private void updateButtonOnAction(ActionEvent E) {

        if(!AuthDatabaseConnection.isConnectionOpen()) {
            return;
        }

        try {
            int staffID = Integer.parseInt(staffIDUpdateTextField.getText());
            String firstName = firstNameUpdateTextField.getText();
            String lastName = lastNameUpdateTextField.getText();
            String username = usernameUpdateTextField.getText();
            String primaryRole = primaryRoleChoiceBox.getValue();
            boolean admin = adminCheckBox.isSelected();
            boolean reception = receptionCheckBox.isSelected();
            boolean triage = triageCheckBox.isSelected();
            boolean education = educationCheckBox.isSelected();
            boolean consultation = consultationCheckBox.isSelected();
            boolean pharmacy = pharmacyCheckBox.isSelected();

            String updateQuery = "UPDATE staffTable SET firstName = ?, lastName = ?, username = ?, primaryRole = ?, " +
                    "admin = ?, reception = ?, triage = ?, education = ?, consultation = ?, pharmacy = ? WHERE staffID = ?";
            try (PreparedStatement statement = AuthDatabaseConnection.connection.prepareStatement(updateQuery)) {
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, username);
                statement.setString(4, primaryRole);
                statement.setBoolean(5, admin);
                statement.setBoolean(6, reception);
                statement.setBoolean(7, triage);
                statement.setBoolean(8, education);
                statement.setBoolean(9, consultation);
                statement.setBoolean(10, pharmacy);
                statement.setInt(11, staffID);

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 1) {
                    Labels.showMessageLabel(warningLabel, "Entry with ID " + staffID + " updated successfully.", true);
                    updateTableView();
                    clearUpdateFields();
                } else {
                    Labels.showMessageLabel(warningLabel, "ID may not exit. Add a new entry.", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Labels.showMessageLabel(warningLabel, "Unexpected Error", false);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Labels.showMessageLabel(warningLabel, "Ensure all fields are filled correctly.", false);
        }
    }


    @FXML
    private void deleteButtonOnAction(ActionEvent E) {

        if(!AuthDatabaseConnection.isConnectionOpen()) {
            return;
        }

        Staff selectedEntry = staffTableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            int id = selectedEntry.getStaffID();
            try {
                String deleteQuery = "DELETE FROM staffTable WHERE staffID = " + id;
                Statement statement = AuthDatabaseConnection.connection.createStatement();
                statement.executeUpdate(deleteQuery);

                Labels.showMessageLabel(warningLabel, "Staff with ID " + id + " deleted successfully.", true);
                updateTableView();

                clearUpdateFields();
                statement.close();

            } catch (Exception e) {
                System.out.println(e);
                Labels.showMessageLabel(warningLabel, "Unexpected error", false);
            }

        } else {
            Labels.showMessageLabel(warningLabel, "Please select a row to delete.", false);
        }
    }


    @FXML
    private void exportPatientDataButtonOnAction(ActionEvent event) {
        // Create a DirectoryChooser to select the folder
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder for Export");

        // Open the dialog and get the selected folder
        File selectedFolder = directoryChooser.showDialog(((Stage) ((Node) event.getSource()).getScene().getWindow()));

        if (selectedFolder != null) {
            // Prompt for the file name
            TextInputDialog fileNameDialog = new TextInputDialog("PatientData.csv");
            fileNameDialog.setTitle("Specify File Name");
            fileNameDialog.setHeaderText("Enter the file name for the exported data.");
            fileNameDialog.setContentText("File name:");

            // Get the file name from user
            Optional<String> result = fileNameDialog.showAndWait();
            if (result.isPresent()) {
                String fileName = result.get().trim();
                if (fileName.isEmpty()) {
                    Labels.showMessageLabel(warningLabel, "File name cannot be empty. Export canceled.", false);
                    return;
                }

                File file = new File(selectedFolder, fileName);

                // Check if the file already exists
                if (file.exists()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("File Already Exists");
                    alert.setHeaderText(null);
                    alert.setContentText("File already exists. Do you want to overwrite it?");
                    Optional<ButtonType> action = alert.showAndWait();

                    if (action.isPresent() && action.get() != ButtonType.OK) {
                        Labels.showMessageLabel(warningLabel, "Export canceled.", false);
                        return;
                    }
                }

                // Create a file writer and print writer
                try (FileWriter fileWriter = new FileWriter(file);
                     PrintWriter printWriter = new PrintWriter(fileWriter)) {

                    // Write header for CSV
                    printWriter.println("queueNumber,name,DOB,age,sex,phoneNumber,address,bmiStatus,snellensStatus,hearingStatus,liceStatus,dentalStatus,historyStatus,educationStatus,doctorConsultStatus,pharmacyStatus,height,weight,bmi,bmiCategory,wpRight,wpLeft,npRight,npLeft,hearingProblems,headLice,bodySystem,PS,duration,drugAllergies,HPI,DH,PH,SH,FH,SR,consultationNotes,prescription,referralStatus,condition,referral");

                    // Execute SQL query with joins
                    String query = """
                                    SELECT \s
                                    pq.queueNumber AS queueNumber, pq.name, pq.DOB, pq.age, pq.sex, pq.phoneNumber, pq.address,\s
                                    pq.bmiStatus, pq.snellensStatus, pq.hearingStatus, pq.liceStatus, pq.dentalStatus, pq.historyStatus,\s
                                    pq.educationStatus, pq.doctorConsultStatus, pq.pharmacyStatus, \s
                                    hwt.height, hwt.weight, hwt.bmi, hwt.bmiCategory, \s
                                    st.wpRight, st.wpLeft, st.npRight, st.npLeft, \s
                                    ht.hearingProblems, \s
                                    hl.headLice, \s
                                    ht_history.bodySystem, ht_history.PS, ht_history.duration, ht_history.drugAllergies, ht_history.HPI,\s
                                    ht_history.DH, ht_history.PH, ht_history.SH, ht_history.FH, ht_history.SR,\s
                                    dc.consultationNotes, dc.prescription, dc.referralStatus, dc.condition, dc.referral\s
                                FROM patientQueueTable pq
                                LEFT JOIN heightAndWeightTable hwt ON pq.queueNumber = hwt.queueNumber\s
                                LEFT JOIN snellensTestTable st ON pq.queueNumber = st.queueNumber\s
                                LEFT JOIN hearingTestTable ht ON pq.queueNumber = ht.queueNumber\s
                                LEFT JOIN headLiceTable hl ON pq.queueNumber = hl.queueNumber\s
                                LEFT JOIN historyTable ht_history ON pq.queueNumber = ht_history.queueNumber\s
                                LEFT JOIN doctorConsultTable dc ON pq.queueNumber = dc.queueNumber;
                            """;

                    try (Statement statement = DatabaseConnection.connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(query)) {

                        while (resultSet.next()) {
                            int queueNumber = resultSet.getInt("queueNumber");
                            String name = escapeCsv(resultSet.getString("name"));
                            String dob = escapeCsv(resultSet.getString("DOB"));
                            int age = resultSet.getInt("age");
                            String sex = escapeCsv(resultSet.getString("sex"));
                            String phoneNumber = escapeCsv(resultSet.getString("phoneNumber"));
                            String address = escapeCsv(resultSet.getString("address"));
                            String bmiStatus = escapeCsv(resultSet.getString("bmiStatus"));
                            String snellensStatus = escapeCsv(resultSet.getString("snellensStatus"));
                            String hearingStatus = escapeCsv(resultSet.getString("hearingStatus"));
                            String liceStatus = escapeCsv(resultSet.getString("liceStatus"));
                            String dentalStatus = escapeCsv(resultSet.getString("dentalStatus"));
                            String historyStatus = escapeCsv(resultSet.getString("historyStatus"));
                            String educationStatus = escapeCsv(resultSet.getString("educationStatus"));
                            String doctorConsultStatus = escapeCsv(resultSet.getString("doctorConsultStatus"));
                            String pharmacyStatus = escapeCsv(resultSet.getString("pharmacyStatus"));

                            double height = resultSet.getDouble("height");
                            double weight = resultSet.getDouble("weight");
                            double bmi = resultSet.getDouble("bmi");
                            String bmiCategory = escapeCsv(resultSet.getString("bmiCategory"));

                            String wpRight = escapeCsv(resultSet.getString("wpRight"));
                            String wpLeft = escapeCsv(resultSet.getString("wpLeft"));
                            String npRight = escapeCsv(resultSet.getString("npRight"));
                            String npLeft = escapeCsv(resultSet.getString("npLeft"));

                            boolean hearingProblems = resultSet.getBoolean("hearingProblems");
                            boolean headLice = resultSet.getBoolean("headLice");

                            String bodySystem = escapeCsv(resultSet.getString("bodySystem"));
                            String PS = escapeCsv(resultSet.getString("PS"));
                            String duration = escapeCsv(resultSet.getString("duration"));
                            String drugAllergies = escapeCsv(resultSet.getString("drugAllergies"));
                            String HPI = escapeCsv(resultSet.getString("HPI"));
                            String DH = escapeCsv(resultSet.getString("DH"));
                            String PH = escapeCsv(resultSet.getString("PH"));
                            String SH = escapeCsv(resultSet.getString("SH"));
                            String FH = escapeCsv(resultSet.getString("FH"));
                            String SR = escapeCsv(resultSet.getString("SR"));

                            String consultationNotes = escapeCsv(resultSet.getString("consultationNotes"));
                            String prescription = escapeCsv(resultSet.getString("prescription"));
                            boolean referralStatus = resultSet.getBoolean("referralStatus");
                            String condition = escapeCsv(resultSet.getString("condition"));
                            String referral = escapeCsv(resultSet.getString("referral"));
                            //Write data to CSV
                            printWriter.printf("%d,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%f,%f,%f,%s,%s,%s,%s,%s,%b,%b,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                                    queueNumber, name, dob, age, sex, phoneNumber, address, bmiStatus, snellensStatus,
                                    hearingStatus, liceStatus, dentalStatus, historyStatus, educationStatus,
                                    doctorConsultStatus, pharmacyStatus, height, weight, bmi, bmiCategory, wpRight,
                                    wpLeft, npRight, npLeft, hearingProblems, headLice, bodySystem, PS, duration,
                                    drugAllergies, HPI, DH, PH, SH, FH, SR, consultationNotes, prescription, referralStatus,
                                    condition, referral);

                        }
                    }

                    Labels.showMessageLabel(exportWarningLabel, "Patient data exported successfully.", true);
                    //Labels.showMessageLabel(exportWarningLabel, "Patient data exported successfully to " + file.getAbsolutePath(), true);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    Labels.showMessageLabel(exportWarningLabel, "Error exporting patient data. Ensure the file is not currently open.", false);
                }
            } else {
                Labels.showMessageLabel(exportWarningLabel, "No file name provided. Export canceled.", false);
            }
        } else {
            Labels.showMessageLabel(exportWarningLabel, "No folder selected. Export canceled.", false);
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Enclose in quotes if necessary
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }




}
