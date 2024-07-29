package com.orb.battambang.education;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.api.FxRobot;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.mockito.Mockito.*;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class EducationControllerTest {

    private EducationController controller;

    @Start
    private void start(Stage stage) {
        controller = new EducationController();
        try {
            initializeFXMLFields(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testSwitchUserButtonOnAction(FxRobot robot) throws Exception {
//        // Mock FXML fields
//        Button switchUserButton = new Button();
//        setPrivateField(controller, "switchUserButton", switchUserButton);
//
//        // Mock the scene and stage for the button
//        Stage mockStage = mock(Stage.class);
//        Scene mockScene = mock(Scene.class);
//        when(mockStage.getScene()).thenReturn(mockScene);
//        when(mockScene.getWindow()).thenReturn(mockStage);
//        switchUserButton.setOnAction(event -> controller.switchUserButtonOnAction(event));
//
//        // Click the switch user button
//        robot.clickOn(switchUserButton);
//
//        // Verify the stage close method was called
//        verify(mockStage, times(1)).close();
//    }

    @Test
    void testSearchButtonOnAction(FxRobot robot) throws Exception {
        // Mock the FXML fields
        setPrivateField(controller, "queueNumberTextField", new TextField());
        setPrivateField(controller, "queueSelectLabel", new Label());
        setPrivateField(controller, "particularsPane", new Pane());

        // Mock the DatabaseConnection and its connection field
        Connection mockConnection = mock(Connection.class);
        setPrivateStaticField(DatabaseConnection.class, "connection", mockConnection);

        // Initialize FXML fields
        TextField queueNumberTextField = (TextField) getPrivateField(controller, "queueNumberTextField");

        // Set test data
        queueNumberTextField.setText("123");

        // Mock Statement and ResultSet
        Statement statement = mock(Statement.class);
        when(mockConnection.createStatement()).thenReturn(statement);

        ResultSet resultSet = mock(ResultSet.class);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("Test Name");
        when(resultSet.getInt("age")).thenReturn(30);
        when(resultSet.getString("sex")).thenReturn("Male");
        when(resultSet.getString("phoneNumber")).thenReturn("1234567890");
        when(resultSet.getString("educationStatus")).thenReturn("Complete");

        // Invoke the method
        controller.searchButtonOnAction(new ActionEvent());

        Pane particularsPane = (Pane) getPrivateField(controller, "particularsPane");
        assertThat(particularsPane.isVisible()).isTrue();

        // Verify the fields are set correctly
        assertThat(((Label) getPrivateField(controller, "nameLabel")).getText()).isEqualTo("Test Name");
        assertThat(((Label) getPrivateField(controller, "ageLabel")).getText()).isEqualTo("30");
        assertThat(((Label) getPrivateField(controller, "sexLabel")).getText()).isEqualTo("Male");
        assertThat(((Label) getPrivateField(controller, "phoneNumberLabel")).getText()).isEqualTo("1234567890");
        assertThat(((CheckBox) getPrivateField(controller, "educationCompleteCheckBox")).isSelected()).isTrue();
    }


    @Test
    void testUpdateButtonOnAction(FxRobot robot) throws Exception {
        // Mock the FXML fields
        setPrivateField(controller, "queueNumberTextField", new TextField());
        setPrivateField(controller, "queueNoLabel", new Label());
        setPrivateField(controller, "warningLabel", new Label());
        setPrivateField(controller, "educationCompleteCheckBox", new CheckBox());

        // Mock the DatabaseConnection and its connection field
        Connection mockConnection = mock(Connection.class);
        setPrivateStaticField(DatabaseConnection.class, "connection", mockConnection);

        // Initialize FXML fields
        TextField queueNumberTextField = (TextField) getPrivateField(controller, "queueNumberTextField");
        Label queueNoLabel = (Label) getPrivateField(controller, "queueNoLabel");
        CheckBox educationCompleteCheckBox = (CheckBox) getPrivateField(controller, "educationCompleteCheckBox");

        // Set test data
        queueNumberTextField.setText("123");
        queueNoLabel.setText("123");
        educationCompleteCheckBox.setSelected(true);

        // Mock PreparedStatement and ResultSet
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(preparedStatement);

        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("educationStatus")).thenReturn("Incomplete");

        // Invoke the method
        controller.updateButtonOnAction(new ActionEvent());

        // Verify database update
        verify(preparedStatement, times(1)).executeUpdate();
    }


    @Test
    void testAddButtonOnAction(FxRobot robot) throws Exception {
        // Mock the FXML fields
        setPrivateField(controller, "waitingListView", new ListView<>());
        setPrivateField(controller, "inProgressListView", new ListView<>());

        // Mock the DatabaseConnection and its connection field
        Connection mockConnection = mock(Connection.class);
        setPrivateStaticField(DatabaseConnection.class, "connection", mockConnection);

        // Initialize FXML fields
        ListView<Integer> waitingListView = (ListView<Integer>) getPrivateField(controller, "waitingListView");
        ListView<Integer> inProgressListView = (ListView<Integer>) getPrivateField(controller, "inProgressListView");

        // Set test data
        waitingListView.getItems().add(123);

        // Mock PreparedStatement and ResultSet
        PreparedStatement nameStatement = mock(PreparedStatement.class);
        PreparedStatement deleteStatement = mock(PreparedStatement.class);
        PreparedStatement insertStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement("SELECT name FROM educationWaitingTable WHERE queueNumber = ?")).thenReturn(nameStatement);
        when(mockConnection.prepareStatement("DELETE FROM educationWaitingTable WHERE queueNumber = ?")).thenReturn(deleteStatement);
        when(mockConnection.prepareStatement("INSERT INTO educationProgressTable (queueNumber, name) VALUES (?, ?)")).thenReturn(insertStatement);

        ResultSet resultSet = mock(ResultSet.class);
        when(nameStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("Test Name");

        // Invoke the method
        controller.addButtonOnAction();

        // Verify ListViews
        assertThat(inProgressListView.getItems()).contains(123);
        assertThat(waitingListView.getItems()).doesNotContain(123);

        // Verify database updates
        verify(nameStatement, times(1)).setInt(1, 123);
        verify(deleteStatement, times(1)).setInt(1, 123);
        verify(insertStatement, times(1)).setInt(1, 123);
        verify(insertStatement, times(1)).setString(2, "Test Name");
        verify(nameStatement, times(1)).executeQuery();
        verify(deleteStatement, times(1)).executeUpdate();
        verify(insertStatement, times(1)).executeUpdate();
    }


    @Test
    void testSendButtonOnAction(FxRobot robot) throws Exception {
        // Mock the FXML fields
        setPrivateField(controller, "inProgressListView", new ListView<>());

        // Mock the DatabaseConnection and its connection field
        Connection mockConnection = mock(Connection.class);
        setPrivateStaticField(DatabaseConnection.class, "connection", mockConnection);

        // Initialize FXML fields
        ListView<Integer> inProgressListView = (ListView<Integer>) getPrivateField(controller, "inProgressListView");

        // Set test data
        inProgressListView.getItems().add(123);

        // Mock PreparedStatement and ResultSet
        PreparedStatement nameStatement = mock(PreparedStatement.class);
        PreparedStatement deleteStatement = mock(PreparedStatement.class);
        PreparedStatement insertStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement("SELECT name FROM educationProgressTable WHERE queueNumber = ?")).thenReturn(nameStatement);
        when(mockConnection.prepareStatement("DELETE FROM educationProgressTable WHERE queueNumber = ?")).thenReturn(deleteStatement);
        when(mockConnection.prepareStatement("INSERT INTO doctorWaitingTable (queueNumber, name) VALUES (?, ?)")).thenReturn(insertStatement);

        ResultSet resultSet = mock(ResultSet.class);
        when(nameStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("name")).thenReturn("Test Name");

        // Invoke the method
        controller.sendButtonOnAction();

        // Verify ListViews
        assertThat(inProgressListView.getItems()).doesNotContain(123);

        // Verify database updates
        verify(nameStatement, times(1)).setInt(1, 123);
        verify(deleteStatement, times(1)).setInt(1, 123);
        verify(insertStatement, times(1)).setInt(1, 123);
        verify(insertStatement, times(1)).setString(2, "Test Name");
        verify(nameStatement, times(1)).executeQuery();
        verify(deleteStatement, times(1)).executeUpdate();
        verify(insertStatement, times(1)).executeUpdate();
    }


    private void initializeFXMLFields(Object controller) throws Exception {
        setPrivateField(controller, "queueNumberTextField", new TextField());
        setPrivateField(controller, "queueSelectLabel", new Label());
        setPrivateField(controller, "queueNoLabel", new Label());
        setPrivateField(controller, "nameLabel", new Label());
        setPrivateField(controller, "ageLabel", new Label());
        setPrivateField(controller, "sexLabel", new Label());
        setPrivateField(controller, "phoneNumberLabel", new Label());
        setPrivateField(controller, "status1Label", new Label());
        setPrivateField(controller, "status1Rectangle", new Rectangle());
        setPrivateField(controller, "switchUserButton", new Button());
        setPrivateField(controller, "particularsPane", new Pane());
        setPrivateField(controller, "waitingListView", new ListView<>());
        setPrivateField(controller, "inProgressListView", new ListView<>());
        setPrivateField(controller, "educationCompleteCheckBox", new CheckBox());
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
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    private Object getPrivateField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
