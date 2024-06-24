package com.orb.battambang.doctor;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HistoryRecordsController extends DatabaseConnection implements Initializable {
    @FXML
    private Text systemText;
    @FXML
    private Text psText;
    @FXML
    private Text durationText;
    @FXML
    private Text hpiText;
    @FXML
    private Text dhText;
    @FXML
    private Text phText;
    @FXML
    private Text shText;
    @FXML
    private Text fhText;
    @FXML
    private Text srText;
    @FXML
    private Text allergiesText;

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
                hpiText.setText(historyResultSet.getString("HPI"));
                fhText.setText(historyResultSet.getString("FH"));
                dhText.setText(historyResultSet.getString("DH"));
                phText.setText(historyResultSet.getString("PH"));
                shText.setText(historyResultSet.getString("SH"));
                srText.setText(historyResultSet.getString("SR"));
                allergiesText.setText(historyResultSet.getString("drugAllergies"));
            }
            historyResultSet.close();

        } catch (SQLException e) {
            systemText.setText("");
            psText.setText("");
            durationText.setText("");
            hpiText.setText("");
            fhText.setText("");
            dhText.setText("");
            phText.setText("");
            shText.setText("");
            srText.setText("");
            allergiesText.setText("");
        } finally {
            queueNumber = -1;
        }
    }
}
