package com.orb.battambang.home;

import com.orb.battambang.login.NewLoginPageController;
import com.orb.battambang.login.Staff;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HomePageControllerTest extends ApplicationTest {

    private HomePageController controller;
    private Staff mockStaff;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX Platform failed to start");
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize controller
        controller = new HomePageController();

        // Mock the Staff class
        mockStaff = mock(Staff.class);
        when(mockStaff.getStaffID()).thenReturn(1);
        when(mockStaff.getFirstName()).thenReturn("John");
        when(mockStaff.getLastName()).thenReturn("Doe");
        when(mockStaff.getUsername()).thenReturn("johndoe");
        when(mockStaff.getPrimaryRole()).thenReturn("Doctor");
        when(mockStaff.getLocation()).thenReturn("MOPK");
        when(mockStaff.isAdmin()).thenReturn(true);
        when(mockStaff.isReception()).thenReturn(true);
        when(mockStaff.isTriage()).thenReturn(false);
        when(mockStaff.isEducation()).thenReturn(false);
        when(mockStaff.isConsultation()).thenReturn(true);
        when(mockStaff.isPharmacy()).thenReturn(false);

        // Set up the NewLoginPageController's getStaffDetails method to return the mock staff
        setStaticField(NewLoginPageController.class, "staff", mockStaff);

        // Set up the controller's private fields
        setPrivateField(controller, "welcomeLabel", new Label());
        setPrivateField(controller, "nameLabel", new Label());
        setPrivateField(controller, "staffIDLabel", new Label());
        setPrivateField(controller, "primaryRoleLabel", new Label());
        setPrivateField(controller, "locationLabel", new Label());
        setPrivateField(controller, "adminPermLabel", new Label());
        setPrivateField(controller, "adminPermRectangle", new Rectangle());
        setPrivateField(controller, "receptionPermLabel", new Label());
        setPrivateField(controller, "receptionPermRectangle", new Rectangle());
        setPrivateField(controller, "triagePermLabel", new Label());
        setPrivateField(controller, "triagePermRectangle", new Rectangle());
        setPrivateField(controller, "educationPermLabel", new Label());
        setPrivateField(controller, "educationPermRectangle", new Rectangle());
        setPrivateField(controller, "consultationPermLabel", new Label());
        setPrivateField(controller, "consultationPermRectangle", new Rectangle());
        setPrivateField(controller, "pharmacyPermLabel", new Label());
        setPrivateField(controller, "pharmacyPermRectangle", new Rectangle());
        setPrivateField(controller, "sliderAnchorPane", new AnchorPane());
        setPrivateField(controller, "menuLabel", new Label());
        setPrivateField(controller, "menuBackLabel", new Label());
        setPrivateField(controller, "menuHomeButton", new Button());
        setPrivateField(controller, "menuReceptionButton", new Button());
        setPrivateField(controller, "menuTriageButton", new Button());
        setPrivateField(controller, "menuEducationButton", new Button());
        setPrivateField(controller, "menuConsultationButton", new Button());
        setPrivateField(controller, "menuPharmacyButton", new Button());
        setPrivateField(controller, "menuQueueManagerButton", new Button());
        setPrivateField(controller, "menuAdminButton", new Button());
        setPrivateField(controller, "menuLogoutButton", new Button());
        setPrivateField(controller, "menuUserButton", new Button());
        setPrivateField(controller, "menuLocationButton", new Button());

        // Initialize the controller
        controller.initialize(null, null);
    }

    @AfterEach
    void tearDown() {
        // Reset the NewLoginPageController's getStaffDetails method
        setStaticField(NewLoginPageController.class, "staff", null);
    }

    @AfterAll
    static void teardown() {
        // Ensure that the JavaFX Platform is not shut down by the tests
        // No call to Platform.exit() here
    }

    @Test
    void testInitialize() {
        assertEquals("Welcome, John", getPrivateField(controller, "welcomeLabel", Label.class).getText());
        assertEquals("John Doe", getPrivateField(controller, "nameLabel", Label.class).getText());
        assertEquals("1", getPrivateField(controller, "staffIDLabel", Label.class).getText());
        assertEquals("Doctor", getPrivateField(controller, "primaryRoleLabel", Label.class).getText());
        assertEquals("MOPK", getPrivateField(controller, "locationLabel", Label.class).getText());
        // Add more assertions as needed to verify that the rectangles are updated correctly
    }

    // Helper method to set private field
    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to get private field
    private <T> T getPrivateField(Object object, String fieldName, Class<T> fieldType) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(object));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to set static field
    private void setStaticField(Class<?> clazz, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
