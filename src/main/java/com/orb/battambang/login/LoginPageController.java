package com.orb.battambang.login;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;

public class LoginPageController{

    double x, y;

    @FXML
    private Label loginMessageLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    // static variable to store logged-in user info
    public static String loggedInUserInfo;

    // Method to fetch user info from database
    private String getUserInfo(String username) {
        String query = "SELECT id, firstName, lastName FROM staffTable WHERE username = '" + username + "';";
        try (Statement statement = DatabaseConnection.connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                return firstName + " " + lastName + " (" + id + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void loginButtonOnAction(ActionEvent e) {
        if (!usernameTextField.getText().isBlank() && !passwordPasswordField.getText().isBlank()) {
            validateLogin(usernameTextField.getText(), passwordPasswordField.getText());
        } else {
            Labels.showMessageLabel(loginMessageLabel, "Please fill all fields.", false);
        }
    }

    @FXML
    private void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void createAccountForm() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("new-user.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setTitle("New User");
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void validateLogin(String username, String password) {
        String verifyLogin = "SELECT count(1) FROM staffTable WHERE username = '"
                + username + "' AND password = '" + password + "';";
        try {
            Statement statement = DatabaseConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(verifyLogin);
            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1) {
                    String userInfo = getUserInfo(username);
                    loggedInUserInfo = userInfo; // Store the logged-in user's info

                    Stage stage = (Stage) loginMessageLabel.getScene().getWindow();
                    String role = getRoleByUsername(username);
                    switch (role) {
                        case "Reception" -> {
                            openPatientRegistration();
                            stage.close();
                        }
                        case "CheckUpStation" -> {
                            stage.close();
                            openCheckupMenu();
                        }
                        case "Education" -> {
                            stage.close();
                            openEducation();
                        }
                        case "Doctor" -> {
                            stage.close();
                            openDoctorConsult();
                        }
                        case "Pharmacy" -> {
                            stage.close();
                            openMedicineDispense();
                        }
                        default -> stage.close();
                    }

                } else {
                    Labels.showMessageLabel(loginMessageLabel, "Invalid Login. Please try again.", false);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    private String getRoleByUsername(String username) {
        String role = null;
        String query = "SELECT role FROM staffTable WHERE username = '" + username + "';";

        try {
            Statement statement = DatabaseConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                role = resultSet.getString("role");
                //System.out.println(role);
            }
            resultSet.close();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return role;
    }

    private void openPatientRegistration() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("patient-registration.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1230 , 650);
            newUserStage.setTitle("Reception");
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
    public void openCheckupMenu() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("checkup-menu.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();

            // Add the event handler to the root node or any specific node
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX());
                stage.setY(event.getScreenY());
            });

            stage.setTitle("Check-up Stations");
            // newUserStage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(root, 1230, 650);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void openEducation() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("education-station.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1230 , 650);
            newUserStage.setTitle("Doctor Consult");
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }


    public void openDoctorConsult() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("doctor-consult.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1230 , 650);
            newUserStage.setTitle("Doctor Consult");
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void openMedicineDispense() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("medicine-dispense.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1230 , 650);
            newUserStage.setTitle("Pharmacy");
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}
