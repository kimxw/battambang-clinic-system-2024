package com.orb.battambang.connection;

import com.orb.battambang.Main;

import java.net.URL;
import java.sql.*;

public class AuthDatabaseConnectionLocal {
    protected static String filePath;
    public static Connection connection = null;

    //USE THIS ONE FOR INTELLIJ DEVELOPMENT
/*
    public static boolean establishConnection() {
        boolean success = false;

        AuthDatabaseConnectionR.filePath = "./src/main/resources/databases/auth-clinicdb.db";

        // Check if the file exists before attempting to connect
        File dbFile = new File(filePath);
        if (!dbFile.exists()) {
            return false;
        }

        try {
            Class.forName("org.sqlite.JDBC");

            String url = "jdbc:sqlite:" + filePath;
            try {
                connection = DriverManager.getConnection(url);
                if (connection.isValid(2)) {
                    success = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }

 */


    //WARNING: USE THE ONE BELOW ONLY WHEN EXPORTING TO JAR

    public static boolean establishConnection() {
        boolean success = false;
        URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.getPath());
        AuthDatabaseConnectionLocal.filePath = location.getPath().replace("%20", " ");
//        System.out.println(filePath);
        filePath = filePath.substring(0, filePath.lastIndexOf('/'));
        filePath = filePath + "/databases/auth-clinicdb.db";
//        System.out.println(filePath);

        try {
            Class.forName("org.sqlite.JDBC");

            String url = "jdbc:sqlite:" + filePath;
            try {
                connection = DriverManager.getConnection(url);
                if (connection.isValid(2)) {
                    success = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }




    public static void closeDatabaseConnection() {
        try {
            if (AuthDatabaseConnectionLocal.connection != null && !AuthDatabaseConnectionLocal.connection.isClosed()) {
                AuthDatabaseConnectionLocal.connection.close();
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
            return !AuthDatabaseConnectionLocal.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }



}