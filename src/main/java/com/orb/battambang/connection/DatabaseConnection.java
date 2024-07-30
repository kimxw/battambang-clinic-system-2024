package com.orb.battambang.connection;

import com.orb.battambang.Main;

import java.net.URL;
import java.sql.*;

public class DatabaseConnection {
    protected static String filePath;
    public static Connection connection = null;

    //USE THIS FOR INTELLIJ DEVELOPMENT
    /*
    public static boolean establishConnection(String location) {
        if (location == null || location.isEmpty()) {
            return false;
        }
        location = location.equals("Kbal Koh") ? "KbalKoh" : location;
        boolean success = false;
        DatabaseConnection.filePath = "./src/main/resources/databases/" + location + "-clinicdb.db";
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + filePath;
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

     */

    //WARNING: USE THE ONE BELOW ONLY WHEN EXPORTING TO JAR

    public static boolean establishConnection(String location) {
        if (location == null || location.isEmpty()) {
            return false;
        }
        location = location.equals("Kbal Koh") ? "KbalKoh" : location;
        boolean success = false;

        URL db_path = Main.class.getProtectionDomain().getCodeSource().getLocation();
        DatabaseConnection.filePath = db_path.getPath().replace("%20", " ");
//        System.out.println(filePath);
        filePath = filePath.substring(0, filePath.lastIndexOf('/'));
        filePath = filePath + "/databases/" + location + "-clinicdb.db";
//        System.out.println(filePath);

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + filePath;
            connection = DriverManager.getConnection(url);

            //executing a simple query to see if connection is legitimate
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM staffTable;");
            if (resultSet.next()) {
//                System.out.println("connected successfully");
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