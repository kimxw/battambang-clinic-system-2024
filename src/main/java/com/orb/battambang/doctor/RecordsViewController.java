package com.orb.battambang.doctor;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecordsViewController {
    @FXML
    private TextArea textView;
    protected static int queueNumber = -1;

    @FXML
    public void initialize() {
        if (queueNumber != -1) {
            addData(queueNumber);
        }
    }

    private void addData(int queueNumber) {
        // Initialize default values
        int id = queueNumber;
        String name = "";
        int age = 0;
        String sex = "";
        String phoneNumber = "";
        double height = 0.0;
        double weight = 0.0;
        double bmi = 0.0;
        String bmiCategory = "";
        String bmiNotes = "";
        String wpRight = "";
        String wpLeft = "";
        String npRight = "";
        String npLeft = "";
        String snellensNotes = "";
        boolean hearingProblem = false;
        String hearingNotes = "";
        boolean liceProblem = false;
        String liceNotes = "";
        String dentalNotes = "";
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

        String patientQuery = "SELECT * FROM patientQueueTable WHERE queueNumber = " + queueNumber;
        String bmiQuery = "SELECT * FROM heightAndWeightTable WHERE queueNumber = " + queueNumber;
        String snellensQuery = "SELECT * FROM snellensTestTable WHERE queueNumber = " + queueNumber;
        String hearingQuery = "SELECT * FROM hearingTestTable WHERE queueNumber = " + queueNumber;
        String liceQuery = "SELECT * FROM headLiceTable WHERE queueNumber = " + queueNumber;
        String dentalQuery = "SELECT * FROM dentalTable WHERE queueNumber = " + queueNumber;
        String historyQuery = "SELECT * FROM historyTable WHERE queueNumber = " + queueNumber;

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
                bmiNotes = defaultIfNull(bmiResultSet.getString("additionalNotes"));
            }
            bmiResultSet.close();

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

            statement.close();

        } catch (SQLException exc) {
            exc.printStackTrace();
            // Handle exception
        }

        RecordsViewController.queueNumber = -1;

        String formattedText = String.format(
                "Patient Particulars%n" +
                        "ID : %d%n" +
                        "Name : %s%n" +
                        "Age : %d%n" +
                        "Sex : %s%n" +
                        "Phone No. : %s%n" +
                        "__________________________________________________________________________________________%n%n" +

                        "Height and Weight%n" +
                        "Height : %.1f cm%n" +
                        "Weight : %.1f kg%n" +
                        "BMI : %.1f kg/m2%n" +
                        "BMI Category : %s%n%n" +
                        "Notes : %s%n" +
                        "__________________________________________________________________________________________%n%n" +

                        "Snellen's Test%n" +
                        "Visual Acuity    Right Eye (OD)    Left Eye (OS)%n" +
                        "With pinhole          %s            %s%n" +
                        "Without pinhole       %s            %s%n%n" +
                        "Notes : %s%n" +
                        "__________________________________________________________________________________________%n%n" +

                        "Hearing Test%n" +
                        "Hearing problems reported : %s%n" +
                        "Notes : %s%n" +
                        "__________________________________________________________________________________________%n%n" +

                        "Head Lice Screening%n" +
                        "Head Lice problems reported : %s%n" +
                        "Notes : %s%n" +
                        "__________________________________________________________________________________________%n%n" +

                        "Dental Checkup%n" +
                        "Notes : %s%n" +
                        "__________________________________________________________________________________________%n%n" +

                        "History%n%n" +
                        "System : %n%s%n%n" +
                        "Presenting : %n%s%n%n" +
                        "Duration : %n%s%n%n" +
                        "History of Presenting Illness (HPI) : %n%s%n%n" +
                        "Drug and Treatment History (DH) : %n%s%n%n" +
                        "Social History (SH) : %n%s%n%n" +
                        "Past History (PH) : %n%s%n%n" +
                        "Family History (FH) : %n%s%n%n" +
                        "Systems Review (SR) : %n%s%n%n" +
                        "Drug Allergies : %n%s%n%n"+
                        "Notes : %n%s%n" +
                        "__________________________________________________________________________________________%n%n",
                id, name, age, sex, phoneNumber,
                height, weight, bmi, bmiCategory, bmiNotes,
                wpRight, wpLeft, npRight, npLeft, snellensNotes,
                hearingProblem ? "Yes" : "No", hearingNotes,
                liceProblem ? "Yes" : "No", liceNotes,
                dentalNotes,
                system, ps, duration, hpi, dh, sh, ph, fh, sr, drugAllergies, historyNotes
        );

        textView.setText(formattedText);
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
