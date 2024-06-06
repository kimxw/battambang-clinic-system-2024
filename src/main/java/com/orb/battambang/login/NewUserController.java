package com.orb.battambang.login;
import com.orb.battambang.util.Labels;

import com.orb.battambang.connection.DatabaseConnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class NewUserController extends DatabaseConnection implements Initializable {
    private String[] roles = {"Reception", "CheckUpStation", "Doctor", "Pharmacy"};
    @FXML
    private Label usermessageLabel;
    @FXML
    private Label passwordmatchLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField confirmPasswordPasswordField;
    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleChoiceBox.getItems().addAll(roles);
    }

    @FXML
    public void createUserButtonOnAction(ActionEvent e) {
        passwordmatchLabel.setText("");
        if (!passwordPasswordField.getText().equals(confirmPasswordPasswordField.getText())) {
            passwordmatchLabel.setText("Password does not match!");
        } else {
            createUser();
        }
    }

    @FXML
    public void createUser() {

        String firstname = firstNameTextField.getText();
        String lastname = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();
        String role = roleChoiceBox.getValue();

        String insertFields = "INSERT INTO staffTable(firstName, lastName, username, password, role) VALUES ('";
        String insertValues = firstname + "', '" + lastname + "', '" + username + "', '" + password + "', '" + role + "')";
        String insertToCreate = insertFields + insertValues;

        String checkUserQuery = "SELECT COUNT(*) FROM staffTable WHERE username = '" + username + "'";

        try {
            Statement statement  = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(checkUserQuery);

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                Labels.showMessageLabel(usermessageLabel, "Username is already taken.", false);
                return;
            }

            statement.executeUpdate(insertToCreate);
            Labels.showMessageLabel(usermessageLabel, "User created successfully!", true);
        } catch (Exception e) {
            Labels.showMessageLabel(usermessageLabel, "Invalid fields.", false);
            e.printStackTrace();
        }

    }

    @FXML
    public void backButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
