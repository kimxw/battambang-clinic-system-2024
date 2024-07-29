package com.orb.battambang.pharmacy;

import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.pharmacy.LowStockController;
import com.orb.battambang.pharmacy.Medicine;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LowStockControllerTest extends ApplicationTest {

    private LowStockController controller;
    private Connection mockConnection;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new LowStockController();
        initializeFXMLFields(controller);

        AnchorPane root = new AnchorPane();
        stage.setScene(new javafx.scene.Scene(root, 800, 600));
        stage.show();
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize mocks
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockResultSet = mock(ResultSet.class);

        // Set up mock behaviors
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Inject mock connection into DatabaseConnection class
        DatabaseConnection.connection = mockConnection;

        // Mock ResultSet behavior for initializeMedicineList method
        when(mockResultSet.next()).thenReturn(true, false); // Only one iteration
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Aspirin");
        when(mockResultSet.getInt("quantityInMilligrams")).thenReturn(500);
        when(mockResultSet.getInt("stockLeft")).thenReturn(10);

        // Initialize controller
        controller.initializeMedicineList();
    }

    @Test
    public void testNameSearchFilter() {
        // Set the search text field to filter by name
        controller.getNameSearchTextField().setText("Aspirin");

        // Verify that the filtered list contains only the expected items
        ObservableList<Medicine> filteredList = controller.getMedicineTableView().getItems();
        assertEquals(1, filteredList.size());
        assertEquals("Aspirin", filteredList.get(0).getName());
    }

    @Test
    public void testIdSearchFilter() {
        // Set the search text field to filter by id
        controller.getIdSearchTextField().setText("1");

        // Verify that the filtered list contains only the expected items
        ObservableList<Medicine> filteredList = controller.getMedicineTableView().getItems();
        assertEquals(1, filteredList.size());
        assertEquals(1, filteredList.get(0).getId());
    }

    private void initializeFXMLFields(Object controller) throws Exception {
        setPrivateField(controller, "idSearchTextField", new TextField());
        setPrivateField(controller, "nameSearchTextField", new TextField());
        setPrivateField(controller, "medicineTableView", new TableView<>());
        setPrivateField(controller, "idTableColumn", new TableColumn<>("ID"));
        setPrivateField(controller, "nameTableColumn", new TableColumn<>("Name"));
        setPrivateField(controller, "quantityTableColumn", new TableColumn<>("Quantity"));
        setPrivateField(controller, "stockTableColumn", new TableColumn<>("Stock"));

        ObservableList<Medicine> observableList = FXCollections.observableArrayList();
        setPrivateField(controller, "medicineObservableList", observableList);
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
