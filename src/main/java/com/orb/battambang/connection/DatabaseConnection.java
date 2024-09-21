package com.orb.battambang.connection;

import com.orb.battambang.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection connection = null;

    public static boolean establishConnection(String location) {
        if (location == null || location.isEmpty()) {
            return false;
        }
        location = location.equals("Kbal Koh") ? "KbalKoh" : location;
        boolean success = false;

//        String user = "dbadmin";
//        String pass = "battam!2024";
//        //String url = "jdbc:mysql://localhost:3306/patient_db";
//        String url = String.format("jdbc:mysql://192.168.2.98:3306/%sClinicDB", location);

        String user = "";
        String pass = "";
        String url = "";

        URL configURL = Main.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = configURL.getPath().replace("%20", " ");
        filePath = filePath.substring(0, filePath.lastIndexOf('/'));
        filePath = filePath + "/databases/dbconfig.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            user = br.readLine();
            pass = br.readLine();
            url = String.format("jdbc:mysql://%s/%sClinicDB", br.readLine(), location);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);

            try (Statement statement = connection.createStatement()) {
                statement.executeQuery("SELECT 1 FROM staffTable LIMIT 1");
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
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