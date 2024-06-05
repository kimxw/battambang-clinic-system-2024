package com.orb.battambang.login;
import com.orb.battambang.MainApp;
import com.orb.battambang.util.Labels;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginPageController extends DatabaseConnection{

    @FXML
    private Label loginMessageLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;

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
                    Stage stage = (Stage) loginMessageLabel.getScene().getWindow();
                    String role = getRoleByUsername(username);
                    if (role.equals("Reception")) {
                        //stage.close();
                        //openRegistrationWindow();
                    } else if (role.equals("CheckUpStation")) {
                        stage.close();
                        //openCheckUpStationWindow();

                    } else if (role.equals("Doctor")) {
                        //openDoctorConsultWindow();
                        stage.close();

                    } else if (role.equals("Pharmacy")) {
                        //openPharmacistDispenseWindow();
                        stage.close();

                    } else {
                        stage.close();
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

}
