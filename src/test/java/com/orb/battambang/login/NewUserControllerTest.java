package com.orb.battambang.login;

import com.orb.battambang.connection.AuthDatabaseConnection;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewUserControllerTest {

    private NewUserController newUserController;
    private Connection connection;

    @BeforeAll
    public void setUp() throws Exception {
        // Initialize JavaFX toolkit
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();

        AuthDatabaseConnection.establishConnection();

        // Set up database connection
        connection = AuthDatabaseConnection.connection;

        // Set up the controller
        newUserController = new NewUserController();

        // Use reflection to set private fields
        setPrivateField(newUserController, "firstNameTextField", new TextField());
        setPrivateField(newUserController, "lastNameTextField", new TextField());
        setPrivateField(newUserController, "usernameTextField", new TextField());
        setPrivateField(newUserController, "passwordPasswordField", new PasswordField());
        setPrivateField(newUserController, "confirmPasswordPasswordField", new PasswordField());
        setPrivateField(newUserController, "roleChoiceBox", new ChoiceBox<>());
        setPrivateField(newUserController, "usermessageLabel", new Label());
        setPrivateField(newUserController, "passwordmatchLabel", new Label());

        // Initialize role choice box
        newUserController.initialize(null, null);
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @BeforeEach
    public void resetDatabase() throws Exception {
        // Reset the database state
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM staffTable WHERE username = 'testUser'");
        statement.close();
    }

    @Test
    public void testCreateUser_ValidUser() throws Exception {
        // Arrange
        setTextFieldValue("firstNameTextField", "John");
        setTextFieldValue("lastNameTextField", "Doe");
        setTextFieldValue("usernameTextField", "testUser");
        setTextFieldValue("passwordPasswordField", "password123");
        setTextFieldValue("confirmPasswordPasswordField", "password123");
        setChoiceBoxValue("roleChoiceBox", "Reception");

        // Act
        newUserController.createUser();

        // Assert
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM staffTable WHERE username = 'testUser'");
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        resultSet.close();
        statement.close();

        // Check the user message label for success message
        assertEquals("User created successfully!", getLabelText("usermessageLabel"));
    }

    @Test
    public void testCreateUser_PasswordMismatch() throws Exception {
        // Arrange
        setTextFieldValue("firstNameTextField", "John");
        setTextFieldValue("lastNameTextField", "Doe");
        setTextFieldValue("usernameTextField", "testUser");
        setTextFieldValue("passwordPasswordField", "password123");
        setTextFieldValue("confirmPasswordPasswordField", "password456");

        // Act
        newUserController.createUserButtonOnAction(null);

        // Assert
        assertEquals("Password does not match!", getLabelText("passwordmatchLabel"));
    }

    @Test
    public void testCreateUser_DuplicateUsername() throws Exception {
        // Arrange
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO staffTable(firstName, lastName, username, password, primaryRole, admin, reception, triage, education, consultation, pharmacy) VALUES ('Jane', 'Doe', 'testUser', 'password123', 'Reception', 0, 1, 1, 0, 0, 0)");
        statement.close();

        setTextFieldValue("firstNameTextField", "John");
        setTextFieldValue("lastNameTextField", "Doe");
        setTextFieldValue("usernameTextField", "testUser");
        setTextFieldValue("passwordPasswordField", "password123");
        setTextFieldValue("confirmPasswordPasswordField", "password123");
        setChoiceBoxValue("roleChoiceBox", "Reception");

        // Act
        newUserController.createUser();

        // Assert
        assertEquals("Username is already taken", getLabelText("usermessageLabel"));
    }

    @AfterAll
    public void tearDown() throws Exception {
        // Close the database connection
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void setTextFieldValue(String fieldName, String value) throws Exception {
        Field field = newUserController.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        ((TextField) field.get(newUserController)).setText(value);
    }

    private void setChoiceBoxValue(String fieldName, String value) throws Exception {
        Field field = newUserController.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        ((ChoiceBox<String>) field.get(newUserController)).setValue(value);
    }

    private String getLabelText(String fieldName) throws Exception {
        Field field = newUserController.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return ((Label) field.get(newUserController)).getText();
    }
}
