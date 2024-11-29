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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class HeightAndWeightController extends CheckupMenuController implements Initializable {

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
    private TextField heightTextField;
    @FXML
    private TextField weightTextField;
    @FXML
    private TextArea additionalNotesTextArea;
    @FXML
    private Label bmiLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label warningLabel;
    @FXML
    private Label deferLabel;
    @FXML
    private Rectangle categoryRectangle;
    @FXML
    private Button searchButton;
    @FXML
    private Button updateButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;

    @FXML
    private ListView<Integer> waitingListView;
//    private ObservableList<Integer> waitingList;
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
                    clearFields();
                }
            }
        });

        particularsPane.setVisible(false);

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
            displayHeightAndWeight(queueNumber);
            updatePreToggle(queueNumber);
        }
    }

    private void displayHeightAndWeight(int queueNumber) {

        String patientQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                weightTextField.setText(resultSet.getString("weight"));
                heightTextField.setText(resultSet.getString("height"));
                additionalNotesTextArea.setText(resultSet.getString("additionalNotes"));
                bmiButtonOnAction(new ActionEvent());
            } else {
                clearFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearFields();
        }
    }

    private void clearFields() {
        weightTextField.setText("");
        heightTextField.setText("");
        additionalNotesTextArea.setText("");
        bmiLabel.setText("");
        categoryLabel.setText("");
        categoryRectangle.setStyle("-fx-fill: #dddddd;");
    }


    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addHeightAndWeight(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addHeightAndWeight(int queueNumber) {
        try {
            String heightStr = heightTextField.getText();
            String weightStr = weightTextField.getText();
            String notes = additionalNotesTextArea.getText();

            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            double bmi = Double.parseDouble(bmiLabel.getText());
            String bmiCategory = categoryLabel.getText();

            //for sqlite
            //String insertOrUpdateQuery = "INSERT OR REPLACE INTO heightAndWeightTable(queueNumber, height, weight, bmi, bmiCategory, additionalNotes) VALUES (?, ?, ?, ?, ?, ?)";

            //for mySQL
            String insertOrUpdateQuery = "INSERT INTO heightAndWeightTable (queueNumber, height, weight, bmi, bmiCategory, additionalNotes) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE height = VALUES(height), weight = VALUES(weight), bmi = VALUES(bmi), bmiCategory = VALUES(bmiCategory), additionalNotes = VALUES(additionalNotes)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdateQuery)) {

                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setDouble(2, height);
                preparedStatement.setDouble(3, weight);
                preparedStatement.setDouble(4, bmi);
                preparedStatement.setString(5, bmiCategory);
                preparedStatement.setString(6, notes);

                preparedStatement.executeUpdate();

                String updateStatusQuery = "UPDATE patientQueueTable SET bmiStatus = 'Complete' WHERE queueNumber = ?";
                try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                    updateStatusStatement.setInt(1, queueNumber);
                    updateStatusStatement.executeUpdate();
                }

                Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);

            } catch (SQLException e) {
                Labels.showMessageLabel(warningLabel, "Database error.", false);
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            Labels.showMessageLabel(warningLabel, "Invalid number format. Please check height and weight fields.", false);
        } catch (Exception e) {
            Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
            e.printStackTrace();
        }
    }

    @FXML
    private void bmiButtonOnAction(ActionEvent e) {
        String height = heightTextField.getText();
        String weight = weightTextField.getText();
        if (height.isEmpty() || weight.isEmpty()) {
            Labels.showMessageLabel(warningLabel, "Input height and weight.", false);
        } else {
            double bmi = BMICalculator.calculateBMI(Double.parseDouble(weight), Double.parseDouble(height));

            String ageSexQuery = "SELECT age, sex FROM patientQueueTable WHERE queueNumber = " + queueNoLabel.getText();
            int age = 0;
            String sex = "";

            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(ageSexQuery);
                if (resultSet.next()) {
                    age = resultSet.getInt("age");
                    sex = resultSet.getString("sex");
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
                Labels.showMessageLabel(warningLabel, "Error fetching patient data.", false);
                return;
            }

            String bmiCategory = BMICalculator.determineBMICategory(bmi, age, sex);
            if (bmiCategory.equals("Underweight")) {
                categoryRectangle.setStyle("-fx-fill: #429ebd;");
            } else if (bmiCategory.equals("Healthy Weight")) {
                categoryRectangle.setStyle("-fx-fill: #94b447;");
            } else if (bmiCategory.equals("Overweight")) {
                categoryRectangle.setStyle("-fx-fill: #cf6024;");
            } else if (bmiCategory.equals("Obese")) {
                categoryRectangle.setStyle("-fx-fill: #c4281c;");
            } else {
                categoryRectangle.setStyle("-fx-fill: #6b6b6b;");
            }
            bmiLabel.setText("" + bmi);
            categoryLabel.setText(bmiCategory);
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

            String updateStatusQuery = "UPDATE patientQueueTable SET bmiStatus = 'Deferred' WHERE queueNumber = ?";
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
