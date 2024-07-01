package com.orb.battambang.reception;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class PatientFilterController implements Initializable{

    @FXML
    private Label messageLabel2;
    @FXML
    private Button switchUserButton;
    @FXML
    private TextField queueSearchTextField;
    @FXML
    private TextField nameSearchTextField;
    @FXML
    private TextField phoneNumberSearchTextField;

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
    private TableColumn<Patient, String> addressTableColumn;

    ObservableList<Patient> patientSearchModelObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String patientViewQuery = "SELECT QueueNumber, Name, Age, Sex, PhoneNumber, Address FROM patientQueueTable";

        try {
            Statement statement = connection.createStatement();
            ResultSet queryOutput = statement.executeQuery(patientViewQuery);

            while (queryOutput.next()) {
                Integer queueNo = queryOutput.getInt("QueueNumber");
                String name = queryOutput.getString("Name");
                Integer age = queryOutput.getInt("Age");
                String sexString = queryOutput.getString("Sex");
                Character sex = sexString != null && !sexString.isEmpty() ? sexString.charAt(0) : null;
                String phoneNumber = queryOutput.getString("PhoneNumber");
                String address = queryOutput.getString("Address");

                patientSearchModelObservableList.add(new Patient(queueNo, name, age, sex, phoneNumber, address));
            }

            queueNoTableColumn.setCellValueFactory(new PropertyValueFactory<>("queueNo"));
            nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            ageTableColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
            sexTableColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
            phoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            addressTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

            patientTableView.setItems(patientSearchModelObservableList);

            FilteredList<Patient> filteredList = new FilteredList<>(patientSearchModelObservableList);

            // Filter by queue number
            queueSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(patientSearchModel -> {
                    String searchQueue = newValue.trim();
                    String searchName = nameSearchTextField.getText().trim().toLowerCase();
                    String searchPhone = phoneNumberSearchTextField.getText().trim();
                    boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                    boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().contains(searchPhone);
                    return matchQueue && matchName && matchPhone;
                });
            });

            // Filter by name
            nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(patientSearchModel -> {
                    String searchQueue = queueSearchTextField.getText().trim();
                    String searchName = newValue.trim().toLowerCase();
                    String searchPhone = phoneNumberSearchTextField.getText().trim();
                    boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                    boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().toString().contains(searchPhone);
                    return matchQueue && matchName && matchPhone;
                });
            });

            // Filter by phone number
            phoneNumberSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(patientSearchModel -> {
                    String searchQueue = queueSearchTextField.getText().trim();
                    String searchName = nameSearchTextField.getText().trim().toLowerCase();
                    String searchPhone = newValue.trim();
                    boolean matchQueue = searchQueue.isEmpty() || patientSearchModel.getQueueNo().toString().contains(searchQueue);
                    boolean matchName = searchName.isEmpty() || patientSearchModel.getName().toLowerCase().contains(searchName);
                    boolean matchPhone = searchPhone.isEmpty() || patientSearchModel.getPhoneNumber().contains(searchPhone);
                    return matchQueue && matchName && matchPhone;
                });
            });

            SortedList<Patient> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(patientTableView.comparatorProperty());
            patientTableView.setItems(sortedList);

        } catch (Exception e) {
            System.out.println(e);
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
            System.out.println(e);
            exc.getCause();
        }
    }

    @FXML
    private void triageButtonOnAction(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("checkup-menu.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception exc) {
            exc.printStackTrace();

        }
    }

    @FXML
    public void newPatientButtonOnAction(ActionEvent e) {
        loadFXML("patient-registration.fxml", e);
    }

    void loadFXML(String fxmlFile, ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception exc) {
            System.out.println(e);
        }
    }
}
