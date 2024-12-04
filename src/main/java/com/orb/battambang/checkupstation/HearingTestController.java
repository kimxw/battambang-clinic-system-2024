package com.orb.battambang.checkupstation;

import com.orb.battambang.util.Labels;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.MiniQueueManager;
import com.orb.battambang.util.Tag;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class HearingTestController extends CheckupMenuController implements Initializable {

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
    private Button searchButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;
    @FXML
    private RadioButton problemsYesRadioButton;
    @FXML
    private RadioButton problemsNoRadioButton;
    @FXML
    private RadioButton fluYesRadioButton;
    @FXML
    private RadioButton fluNoRadioButton;
    @FXML
    private TextArea additionalNotesTextArea;
    @FXML
    private Label warningLabel;
    @FXML
    private Label deferLabel;

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
                menuAdminButton, menuLogoutButton, menuUserButton, menuLocationButton, connectionImageView, connectionStatus);

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
                    clearRecordFields();
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

        // Create a ToggleGroup
        ToggleGroup problemsGroup = new ToggleGroup();
        ToggleGroup fluGroup = new ToggleGroup();

        // Add the radio buttons to the group
        problemsYesRadioButton.setToggleGroup(problemsGroup);
        problemsNoRadioButton.setToggleGroup(problemsGroup);
        fluYesRadioButton.setToggleGroup(fluGroup);
        fluNoRadioButton.setToggleGroup(fluGroup);

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
            updateParticularsPane(queueNumber);
            particularsPane.setVisible(true);
            displayHearingRecords(queueNumber);
            updatePreToggle(queueNumber);
        }
    }

    private void displayHearingRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM hearingTestTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                boolean hasHearingProblems = resultSet.getBoolean("hearingProblems");
                if (hasHearingProblems) {
                    problemsYesRadioButton.setSelected(true);
                } else {
                    problemsNoRadioButton.setSelected(true);
                }

                boolean hasFluLikeSymptoms = resultSet.getBoolean("fluSymptoms");
                if (hasFluLikeSymptoms) {
                    fluYesRadioButton.setSelected(true);
                } else {
                    fluNoRadioButton.setSelected(true);
                }

                additionalNotesTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearRecordFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearRecordFields();
        }
    }

    private void clearRecordFields() {
        problemsYesRadioButton.setSelected(false);
        problemsNoRadioButton.setSelected(false);
        fluYesRadioButton.setSelected(false);
        fluNoRadioButton.setSelected(false);
        additionalNotesTextArea.setText("");
    }

    @FXML
    private void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addHearingTest(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addHearingTest(int queueNumber) {

        Boolean inputHearingProblems = null;
        if (problemsYesRadioButton.isSelected()) {
            inputHearingProblems = true;
        } else if (problemsNoRadioButton.isSelected()) {
            inputHearingProblems = false;
        } else {
            Labels.showMessageLabel(warningLabel, "All fields except additional notes are compulsory", false);
        }

        Boolean inputFluSymptoms = null;
        if (fluYesRadioButton.isSelected()) {
            inputFluSymptoms = true;
        } else if (fluNoRadioButton.isSelected()) {
            inputFluSymptoms = false;
        } else {
            Labels.showMessageLabel(warningLabel, "All fields except additional notes are compulsory", false);
        }

        String notes = additionalNotesTextArea.getText().isEmpty() ? "" : additionalNotesTextArea.getText();

//            String insertToCreate = "INSERT OR REPLACE INTO hearingTestTable (queueNumber, hearingProblems, fluSymptoms, additionalNotes) VALUES (?, ?, ?)";
        String insertToCreate = "INSERT INTO hearingTestTable (queueNumber, hearingProblems, fluSymptoms, additionalNotes) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "hearingProblems = VALUES(hearingProblems), fluSymptoms = VALUES(fluSymptoms), " +
                "additionalNotes = VALUES(additionalNotes)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertToCreate)) {
            preparedStatement.setInt(1, queueNumber);
            preparedStatement.setBoolean(2, inputHearingProblems);
            preparedStatement.setBoolean(3, inputFluSymptoms);
            preparedStatement.setString(4, notes);

            preparedStatement.executeUpdate();

            String updateStatusQuery = "UPDATE patientQueueTable SET hearingStatus = 'Complete' WHERE queueNumber = ?";
            try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                updateStatusStatement.setInt(1, queueNumber);
                updateStatusStatement.executeUpdate();
            }

            Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
        } catch (SQLException e) {
            System.out.println(e);
            Labels.showMessageLabel(warningLabel, "All fields except additional notes are compulsory", false);
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
    private void deferButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);

        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            String updateStatusQuery = "UPDATE patientQueueTable SET hearingStatus = 'Deferred' WHERE queueNumber = ?";
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
}
