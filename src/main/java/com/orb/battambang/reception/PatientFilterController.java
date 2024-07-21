package com.orb.battambang.reception;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.MenuGallery;
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
import javafx.scene.layout.AnchorPane;
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
    private Button logoutButton;
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

    @FXML
    private AnchorPane sliderAnchorPane;
    @FXML
    private Label menuLabel;
    @FXML
    private Label menuBackLabel;
    @FXML
    private Button menuHomeButton;
    @FXML
    private Button menuReceptionButton;
    @FXML
    private Button menuTriageButton;
    @FXML
    private Button menuEducationButton;
    @FXML
    private Button menuConsultationButton;
    @FXML
    private Button menuPharmacyButton;
    @FXML
    private Button menuQueueManagerButton;
    @FXML
    private Button menuAdminButton;
    @FXML
    private Button menuLogoutButton;
    @FXML
    private Button menuUserButton;
    @FXML
    private Button menuLocationButton;


    ObservableList<Patient> patientSearchModelObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);


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
