package com.orb.battambang.login;

import com.orb.battambang.connection.AuthDatabaseConnection;
import com.orb.battambang.connection.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewLoginPageControllerTest {

    private NewLoginPageController controller;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX Platform failed to start");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Initialize controller
        controller = new NewLoginPageController();

        // Establish authentication database connection
        boolean authConnectionSuccess = AuthDatabaseConnection.establishConnection();
        assertTrue(authConnectionSuccess, "Failed to establish connection to authentication database");

        // Set up the controller's private fields
        setPrivateField(controller, "usernameTextField", new TextField());
        setPrivateField(controller, "passwordPasswordField", new PasswordField());
        setPrivateField(controller, "loginMessageLabel", new Label());
        setPrivateField(controller, "verificationLabel", new Label());
        setPrivateField(controller, "verificationImageView", new javafx.scene.image.ImageView());
        setPrivateField(controller, "loginButton", new javafx.scene.control.Button());
        setPrivateField(controller, "cancelButton", new javafx.scene.control.Button());
        setPrivateField(controller, "newUserButton", new javafx.scene.control.Button());
        setPrivateField(controller, "line", new javafx.scene.shape.Line());
        setPrivateField(controller, "locationChoiceBox", new javafx.scene.control.ChoiceBox<>());
        setPrivateField(controller, "locationLabel", new Label());
        setPrivateField(controller, "locationWarningLabel", new Label());
        setPrivateField(controller, "goButton", new javafx.scene.control.Button());

        // Initialize the controller
        controller.initialize(null, null);
    }

    @AfterEach
    void tearDown() {
        // Close database connections after each test
        AuthDatabaseConnection.closeDatabaseConnection();
        DatabaseConnection.closeDatabaseConnection();
    }

    @AfterAll
    static void teardown() {
        // Ensure that the JavaFX Platform is not shut down by the tests
        // No call to Platform.exit() here
    }

    @Test
    void testValidLogin() throws Exception {
        TextField usernameTextField = (TextField) getPrivateField(controller, "usernameTextField");
        PasswordField passwordPasswordField = (PasswordField) getPrivateField(controller, "passwordPasswordField");

        usernameTextField.setText("kimaya");
        passwordPasswordField.setText("123");
        controller.loginButtonOnAction(new ActionEvent());

        Label verificationLabel = (Label) getPrivateField(controller, "verificationLabel");
        assertEquals("Verified user", verificationLabel.getText());
        assertEquals("-fx-text-fill: #5f8b07 ;", verificationLabel.getStyle());
    }

    @Test
    void testInvalidLogin() throws Exception {
        TextField usernameTextField = (TextField) getPrivateField(controller, "usernameTextField");
        PasswordField passwordPasswordField = (PasswordField) getPrivateField(controller, "passwordPasswordField");

        usernameTextField.setText("invalidUsername");
        passwordPasswordField.setText("invalidPassword");
        controller.loginButtonOnAction(new ActionEvent());

        Label verificationLabel = (Label) getPrivateField(controller, "verificationLabel");
        assertEquals("Invalid user", verificationLabel.getText());
        assertEquals("-fx-text-fill: #bf1b15 ;", verificationLabel.getStyle());
    }

    @Test
    void testGoButtonWithValidLocation() throws Exception {
        TextField usernameTextField = (TextField) getPrivateField(controller, "usernameTextField");
        PasswordField passwordPasswordField = (PasswordField) getPrivateField(controller, "passwordPasswordField");
        javafx.scene.control.ChoiceBox<String> locationChoiceBox = (javafx.scene.control.ChoiceBox<String>) getPrivateField(controller, "locationChoiceBox");

        usernameTextField.setText("kimaya");
        passwordPasswordField.setText("123");
        controller.loginButtonOnAction(new ActionEvent());

        locationChoiceBox.setValue("Kbal Koh");
        controller.goButtonOnAction(new ActionEvent());

        Label locationWarningLabel = (Label) getPrivateField(controller, "locationWarningLabel");
        assertEquals("Connecting to database . . .", locationWarningLabel.getText());
        //assertEquals("green", locationWarningLabel.getStyle().split(": ")[1].trim());
    }

    @Test
    void testGoButtonWithInvalidLocation() throws Exception {
        TextField usernameTextField = (TextField) getPrivateField(controller, "usernameTextField");
        PasswordField passwordPasswordField = (PasswordField) getPrivateField(controller, "passwordPasswordField");
        javafx.scene.control.ChoiceBox<String> locationChoiceBox = (javafx.scene.control.ChoiceBox<String>) getPrivateField(controller, "locationChoiceBox");

        usernameTextField.setText("kimaya");
        passwordPasswordField.setText("123");
        controller.loginButtonOnAction(new ActionEvent());

        locationChoiceBox.setValue(null);
        controller.goButtonOnAction(new ActionEvent());

        Label locationWarningLabel = (Label) getPrivateField(controller, "locationWarningLabel");
        assertEquals("Please select a location.", locationWarningLabel.getText());
        //assertEquals("-fx-text-fill: red ;", locationWarningLabel.getStyle());
    }

    // Helper method to set private field
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    // Helper method to get private field
    private Object getPrivateField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}
