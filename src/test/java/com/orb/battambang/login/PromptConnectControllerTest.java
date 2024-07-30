package com.orb.battambang.login;

import com.orb.battambang.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.assertions.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

class PromptConnectControllerTest extends ApplicationTest {

    private PromptConnectController controller;
    private Button goButton;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("prompt-connect.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setScene(scene);
        stage.show();

        controller = loader.getController();
        goButton = lookup("#goButton").queryButton();
    }

    @BeforeEach
    void setUp() {
        // Any setup needed before each test
    }

    @Test
    void testGoButtonOnAction() {
        // Click the go button
        FxRobot robot = new FxRobot();
        robot.clickOn(goButton);

        // Check if the login page is opened
        // Verify that a new window with the title "Login" is shown
        FxRobot newRobot = new FxRobot();
        Stage newStage = (Stage) newRobot.listWindows().stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElse(null);
        assertNotNull(newStage, "New stage should be opened");
        assertEquals("Login", newStage.getTitle());
    }
}

