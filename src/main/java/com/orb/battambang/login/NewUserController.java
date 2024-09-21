package com.orb.battambang.login;
import com.orb.battambang.connection.AuthDatabaseConnection;
import com.orb.battambang.util.Labels;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class NewUserController implements Initializable {
    private String[] roles = {"Admin", "Reception", "Triage", "Education", "Consultation", "Pharmacy"};
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
        String primaryRole = roleChoiceBox.getValue();

        String checkUserQuery = "SELECT COUNT(*) FROM staffTable WHERE username = '" + username + "'";

        String insertFields = "INSERT INTO staffTable(firstName, lastName, username, password, primaryRole, admin, reception, triage, education, consultation, pharmacy) VALUES ('";
        String insertValues = firstname + "', '" + lastname + "', '" + username + "', '" + password + "', '" + primaryRole + "', 0, 0, 0, 0, 0, 0)";
        String insertToCreate = insertFields + insertValues;

        //give receptionist access to triage for height and weight
        String givePerms = "UPDATE staffTable SET " + primaryRole.toLowerCase() + " = 1 WHERE username = '" + username + "'";

        if (primaryRole.equalsIgnoreCase("admin")) {
            givePerms = "UPDATE staffTable SET admin = 1, reception = 1, triage = 1, education = 1, consultation = 1, pharmacy = 1 WHERE username = '" + username + "'";
        }

        try {
            Statement statement  = AuthDatabaseConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(checkUserQuery);

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                Labels.showMessageLabel(usermessageLabel, "Username is already taken", false);
                return;
            }

            statement.executeUpdate(insertToCreate);
            statement.executeUpdate(givePerms);

            Labels.showMessageLabel(usermessageLabel, "User created successfully!", true);
            statement.close();
        } catch (Exception e) {
            Labels.showMessageLabel(usermessageLabel, "Please check all fields", false);
            System.out.println(e);
        }

    }

    @FXML
    public void backButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
