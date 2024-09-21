package com.orb.battambang.connection;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionLocal {

    public static Connection connection = null;

    public static boolean establishConnection(String location) {
        if (location == null || location.isEmpty()) {
            return false;
        }
        location = location.equals("Kbal Koh") ? "KbalKoh" : location;
        boolean success = false;

        String user = "dbadmin";
        String pass = "battam!2024";
        //String url = "jdbc:mysql://localhost:3306/patient_db";
        String url = String.format("jdbc:mysql://192.168.2.98:3306/%s-clinicdb", location);


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return success;
    }


    public static void closeDatabaseConnection() {
        try {
            if (DatabaseConnectionLocal.connection != null && !DatabaseConnectionLocal.connection.isClosed()) {
                DatabaseConnectionLocal.connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


}