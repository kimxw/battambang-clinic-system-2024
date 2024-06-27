package com.orb.battambang.pharmacy;

import com.orb.battambang.connection.DatabaseConnection;
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
import java.sql.Statement;
import java.util.ResourceBundle;

public class LowStockController extends DatabaseConnection implements Initializable {

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

    ObservableList<Medicine> medicineObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

            // Create a filtered list and set the initial predicate to filter medicines with stock less than 20
            FilteredList<Medicine> filteredList = new FilteredList<>(medicineObservableList, medicine -> medicine.getStockLeft() < 20);

            //filter by id
            idSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(medicineSearchModel -> {
                    String searchId = newValue.trim();
                    String searchName = nameSearchTextField.getText().trim().toLowerCase();
                    boolean matchId = searchId.isEmpty() || medicineSearchModel.getId().toString().contains(searchId);
                    boolean matchName = searchName.isEmpty() || medicineSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchStock = medicineSearchModel.getStockLeft() < 20; // Add this line
                    return matchId && matchName && matchStock;
                });
            });

            // Filter by name
            nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(medicineSearchModel -> {
                    String searchId = idSearchTextField.getText().trim();
                    String searchName = newValue.trim().toLowerCase();
                    boolean matchQueue = searchId.isEmpty() || medicineSearchModel.getId().toString().contains(searchId);
                    boolean matchName = searchName.isEmpty() || medicineSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchStock = medicineSearchModel.getStockLeft() < 20; // Add this line
                    return matchQueue && matchName && matchStock;
                });
            });

            SortedList<Medicine> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(medicineTableView.comparatorProperty());
            medicineTableView.setItems(sortedList);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
