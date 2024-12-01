package com.orb.battambang.checkupstation;

import com.orb.battambang.util.Labels;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class HealthEFController extends CheckupMenuController implements Initializable {

    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label queueNoLabel;

    @FXML
    private RadioButton qn1YesRadioButton;
    @FXML
    private RadioButton qn1NoRadioButton;
    @FXML
    private RadioButton qn2YesRadioButton;
    @FXML
    private RadioButton qn2NoRadioButton;
    @FXML
    private TextArea qn3TextArea;

    @FXML
    private Label warningLabel;
    @FXML
    private Label deferLabel;
    @FXML
    private Button updateButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;

    @FXML
    private Pane editBlockPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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


        ToggleGroup qn1Group = new ToggleGroup();
        ToggleGroup qn2Group = new ToggleGroup();

        qn1YesRadioButton.setToggleGroup(qn1Group);
        qn1NoRadioButton.setToggleGroup(qn1Group);

        qn2YesRadioButton.setToggleGroup(qn2Group);
        qn2NoRadioButton.setToggleGroup(qn2Group);

    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(queueNumber);
            particularsPane.setVisible(true);
            displayHEFRecords(queueNumber);
            updatePreToggle(queueNumber);
        }
    }

    private void displayHEFRecords(int queueNumber) {

        String patientQuery = "SELECT * FROM healthEFTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                boolean knowsHEF = resultSet.getBoolean("knowsHEF");
                if (knowsHEF) {
                    qn1YesRadioButton.setSelected(true);
                } else {
                    qn1NoRadioButton.setSelected(true);
                }

                boolean hasHEF = resultSet.getBoolean("hasHEF");
                if (hasHEF) {
                    qn2YesRadioButton.setSelected(true);
                } else {
                    qn2NoRadioButton.setSelected(true);
                }

                qn3TextArea.setText(resultSet.getString("usesHEF"));
            } else {
                clearFields();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearFields();
        }
    }

    private void clearFields() {
        qn1YesRadioButton.setSelected(false);
        qn1NoRadioButton.setSelected(false);
        qn2YesRadioButton.setSelected(false);
        qn2NoRadioButton.setSelected(false);
        qn3TextArea.setText("");
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addHEFRecord(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addHEFRecord(int queueNumber) {
        try {
            boolean knowsHEF = qn1YesRadioButton.isSelected() ? true : qn1NoRadioButton.isSelected() ? false : null;
            boolean hasHEF = qn2YesRadioButton.isSelected() ? true : qn2NoRadioButton.isSelected() ? false : null;
            String usesHEF = qn3TextArea.getText();

            //for mySQL
            String insertOrUpdateQuery = """
                    INSERT INTO healthEFTable (queueNumber, knowsHEF, hasHEF, usesHEF)\s
                    VALUES (?, ?, ?, ?)\s
                    ON DUPLICATE KEY UPDATE\s
                        knowsHEF = VALUES(knowsHEF),\s
                        hasHEF = VALUES(hasHEF),\s
                        usesHEF = VALUES(usesHEF);
                        """;

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdateQuery)) {

                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setBoolean(2, knowsHEF);
                preparedStatement.setBoolean(3, hasHEF);
                preparedStatement.setString(4, usesHEF);

                preparedStatement.executeUpdate();

                String updateStatusQuery = "UPDATE patientQueueTable SET healthEFStatus = 'Complete' WHERE queueNumber = ?";
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
            Labels.showMessageLabel(warningLabel, "Invalid number format.", false);
        } catch (Exception e) {
            Labels.showMessageLabel(warningLabel, "Please check all fields.", false);
            e.printStackTrace();
        }
    }


    @FXML
    private void deferButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);

        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());

            String updateStatusQuery = "UPDATE patientQueueTable SET healthEFStatus = 'Deferred' WHERE queueNumber = ?";
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
