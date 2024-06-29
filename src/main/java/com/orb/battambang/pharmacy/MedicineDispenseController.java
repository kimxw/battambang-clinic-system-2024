package com.orb.battambang.pharmacy;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.QueueManager;
import com.orb.battambang.util.Rectangles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MedicineDispenseController extends DatabaseConnection implements Initializable {

    @FXML
    private Label messageLabel1;
    @FXML
    private Label messageLabel2;
    @FXML
    private Label warningLabel;
    @FXML
    private Button switchUserButton;
    @FXML
    private TextField inputIdTextField;
    @FXML
    private TextField inputNameTextField;
    @FXML
    private TextField inputQuantityTextField;
    @FXML
    private TextField inputUnitTextField;
    @FXML
    private TableView<Medicine> medicineTableView;
    @FXML
    private TableColumn<Medicine, Integer> idTableColumn;
    @FXML
    private TableColumn<Medicine, String> nameTableColumn;
    @FXML
    private TableColumn<Medicine, Integer> quantityTableColumn;
    @FXML
    private TableColumn<Medicine, Integer> stockTableColumn;
    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;
    @FXML
    private Pane particularsPane;
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
    private Label queueSelectLabel;
    @FXML
    private Label status1Label;
    @FXML
    private Rectangle status1Rectangle;
    @FXML
    private TextArea prescriptionTextArea;
    @FXML
    private TextArea allergiesTextArea;

    ObservableList<Medicine> medicineObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        QueueManager waitingQueueManager = new QueueManager(waitingListView, "pharmacyWaitingTable");
        QueueManager progressQueueManager = new QueueManager(inProgressListView, "pharmacyProgressTable");

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

        initialiseMedicineTable();

    }

    private void initialiseMedicineTable() {
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
            nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
            stockTableColumn.setCellValueFactory(new PropertyValueFactory<>("stockLeft"));

            medicineTableView.setItems(medicineObservableList);

            FilteredList<Medicine> filteredList = new FilteredList<>(medicineObservableList);

            //filter by id
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

            //Filter by quantity
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

    private void clearParticularsFields() {
        queueNoLabel.setText("");
        nameLabel.setText("");
        ageLabel.setText("");
        sexLabel.setText("");
        phoneNumberLabel.setText("");
        prescriptionTextArea.setText("");
        allergiesTextArea.setText("");
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(queueNumber);
            particularsPane.setVisible(true);
            displayPrescription(queueNumber);
        }
    }

    private void updateParticularsPane(int queueNumber) {
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

                String pharmacyStatus = patientResultSet.getString("pharmacyStatus");

                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, pharmacyStatus);

            } else {
                nameLabel.setText("");
                ageLabel.setText("");
                sexLabel.setText("");
                phoneNumberLabel.setText("");
                Labels.showMessageLabel(queueSelectLabel, "Patient does not exist", false);
                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, "Not found");
            }

            // Close the statement and resultSet
            patientResultSet.close();
            statement.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
    }

    private void displayPrescription(int queueNumber) {
        String prescriptionQuery = "SELECT prescription FROM doctorConsultTable WHERE queueNumber = " + queueNumber;
        String allergiesQuery = "SELECT drugAllergies FROM historyTable WHERE queueNumber = " + queueNumber;

        try {
            Statement statement = DatabaseConnection.connection.createStatement();

            ResultSet prescriptionResultSet = statement.executeQuery(prescriptionQuery);
            if (prescriptionResultSet.next()) {
                String prescription = prescriptionResultSet.getString("prescription");

                prescriptionTextArea.setText(formatPrescription(prescription));

            }
            prescriptionResultSet.close();

            ResultSet allergiesResultSet = statement.executeQuery(allergiesQuery);
            if (allergiesResultSet.next()) {
                String allergies = prescriptionResultSet.getString("drugAllergies");

                allergiesTextArea.setText(allergies);
            }
            allergiesResultSet.close();

            statement.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
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
            exc.printStackTrace();
            exc.getCause();
        }
    }

    @FXML
    public void lowStockButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("low-stock.fxml"));
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
    public void dispenseButtonOnAction(ActionEvent e) {
        String id = inputIdTextField.getText();
        String name = inputNameTextField.getText();
        String quantityStr = inputQuantityTextField.getText();
        String unitsStr = inputUnitTextField.getText();

        if (!id.isEmpty() || (!name.isEmpty() && !quantityStr.isEmpty())) {
            try {
                int units = Integer.parseInt(unitsStr);
                int quantity = name.isEmpty() ? 0 : Integer.parseInt(quantityStr);

                // Query to check the current stock
                String checkStockQuery = "SELECT stockLeft, name, quantityInMilligrams FROM medicineTable WHERE id = ? OR (name = ? AND quantityInMilligrams = ?)";
                try (PreparedStatement checkStatement = connection.prepareStatement(checkStockQuery)) {
                    checkStatement.setString(1, id);
                    checkStatement.setString(2, name);
                    checkStatement.setInt(3, quantity);
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (resultSet.next()) {
                        int currentStock = resultSet.getInt("stockLeft");
                        String medicineName = resultSet.getString("name");
                        int medicineQuantity = resultSet.getInt("quantityInMilligrams");

                        if (currentStock < units) {
                            Labels.showMessageLabel(messageLabel2, "Not enough units to be dispensed.", false);
                            warningLabel.setText(""); // Clear the warning label if there's an error
                            return;
                        }

                        // Update query to dispense the medicine
                        String updateQuery = "UPDATE medicineTable SET stockLeft = stockLeft - ? WHERE id = ? OR (name = ? AND quantityInMilligrams = ?)";
                        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                            statement.setInt(1, units);
                            statement.setString(2, id);
                            statement.setString(3, medicineName); // Use medicineName from the result set
                            statement.setInt(4, medicineQuantity); // Use medicineQuantity from the result set
                            int rowsAffected = statement.executeUpdate();

                            if (rowsAffected > 0) {
                                // Check the stock again after dispensing
                                resultSet = checkStatement.executeQuery();
                                if (resultSet.next()) {
                                    int newStock = resultSet.getInt("stockLeft");
                                    Labels.showMessageLabel(messageLabel2, "Medicine dispensed successfully.", true);

                                    if (newStock < 20) {
                                        // Inside the if (newStock < 20) block
                                        Labels.showMessageLabel(warningLabel, "Only " + newStock + " units of " + medicineName + " (" + medicineQuantity + "mg) left.", true);
                                        warningLabel.setStyle("-fx-text-fill: orange;");
                                    } else {
                                        warningLabel.setText(""); // Clear the warning label if stock is sufficient
                                        warningLabel.setStyle(""); // Reset the style
                                    }
                                }

                                // Update the TableView
                                medicineObservableList.clear();
                                initialiseMedicineTable();

                            } else {
                                Labels.showMessageLabel(messageLabel2, "Medicine not found.", false);
                                warningLabel.setText(""); // Clear the warning label if there's an error
                                warningLabel.setStyle(""); // Reset the style
                            }
                        }
                    } else {
                        Labels.showMessageLabel(messageLabel2, "Medicine not found.", false);
                        warningLabel.setText(""); // Clear the warning label if there's an error
                        warningLabel.setStyle(""); // Reset the style
                    }
                }
            } catch (NumberFormatException ex) {
                Labels.showMessageLabel(messageLabel2, "Invalid quantity or units.", false);
                warningLabel.setText(""); // Clear the warning label if there's an error
                warningLabel.setStyle(""); // Reset the style
            } catch (Exception ex) {
                ex.printStackTrace();
                Labels.showMessageLabel(messageLabel2, "Unexpected error.", false);
                warningLabel.setText(""); // Clear the warning label if there's an error
                warningLabel.setStyle(""); // Reset the style
            }
        } else {
            Labels.showMessageLabel(messageLabel2, "Please enter either ID or name and quantity.", false);
            warningLabel.setText(""); // Clear the warning label if there's an error
            warningLabel.setStyle(""); // Reset the style
        }

        // Clear the text fields
        inputIdTextField.clear();
        inputNameTextField.clear();
        inputQuantityTextField.clear();
        inputUnitTextField.clear();
    }

    @FXML public void clearButtonOnAction(ActionEvent e) {
        inputIdTextField.clear();
        inputNameTextField.clear();
        inputQuantityTextField.clear();
        inputUnitTextField.clear();
    }

    @FXML
    public void addButtonOnAction(ActionEvent e) {
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

        String deleteFromWaitingListQuery = "DELETE FROM pharmacyWaitingTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO pharmacyProgressTable (queueNumber) VALUES (?)";

        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
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
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void sendButtonOnAction(ActionEvent e) {
        Integer selectedPatient = inProgressListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            if (!inProgressListView.getItems().isEmpty()) {
                selectedPatient = inProgressListView.getItems().get(0);
            }
        }

        if (selectedPatient != null) {
            removeFromQueue(selectedPatient);
        }

        int queueNumber = selectedPatient.intValue();
        //update dispense status to complete
        String updateStatusQuery = "UPDATE patientQueueTable SET pharmacyStatus = 'Dispensed' WHERE queueNumber = ?";
        try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
            updateStatusStatement.setInt(1, queueNumber);
            updateStatusStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        Labels.showMessageLabel(warningLabel, "Q" + queueNumber + " status updated successfully", true);
    }

    private void removeFromQueue(Integer queueNumber) {

        String deleteFromProgressListQuery = "DELETE FROM pharmacyProgressTable WHERE queueNumber = ?";

        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteFromProgressListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

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
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    //move to utils later

    private String formatPrescription(String prescriptionString) {

        String[] medicationEntries = prescriptionString.split(";");
        StringBuilder formattedString = new StringBuilder();

        for (String entry : medicationEntries) {
            String[] parts = entry.split(",");

            // Ensure each entry has exactly four parts before processing
            if (parts.length == 4) {
                String name = parts[0].trim();
                String quantityInMilligrams = parts[1].trim();
                String units = parts[2].trim();
                String dosageInstructions = parts[3].trim();

                if (formattedString.length() > 0) {
                    formattedString.append("\n");
                }

                formattedString.append(String.format("%s %s - %s units%n%s%n", name, quantityInMilligrams, units, dosageInstructions));
            }
        }

        return formattedString.toString();
    }


}
