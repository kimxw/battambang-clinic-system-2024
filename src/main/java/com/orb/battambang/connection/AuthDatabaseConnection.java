package com.orb.battambang.connection;

import com.orb.battambang.Main;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class AuthDatabaseConnection {


    public static Connection connection = null;

    public static boolean establishConnection() {
        boolean success = false;

//        String user = "admin";
//        String pass = "btb!2024";
//        String url = "jdbc:mysql://localhost:3306/patient_db";
//        String url = "jdbc:mysql://192.168.2.98:3306/AuthClinicDB";

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
            url = String.format("jdbc:mysql://%s/AuthClinicDB", br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);

            //executing a simple query to see if connection is legitimate
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM staffTable;");
            if (resultSet.next()) {
                //System.out.println("connected successfully");
                success = true;
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.out.println(e);
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