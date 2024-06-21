package com.orb.battambang.reception;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;
import com.orb.battambang.connection.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PatientRegistrationController extends DatabaseConnection implements Initializable {

    @FXML
    private Label messageLabel1;
    @FXML
    private Label messageLabel2;
    @FXML
    private Label messageLabel3;
    @FXML
    private Button switchUserButton;
    @FXML
    private TextField inputnameTextField;
    @FXML
    private TextField inputageTextField;
    @FXML
    private TextField inputphonenumberTextField;
    @FXML
    private TableView<Patient> patientTableView;
    @FXML
    private TableColumn<Patient, Integer> queueNoTableColumn;
    @FXML
    private TableColumn<Patient, String> nameTableColumn;
    @FXML
    private TableColumn<Patient, Integer> ageTableColumn;
    @FXML
    private TableColumn<Patient, Character> sexTableColumn;
    @FXML
    private TableColumn<Patient, String> phoneNumberTableColumn;
    @FXML
    private ChoiceBox<Character> inputSexChoiceBox;
    private final Character[] choiceBoxOptions = new Character[] {'M', 'F'};

    ObservableList<Patient> patientObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputSexChoiceBox.getItems().addAll(choiceBoxOptions);

        // Set cell value factories
        queueNoTableColumn.setCellValueFactory(new PropertyValueFactory<>("queueNo"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageTableColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        sexTableColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        phoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        String patientViewQuery = "SELECT queueNumber, name, age, sex, phoneNumber FROM patientQueueTable;";

        try (
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(patientViewQuery)) {

            while (resultSet.next()) {
                Integer queueNo = resultSet.getInt("QueueNumber");
                String name = resultSet.getString("Name");
                Integer age = resultSet.getInt("Age");
                String sexString = resultSet.getString("Sex");
                Character sex = !sexString.isEmpty() ? sexString.charAt(0) : null;
                String phoneNumber = resultSet.getString("PhoneNumber");

                patientObservableList.add(new Patient(queueNo, name, age, sex, phoneNumber));
            }

            // Set items to the TableView
            patientTableView.setItems(patientObservableList);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @FXML
    public void switchUserButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);

            newUserStage.setTitle("Login");
            newUserStage.setScene(scene);
            Stage stage = (Stage) switchUserButton.getScene().getWindow();
            stage.close();
            newUserStage.show();
        } catch (Exception exc) {
            exc.printStackTrace();
            exc.getCause();
        }
    }

    @FXML
    public void addButtonOnAction(ActionEvent e) {
        String inputName = inputnameTextField.getText();
        String inputAge = inputageTextField.getText();
        String inputPhoneNumber = inputphonenumberTextField.getText();
        if (inputPhoneNumber.isEmpty()) {
            inputPhoneNumber = "";
        }
        Character inputSex = inputSexChoiceBox.getValue();

        String insertFields = "INSERT INTO patientQueueTable(name, age, sex, phoneNumber, bmiStatus, snellensStatus, hearingStatus, liceStatus, dentalStatus, historyStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertFields, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, inputName);
            statement.setInt(2, Integer.parseInt(inputAge));
            statement.setString(3, String.valueOf(inputSex));
            statement.setString(4, inputPhoneNumber);
            statement.setString(5, "Incomplete");
            statement.setString(6, "Incomplete");
            statement.setString(7, "Incomplete");
            statement.setString(8, "Incomplete");
            statement.setString(9, "Incomplete");
            statement.setString(10, "Incomplete");

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 1) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int queueNo = generatedKeys.getInt(1);
                    Labels.showMessageLabel(messageLabel1, "Patient added successfully.", true);
                    // Update TableView if insertion was successful
                    patientObservableList.add(new Patient(queueNo, inputName, Integer.parseInt(inputAge), inputSex, inputPhoneNumber));
                    patientTableView.setItems(patientObservableList);
                }
            }
            // Clear the input fields
            inputnameTextField.setText("");
            inputageTextField.setText("");
            inputphonenumberTextField.setText("");
            inputSexChoiceBox.getSelectionModel().clearSelection(); // Clear the selected value in the choice box
        } catch (Exception exc) {
            Labels.showMessageLabel(messageLabel1, "Invalid fields.", false);
        }

    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("patient-search.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception exc) {
            Labels.showMessageLabel(messageLabel2, "Unexpected error.", false);
        }
    }

    @FXML
    public void deleteButtonOnAction(ActionEvent e) {
        // Get the selected item from the TableView
        Patient selectedItem = patientTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            patientObservableList.remove(selectedItem);

            try {
                String deleteQuery = "DELETE FROM patientQueueTable WHERE queueNumber = " + selectedItem.getQueueNo();
                Statement statement = connection.createStatement();
                statement.executeUpdate(deleteQuery);
                Labels.showMessageLabel(messageLabel3, "Patient deleted successfully.", true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Labels.showMessageLabel(messageLabel3, "Please select a row.", false);
        }
    }
}