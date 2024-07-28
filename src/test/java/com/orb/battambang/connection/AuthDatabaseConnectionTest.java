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

public class AuthDatabaseConnectionTest {

    @BeforeEach
    void setUp() {
        // Reset the connection before each test
        AuthDatabaseConnection.connection = null;
    }

    @AfterEach
    void tearDown() {
        // Ensure the connection is closed after each test
        AuthDatabaseConnection.closeDatabaseConnection();
    }

    @Test
    public void testEstablishConnection_Success() {
        // Act
        boolean result = AuthDatabaseConnection.establishConnection();

        // Assert
        assertTrue(result, "Connection should be successfully established.");
    }

    @Test
    void testEstablishConnectionWithInvalidLocation() {
        // Intentionally providing an invalid location to make the test fail
        String invalidLocation = "InvalidLocation";
        boolean connectionEstablished = DatabaseConnection.establishConnection(invalidLocation);

        // Assert that the connection should not be established
        assertFalse(connectionEstablished, "Connection should not be established with an invalid location.");
    }

    @Test
    void testEstablishConnectionWithNullLocation() {
        // Providing null as location
        boolean connectionEstablished = DatabaseConnection.establishConnection(null);

        // Assert that the connection should not be established
        assertFalse(connectionEstablished, "Connection should not be established with a null location.");
    }

    @Test
    void testEstablishConnectionWithEmptyLocation() {
        // Providing an empty string as location
        boolean connectionEstablished = DatabaseConnection.establishConnection("");

        // Assert that the connection should not be established
        assertFalse(connectionEstablished, "Connection should not be established with an empty location.");
    }

    @Test
    void testCloseDatabaseConnection() {
        // Establish a valid connection first
        String validLocation = "Kbal Koh"; // Assuming this is a valid location
        DatabaseConnection.establishConnection(validLocation);

        // Close the connection
        DatabaseConnection.closeDatabaseConnection();

        // Assert that the connection is closed
        try {
            Connection connection = DatabaseConnection.connection;
            assertTrue(connection == null || connection.isClosed(), "Connection should be closed.");
        } catch (SQLException e) {
            fail("SQLException should not be thrown.");
        }
    }
}
