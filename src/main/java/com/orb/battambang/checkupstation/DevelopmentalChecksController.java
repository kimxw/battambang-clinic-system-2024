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

public class DevelopmentalChecksController extends CheckupMenuController implements Initializable {

    private int initialisingQueueNumber = -1;

    @FXML
    private Pane particularsPane;
    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label queueNoLabel;

    @FXML
    private TextField queueNumberTextField;
    @FXML
    private RadioButton yesRadioButton;
    @FXML
    private RadioButton noRadioButton;
    @FXML
    private TextArea additionalHeadLiceNotesTextArea;
    @FXML
    private TextArea additionalDentalNotesTextArea;
    @FXML
    private TextField angleTextField;

    @FXML
    private Label headLiceWarningLabel;
    @FXML
    private Label dentalWarningLabel;
    @FXML
    private Label scoliosisWarningLabel;

    @FXML
    private Label headLiceDeferLabel;
    @FXML
    private Label dentalDeferLabel;
    @FXML
    private Label scoliosisDeferLabel;


    @FXML
    private Pane editBlockPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //sets up particulars pane, tags, menu gallery and mini queue view
        super.initialize(location, resources);


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

        // ToggleGroup for head lice
        ToggleGroup group = new ToggleGroup();

        // Add the radio buttons to the group
        yesRadioButton.setToggleGroup(group);
        noRadioButton.setToggleGroup(group);

    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(queueNumber);
            particularsPane.setVisible(true);
            displayHeadLiceRecords(queueNumber);
            displayDentalRecords(queueNumber);
            displayScoliosisRecords(queueNumber);
            updatePreToggle(queueNumber);
        }
    }

    private void displayHeadLiceRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM headLiceTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                boolean hadHeadLice = resultSet.getBoolean("headLice");
                if (hadHeadLice) {
                    yesRadioButton.setSelected(true);
                } else {
                    noRadioButton.setSelected(true);
                }
                additionalHeadLiceNotesTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearHeadLiceFields();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearAllRecordFields();
        }
    }

    private void displayDentalRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM dentalTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                additionalDentalNotesTextArea.setText(resultSet.getString("additionalNotes"));
            } else {
                clearDentalFields();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearAllRecordFields();
        }
    }

    private void displayScoliosisRecords(int queueNumber) {
        String patientQuery = "SELECT * FROM scoliosisTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                angleTextField.setText(resultSet.getString("angleOfTruncalRotation"));
            } else {
                clearScoliosisFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearAllRecordFields();
        }
    }

    private void clearAllRecordFields() {
        clearHeadLiceFields();
        clearDentalFields();
        clearScoliosisFields();
    }

    private void clearHeadLiceFields() {
        yesRadioButton.setSelected(false);
        noRadioButton.setSelected(false);
        additionalHeadLiceNotesTextArea.setText("");
    }

    private void clearDentalFields() {
        additionalDentalNotesTextArea.setText("");
    }

    private void clearScoliosisFields() {
        angleTextField.setText("");
    }

    @FXML
    private void updateHeadLiceButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addHeadLiceRecord(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    @FXML
    private void updateDentalButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addDentalRecord(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    @FXML
    private void updateScoliosisButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addScoliosisRecord(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addHeadLiceRecord(int queueNumber) {
        try {

            Boolean headLice = yesRadioButton.isSelected() ? true : noRadioButton.isSelected() ? false : null;
            String notes = additionalHeadLiceNotesTextArea.getText();

//            String insertToCreate = "INSERT OR REPLACE INTO headLiceTable (queueNumber, headLice, additionalNotes) VALUES (?, ?, ?)";
            String insertToCreate = "INSERT INTO headLiceTable (queueNumber, headLice, additionalNotes) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE headLice = VALUES(headLice), additionalNotes = VALUES(additionalNotes)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertToCreate)) {
                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setBoolean(2, headLice);
                preparedStatement.setString(3, notes);

                preparedStatement.executeUpdate();

                String updateStatusQuery = "UPDATE patientQueueTable SET liceStatus = 'Complete' WHERE queueNumber = ?";
                try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                    updateStatusStatement.setInt(1, queueNumber);
                    updateStatusStatement.executeUpdate();
                }

                Labels.showMessageLabel(headLiceWarningLabel, "Updated Q" + queueNumber + " successfully", true);
            } catch (SQLException e1) {
                Labels.showMessageLabel(headLiceWarningLabel, "Please check all fields.", false);
            }

        } catch (Exception e2) {
            Labels.showMessageLabel(headLiceWarningLabel, "Please check all fields.", false);
        }
    }

    private void addDentalRecord(int queueNumber) {
        try {
            String notes = additionalDentalNotesTextArea.getText();

            //String insertToCreate = "INSERT OR REPLACE INTO dentalTable (queueNumber, additionalNotes) VALUES (?, ?)";
            String insertToCreate = "INSERT INTO dentalTable (queueNumber, additionalNotes) VALUES (?, ?) ON DUPLICATE KEY UPDATE additionalNotes = VALUES(additionalNotes)";


            try (PreparedStatement preparedStatement = connection.prepareStatement(insertToCreate)) {
                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setString(2, notes);

                preparedStatement.executeUpdate();

                String updateStatusQuery = "UPDATE patientQueueTable SET dentalStatus = 'Complete' WHERE queueNumber = ?";
                try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                    updateStatusStatement.setInt(1, queueNumber);
                    updateStatusStatement.executeUpdate();
                }

                Labels.showMessageLabel(dentalWarningLabel, "Updated Q" + queueNumber + " successfully", true);
            } catch (SQLException e1) {
                Labels.showMessageLabel(dentalWarningLabel, "Please check all fields.", false);
            }

        } catch (Exception e2) {
            Labels.showMessageLabel(dentalWarningLabel, "Please check all fields.", false);
        }
    }

    private void addScoliosisRecord(int queueNumber) {
        try {
            String angleStr = angleTextField.getText();

            if (angleStr.isEmpty() || !angleStr.matches("\\d+(\\.\\d+)?")) {
                Labels.showMessageLabel(scoliosisWarningLabel, "Invalid temperature format.", false);
                return;
            }

            double angle = Double.parseDouble(angleStr);

            //for mySQL
            String insertOrUpdateQuery = """
                    INSERT INTO scoliosisTable (queueNumber, angleOfTruncalRotation)\s
                    VALUES (?, ?)\s
                    ON DUPLICATE KEY UPDATE\s
                        angleOfTruncalRotation = VALUES(angleOfTruncalRotation);
                        """;

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdateQuery)) {

                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setDouble(2, angle);

                preparedStatement.executeUpdate();

                String updateStatusQuery = "UPDATE patientQueueTable SET scoliosisStatus = 'Complete' WHERE queueNumber = ?";
                try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                    updateStatusStatement.setInt(1, queueNumber);
                    updateStatusStatement.executeUpdate();
                }

                Labels.showMessageLabel(scoliosisWarningLabel, "Updated Q" + queueNumber + " successfully", true);

            } catch (SQLException e) {
                Labels.showMessageLabel(scoliosisWarningLabel, "Please check all fields.", false);
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            Labels.showMessageLabel(scoliosisWarningLabel, "Invalid number format.", false);
        }
    }

    @FXML
    private void deferHeadLiceButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);

        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            String updateStatusQuery = "UPDATE patientQueueTable SET liceStatus = 'Deferred' WHERE queueNumber = ?";
            try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                updateStatusStatement.setInt(1, queueNumber);
                updateStatusStatement.executeUpdate();
                Labels.showMessageLabel(headLiceDeferLabel, "Defered Q" + queueNumber + " successfully", "blue");
            } catch (SQLException e1) {
                Labels.showMessageLabel(headLiceDeferLabel, "Unable to defer Q" + queueNumber, false);
            }
            updateParticularsPane(queueNumber);
        }
    }

    @FXML
    private void deferDentalButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);

        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            String updateStatusQuery = "UPDATE patientQueueTable SET dentalStatus = 'Deferred' WHERE queueNumber = ?";
            try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                updateStatusStatement.setInt(1, queueNumber);
                updateStatusStatement.executeUpdate();
                Labels.showMessageLabel(dentalDeferLabel, "Defered Q" + queueNumber + " successfully", "blue");
            } catch (SQLException e1) {
                Labels.showMessageLabel(dentalDeferLabel, "Unable to defer Q" + queueNumber, false);
            }
            updateParticularsPane(queueNumber);
        }
    }

    @FXML
    private void deferScoliosisButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);

        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            String updateStatusQuery = "UPDATE patientQueueTable SET scoliosisStatus = 'Deferred' WHERE queueNumber = ?";
            try (PreparedStatement updateStatusStatement = connection.prepareStatement(updateStatusQuery)) {
                updateStatusStatement.setInt(1, queueNumber);
                updateStatusStatement.executeUpdate();
                Labels.showMessageLabel(scoliosisDeferLabel, "Defered Q" + queueNumber + " successfully", "blue");
            } catch (SQLException e1) {
                Labels.showMessageLabel(scoliosisDeferLabel, "Unable to defer Q" + queueNumber, false);
            }
            updateParticularsPane(queueNumber);
        }
    }

}
