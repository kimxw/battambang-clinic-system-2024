package com.orb.battambang.util;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.reception.Patient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class PatientTableViewUpdater {

    private final ObservableList<Patient> patientObservableList;
    private final TableView<Patient> patientTableView;
    private final Connection connection = DatabaseConnection.connection;

    public PatientTableViewUpdater(ObservableList<Patient> patientObservableList, TableView<Patient> patientTableView) {
        this.patientObservableList = patientObservableList;
        this.patientTableView = patientTableView;
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
        String query = "SELECT queueNumber, name, DOB, age, sex, phoneNumber, address FROM patientQueueTable";
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

            patientTableView.setItems(patientObservableList); // Update the TableView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
