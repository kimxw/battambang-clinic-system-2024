package com.orb.battambang.doctor;

import com.orb.battambang.pharmacy.Medicine;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Prescription;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static org.junit.jupiter.api.Assertions.fail;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PrescriptionControllerTest extends ApplicationTest {

    @Mock
    private DoctorConsultController mockDoctorConsultController;

    @InjectMocks
    private PrescriptionController controller;

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
        controller = new PrescriptionController();
        initializeFXMLFields(controller);

        // Establish database connection
        boolean dbConnectionSuccess = DatabaseConnection.establishConnection("MOPK");
        assertTrue(dbConnectionSuccess, "Failed to establish connection to clinic database");

        // Set the doctor consult controller
        controller.setDoctorConsultController(mockDoctorConsultController);

        // Call initialize method
        Method initMethod = PrescriptionController.class.getDeclaredMethod("initialize", URL.class, ResourceBundle.class);
        initMethod.setAccessible(true);
        initMethod.invoke(controller, (URL) null, (ResourceBundle) null);
    }

    @AfterEach
    void tearDown() throws Exception {
        DatabaseConnection.closeDatabaseConnection();
    }

    @Test
    void testInitialize() {
        // Check if tables are initialized properly
        try {
            assertTrue(getPrivateField(controller, "medicineObservableList", ObservableList.class).size() > 0);
            assertTrue(getPrivateField(controller, "prescriptionObservableList", ObservableList.class).isEmpty());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testAddUpdateButtonOnAction() throws Exception {
        // Set input fields
        setTextField(controller, "inputNameTextField", "Paracetamol");
        setTextField(controller, "inputQuantityTextField", "500");
        setTextField(controller, "inputUnitsTextField", "mg");
        setTextArea(controller, "inputDosageTextArea", "Take one tablet every 6 hours");

        // Simulate button click
        controller.addUpdateButtonOnAction(new ActionEvent());

        // Verify the prescription entry is added/updated
        ObservableList<Prescription.PrescriptionEntry> prescriptionList = getPrivateField(controller, "prescriptionObservableList", ObservableList.class);
        assertEquals(1, prescriptionList.size());
        Prescription.PrescriptionEntry entry = prescriptionList.get(0);
        assertEquals("Paracetamol", entry.getName());
        assertEquals("500", entry.getQuantityInMilligrams());
        assertEquals("mg", entry.getUnits());
        assertEquals("Take one tablet every 6 hours", entry.getDosageInstructions());
    }

    @Test
    void testDeleteButtonOnAction() throws Exception {
        // Add a prescription entry
        testAddUpdateButtonOnAction();

        // Select the entry in the table
        TableView<Prescription.PrescriptionEntry> prescriptionTableView = getPrivateField(controller, "prescriptionTableView", TableView.class);
        prescriptionTableView.getSelectionModel().select(0);

        // Simulate delete button click
        controller.deleteButtonOnAction(new ActionEvent());

        // Verify the prescription entry is deleted
        ObservableList<Prescription.PrescriptionEntry> prescriptionList = getPrivateField(controller, "prescriptionObservableList", ObservableList.class);
        assertTrue(prescriptionList.isEmpty());
    }

    @Test
    void testClearButtonOnAction() throws Exception {
        // Set input fields
        setTextField(controller, "inputNameTextField", "Paracetamol");
        setTextField(controller, "inputQuantityTextField", "500");
        setTextField(controller, "inputUnitsTextField", "mg");
        setTextArea(controller, "inputDosageTextArea", "Take one tablet every 6 hours");

        // Simulate clear button click
        controller.clearButtonOnAction(new ActionEvent());

        // Verify the input fields are cleared
        assertTrue(getTextField(controller, "inputNameTextField").isEmpty());
        assertTrue(getTextField(controller, "inputQuantityTextField").isEmpty());
        assertTrue(getTextField(controller, "inputUnitsTextField").isEmpty());
        assertTrue(getTextArea(controller, "inputDosageTextArea").isEmpty());
    }

    @Test
    void testExitButtonOnAction() throws Exception {
        Stage mockStage = mock(Stage.class);
        Scene mockScene = mock(Scene.class);

        // Create a mock exitButton
        Button mockExitButton = mock(Button.class);

        // Set the scene for the mock exitButton
        when(mockExitButton.getScene()).thenReturn(mockScene);

        // Set the window for the mock scene
        when(mockScene.getWindow()).thenReturn(mockStage);

        // Use reflection to set the exitButton field in the controller
        setPrivateField(controller, "exitButton", mockExitButton);

        // Trigger the exit button action
        controller.exitButtonOnAction(new ActionEvent());

        // Verify that the stage's close method was called
        verify(mockStage).close();
    }
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

    // Helper method to set TextField value
    private void setTextField(Object object, String fieldName, String value) throws Exception {
        TextField textField = getPrivateField(object, fieldName, TextField.class);
        textField.setText(value);
    }

    // Helper method to set TextArea value
    private void setTextArea(Object object, String fieldName, String value) throws Exception {
        TextArea textArea = getPrivateField(object, fieldName, TextArea.class);
        textArea.setText(value);
    }

    // Helper method to get TextField value
    private String getTextField(Object object, String fieldName) throws Exception {
        TextField textField = getPrivateField(object, fieldName, TextField.class);
        return textField.getText();
    }

    // Helper method to get TextArea value
    private String getTextArea(Object object, String fieldName) throws Exception {
        TextArea textArea = getPrivateField(object, fieldName, TextArea.class);
        return textArea.getText();
    }

    // Helper method to initialize FXML fields using reflection
    private void initializeFXMLFields(Object controller) throws Exception {
        setPrivateField(controller, "prescriptionTableView", new TableView<>());
        setPrivateField(controller, "nameColumn", new TableColumn<>());
        setPrivateField(controller, "quantityColumn", new TableColumn<>());
        setPrivateField(controller, "unitsColumn", new TableColumn<>());
        setPrivateField(controller, "dosageColumn", new TableColumn<>());
        setPrivateField(controller, "inputIdTextField", new TextField());
        setPrivateField(controller, "inputNameTextField", new TextField());
        setPrivateField(controller, "inputQuantityTextField", new TextField());
        setPrivateField(controller, "inputUnitsTextField", new TextField());
        setPrivateField(controller, "inputDosageTextArea", new TextArea());
        setPrivateField(controller, "addUpdateButton", new Button());
        setPrivateField(controller, "deleteButton", new Button());
        setPrivateField(controller, "clearButton", new Button());
        setPrivateField(controller, "exitButton", new Button());
        setPrivateField(controller, "medicineTableView", new TableView<>());
        setPrivateField(controller, "idTableColumn", new TableColumn<>());
        setPrivateField(controller, "medicineNameTableColumn", new TableColumn<>());
        setPrivateField(controller, "medicineQuantityTableColumn", new TableColumn<>());
        setPrivateField(controller, "stockTableColumn", new TableColumn<>());
        setPrivateField(controller, "warningLabel", new Label());
        setPrivateField(controller, "medicineObservableList", FXCollections.observableArrayList());
        setPrivateField(controller, "prescriptionObservableList", FXCollections.observableArrayList());
    }
}
