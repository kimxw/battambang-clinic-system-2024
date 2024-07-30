package com.orb.battambang.connection;

import com.orb.battambang.Main;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;

public class AuthDatabaseConnection {
    protected static String filePath;
    public static Connection connection = null;

    //USE THIS ONE FOR INTELLIJ DEVELOPMENT
/*
    public static boolean establishConnection() {
        boolean success = false;

        AuthDatabaseConnection.filePath = "./src/main/resources/databases/auth-clinicdb.db";

        // Check if the file exists before attempting to connect
        File dbFile = new File(filePath);
        if (!dbFile.exists()) {
            return false;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + filePath;
            connection = DriverManager.getConnection(url);

            //executing a simple query to see if connection is legitimate
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

 */


    //WARNING: USE THE ONE BELOW ONLY WHEN EXPORTING TO JAR

    public static boolean establishConnection() {
        boolean success = false;
        URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.getPath());
        AuthDatabaseConnection.filePath = location.getPath().replace("%20", " ");
//        System.out.println(filePath);
        filePath = filePath.substring(0, filePath.lastIndexOf('/'));
        filePath = filePath + "/databases/auth-clinicdb.db";
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

    public static boolean isConnectionOpen() {
        if (connection == null) {
            return false;
        }
        try {
            return !AuthDatabaseConnection.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }



}