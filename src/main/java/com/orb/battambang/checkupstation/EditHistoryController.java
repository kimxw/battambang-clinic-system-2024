package com.orb.battambang.checkupstation;

import com.orb.battambang.util.Labels;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static com.orb.battambang.connection.DatabaseConnection.connection;

public class EditHistoryController extends CheckupMenuController implements Initializable {

    protected static int queueNumber = -1;

    @FXML
    private Label warningLabel;
    @FXML
    private Button saveAndExitButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextArea HPITextArea;
    @FXML
    private TextArea DHTextArea;
    @FXML
    private TextArea PHTextArea;
    @FXML
    private TextArea SHTextArea;
    @FXML
    private TextArea FHTextArea;
    @FXML
    private TextArea SRTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (queueNumber == -1) {
            Labels.showMessageLabel(warningLabel, "Queue number not set", false);
        } else {
            String patientQuery = "SELECT * FROM historyTable WHERE queueNumber = " + queueNumber;
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(patientQuery);

                if (resultSet.next()) {
                    HPITextArea.setText(resultSet.getString("HPI"));
                    DHTextArea.setText(resultSet.getString("DH"));
                    PHTextArea.setText(resultSet.getString("PH"));
                    SHTextArea.setText(resultSet.getString("SH"));
                    FHTextArea.setText(resultSet.getString("FH"));
                    SRTextArea.setText(resultSet.getString("SR"));
                } else {
                    clearFields();
                }
            } catch (SQLException exc) {
                Labels.showMessageLabel(warningLabel, "Error fetching data.", false);
                clearFields();
            }
        }
    }

    private void clearFields() {
        HPITextArea.setText("");
        DHTextArea.setText("");
        PHTextArea.setText("");
        SHTextArea.setText("");
        FHTextArea.setText("");
        SRTextArea.setText("");
    }

    @FXML
    private void saveAndExitButtonOnAction(ActionEvent e) {
        try {
            if (queueNumber == -1) {
                Labels.showMessageLabel(warningLabel, "Q" + queueNumber + " save failed.", false);
            } else {
                addHistory(queueNumber);
            }
            Stage stage = (Stage) saveAndExitButton.getScene().getWindow();
            EditHistoryController.queueNumber = -1;
            stage.close();
        } catch (Exception exc) {
            Labels.showMessageLabel(warningLabel, "Q" + queueNumber + " save failed.", false);
        }
    }

    private void addHistory(int queueNumber) throws SQLException {
        try {
            String HPI = HPITextArea.getText();
            String DH = DHTextArea.getText();
            String PH = PHTextArea.getText();
            String SH = SHTextArea.getText();
            String FH = FHTextArea.getText();
            String SR = SRTextArea.getText();

            // Check if the row exists
            String checkExistQuery = "SELECT COUNT(*) FROM historyTable WHERE queueNumber = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkExistQuery)) {
                checkStatement.setInt(1, queueNumber);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    // If row exists, update the specific columns
                    String updateQuery = "UPDATE historyTable SET HPI = ?, DH = ?, PH = ?, SH = ?, FH = ?, SR = ? WHERE queueNumber = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, HPI);
                        updateStatement.setString(2, DH);
                        updateStatement.setString(3, PH);
                        updateStatement.setString(4, SH);
                        updateStatement.setString(5, FH);
                        updateStatement.setString(6, SR);
                        updateStatement.setInt(7, queueNumber);

                        updateStatement.executeUpdate();
                        Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
                    }
                } else {
                    // If row doesn't exist, insert a new row
                    String insertQuery = "INSERT INTO historyTable (queueNumber, HPI, DH, PH, SH, FH, SR) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setInt(1, queueNumber);
                        insertStatement.setString(2, HPI);
                        insertStatement.setString(3, DH);
                        insertStatement.setString(4, PH);
                        insertStatement.setString(5, SH);
                        insertStatement.setString(6, FH);
                        insertStatement.setString(7, SR);


                        insertStatement.executeUpdate();
                        Labels.showMessageLabel(warningLabel, "Updated Q" + queueNumber + " successfully", true);
                    }
                }
            }
        } catch (SQLException exc) {
            throw exc;
        }
    }

    @FXML
    private void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        EditHistoryController.queueNumber = -1;
        stage.close();
    }
}
