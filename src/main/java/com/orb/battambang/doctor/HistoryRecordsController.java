package com.orb.battambang.doctor;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HistoryRecordsController implements Initializable {

    @FXML
    private TextFlow textFlow;

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
                String system = historyResultSet.getString("bodySystem");
                String ps = historyResultSet.getString("PS");
                String duration = historyResultSet.getString("duration");
                String hpi = historyResultSet.getString("HPI");
                String fh = historyResultSet.getString("FH");
                String dh = historyResultSet.getString("DH");
                String ph = historyResultSet.getString("PH");
                String sh = historyResultSet.getString("SH");
                String sr = historyResultSet.getString("SR");
                String historyNotes = historyResultSet.getString("additionalNotes");
                String drugAllergies = historyResultSet.getString("drugAllergies");

                textFlow.getChildren().clear();
                textFlow.getChildren().addAll(
                        createStyledText("PATIENT HISTORY\n", "header"),
                        createStyledText("System\n", "header"),
                        createStyledText(system + "\n\n", "content"),
                        createStyledText("Presenting Symptoms\n", "header"),
                        createStyledText(ps + "\n\n", "content"),
                        createStyledText("Duration\n", "header"),
                        createStyledText(duration + "\n\n", "content"),
                        createStyledText("History of Presenting Illness (HPI)\n", "header"),
                        createStyledText(hpi + "\n\n", "content"),
                        createStyledText("Drug and Treatment History (DH)\n", "header"),
                        createStyledText(dh + "\n\n", "content"),
                        createStyledText("Social History (SH)\n", "header"),
                        createStyledText(sh + "\n\n", "content"),
                        createStyledText("Past History (PH)\n", "header"),
                        createStyledText(ph + "\n\n", "content"),
                        createStyledText("Family History (FH)\n", "header"),
                        createStyledText(fh + "\n\n", "content"),
                        createStyledText("Systems Review (SR)\n", "header"),
                        createStyledText(sr + "\n\n", "content"),
                        createStyledText("Notes\n", "header"),
                        createStyledText(historyNotes + "\n\n", "content"),
                        createStyledText("Drug Allergies\n", "header"),
                        createStyledText(drugAllergies + "\n\n", "content")
                );

            }
            historyResultSet.close();

        } catch (SQLException e) {
            textFlow.getChildren().clear();
        } finally {
            queueNumber = -1;
        }
    }

    private Text createStyledText(String content, String styleClass) {
        Text text = new Text(content);
        text.getStyleClass().add(styleClass);
        return text;
    }

    private String defaultIfNull(String value) {
        return value != null ? value : "";
    }

    private int defaultIfNull(int value) {
        return value;
    }

    private double defaultIfNull(double value) {
        return value;
    }
}
