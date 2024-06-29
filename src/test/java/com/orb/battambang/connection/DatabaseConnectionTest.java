package com.orb.battambang.connection;

import com.orb.battambang.connection.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;


public class DatabaseConnectionTest {

    private static String dbpath = "test.db";

    // Method to create test SQLite database and table
    private static void createDatabase(String dbPath) {
        String url = "jdbc:sqlite:" + dbPath;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                // Create table if not exists
                String createTableSQL = "CREATE TABLE IF NOT EXISTS staffTable (\n"
                        + "    id INTEGER PRIMARY KEY,\n"
                        + "    name TEXT\n"
                        + ");";
                Statement stmt = conn.createStatement();
                stmt.execute(createTableSQL);

                // Insert a sample record into staffTable
                String insertSQL = "INSERT INTO staffTable (name) VALUES ('John Doe');";
                stmt.executeUpdate(insertSQL);

                //System.out.println("SQLite database and table created successfully.");
                //System.out.println("Sample record inserted into staffTable.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating SQLite database: " + e.getMessage());
        }
    }


    // Method to get the absolute path of the SQLite database file
    private static String getAbsolutePath(String dbPath) {
        File file = new File(dbPath);
        return file.getAbsolutePath();
    }

    // Method to delete the SQLite database file
    private static void deleteDatabase(String dbPath) {
        File file = new File(dbPath);
        if (file.exists()) {
            if (file.delete()) {
                // Successfully deleted the database file
            } else {
                // Failed to delete the database file
                System.err.println("Failed to delete the database file.");
            }
        } else {
            // Database file does not exist
            System.err.println("Database file does not exist.");
        }
    }


    @Test
    public void testEstablishConnection_Success() {

        createDatabase(dbpath);
        // Arrange
        String absolutePath = getAbsolutePath(dbpath);

        // Act
        boolean result = DatabaseConnection.establishConnection(absolutePath);

        // Assert
        assertTrue(result, "Connection should be successfully established.");

        DatabaseConnection.closeDatabaseConnection();

        deleteDatabase(dbpath);
    }



    @Test
    public void testEstablishConnection_Failure() {
        // Arrange
        String invalidPath = "nonexistent.db";

        // Act
        boolean result = DatabaseConnection.establishConnection(invalidPath);

        // Assert
        assertFalse(result, "Connection should fail for invalid database path.");
    }

    public static void main(String[] args) {
        // Create the SQLite database and table
        createDatabase(dbpath);

        // Get and print the absolute path of the created database
        String absolutePath = getAbsolutePath(dbpath);
        System.out.println("Absolute Path of the Database: " + absolutePath);

        // Delete the SQLite database
        deleteDatabase(dbpath);
        System.out.println("Database deleted successfully.");
    }
}
