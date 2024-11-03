package com.orb.battambang.checkupstation;

import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.MiniQueueManager;
import com.orb.battambang.util.Tag;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class HistoryController extends CheckupMenuController implements Initializable {

    private int initialisingQueueNumber = -1;

    @FXML
    private Label queueSelectLabel;
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
    private Label status6Label;
    @FXML
    private Rectangle status1Rectangle;
    @FXML
    private Rectangle status2Rectangle;
    @FXML
    private Rectangle status3Rectangle;
    @FXML
    private Rectangle status6Rectangle;

    @FXML
    private Rectangle TtagRectangle;
    @FXML
    private Rectangle OtagRectangle;
    @FXML
    private Rectangle HtagRectangle;
    @FXML
    private Rectangle StagRectangle;
    @FXML
    private Rectangle PtagRectangle;

    @FXML
    private Label TtagLabel;
    @FXML
    private Label OtagLabel;
    @FXML
    private Label HtagLabel;
    @FXML
    private Label StagLabel;
    @FXML
    private Label PtagLabel;

    @FXML
    private Button editButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;

    @FXML
    private Label warningLabel;
    @FXML
    private Label editWarningLabel;
    @FXML
    private Label deferLabel;
    @FXML
    private Label queueNoLabel;
    @FXML
    private TextField systemTextField;
    @FXML
    private TextArea PSTextArea;
    @FXML
    private TextField durationTextField;
    @FXML
    private TextArea drugAllergiesTextArea;
    @FXML
    private TextArea additionalNotesTextArea;

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
    private Pane editBlockPane;

    private List<Tag> tagList = super.tagList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseTags();
        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPhysiotherapistButton, menuAudiologistButton, menuPharmacyButton, menuQueueManagerButton,
                menuAdminButton, menuLogoutButton, menuUserButton, menuLocationButton);

        // for waiting list
        // Initialize the waiting list
        MiniQueueManager waitingQueueManager = new MiniQueueManager(waitingListView, "triageWaitingTable");
        MiniQueueManager progressQueueManager = new MiniQueueManager(inProgressListView, "triageProgressTable");

        // Add a listener to the text property of the queueNumberTextField
        queueNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Hide particularsPane when typing starts
                if (newValue != null && !newValue.isEmpty()) {
                    particularsPane.setVisible(false);
                    clearParticularsFields();
                    clearFields();
                }
            }
        });

        // EDIT BLOCK PANE, QUEUENOLABEL LISTENER
        queueNoLabel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Check if the new value is empty
                if (newValue == null || newValue.trim().isEmpty()) {
                    // Show the pane if the text is empty
                    editBlockPane.setVisible(true);
                } else {
                    // Hide the pane if the text is not empty
                    editBlockPane.setVisible(false);
                }
            }
        });


        particularsPane.setVisible(false);
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
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(Integer.parseInt(queueNumberTextField.getText()));
            particularsPane.setVisible(true);

            String patientQuery = "SELECT * FROM historyTable WHERE queueNumber = " + queueNumber;
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(patientQuery);

                if (resultSet.next()) {
                    systemTextField.setText(resultSet.getString("bodySystem"));
                    PSTextArea.setText(resultSet.getString("PS"));
                    durationTextField.setText(resultSet.getString("duration"));
                    drugAllergiesTextArea.setText(resultSet.getString("drugAllergies"));
                    additionalNotesTextArea.setText(resultSet.getString("additionalNotes"));
                } else {
                    clearFields();
                }
            } catch (SQLException ex) {
                Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
                //System.out.println(ex);
                clearFields();
            }
            updatePreToggle(queueNumber);
        }
    }

    private void clearFields() {
        systemTextField.setText("");
        PSTextArea.setText("");
        durationTextField.setText("");
        drugAllergiesTextArea.setText("");
        additionalNotesTextArea.setText("");
    }


    @FXML
    private void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            addSymptoms(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addSymptoms(int queueNumber) {
        try {
            String system = systemTextField.getText().isEmpty() ? null : systemTextField.getText();
            String PS = PSTextArea.getText().isEmpty() ? null : PSTextArea.getText();
            String duration = durationTextField.getText().isEmpty() ? null : durationTextField.getText();
            String allergies = drugAllergiesTextArea.getText().isEmpty() ? null : drugAllergiesTextArea.getText();
            String additionalNotes = additionalNotesTextArea.getText().isEmpty() ? null : additionalNotesTextArea.getText();

            // Check if the row exists
            String checkExistQuery = "SELECT COUNT(*) FROM historyTable WHERE queueNumber = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkExistQuery)) {
                checkStatement.setInt(1, queueNumber);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    // If row exists, update the specific columns
                    String updateQuery = "UPDATE historyTable SET bodySystem = ?, PS = ?, duration = ?, drugAllergies = ?, additionalNotes = ? WHERE queueNumber = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, system);
                        updateStatement.setString(2, PS);
                        updateStatement.setString(3, duration);
                        updateStatement.setString(4, allergies);
                        updateStatement.setString(5, additionalNotes);
                        updateStatement.setInt(6, queueNumber);

                        updateStatement.executeUpdate();
                    }
                } else {
                    // If row doesn't exist, insert a new row
                    String insertQuery = "INSERT INTO historyTable (queueNumber, bodySystem, PS, duration, drugAllergies, additionalNotes) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setInt(1, queueNumber);
                        insertStatement.setString(2, system);
                        insertStatement.setString(3, PS);
                        insertStatement.setString(4, duration);
                        insertStatement.setString(5, allergies);
                        insertStatement.setString(6, additionalNotes);

                        insertStatement.executeUpdate();
                    }

                }

                String updateStatusQuery = "UPDATE patientQueueTable SET historyStatus = 'Complete' WHERE queueNumber = ?";
                try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                    updateStatusStatement.setInt(1, queueNumber);
                    updateStatusStatement.executeUpdate();
                } catch (Exception ex1) {
                    System.out.println(ex1);
                    //Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Select a target queue", "#bf1b15", "/icons/cross.png");
                }
                Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
            }
        } catch (Exception exc2) {
            Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
        }
    }

    @FXML
    private void deferButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);

        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            String updateStatusQuery = "UPDATE patientQueueTable SET historyStatus = 'Deferred' WHERE queueNumber = ?";
            try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                updateStatusStatement.setInt(1, queueNumber);
                updateStatusStatement.executeUpdate();
                Labels.showMessageLabel(deferLabel, "Defered Q" + queueNumber + " successfully", "blue");
            } catch (SQLException e1) {
                Labels.showMessageLabel(deferLabel, "Unable to defer Q" + queueNumber, false);
            }
            updateParticularsPane(queueNumber);
        }
    }

    void loadFXMLInNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            Stage currentStage = (Stage) editButton.getScene().getWindow();
            stage.initOwner(currentStage);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void editButtonOnAction(ActionEvent e) {
        try {
            EditHistoryController.queueNumber = Integer.parseInt(queueNoLabel.getText());
            loadFXMLInNewWindow("edit-history.fxml", "Edit history");
        } catch (Exception exc) {
            Labels.showMessageLabel(editWarningLabel, "Select a patient.", false);
        }

    }

    private void clearParticularsFields() {
        queueNoLabel.setText("");
        nameLabel.setText("");
        ageLabel.setText("");
        sexLabel.setText("");
        phoneNumberLabel.setText("");
    }

}
