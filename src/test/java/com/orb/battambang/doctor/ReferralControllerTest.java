package com.orb.battambang.doctor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.fail;

import com.orb.battambang.connection.DatabaseConnection;

public class ReferralControllerTest extends ApplicationTest {

    @InjectMocks
    private ReferralController controller;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX Platform failed to start");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Initialize the controller with FXML fields using reflection
        controller = new ReferralController();
        initializeFXMLFields(controller);

        // Establish database connection
        boolean dbConnectionSuccess = DatabaseConnection.establishConnection("MOPK");
        assertTrue(dbConnectionSuccess, "Failed to establish connection to clinic database");

        // Set the queue number and initialize data
        controller.setQueueNumber(1);

        // Call initialize method
        Method initMethod = ReferralController.class.getDeclaredMethod("initialize", URL.class, ResourceBundle.class);
        initMethod.setAccessible(true);
        initMethod.invoke(controller, null, null);
    }

    @AfterEach
    void tearDown() throws Exception {
        DatabaseConnection.closeDatabaseConnection();
    }

    @Test
    void testInitialize() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            assertEquals(today, getPrivateField(controller, "dateTextField", TextField.class).getText());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testFillPatientDetails() throws Exception {
        invokePrivateMethod(controller, "fillPatientDetails", new Class<?>[]{int.class}, new Object[]{1});

        // Replace with expected values from your database
        assertEquals("Vuthy Roeun", getPrivateField(controller, "nameTextField", TextField.class).getText());
        assertEquals("25", getPrivateField(controller, "ageTextField", TextField.class).getText());
        assertEquals("M", getPrivateField(controller, "genderTextField", TextField.class).getText());
        assertEquals("Street 1, Battambang", getPrivateField(controller, "addressTextArea", TextArea.class).getText());
    }

    @Test
    void testFillDoctorConsultDetails() throws Exception {
        invokePrivateMethod(controller, "fillDoctorConsultDetails", new Class<?>[]{int.class}, new Object[]{1});

        // Replace with expected values from your database
        assertEquals("Sreelakshmi Haridos | Staff ID: 6", getPrivateField(controller, "docNameTextField", TextField.class).getText());
        //assertEquals("Medication A", getPrivateField(controller, "medicationTextArea", TextArea.class).getText());
    }

    @Test
    void testFillDoctorNotes() throws Exception {
        invokePrivateMethod(controller, "fillDoctorNotes", new Class<?>[]{int.class}, new Object[]{1});

        // Replace with expected values from database
        assertEquals("Diagnosis", getPrivateField(controller, "diagnosisTextField", TextField.class).getText());
        assertEquals("Duration", getPrivateField(controller, "durationTextField", TextField.class).getText());
        assertEquals("Notes", getPrivateField(controller, "notesTextArea", TextArea.class).getText());
    }

    @Test
    void testUpdateButtonOnAction() throws Exception {
        getPrivateField(controller, "diagnosisTextField", TextField.class).setText("Diagnosis");
        getPrivateField(controller, "durationTextField", TextField.class).setText("Duration");
        getPrivateField(controller, "notesTextArea", TextArea.class).setText("Notes");

        controller.updateButtonOnAction(new ActionEvent());

        try (Connection conn = DatabaseConnection.connection;
             PreparedStatement stmt = conn.prepareStatement("SELECT referral FROM doctorConsultTable WHERE queueNumber = ?")) {
            stmt.setInt(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "No data found for queue number 1");
                assertEquals("Diagnosis;Duration;Notes", rs.getString("referral"));
            }
        }
    }


//    @Test
//    void testExitButtonOnAction() throws Exception {
//        Stage mockStage = mock(Stage.class);
//        Scene mockScene = mock(Scene.class);
//        when(mockStage.getScene()).thenReturn(mockScene);
//
//        Button exitButton = getPrivateField(controller, "exitButton", Button.class);
//        setPrivateField(exitButton, "scene", mockScene);
//
//        controller.exitButtonOnAction(new ActionEvent());
//
//        verify(mockStage).close();
//    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    // Helper method to get private fields using reflection
    private <T> T getPrivateField(Object object, String fieldName, Class<T> fieldType) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return fieldType.cast(field.get(object));
    }

    // Helper method to invoke private methods using reflection
    private Object invokePrivateMethod(Object object, String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method method = object.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(object, args);
    }

    // Helper method to initialize FXML fields using reflection
    private void initializeFXMLFields(Object controller) throws Exception {
        setPrivateField(controller, "dateTextField", new TextField());
        setPrivateField(controller, "nameTextField", new TextField());
        setPrivateField(controller, "ageTextField", new TextField());
        setPrivateField(controller, "genderTextField", new TextField());
        setPrivateField(controller, "addressTextArea", new TextArea());
        setPrivateField(controller, "diagnosisTextField", new TextField());
        setPrivateField(controller, "durationTextField", new TextField());
        setPrivateField(controller, "docNameTextField", new TextField());
        setPrivateField(controller, "medicationTextArea", new TextArea());
        setPrivateField(controller, "notesTextArea", new TextArea());
        setPrivateField(controller, "exitButton", new Button());
        setPrivateField(controller, "printButton", new Button());
        setPrivateField(controller, "updateButton", new Button());
        setPrivateField(controller, "pane1", new Pane());
    }

    // Helper method to close mocks
    private void closeMocks() {
        // Any cleanup logic can be added here
    }
}
