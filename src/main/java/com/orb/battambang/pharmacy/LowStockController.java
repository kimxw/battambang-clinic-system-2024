package com.orb.battambang.pharmacy;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class LowStockController implements Initializable {

    @FXML
    private TextField idSearchTextField;
    @FXML
    private TextField nameSearchTextField;
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
    private FilteredList<Medicine> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableColumns();
        initializeMedicineList();
        setupFilters();
        startPolling();
    }

    private void initializeTableColumns() {
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
        stockTableColumn.setCellValueFactory(new PropertyValueFactory<>("stockLeft"));
    }

    public void initializeMedicineList() {
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

            filteredList = new FilteredList<>(medicineObservableList, medicine -> medicine.getStockLeft() < 20);
            SortedList<Medicine> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(medicineTableView.comparatorProperty());
            medicineTableView.setItems(sortedList);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void setupFilters() {
        idSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(medicine -> filterMedicine(medicine, newValue.trim(), nameSearchTextField.getText().trim().toLowerCase()));
        });

        nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(medicine -> filterMedicine(medicine, idSearchTextField.getText().trim(), newValue.trim().toLowerCase()));
        });
    }

    private boolean filterMedicine(Medicine medicine, String searchId, String searchName) {
        boolean matchId = searchId.isEmpty() || medicine.getId().toString().contains(searchId);
        boolean matchName = searchName.isEmpty() || medicine.getName().toLowerCase().contains(searchName);
        boolean matchStock = medicine.getStockLeft() < 20;
        return matchId && matchName && matchStock;
    }

    private void startPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(LowStockController.this::updateTableView);
            }
        }, 0, 30000); // Poll every 30 seconds
    }

    public void updateTableView() {
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
}
