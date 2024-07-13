package com.orb.battambang.connection;

import com.orb.battambang.MainApp;

import java.net.URL;
import java.sql.*;

public class AuthDatabaseConnection {
    protected static String filePath;
    public static Connection connection = null;

    public static boolean establishConnection() {
        boolean success = false;
        AuthDatabaseConnection.filePath = "";
        URL authDbUrl = MainApp.class.getResource("/databases/auth-clinicdb.db");
        if (authDbUrl != null) {
            AuthDatabaseConnection.filePath = authDbUrl.getFile().replace("%20", " ");
        }
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + filePath;
            connection = DriverManager.getConnection(url);

            // Executing a simple query to see if connection is legitimate
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM staffTable;");
            if (resultSet.next()) {
                success = true;
            }
            resultSet.close();
            statement.close();
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
        return success;
    }

    public static void closeDatabaseConnection() {
        try {
            if (AuthDatabaseConnection.connection != null && !AuthDatabaseConnection.connection.isClosed()) {
                AuthDatabaseConnection.connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}