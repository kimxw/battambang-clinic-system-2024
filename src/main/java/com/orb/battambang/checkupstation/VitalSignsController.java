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

public class VitalSignsController extends CheckupMenuController implements Initializable {

    private int initialisingQueueNumber = -1;

    @FXML
    private Label queueSelectLabel;
    @FXML
    private Label queueNoLabel;

    @FXML
    private TextField bloodPressureTextField;
    @FXML
    private TextField temperatureTextField;

    @FXML
    private Label warningLabel;
    @FXML
    private Label deferLabel;
    @FXML
    private Button searchButton;
    @FXML
    private Button updateButton;
    @FXML
    private TextField queueNumberTextField;
    @FXML
    private Pane particularsPane;

    @FXML
    private Pane editBlockPane;

    private List<Tag> tagList = super.tagList;

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

    }

    @FXML
    public void searchButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || !queueNumberTextField.getText().matches("\\d+")) {
            Labels.showMessageLabel(queueSelectLabel, "Input a queue number.", false);
        } else {
            int queueNumber = Integer.parseInt(queueNumberTextField.getText());
            updateParticularsPane(queueNumber);
            particularsPane.setVisible(true);
            displayVitalSigns(queueNumber);
            updatePreToggle(queueNumber);
        }
    }

    private void displayVitalSigns(int queueNumber) {

        String patientQuery = "SELECT * FROM vitalSignsTable WHERE queueNumber = " + queueNumber;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(patientQuery);

            if (resultSet.next()) {
                bloodPressureTextField.setText(resultSet.getString("bloodPressure"));
                temperatureTextField.setText(resultSet.getString("temperature"));
            } else {
                clearFields();
            }
        } catch (SQLException ex) {
            Labels.showMessageLabel(queueSelectLabel, "Error fetching data.", false);
            clearFields();
        }
    }

    private void clearFields() {
        bloodPressureTextField.setText("");
        temperatureTextField.setText("");
    }

    @FXML
    public void updateButtonOnAction(ActionEvent e) {
        if (queueNumberTextField.getText().isEmpty() || queueNoLabel.getText().isEmpty()) {
            Labels.showMessageLabel(queueSelectLabel, "Select a patient", false);
        } else {
            int queueNumber = Integer.parseInt(queueNoLabel.getText());
            addVitalSignsRecord(queueNumber);
            updateParticularsPane(queueNumber);
        }
    }

    private void addVitalSignsRecord(int queueNumber) {
        try {
            String bloodPressureStr = bloodPressureTextField.getText();
            String temperatureStr = temperatureTextField.getText();

            if (bloodPressureStr.isEmpty() || !bloodPressureStr.matches("\\d+")) {
                Labels.showMessageLabel(warningLabel, "Invalid blood pressure format.", false);
                return;
            }

            if (temperatureStr.isEmpty() || !temperatureStr.matches("\\d+(\\.\\d+)?")) {
                Labels.showMessageLabel(warningLabel, "Invalid temperature format.", false);
                return;
            }

            int bloodPressure = Integer.parseInt(bloodPressureStr);
            double temperature = Double.parseDouble(temperatureStr);

            //for mySQL
            String insertOrUpdateQuery = """
                    INSERT INTO vitalSignsTable (queueNumber, bloodPressure, temperature)\s
                    VALUES (?, ?, ?)\s
                    ON DUPLICATE KEY UPDATE\s
                        bloodPressure = VALUES(bloodPressure),\s
                        temperature = VALUES(temperature);
                        """;

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdateQuery)) {

                preparedStatement.setInt(1, queueNumber);
                preparedStatement.setInt(2, bloodPressure);
                preparedStatement.setDouble(3, temperature);

                preparedStatement.executeUpdate();

                String updateStatusQuery = "UPDATE patientQueueTable SET vitalSignsStatus = 'Complete' WHERE queueNumber = ?";
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

            String updateStatusQuery = "UPDATE patientQueueTable SET vitalSignsStatus = 'Deferred' WHERE queueNumber = ?";
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
