package com.orb.battambang.util;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.util.Callback;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class QueueManager {

    private final String currentTable;
    private final ListView<String> currentListView;
    private final ListView<String> currentTagListView;
    private final ObservableList<String> queueList;
    private final ObservableList<String> tagList;

    private QueueManager nextQM;
    //private final String targetTable;
    //private final ListView<String> targetListView;
    private static final Connection connection = DatabaseConnection.connection;

    public QueueManager(ListView<String> currentListView, String currentTable, ListView<String> currentTagListView, QueueManager nextQM) {
        this.currentListView = currentListView;
        this.queueList = FXCollections.observableArrayList();
        this.currentListView.setItems(queueList);

        this.currentTable = currentTable;

        this.currentTagListView = currentTagListView;
        this.tagList = FXCollections.observableArrayList();
        this.currentTagListView.setItems(tagList);

        currentTagListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("tuberculosis", "optometry", "hearing", "social-worker", "physiotherapist");
                } else {
                    setText(item.toString());
                    // Apply style based on the String
                    switch (item) {
                        case "T":
                            getStyleClass().add("tuberculosis");
                            break;
                        case "O":
                            getStyleClass().add("optometry");
                            break;
                        case "H":
                            getStyleClass().add("hearing");
                            break;
                        case "S":
                            getStyleClass().add("social-worker");
                            break;
                        case "P":
                            getStyleClass().add("physiotherapist");
                            break;
                        case "?":
                            getStyleClass().add("unknown");
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        this.nextQM = nextQM;

        startPolling();
    }

    public String getCurrentTable() {
        return this.currentTable;
    }

    public ListView<String> getCurrentListView() {
        return currentListView;
    }
    public ListView<String> getCurrentTagListView() {
        return this.currentTagListView;
    }
    public QueueManager getNextQueueManager() {
        return this.nextQM;
    }

    private void startPolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateListView());
            }
        }, 0, 5000); // Poll every 5 seconds
    }

    public void updateListView() {
        //update queueNumber + name
        SelectionModel<String> selectionModel = this.getCurrentListView().getSelectionModel();
        String selected = selectionModel.getSelectedItem();
        String query = "SELECT queueNumber, name FROM " + currentTable + " ORDER BY createdAt;";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            queueList.clear();

            while (resultSet.next()) {
                queueList.add(String.format("%d: %s", resultSet.getInt("queueNumber"), resultSet.getString("name")));
            }

            if(selected != null) {
                selectionModel.select(selected);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //update tags
        tagList.clear();
        for (String queueItem : queueList) {
            int queueNumber = Integer.parseInt(queueItem.substring(0, queueItem.indexOf(':')));
            String tagQuery = "SELECT tagSequence FROM patientTagTable WHERE queueNumber = " + queueNumber;
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(tagQuery)) {
                 String tag;
                 if (resultSet.next()) {
                     tag = resultSet.getString("tagSequence");
                 } else {
                     tag = "";
                 }
                 tagList.add(tag);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public boolean search(int queueNumber) {
        String query = String.format("SELECT 1 FROM %s WHERE queueNumber = %d", this.currentTable, queueNumber);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addNew(int queueNumber, QueueManager queueManager) {
        ListView<String> targetListView = queueManager.getCurrentListView();
        String targetTable = queueManager.getCurrentTable();
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

            queueManager.updateListView();

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

    public static void move(QueueManager sourceQueueManager, QueueManager targetQueueManager) {
        ListView<String> currentListView = sourceQueueManager.getCurrentListView();
        String currentTable = sourceQueueManager.getCurrentTable();
        ListView<String> targetListView = targetQueueManager.getCurrentListView();
        String targetTable = targetQueueManager.getCurrentTable();

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

            currentListView.getSelectionModel().clearSelection();
            sourceQueueManager.updateListView();
            targetQueueManager.updateListView();
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

    public static void move(int queueNumber, QueueManager sourceQueueManager, QueueManager targetQueueManager){

        ListView<String> currentListView = sourceQueueManager.getCurrentListView();
        String currentTable = sourceQueueManager.getCurrentTable();
        ListView<String> targetListView = targetQueueManager.getCurrentListView();
        String targetTable = targetQueueManager.getCurrentTable();

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
            sourceQueueManager.updateListView();
            targetQueueManager.updateListView();
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
    public static void remove(QueueManager queueManager) {
        ListView<String> currentListView = queueManager.getCurrentListView();
        String currentTable = queueManager.getCurrentTable();

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
            currentListView.getSelectionModel().clearSelection();
            queueManager.updateListView();
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

    public static void remove(int queueNumber, QueueManager queueManager) {
        ListView<String> currentListView = queueManager.getCurrentListView();
        String currentTable = queueManager.getCurrentTable();

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
            queueManager.updateListView();
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
        ListView<String> targetListView = this.nextQM.getCurrentListView();
        String targetTable = this.nextQM.getCurrentTable();
        if(targetListView == null && targetTable == null) {
            QueueManager.remove(this);
        } else if (targetListView == null || targetTable == null) {
            //internal error
        } else {
            QueueManager.move(this, this.nextQM);
        }
    }

    public void swapPosition(boolean isUp) {
        SelectionModel<String> selectionModel = this.currentListView.getSelectionModel();
        String currentEntry = selectionModel.getSelectedItem();
        if (currentEntry == null) {
            throw new RuntimeException("No patient selected");
        }
        int queueNumberA = Integer.parseInt(currentEntry.substring(0, currentEntry.indexOf(':')));
        if (isUp) {
            selectionModel.selectPrevious();
            String prevEntry = selectionModel.getSelectedItem();
            selectionModel.clearSelection();

            if (currentEntry.equals(prevEntry)) {
                return;
            }

            int queueNumberB = Integer.parseInt(prevEntry.substring(0, prevEntry.indexOf(':')));
            swapHelper(queueNumberA, queueNumberB);
        } else {
            selectionModel.selectNext();
            String nextEntry = selectionModel.getSelectedItem();
            selectionModel.clearSelection();

            if (currentEntry.equals(nextEntry)) {
                return;
            }
            int queueNumberB = Integer.parseInt(nextEntry.substring(0, nextEntry.indexOf(':')));
            swapHelper(queueNumberA, queueNumberB);
        }
        this.currentListView.refresh();
        selectionModel.select(currentEntry);
    }

    private void swapHelper(int queueNumberA, int queueNumberB) {

        String nameAQuery = "SELECT name FROM " + currentTable + " WHERE queueNumber = ?";
        String nameBQuery = "SELECT name FROM " + currentTable + " WHERE queueNumber = ?";
        String updateQuery = "UPDATE " + currentTable + " SET queueNumber = ?, name = ? WHERE queueNumber = ?";

        try (PreparedStatement nameAStatement = connection.prepareStatement(nameAQuery);
             PreparedStatement nameBStatement = connection.prepareStatement(nameBQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String nameA = "";
            String nameB = "";

            nameAStatement.setInt(1, queueNumberA);
            ResultSet rsA = nameAStatement.executeQuery();
            if (!rsA.next()) {
                System.out.println("rsA no next");
            }
            nameA = rsA.getString("name");
            rsA.close();

            nameBStatement.setInt(1, queueNumberB);
            ResultSet rsB = nameBStatement.executeQuery();
            if (!rsB.next()) {
                System.out.println("rsB no next");
            }
            nameB = rsB.getString("name");
            rsB.close();

            if (nameA.equals("") || nameB.equals("")) {
                return;
            }

//            // perform swap (-1 and temp patient due to unique constraint which makes copying not possible)
            updateStatement.setInt(1, -1);
            updateStatement.setString(2, "TEMPORARY PATIENT");
            updateStatement.setInt(3, queueNumberB);
            updateStatement.executeUpdate();

            updateStatement.clearParameters();
            updateStatement.setInt(1, queueNumberB);
            updateStatement.setString(2, nameB);
            updateStatement.setInt(3, queueNumberA);
            updateStatement.executeUpdate();

            updateStatement.clearParameters();
            updateStatement.setInt(1, queueNumberA);
            updateStatement.setString(2, nameA);
            updateStatement.setInt(3, -1);
            updateStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            updateListView();

        } catch (SQLException e) {
            //add my exception handling here
            try {
                connection.rollback(); // Roll back transaction if any error occurs
                System.out.print(e);
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

}
