package com.orb.battambang.pharmacy;

import com.orb.battambang.util.Labels;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class UpdateInventoryController implements Initializable {

    @FXML
    private TextField idSearchTextField;
    @FXML
    private TextField nameSearchTextField;

    @FXML
    private TextField nameAddTextField;
    @FXML
    private TextField quantityAddTextField;
    @FXML
    private TextField stockAddTextField;

    @FXML
    private TextField idUpdateTextField;
    @FXML
    private TextField nameUpdateTextField;
    @FXML
    private TextField quantityUpdateTextField;
    @FXML
    private TextField stockUpdateTextField;


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

    private final ObservableList<Medicine> medicineObservableList = FXCollections.observableArrayList();
    private FilteredList<Medicine> filteredList = new FilteredList<>(medicineObservableList);

    @FXML
    private Label warningLabel1;
    @FXML
    private Label warningLabel2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableColumns();
        initializeMedicineList();
        setupListeners();
        startPolling();
        clearAddFields();
        clearUpdateFields();
    }

    private void initializeTableColumns() {
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
        stockTableColumn.setCellValueFactory(new PropertyValueFactory<>("stockLeft"));
    }

    private void initializeMedicineList() {
        String medicineViewQuery = "SELECT id, name, quantityInMilligrams, stockLeft FROM medicineTable;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(medicineViewQuery)) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Integer quantity = resultSet.getInt("quantityInMilligrams");
                Integer stock = resultSet.getInt("stockLeft");

                medicineObservableList.add(new Medicine(id, name, quantity, stock));
            }

            SortedList<Medicine> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(medicineTableView.comparatorProperty());
            medicineTableView.setItems(sortedList);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void setupListeners() {
        idSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(medicine -> filterMedicine(medicine, newValue.trim(), nameSearchTextField.getText().trim().toLowerCase()));
        });

        nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(medicine -> filterMedicine(medicine, idSearchTextField.getText().trim(), newValue.trim().toLowerCase()));
        });

        // Listener for row selection
        medicineTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                idUpdateTextField.setText(newValue.getId().toString());
                nameUpdateTextField.setText(newValue.getName());
                quantityUpdateTextField.setText(newValue.getQuantityInMilligrams().toString());
                stockUpdateTextField.setText(newValue.getStockLeft().toString());
            }
        });
    }

    private boolean filterMedicine(Medicine medicine, String searchId, String searchName) {
        boolean matchId = searchId.isEmpty() || medicine.getId().toString().contains(searchId);
        boolean matchName = searchName.isEmpty() || medicine.getName().toLowerCase().contains(searchName);
        return matchId && matchName;
    }

    private void startPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(UpdateInventoryController.this::updateTableView);
            }
        }, 0, 30000); // Poll every 30 seconds
    }

    private void updateTableView() {
        String query = "SELECT id, name, quantityInMilligrams, stockLeft FROM medicineTable";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            medicineObservableList.clear(); // Clear the list before adding new items

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Integer quantity = resultSet.getInt("quantityInMilligrams");
                Integer stockLeft = resultSet.getInt("stockLeft");

                medicineObservableList.add(new Medicine(id, name, quantity, stockLeft));
            }

            // Reapply the filter after updating the list
            filteredList.setPredicate(medicine -> filterMedicine(medicine, idSearchTextField.getText().trim(), nameSearchTextField.getText().trim().toLowerCase()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAddFields() {
        nameAddTextField.setText("");
        quantityAddTextField.setText("");
        stockAddTextField.setText("");

    }

    private void clearUpdateFields() {
        idUpdateTextField.setText("");
        idUpdateTextField.setEditable(false);
        nameUpdateTextField.setText("");
        quantityUpdateTextField.setText("");
        stockUpdateTextField.setText("");
    }

    // Getters for testing purposes
    public TextField getIdSearchTextField() {
        return idSearchTextField;
    }

    public TextField getNameSearchTextField() {
        return nameSearchTextField;
    }

    public TableView<Medicine> getMedicineTableView() {
        return medicineTableView;
    }

    public TableColumn<Medicine, Integer> getIdTableColumn() {
        return idTableColumn;
    }

    public TableColumn<Medicine, String> getNameTableColumn() {
        return nameTableColumn;
    }

    public TableColumn<Medicine, Integer> getQuantityTableColumn() {
        return quantityTableColumn;
    }

    public TableColumn<Medicine, Integer> getStockTableColumn() {
        return stockTableColumn;
    }

    @FXML
    private void addNewButtonOnAction(ActionEvent E) {
        try {
            String name = nameAddTextField.getText();
            int quantity = Integer.parseInt(quantityAddTextField.getText());
            int stock = Integer.parseInt(stockAddTextField.getText());

            String insertQuery = "INSERT INTO medicineTable (name, quantityInMilligrams, stockLeft) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, name);
                statement.setInt(2, quantity);
                statement.setInt(3, stock);

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 1) {
                    Labels.showMessageLabel(warningLabel1, name + " (" + quantity + "mg) added successfully", true);
                    updateTableView();
                    clearAddFields();
                } else {
                    Labels.showMessageLabel(warningLabel1, "Error adding entry.", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Labels.showMessageLabel(warningLabel1, "Unexpected Error", false);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Labels.showMessageLabel(warningLabel1, "Ensure all fields are filled correctly", false);
        }
    }

    @FXML
    private void updateButtonOnAction(ActionEvent E) {
        try {
            int id = Integer.parseInt(idUpdateTextField.getText());
            String name = nameUpdateTextField.getText();
            int quantity = Integer.parseInt(quantityUpdateTextField.getText());
            int stock = Integer.parseInt(stockUpdateTextField.getText());

            String updateQuery = "UPDATE medicineTable SET name = ?, quantityInMilligrams = ?, stockLeft = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, name);
                statement.setInt(2, quantity);
                statement.setInt(3, stock);
                statement.setInt(4, id);

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 1) {
                    Labels.showMessageLabel(warningLabel2, "Entry with ID " + id + " updated successfully.", true);
                    updateTableView();
                    clearUpdateFields();
                } else {
                    Labels.showMessageLabel(warningLabel2, "ID may not exit. Add a new entry.", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Labels.showMessageLabel(warningLabel2, "Unexpected Error", false);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Labels.showMessageLabel(warningLabel2, "Ensure all fields are filled correctly.", false);
        }
    }


    @FXML
    private void deleteButtonOnAction(ActionEvent E) {
        Medicine selectedEntry = medicineTableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            int id = selectedEntry.getId();
            try {
                String deleteQuery = "DELETE FROM medicineTable WHERE id = " + id;
                Statement statement = connection.createStatement();
                statement.executeUpdate(deleteQuery);

                Labels.showMessageLabel(warningLabel2, "Entry with ID " + id + " deleted successfully.", true);
                medicineObservableList.remove(selectedEntry);

                clearUpdateFields();
                statement.close();

            } catch (Exception e) {
                System.out.println(e);
                Labels.showMessageLabel(warningLabel2, "Unexpected error", false);
            }

        } else {
            Labels.showMessageLabel(warningLabel2, "Please select a row to delete.", false);
        }
    }
}
