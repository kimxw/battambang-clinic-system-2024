package com.orb.battambang.connection;

import java.sql.*;

public class DatabaseConnection {
    protected static String filePath;
    public static Connection connection = null;

    public static boolean establishConnection(String inputPath) {
        boolean success = false;
        DatabaseConnection.filePath = inputPath;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:/" + filePath;
            connection = DriverManager.getConnection(url);

            //executing a simple query to see if connection is legitimate
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM staffTable;");
            if (resultSet.next()) {
                //System.out.println("connected successfully");
                success = true;
            }
            resultSet.close();
            statement.close();
        } catch (Exception exc) {
            //exc.printStackTrace();
            return false;
        }
        return success;
    }

    public static void closeDatabaseConnection() {
        try {
            if (DatabaseConnection.connection != null && !DatabaseConnection.connection.isClosed()) {
                DatabaseConnection.connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}