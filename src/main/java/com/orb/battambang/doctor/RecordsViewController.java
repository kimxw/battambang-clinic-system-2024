package com.orb.battambang.doctor;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecordsViewController {
    @FXML
    private TextFlow textFlow;

    protected static int queueNumber = -1;

    @FXML
    public void initialize() {
        if (queueNumber != -1) {
            addData(queueNumber);
        }

    }

    private void addData(int queueNumber) {
        int id = queueNumber;
        String name = "";
        int age = 0;
        String sex = "";
        String phoneNumber = "";
        double height = 0.0;
        double weight = 0.0;
        double bmi = 0.0;
        String bmiCategory = "";
        String malnourishmentFlag = "";
        String bmiNotes = "";
        int bloodPressure = 0;
        double temperature = 0.0;
        String wpRight = "";
        String wpLeft = "";
        String npRight = "";
        String npLeft = "";
        String snellensNotes = "";
        boolean hearingProblem = false;
        boolean fluSymptoms = false;
        String hearingNotes = "";
        boolean liceProblem = false;
        String liceNotes = "";
        String dentalNotes = "";
        double angleOfTruncalRotation = 0.00;
        String system = "";
        String ps = "";
        String duration = "";
        String hpi = "";
        String fh = "";
        String dh = "";
        String ph = "";
        String sh = "";
        String sr = "";
        String drugAllergies = "";
        String historyNotes = "";
        boolean knowsHEF = false;
        boolean hasHEF = false;
        String usesHEF = "";

        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        String bmiQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
        String vitalsQuery = "SELECT * FROM vitalSignsTable WHERE queueNumber = " + queueNumber;
        String snellensQuery = "SELECT * FROM snellensTestTable WHERE queueNumber = " + queueNumber;
        String hearingQuery = "SELECT * FROM hearingTestTable WHERE queueNumber = " + queueNumber;
        String liceQuery = "SELECT * FROM headLiceTable WHERE queueNumber = " + queueNumber;
        String dentalQuery = "SELECT * FROM dentalTable WHERE queueNumber = " + queueNumber;
        String scoliosisQuery = "SELECT * FROM scoliosisTable WHERE queueNumber = " + queueNumber;
        String historyQuery = "SELECT * FROM historyTable WHERE queueNumber = " + queueNumber;
        String HEFQuery = "SELECT * FROM healthEFTable WHERE queueNumber = " + queueNumber;

        try {
            Statement statement = DatabaseConnection.connection.createStatement();

            ResultSet patientResultSet = statement.executeQuery(patientQuery);
            if (patientResultSet.next()) {
                name = defaultIfNull(patientResultSet.getString("name"));
                age = defaultIfNull(patientResultSet.getInt("age"));
                sex = defaultIfNull(patientResultSet.getString("sex"));
                phoneNumber = defaultIfNull(patientResultSet.getString("phoneNumber"));
            }
            patientResultSet.close();

            ResultSet bmiResultSet = statement.executeQuery(bmiQuery);
            if (bmiResultSet.next()) {
                height = defaultIfNull(bmiResultSet.getDouble("height"));
                weight = defaultIfNull(bmiResultSet.getDouble("weight"));
                bmi = defaultIfNull(bmiResultSet.getDouble("bmi"));
                bmiCategory = defaultIfNull(bmiResultSet.getString("bmiCategory"));
                malnourishmentFlag = defaultIfNull(bmiResultSet.getBoolean("malnourishmentFlagged")
                                        ? "FLAGGED: Child below 3rd percentile (BMI by age)"
                                        : "No flags" );
                bmiNotes = defaultIfNull(bmiResultSet.getString("additionalNotes"));
            }
            bmiResultSet.close();

            ResultSet vitalsResultSet = statement.executeQuery(vitalsQuery);
            if (vitalsResultSet.next()) {
                bloodPressure = defaultIfNull(vitalsResultSet.getInt("bloodPressure"));
                temperature = defaultIfNull(vitalsResultSet.getDouble("temperature"));
            }
            vitalsResultSet.close();

            ResultSet snellensResultSet = statement.executeQuery(snellensQuery);
            if (snellensResultSet.next()) {
                wpRight = defaultIfNull(snellensResultSet.getString("wpRight"));
                wpLeft = defaultIfNull(snellensResultSet.getString("wpLeft"));
                npRight = defaultIfNull(snellensResultSet.getString("npRight"));
                npLeft = defaultIfNull(snellensResultSet.getString("npLeft"));
                snellensNotes = defaultIfNull(snellensResultSet.getString("additionalNotes"));
            }
            snellensResultSet.close();

            ResultSet hearingResultSet = statement.executeQuery(hearingQuery);
            if (hearingResultSet.next()) {
                hearingProblem = hearingResultSet.getBoolean("hearingProblems");
                fluSymptoms = hearingResultSet.getBoolean("fluSymptoms");
                hearingNotes = defaultIfNull(hearingResultSet.getString("additionalNotes"));
            }
            hearingResultSet.close();

            ResultSet liceResultSet = statement.executeQuery(liceQuery);
            if (liceResultSet.next()) {
                liceProblem = liceResultSet.getBoolean("headLice");
                liceNotes = defaultIfNull(liceResultSet.getString("additionalNotes"));
            }
            liceResultSet.close();

            ResultSet dentalResultSet = statement.executeQuery(dentalQuery);
            if (dentalResultSet.next()) {
                dentalNotes = defaultIfNull(dentalResultSet.getString("additionalNotes"));
            }
            dentalResultSet.close();

            ResultSet scoliosisResultSet = statement.executeQuery(scoliosisQuery);
            if (scoliosisResultSet.next()) {
                angleOfTruncalRotation = defaultIfNull(scoliosisResultSet.getDouble("angleOfTruncalRotation"));
            }
            scoliosisResultSet.close();

            ResultSet historyResultSet = statement.executeQuery(historyQuery);
            if (historyResultSet.next()) {
                system = defaultIfNull(historyResultSet.getString("bodySystem"));
                ps = defaultIfNull(historyResultSet.getString("PS"));
                duration = defaultIfNull(historyResultSet.getString("duration"));
                hpi = defaultIfNull(historyResultSet.getString("HPI"));
                fh = defaultIfNull(historyResultSet.getString("FH"));
                dh = defaultIfNull(historyResultSet.getString("DH"));
                ph = defaultIfNull(historyResultSet.getString("PH"));
                sh = defaultIfNull(historyResultSet.getString("SH"));
                sr = defaultIfNull(historyResultSet.getString("SR"));
                drugAllergies = defaultIfNull(historyResultSet.getString("drugAllergies"));
                historyNotes = defaultIfNull(historyResultSet.getString("additionalNotes"));
            }
            historyResultSet.close();

            ResultSet HEFResultSet = statement.executeQuery(HEFQuery);
            if (HEFResultSet.next()) {
                knowsHEF = HEFResultSet.getBoolean("knowsHEF");
                hasHEF = HEFResultSet.getBoolean("hasHEF");
                usesHEF = defaultIfNull(HEFResultSet.getString("usesHEF"));
            }
            HEFResultSet.close();

            statement.close();

        } catch (SQLException exc) {
            exc.printStackTrace();
        }

        RecordsViewController.queueNumber = -1;

        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(
                createStyledText("PATIENT RECORDS\n", "header"),
                createStyledText("\nPatient Particulars\n\n", "header"),
                createStyledText(String.format(
                        "ID : %d\nName : %s\nAge : %d\nSex : %s\nPhone No. : %s\n\n",
                        id, name, age, sex, phoneNumber
                ), "content"),
                createStyledText("Height and Weight\n\n", "header"),
                createStyledText(String.format(
                        "Height : %.1f cm\nWeight : %.1f kg\nBMI : %.1f kg/m2\nBMI Category : %s\n%s\nNotes : %s\n\n",
                        height, weight, bmi, bmiCategory, malnourishmentFlag, bmiNotes
                ), "content"),
                createStyledText("\nVital Signs\n\n", "header"),
                createStyledText(String.format(
                        "Blood Pressure : %d mmHg\nTemperature : %.1f °C\n\n",
                        bloodPressure, temperature
                ), "content"),
                createStyledText("Snellen's Test\n\n", "header"),
                createStyledText(String.format(
                        "Visual Acuity    Right Eye (OD)    Left Eye (OS)\nWith pinhole            %s            %s\nWithout pinhole       %s            %s\nNotes : %s\n\n",
                        wpRight, wpLeft, npRight, npLeft, snellensNotes
                ), "content"),
                createStyledText("Hearing Test\n\n", "header"),
                createStyledText(String.format(
                        "Hearing problems reported : %s\nFlu-like symptoms reported : %s\nNotes : %s\n\n",
                        hearingProblem ? "Yes" : "No", fluSymptoms ? "Yes" : "No", hearingNotes
                ), "content"),
                createStyledText("Head Lice Screening\n\n", "header"),
                createStyledText(String.format(
                        "Head Lice problems reported : %s\nNotes : %s\n\n",
                        liceProblem ? "Yes" : "No", liceNotes
                ), "content"),
                createStyledText("Dental Checkup\n\n", "header"),
                createStyledText(String.format(
                        "Notes : %s\n\n",
                        dentalNotes
                ), "content"),
                createStyledText("Scoliosis Checkup\n\n", "header"),
                createStyledText(String.format(
                        "Angle of Truncal Rotation : %.2f degrees\n\n",
                        angleOfTruncalRotation
                ), "content"),
                createStyledText("History\n\n", "header"),
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
                createStyledText(drugAllergies + "\n\n", "content"),
                createStyledText("Health Equity Fund\n", "header"),
                createStyledText(String.format(
                        "Do you know about HEF? : %s\nDo you have HEF? : %s\n(If applicable) Do you use it? Why or why not? : %s\n\n",
                        knowsHEF ? "Yes" : "No", hasHEF ? "Yes" : "No", usesHEF
                ), "content")
        );

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