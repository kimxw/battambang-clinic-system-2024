package com.orb.battambang.reception;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class PatientSearchController extends DatabaseConnection implements Initializable{

    @FXML
    private TextField queueSearchTextField;
    @FXML
    private TextField nameSearchTextField;
    @FXML
    private TextField phoneNumberSearchTextField;
    @FXML
    private TableView<Patient> patientTableView;
    @FXML
    private TableColumn<Patient, Integer> queuenoTableColumn;
    @FXML
    private TableColumn<Patient, String> nameTableColumn;
    @FXML
    private TableColumn<Patient, Integer> ageTableColumn;
    @FXML
    private TableColumn<Patient, Character> sexTableColumn;
    @FXML
    private TableColumn<Patient, Integer> phoneNumberTableColumn;

    ObservableList<Patient> patientSearchModelObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String patientViewQuery = "SELECT QueueNumber, Name, Age, Sex, PhoneNumber FROM patientQueueTable";

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

                patientSearchModelObservableList.add(new Patient(queueNo, name, age, sex, phoneNumber));
            }

            queuenoTableColumn.setCellValueFactory(new PropertyValueFactory<>("queueNo"));
            nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            ageTableColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
            sexTableColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
            phoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

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
            e.printStackTrace();
        }
    }
}
