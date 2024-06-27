package com.orb.battambang.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Prescription {
    public static class PrescriptionEntry {
        private String name;
        private String quantityInMilligrams;
        private String units;
        private String dosageInstructions;

        public PrescriptionEntry(String name, String quantityInMilligrams, String units, String dosageInstructions) {
            this.name = name;
            this.quantityInMilligrams = quantityInMilligrams;
            this.units = units;
            this.dosageInstructions = dosageInstructions;
        }

        // getters
        public String getName() {
            return name;
        }

        public String getQuantityInMilligrams() {
            return quantityInMilligrams;
        }

        public String getUnits() {
            return units;
        }

        public String getDosageInstructions() {
            return dosageInstructions;
        }

        // Setters
        public void setUnits(String units) {
            this.units = units;
        }

        public void setDosageInstructions(String dosageInstructions) {
            this.dosageInstructions = dosageInstructions;
        }
    }

    public static ObservableList<PrescriptionEntry> convertToObservableList(String prescriptionString) {
        ObservableList<PrescriptionEntry> observableList = FXCollections.observableArrayList();

        // Splitting the prescriptionString by semicolons to separate different medication entries
        String[] medicationEntries = prescriptionString.split(";");

        for (String entry : medicationEntries) {
            // Splitting each entry by commas to get name, quantityInMilligrams, units, and dosage instructions
            String[] parts = entry.split(",");

            // Ensure each entry has exactly four parts before processing
            if (parts.length == 4) {
                String name = parts[0].trim();
                String quantityInMilligrams = parts[1].trim();
                String units = parts[2].trim();
                String dosageInstructions = parts[3].trim();

                // Create a PrescriptionEntry object and add to observableList
                PrescriptionEntry prescriptionEntry = new PrescriptionEntry(name, quantityInMilligrams, units, dosageInstructions);
                observableList.add(prescriptionEntry);
            }
        }

        return observableList;
    }

    public static String convertToString(ObservableList<PrescriptionEntry> observableList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (PrescriptionEntry entry : observableList) {
            // Append each entry in the format: name, quantityInMilligrams, units, dosageInstructions;
            stringBuilder.append(entry.getName()).append(", ")
                    .append(entry.getQuantityInMilligrams()).append(", ")
                    .append(entry.getUnits()).append(", ")
                    .append(entry.getDosageInstructions()).append("; ");
        }

        // Remove the last "; " if the list is not empty
        if (!observableList.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return stringBuilder.toString();
    }
}
