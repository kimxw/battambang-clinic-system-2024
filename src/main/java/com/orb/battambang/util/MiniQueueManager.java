package com.orb.battambang.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import com.orb.battambang.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class QueueManager {

    private final String column;
    private final ObservableList<Integer> queueList;
    private final Connection connection = DatabaseConnection.connection;

    public QueueManager(ListView<Integer> queueListView, String column) {
        this.queueList = FXCollections.observableArrayList();
        this.column = column;
        queueListView.setItems(queueList);
        startPolling();
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
        String query = "SELECT queueNumber FROM " + column;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            queueList.clear();

            while (resultSet.next()) {
                queueList.add(resultSet.getInt("queueNumber"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
