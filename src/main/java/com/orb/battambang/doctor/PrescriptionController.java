package com.orb.battambang.doctor;

import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.pharmacy.Medicine;
import com.orb.battambang.util.Prescription;
import com.orb.battambang.util.WrappedTextCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.util.function.Predicate;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;

public class PrescriptionController extends DatabaseConnection implements Initializable {

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
    private TextField inputIdTextField;

    @FXML
    private TextField inputNameTextField;

    @FXML
    private TextField inputQuantityTextField;

    @FXML
    private TextField inputUnitsTextField;

    @FXML
    private TextArea inputDosageTextArea;

    @FXML
    private Button addUpdateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button exitButton;

    @FXML
    private TableView<Medicine> medicineTableView;

    @FXML
    private TableColumn<Medicine, Integer> idTableColumn;

    @FXML
    private TableColumn<Medicine, String> medicineNameTableColumn;

    @FXML
    private TableColumn<Medicine, Integer> medicineQuantityTableColumn;

    @FXML
    private TableColumn<Medicine, Integer> stockTableColumn;

    private int queueNumber;

    private DoctorConsultController doctorConsultController;

    ObservableList<Medicine> medicineObservableList = FXCollections.observableArrayList();
    ObservableList<Prescription.PrescriptionEntry> prescriptionObservableList = FXCollections.observableArrayList();

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
        loadPrescriptions();
    }

    public void setDoctorConsultController(DoctorConsultController doctorConsultController) {
        this.doctorConsultController = doctorConsultController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMedicineTable();
        initializePrescriptionTable();
    }

    private void initializeMedicineTable() {
        String medicineViewQuery = "SELECT id, name, quantityInMilligrams, stockLeft FROM medicineTable;";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(medicineViewQuery);

            while (resultSet.next()) {
                Integer id = resultSet.getInt("Id");
                String name = resultSet.getString("Name");
                Integer quantity = resultSet.getInt("QuantityInMilligrams");
                Integer stock = resultSet.getInt("StockLeft");

                medicineObservableList.add(new Medicine(id, name, quantity, stock));
            }

            idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            medicineNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            medicineQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
            stockTableColumn.setCellValueFactory(new PropertyValueFactory<>("stockLeft"));

            medicineTableView.setItems(medicineObservableList);

            FilteredList<Medicine> filteredList = new FilteredList<>(medicineObservableList);

            // Filter by id
            inputIdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(medicineSearchModel -> {
                    String searchId = newValue.trim();
                    String searchName = inputNameTextField.getText().trim().toLowerCase();
                    String searchQuantity = inputQuantityTextField.getText().trim();
                    boolean matchId = searchId.isEmpty() || medicineSearchModel.getId().toString().contains(searchId);
                    boolean matchName = searchName.isEmpty() || medicineSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchQuantity = searchQuantity.isEmpty() || medicineSearchModel.getQuantityInMilligrams().toString().contains(searchQuantity);
                    return matchId && matchName && matchQuantity;
                });
            });

            // Filter by name
            inputNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(medicineSearchModel -> {
                    String searchId = inputIdTextField.getText().trim();
                    String searchName = newValue.trim().toLowerCase();
                    String searchQuantity = inputQuantityTextField.getText().trim();
                    boolean matchId = searchId.isEmpty() || medicineSearchModel.getId().toString().contains(searchId);
                    boolean matchName = searchName.isEmpty() || medicineSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchQuantity = searchQuantity.isEmpty() || medicineSearchModel.getQuantityInMilligrams().toString().contains(searchQuantity);
                    return matchId && matchName && matchQuantity;
                });
            });

            // Filter by quantity
            inputQuantityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(medicineSearchModel -> {
                    String searchId = inputIdTextField.getText().trim();
                    String searchName = inputNameTextField.getText().trim().toLowerCase();
                    String searchQuantity = newValue.trim();
                    boolean matchId = searchId.isEmpty() || medicineSearchModel.getId().toString().contains(searchId);
                    boolean matchName = searchName.isEmpty() || medicineSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchQuantity = searchQuantity.isEmpty() || medicineSearchModel.getQuantityInMilligrams().toString().contains(searchQuantity);
                    return matchId && matchName && matchQuantity;
                });
            });

            SortedList<Medicine> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(medicineTableView.comparatorProperty());
            medicineTableView.setItems(sortedList);

            // Listener for row selection
            medicineTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    inputIdTextField.setText(newValue.getId().toString());
                    inputNameTextField.setText(newValue.getName());
                    inputQuantityTextField.setText(newValue.getQuantityInMilligrams().toString());
                }
            });

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void initializePrescriptionTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosageInstructions"));

        nameColumn.setCellFactory(new WrappedTextCellFactory<>());
        quantityColumn.setCellFactory(new WrappedTextCellFactory<>());
        unitsColumn.setCellFactory(new WrappedTextCellFactory<>());
        dosageColumn.setCellFactory(new WrappedTextCellFactory<>());

        // Listener for row selection
        prescriptionTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                inputNameTextField.setText(newValue.getName());
                inputQuantityTextField.setText(newValue.getQuantityInMilligrams());
                inputUnitsTextField.setText(newValue.getUnits());
                inputDosageTextArea.setText(newValue.getDosageInstructions());
            }
        });

    }

    private void loadPrescriptions() {
        String patientQuery = "SELECT prescription FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                String prescriptionText = resultSet.getString("prescription");

                if (prescriptionText != null && !prescriptionText.isEmpty()) {
                    // Convert prescriptionText to ObservableList<Prescription.PrescriptionEntry>
                    prescriptionObservableList = Prescription.convertToObservableList(prescriptionText);

                    // Display prescriptionList in TableView
                    prescriptionTableView.setItems(prescriptionObservableList);
                } else {
                    // If prescriptionText is null or empty, clear the TableView
                    prescriptionTableView.setItems(FXCollections.observableArrayList());
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(PrescriptionController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void addUpdateButtonOnAction(ActionEvent event) {
        // Get the input values
        String name = inputNameTextField.getText().trim();
        String quantity = inputQuantityTextField.getText().trim();
        String units = inputUnitsTextField.getText().trim();
        String dosageInstructions = inputDosageTextArea.getText().trim();

        // Validate input (ensure all fields are filled)
        if (name.isEmpty() || quantity.isEmpty() || units.isEmpty() || dosageInstructions.isEmpty()) {
            // Optionally show an error message or handle empty fields scenario
            System.out.println("Please fill in all fields.");
            return;
        }

        // Check if a combination of name and quantity already exists in prescriptionObservableList
        Prescription.PrescriptionEntry existingEntry = null;
        for (Prescription.PrescriptionEntry entry : prescriptionObservableList) {
            if (entry.getName().equals(name) && entry.getQuantityInMilligrams().equals(quantity)) {
                existingEntry = entry;
                break;
            }
        }

        // If exists, update the existing entry
        if (existingEntry != null) {
            existingEntry.setUnits(units);
            existingEntry.setDosageInstructions(dosageInstructions);
            prescriptionTableView.refresh(); // Refresh table view to reflect changes
        } else {
            // If not exists, add a new entry
            Prescription.PrescriptionEntry newEntry = new Prescription.PrescriptionEntry(name, quantity, units, dosageInstructions);
            prescriptionObservableList.add(newEntry);
        }

        // clear input fields after adding/updating
        clearInputFields();
    }

    // Helper method to clear input fields
    private void clearInputFields() {
        inputNameTextField.clear();
        inputQuantityTextField.clear();
        inputUnitsTextField.clear();
        inputDosageTextArea.clear();
    }


    @FXML
    private void deleteButtonOnAction(ActionEvent event) {
        // Get the selected PrescriptionEntry
        Prescription.PrescriptionEntry selectedEntry = prescriptionTableView.getSelectionModel().getSelectedItem();

        if (selectedEntry != null) {
            // Create a predicate to match by name and quantity
            Predicate<Prescription.PrescriptionEntry> matchPredicate = entry ->
                    entry.getName().equals(selectedEntry.getName()) &&
                            entry.getQuantityInMilligrams().equals(selectedEntry.getQuantityInMilligrams());

            // Remove the matched entry from prescriptionObservableList
            prescriptionObservableList.removeIf(matchPredicate);

            // Clear selection in the table view
            prescriptionTableView.getSelectionModel().clearSelection();
        }
    }


    @FXML
    private void exitButtonOnAction(ActionEvent event) {
        // 1. Convert prescriptionObservableList to a string
        String prescriptionString = Prescription.convertToString(prescriptionObservableList);

        // Update doctorConsultTable's prescription column for the current queueNumber
        updatePrescriptionInDatabase(queueNumber, prescriptionString);

        doctorConsultController.displayPrescription(queueNumber);

        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    private void updatePrescriptionInDatabase(int queueNumber, String prescriptionString) {
        String updateQuery = "UPDATE doctorConsultTable SET prescription = ? WHERE queueNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, prescriptionString);
            pstmt.setInt(2, queueNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}