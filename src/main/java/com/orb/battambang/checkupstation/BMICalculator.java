package com.orb.battambang.checkupstation;

public class BMICalculator {

    public static double calculateBMI(double weight, double height) {
        double bmi = (weight / (height * height)) * 10000;  // Corrected the formula to remove the division by 10000
        String formattedBMI = String.format("%.2f", bmi);
        return Double.parseDouble(formattedBMI);
    }

    public static String determineBMICategory(double bmi, int age, String sex) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Healthy Weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else if (bmi >= 30) {
            return "Obese";
        } else {
            return "Unknown";
        }
    }
}
