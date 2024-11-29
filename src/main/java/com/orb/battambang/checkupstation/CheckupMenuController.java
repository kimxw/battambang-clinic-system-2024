package com.orb.battambang.checkupstation;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class CheckupMenuController implements Initializable {

    private int initialisingQueueNumber = -1;

    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label queueNoLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label sexLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label status1Label;
    @FXML
    private Label status2Label;
    @FXML
    private Label status3Label;
    @FXML
    private Label status4Label;
    @FXML
    private Label status5Label;
    @FXML
    private Label status6Label;
    @FXML
    private Rectangle status1Rectangle;
    @FXML
    private Rectangle status2Rectangle;
    @FXML
    private Rectangle status3Rectangle;
    @FXML
    private Rectangle status4Rectangle;
    @FXML
    private Rectangle status5Rectangle;
    @FXML
    private Rectangle status6Rectangle;

    @FXML
    protected ToggleButton tbToggleButton;
    @FXML
    protected ToggleButton optometryToggleButton;
    @FXML
    protected ToggleButton hearingToggleButton;
    @FXML
    protected ToggleButton socialToggleButton;
    @FXML
    protected ToggleButton physioToggleButton;


    @FXML
    private Button switchUserButton;
    @FXML
    private Button heightAndWeightButton;
    @FXML
    private Button snellensTestButton;
    @FXML
    private Button hearingTestButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button searchButton;

    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;
    @FXML
    private ListView<Integer> waitingListView;
    @FXML
    private ListView<Integer> inProgressListView;

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
    private Button menuPhysiotherapistButton;
    @FXML
    private Button menuAudiologistButton;
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

    @FXML
    public ImageView connectionImageView;
    @FXML
    public Label connectionStatus;


    protected List<Tag> tagList = new ArrayList<>();
    private FXMLLoader fxmlLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initialiseTags();

        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPhysiotherapistButton, menuAudiologistButton, menuPharmacyButton, menuQueueManagerButton,
                menuAdminButton, menuLogoutButton, menuUserButton, menuLocationButton, connectionImageView, connectionStatus);

        // for waiting list
        // Initialize the waiting list

        MiniQueueManager waitingQueueManager = new MiniQueueManager(waitingListView, "triageWaitingTable");
        MiniQueueManager progressQueueManager = new MiniQueueManager(inProgressListView, "triageProgressTable");

        particularsPane.setVisible(false); // Initially hide the particularsPane

        // Add a listener to the text property of the queueNumberTextField
        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null && !newValue.isEmpty()) {
                    particularsPane.setVisible(false);
                    clearParticularsFields();
                }
            }
        });
    }

    public void postInitializationSetup() {
        if (initialisingQueueNumber == -1) {
            particularsPane.setVisible(false); // Initially hide the particularsPane
        } else {
            queueNumberTextField.setText(String.valueOf(initialisingQueueNumber));
            searchButtonOnAction(new ActionEvent());
        }
    }

    public void setInitialisingQueueNumber(int initialisingQueueNumber) {
        this.initialisingQueueNumber = initialisingQueueNumber;
    }

    private void initialiseTags() {
        Tag Ttag = new Tag(tbToggleButton);
        Tag Otag = new Tag(optometryToggleButton);
        Tag Htag = new Tag(hearingToggleButton);
        Tag Stag = new Tag(socialToggleButton);
        Tag Ptag = new Tag(physioToggleButton);

        tagList.add(Ttag);
        tagList.add(Otag);
        tagList.add(Htag);
        tagList.add(Stag);
        tagList.add(Ptag);
    }

    @FXML
    protected void tagToggleOnAction(ActionEvent e) {
        updatePostToggle(Integer.parseInt(queueNoLabel.getText()));
    }

    protected void updatePreToggle(int queueNumber) {
        boolean tb = false;
        boolean opto = false;
        boolean hearing = false;
        boolean social = false;
        boolean physio = false;

        String updateQuery = "SELECT * FROM patientTagTable WHERE queueNumber = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, queueNumber); // Set the queueNumber parameter

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    tb = resultSet.getBoolean("tag_T");
                    opto = resultSet.getBoolean("tag_O");
                    hearing = resultSet.getBoolean("tag_H");
                    social = resultSet.getBoolean("tag_S");
                    physio = resultSet.getBoolean("tag_P");
                } else {
                    Labels.showMessageLabel(queueSelectLabel, "Unable to fetch tags", false);
                }
            }
        } catch (SQLException e) {
            Labels.showMessageLabel(queueSelectLabel, "Unable to fetch tags", false);
            throw new RuntimeException(e);
        }

        tbToggleButton.setSelected(tb);
        optometryToggleButton.setSelected(opto);
        hearingToggleButton.setSelected(hearing);
        socialToggleButton.setSelected(social);
        physioToggleButton.setSelected(physio);

    }

    private void updatePostToggle(int queueNumber) {
        boolean tb = tbToggleButton.isSelected();
        boolean opto = optometryToggleButton.isSelected();
        boolean hearing = hearingToggleButton.isSelected();
        boolean social = socialToggleButton.isSelected();
        boolean physio = physioToggleButton.isSelected();
        System.out.println(tb);
        System.out.println(opto);
        System.out.println(hearing);
        System.out.println(social);
        System.out.println(physio);

        String updateQuery = "UPDATE patientTagTable SET tag_T = ?, tag_O = ?, tag_H = ?, tag_S = ?, tag_P = ? WHERE queueNumber = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setBoolean(1, tb);
            updateStatement.setBoolean(2, opto);
            updateStatement.setBoolean(3, hearing);
            updateStatement.setBoolean(4, social);
            updateStatement.setBoolean(5, physio);
            updateStatement.setInt(6, queueNumber);

            System.out.println(updateStatement);

            updateStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void editBlockPaneOnMouseClicked(MouseEvent e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a patient first.", ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null); // No header text
        alert.showAndWait();
    }

    void loadFXML(String fxmlFile, ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            if (!queueNumberTextField.getText().isBlank()) {
                // Get the controller instance
                CheckupMenuController controller = fxmlLoader.getController();

                String queueNumberText = queueNumberTextField.getText().trim();
                controller.setInitialisingQueueNumber(Integer.parseInt(queueNumberText));
                controller.postInitializationSetup();
            }

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setResizable(false);
            stage.setScene(scene);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @FXML
    public void heightAndWeightButtonOnAction(ActionEvent e) {
        loadFXML("height-and-weight.fxml", e);
    }

    @FXML
    public void vitalSignsButtonOnAction(ActionEvent e) {
        loadFXML("height-and-weight.fxml", e);
    }

    @FXML
    public void snellensTestButtonOnAction(ActionEvent e) {
        loadFXML("snellens-test.fxml", e);
    }

    @FXML
    public void hearingTestButtonOnAction(ActionEvent e) {
        loadFXML("hearing-test.fxml", e);
    }

    @FXML
    public void headLiceButtonOnAction(ActionEvent e) {
        loadFXML("developmental-checks.fxml", e);
    }

    @FXML
    public void dentalButtonOnAction(ActionEvent e) {
        loadFXML("dental.fxml", e);
    }

    @FXML
    public void historyButtonOnAction(ActionEvent e) {
        loadFXML("history.fxml", e);
    }

    @FXML
    public void onEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchButtonOnAction(new ActionEvent());
        }
    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(queueNumber);
            updatePreToggle(queueNumber);
            particularsPane.setVisible(true);
        }
    }

    public void updateParticularsPane(int queueNumber) {
        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        String tagQuery = "SELECT tagSequence FROM patientTagTable WHERE queueNumber = " + queueNumber;

        try {
            Statement statement = connection.createStatement();

            // Fetch patient details
            ResultSet patientResultSet = statement.executeQuery(patientQuery);
            if (patientResultSet.next()) {
                String name = patientResultSet.getString("name");
                int age = patientResultSet.getInt("age");
                String sex = patientResultSet.getString("sex");
                String phoneNumber = patientResultSet.getString("phoneNumber");

                queueNoLabel.setText(String.valueOf(queueNumber));
                nameLabel.setText(name);
                ageLabel.setText(String.valueOf(age));
                sexLabel.setText(sex);
                phoneNumberLabel.setText(phoneNumber);

                String bmiStatus = patientResultSet.getString("bmiStatus");
                String snellensStatus = patientResultSet.getString("snellensStatus");
                String hearingStatus = patientResultSet.getString("hearingStatus");
                String liceStatus = patientResultSet.getString("liceStatus");
                String dentalStatus = patientResultSet.getString("dentalStatus");
                String historyStatus = patientResultSet.getString("historyStatus");


                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, bmiStatus);
                Rectangles.updateStatusRectangle(status2Rectangle, status2Label, snellensStatus);
                Rectangles.updateStatusRectangle(status3Rectangle, status3Label, hearingStatus);
                Rectangles.updateStatusRectangle(status4Rectangle, status4Label, liceStatus);
                Rectangles.updateStatusRectangle(status5Rectangle, status5Label, dentalStatus);
                Rectangles.updateStatusRectangle(status6Rectangle, status6Label, historyStatus);

            } else {
                nameLabel.setText("");
                ageLabel.setText("");
                sexLabel.setText("");
                phoneNumberLabel.setText("");
                Labels.showMessageLabel(queueSelectLabel, "Patient does not exist", false);
                Rectangles.updateStatusRectangle(status1Rectangle, status1Label, "Not found");
                Rectangles.updateStatusRectangle(status2Rectangle, status2Label, "Not found");
                Rectangles.updateStatusRectangle(status3Rectangle, status3Label, "Not found");
                Rectangles.updateStatusRectangle(status4Rectangle, status4Label, "Not found");
                Rectangles.updateStatusRectangle(status5Rectangle, status5Label, "Not found");
                Rectangles.updateStatusRectangle(status6Rectangle, status6Label, "Not found");

            }

            patientResultSet.close();

            ResultSet tagResultSet = statement.executeQuery(tagQuery);
            String tagSequence = "";
            if (tagResultSet.next()) {
               tagSequence = tagResultSet.getString("tagSequence");

            }

            for(Tag t : tagList) {
                t.updateTag(tagSequence);
            }

            tagResultSet.close();

            // Close the statement
            statement.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Database error occurred", false);
        }
    }

    private void clearParticularsFields() {
        queueNoLabel.setText("");
        nameLabel.setText("");
        ageLabel.setText("");
        sexLabel.setText("");
        phoneNumberLabel.setText("");
    }

    @FXML
    private void addButtonOnAction() {
        Integer selectedPatient = waitingListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            if (!waitingListView.getItems().isEmpty()) {
                selectedPatient = waitingListView.getItems().get(0);
            }
        }

        if (selectedPatient != null) {
            movePatientToInProgress(selectedPatient);
        }
    }

    private void movePatientToInProgress(Integer queueNumber) {
        String nameFromWaitingListQuery = "SELECT name FROM triageWaitingTable WHERE queueNumber = ?";
        String deleteFromWaitingListQuery = "DELETE FROM triageWaitingTable WHERE queueNumber = ?";
        String insertIntoProgressListQuery = "INSERT INTO triageProgressTable (queueNumber, name) VALUES (?, ?)";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromWaitingListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoProgressListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                 name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
            insertStatement.setString(2, name);
            insertStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            waitingListView.getItems().remove(queueNumber);
            inProgressListView.getItems().add(queueNumber);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Roll back transaction if any error occurs
                }
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    @FXML
    private void sendButtonOnAction() {
        Integer selectedPatient = inProgressListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            if (!inProgressListView.getItems().isEmpty()) {
                selectedPatient = inProgressListView.getItems().get(0);
            }
        }

        if (selectedPatient != null) {
            movePatientToEducation(selectedPatient);
        }
    }

    private void movePatientToEducation(Integer queueNumber) {
        String nameFromWaitingListQuery = "SELECT name FROM triageProgressTable WHERE queueNumber = ?";
        String deleteFromProgressListQuery = "DELETE FROM triageProgressTable WHERE queueNumber = ?";
        String insertIntoNextListQuery = "INSERT INTO educationWaitingTable (queueNumber, name) VALUES (?, ?)";

        try (PreparedStatement nameStatement = connection.prepareStatement(nameFromWaitingListQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteFromProgressListQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertIntoNextListQuery)) {

            // Start a transaction
            connection.setAutoCommit(false);

            //Get name from waiting list
            String name = "";
            nameStatement.setInt(1, queueNumber);
            ResultSet rs = nameStatement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }

            // Delete from waiting list
            deleteStatement.setInt(1, queueNumber);
            deleteStatement.executeUpdate();

            // Insert into progress list
            insertStatement.setInt(1, queueNumber);
            insertStatement.setString(2, name);
            insertStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Update the ListViews
            inProgressListView.getItems().remove(queueNumber);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Roll back transaction if any error occurs
                }
            } catch (SQLException rollbackEx) {
                System.out.println(rollbackEx);
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }
}
