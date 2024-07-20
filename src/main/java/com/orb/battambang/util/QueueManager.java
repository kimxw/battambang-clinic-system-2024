package com.orb.battambang.util;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.sql.*;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class QueueManager {

    private final String currentTable;
    private final ListView<String> currentListView;
    private final ObservableList<String> queueList;
    private final String targetTable;
    private final ListView<String> targetListView;
    private static final Connection connection = DatabaseConnection.connection;

    public QueueManager(ListView<String> currentListView, String currentTable, ListView<String> targetListView, String targetTable) {
        this.currentListView = currentListView;
        this.queueList = FXCollections.observableArrayList();
        this.currentListView.setItems(queueList);

        this.currentTable = currentTable;

        this.targetListView = targetListView;

        this.targetTable = targetTable;

        startPolling();
    }

    public String getCurrentTable() {
        return this.currentTable;
    }

    public ListView<String> getCurrentListView() {
        return currentListView;
    }

    private void startPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateWaitingList());
            }
        }, 0, 5000); // Poll every 5 seconds
    }

    public void updateWaitingList() {
        String query = "SELECT queueNumber, name FROM " + currentTable;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            queueList.clear();

            while (resultSet.next()) {
                queueList.add(String.format("%d: %s", resultSet.getInt("queueNumber"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addNew(int queueNumber, ListView<String> targetListView, String targetTable) throws SQLException {
        String nameFromWaitingListQuery = "SELECT name FROM patientQueueTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO " + targetTable + " (queueNumber, name) VALUES (?, ?)";

        try {
            String name = "";
            // Start a transaction
            connection.setAutoCommit(false);
            try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery)) {
                nameStatement.setInt(1, queueNumber);
                ResultSet rs = nameStatement.executeQuery();
                if (rs.next()) {
                    name = rs.getString("name");
                } else {
                    throw new RuntimeException("Patient not found in records");
                }
            } catch (Exception exc) {
                throw exc;
            }


            try(PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

                insertStatement.setInt(1, queueNumber);
                insertStatement.setString(2, name);
                insertStatement.executeUpdate();

            } catch (Exception e) {
                throw new RuntimeException("Patient already exists in target queue");
            }

            // Commit the transaction
            connection.commit();

            targetListView.getItems().add(String.format("%d: %s", queueNumber, name));

        } catch (Exception exc) {
            try {
                connection.rollback(); // Roll back transaction if any error occurs
                throw exc;
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }
    public static void move(ListView<String> currentListView, String currentTable, ListView<String> targetListView, String targetTable) {
        String selectedPatient = currentListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            if (!currentListView.getItems().isEmpty()) {
                selectedPatient = currentListView.getItems().get(0);
            }
        }

        if (selectedPatient == null) {
            return;
        }
        int queueNumber = Integer.parseInt(selectedPatient.substring(0, selectedPatient.indexOf(':')));

        String nameFromWaitingListQuery = "SELECT name FROM " + currentTable + " WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM " + currentTable + " WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO " + targetTable + " (queueNumber, name) VALUES (?, ?)";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
            insertStatement.setString(2, name);
            insertStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            currentListView.getItems().remove(String.format("%d: %s", queueNumber, name));
            targetListView.getItems().add(String.format("%d: %s", queueNumber, name));
        } catch (SQLException e) {
            try {
                connection.rollback(); // Roll back transaction if any error occurs
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    public static void move(int queueNumber, ListView<String> currentListView, String currentTable, ListView<String> targetListView, String targetTable){

        String nameFromWaitingListQuery = "SELECT name FROM " + currentTable + " WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM " + currentTable + " WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO " + targetTable + " (queueNumber, name) VALUES (?, ?)";

        try {
            String name = "";
            // Start a transaction
            connection.setAutoCommit(false);
            try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery)) {
                nameStatement.setInt(1, queueNumber);
                ResultSet rs = nameStatement.executeQuery();
                if (rs.next()) {
                    name = rs.getString("name");
                } else {
                    throw new RuntimeException("Patient not found in queue");
                }
            } catch (Exception exc) {
                throw exc;
            }

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery)) {
                deleteStatement.setInt(1, queueNumber);
                deleteStatement.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Patient not found in queue");
            }

            try(PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

                insertStatement.setInt(1, queueNumber);
                insertStatement.setString(2, name);
                insertStatement.executeUpdate();

            } catch (Exception e) {
                throw new RuntimeException("Patient already exists in target queue");
            }

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            currentListView.getItems().remove(String.format("%d: %s", queueNumber, name));
            targetListView.getItems().add(String.format("%d: %s", queueNumber, name));
        } catch (Exception exc) {
            try {
                connection.rollback(); // Roll back transaction if any error occurs
                throw exc;
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }
    public static void remove( ListView<String> currentListView, String currentTable) {
        String selectedPatient = currentListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            if (!currentListView.getItems().isEmpty()) {
                selectedPatient = currentListView.getItems().get(0);
            }
        }

        if (selectedPatient == null) {
            return;
        }
        int queueNumber = Integer.parseInt(selectedPatient.substring(0, selectedPatient.indexOf(':')));

        String nameFromWaitingListQuery = "SELECT name FROM " + currentTable + " WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM " + currentTable + " WHERE queueNumber = ?";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            currentListView.getItems().remove(String.format("%d: %s", queueNumber, name));
        } catch (SQLException e) {
            //add my exception handling here
            try {
                connection.rollback(); // Roll back transaction if any error occurs
                throw new RuntimeException("Patient not found in queue");
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    public static void remove(int queueNumber, ListView<String> currentListView, String currentTable) {

        String nameFromWaitingListQuery = "SELECT name FROM " + currentTable + " WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM " + currentTable + " WHERE queueNumber = ?";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            } else {
                throw new RuntimeException("Patient not found in queue");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            currentListView.getItems().remove(String.format("%d: %s", queueNumber, name));
        } catch (SQLException e) {
            try {
                connection.rollback(); // Roll back transaction if any error occurs
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }
    public void moveToNext() throws RuntimeException {
        if(this.targetListView == null && this.targetTable == null) {
            QueueManager.remove( this.currentListView, this.currentTable);
        } else if (this.targetListView == null || this.targetTable == null) {
            //internal error
        } else {
            QueueManager.move( this.currentListView, this.currentTable, this.targetListView, this.targetTable);
        }
    }

    public void swapPosition(boolean isUp) {

    }


}
