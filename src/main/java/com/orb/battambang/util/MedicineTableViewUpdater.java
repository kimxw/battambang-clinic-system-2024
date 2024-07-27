package com.orb.battambang.util;

import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.pharmacy.Medicine;
import com.orb.battambang.reception.Patient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class MedicineTableViewUpdater {

    private final ObservableList<Medicine> medicineObservableList;
    private final TableView<Medicine> medicineTableView;
    private final Connection connection = DatabaseConnection.connection;

    public MedicineTableViewUpdater(ObservableList<Medicine> medicineObservableList, TableView<Medicine> medicineTableView) {
        this.medicineObservableList = medicineObservableList;
        this.medicineTableView = medicineTableView;
        startPolling();
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

            medicineTableView.setItems(medicineObservableList); // Update the TableView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
