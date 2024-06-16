package com.orb.battambang.pharmacy;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;
import com.orb.battambang.connection.DatabaseConnection;
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
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MedicineDispenseController extends DatabaseConnection implements Initializable {

    @FXML
    private Label messageLabel1;
    @FXML
    private Label messageLabel2;
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

    ObservableList<Medicine> medicineObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set cell value factories
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInMilligrams"));
        stockTableColumn.setCellValueFactory(new PropertyValueFactory<>("stockLeft"));

        String medicineViewQuery = "SELECT id, name, quantityInMilligrams, stockLeft FROM medicineTable;";

        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(medicineViewQuery)) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("Id");
                String name = resultSet.getString("Name");
                Integer quantity = resultSet.getInt("QuantityInMilligrams");
                Integer stockLeft = resultSet.getInt("StockLeft");

                medicineObservableList.add(new Medicine(id, name, quantity, stockLeft));
            }
            // Set items to the TableView
            medicineTableView.setItems(medicineObservableList);

        } catch (Exception exc) {
            exc.printStackTrace();
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
    public void searchButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("medicine-search.fxml"));
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

                String updateQuery = "UPDATE medicineTable SET stockLeft = stockLeft - ? WHERE id = ? OR (name = ? AND quantityInMilligrams = ?)";
                try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                    statement.setInt(1, units);
                    statement.setString(2, id);
                    statement.setString(3, name);
                    statement.setInt(4, quantity);
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0) {
                        Labels.showMessageLabel(messageLabel2, "Medicine dispensed successfully.", true);
                        // Update the TableView
                        medicineObservableList.clear();
                        initialize(null, null); // Refresh the TableView
                    } else {
                        Labels.showMessageLabel(messageLabel2, "Medicine not found.", false);
                    }
                }
            } catch (NumberFormatException ex) {
                Labels.showMessageLabel(messageLabel2, "Invalid quantity or units.", false);
            } catch (Exception ex) {
                ex.printStackTrace();
                Labels.showMessageLabel(messageLabel2, "Unexpected error.", false);
            }
        } else {
            Labels.showMessageLabel(messageLabel2, "Please enter either ID or name and quantity.", false);
        }

        // Clear the text fields
        inputIdTextField.clear();
        inputNameTextField.clear();
        inputQuantityTextField.clear();
        inputUnitTextField.clear();
    }
}
