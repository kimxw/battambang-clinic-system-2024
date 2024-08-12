package com.orb.battambang.doctor;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HistoryRecordsController implements Initializable {
    @FXML
    private Text systemText;
    @FXML
    private Text psText;
    @FXML
    private Text durationText;
    @FXML
    private TextArea hpiTextArea;
    @FXML
    private TextArea dhTextArea;
    @FXML
    private TextArea phTextArea;
    @FXML
    private TextArea shTextArea;
    @FXML
    private TextArea fhTextArea;
    @FXML
    private TextArea srTextArea;
    @FXML
    private TextArea allergiesTextArea;

    protected static int queueNumber = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (queueNumber == -1) {
            return;
        }
        try {
            Statement statement = DatabaseConnection.connection.createStatement();

            String historyQuery = "SELECT * FROM historyTable WHERE queueNumber = " + queueNumber;
            ResultSet historyResultSet = statement.executeQuery(historyQuery);
            if (historyResultSet.next()) {
                systemText.setText(historyResultSet.getString("bodySystem"));
                psText.setText(historyResultSet.getString("PS"));
                durationText.setText(historyResultSet.getString("duration"));
                hpiTextArea.setText(historyResultSet.getString("HPI"));
                fhTextArea.setText(historyResultSet.getString("FH"));
                dhTextArea.setText(historyResultSet.getString("DH"));
                phTextArea.setText(historyResultSet.getString("PH"));
                shTextArea.setText(historyResultSet.getString("SH"));
                srTextArea.setText(historyResultSet.getString("SR"));
                allergiesTextArea.setText(historyResultSet.getString("drugAllergies"));
            }
            historyResultSet.close();

        } catch (SQLException e) {
            systemText.setText("");
            psText.setText("");
            durationText.setText("");
            hpiTextArea.setText("");
            fhTextArea.setText("");
            dhTextArea.setText("");
            phTextArea.setText("");
            shTextArea.setText("");
            srTextArea.setText("");
            allergiesTextArea.setText("");
        } finally {
            queueNumber = -1;
        }
    }
}
