package com.orb.battambang.checkupstation;

public class BMICalculator {

    public static double calculateBMI(double weight, double height) {
        double bmi = (weight / (height * height)) * 10000;  // Corrected the formula to remove the division by 10000
        String formattedBMI = String.format("%.2f", bmi);
        return Double.parseDouble(formattedBMI);
    }

    public static String determineBMICategory(double bmi, int age, String sex) {

        if (age >= 2 && age <= 19) {
            if (bmi < 5) {
                return "Underweight";
            } else if (bmi < 85) {
                return "Healthy Weight";
            } else if (bmi < 95) {
                return "Overweight";
            } else {
                return "Obese";
            }
        } else if (age >= 20) {
            if (bmi < 18.5) {
                return "Underweight";
            } else if (bmi < 24.9) {
                return "Healthy Weight";
            } else if (bmi < 29.9) {
                return "Overweight";
            } else {
                return "Obese";
            }
        }
        return "Unknown";
    }
}
