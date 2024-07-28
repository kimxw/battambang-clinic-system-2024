package com.orb.battambang.login;

import com.orb.battambang.MainApp;
import com.orb.battambang.connection.AuthDatabaseConnection;
import com.orb.battambang.connection.DatabaseConnection;
import com.orb.battambang.util.Labels;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NewLoginPageController implements Initializable {

    double x, y;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private Label verificationLabel;
    @FXML
    private ImageView verificationImageView;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button newUserButton;
    @FXML
    private Line line;

    @FXML
    private Label locationLabel;
    @FXML
    private Label locationWarningLabel;
    @FXML
    private ChoiceBox<String> locationChoiceBox;
    private final String[] choiceBoxOptions = new String[] {"MOPK", "TNK5", "Kbal Koh"};
    @FXML
    private Button goButton;
    private static Staff staff;

    boolean connectionSuccess;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        connectionSuccess = AuthDatabaseConnection.establishConnection();

        locationChoiceBox.getItems().clear();
        locationChoiceBox.getItems().addAll(choiceBoxOptions);

        loginMessageLabel.setVisible(true);
        verificationImageView.setVisible(false);
        verificationLabel.setVisible(false);
        usernameTextField.setEditable(true);
        passwordPasswordField.setEditable(true);
        loginMessageLabel.setText("Enter your credentials");
        usernameTextField.setText("");
        passwordPasswordField.setText("");

        hideLocationOption();
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
        locationChoiceBox.getItems().addAll(choiceBoxOptions);

        loginMessageLabel.setVisible(true);
        verificationImageView.setVisible(false);
        verificationLabel.setVisible(false);
        usernameTextField.setEditable(true);
        passwordPasswordField.setEditable(true);
        loginMessageLabel.setText("Enter your credentials");
        usernameTextField.setText("");
        passwordPasswordField.setText("");

        hideLocationOption();
    }

    public void createAccountForm() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("new-user.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 520, 400);
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setResizable(false);
            newUserStage.setTitle("New User");
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void validateLogin(String username, String password) {
        String verifyLogin = "SELECT * FROM staffTable WHERE username = '"
                + username + "' AND password = '" + password + "';";
        try {

            if (!connectionSuccess) {
                verificationDisplay("Authentication failed", "#bf1b15", "/icons/cross.png");
                return;
            }

            Statement statement = AuthDatabaseConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(verifyLogin);
            if(resultSet.next()) {
                int staffID = resultSet.getInt("staffID");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String store_username = resultSet.getString("username");
                String primaryRole = resultSet.getString("primaryRole");
                boolean admin = resultSet.getBoolean("admin");
                boolean reception = resultSet.getBoolean("reception");
                boolean triage = resultSet.getBoolean("triage");
                boolean education = resultSet.getBoolean("education");
                boolean consultation = resultSet.getBoolean("consultation");
                boolean pharmacy = resultSet.getBoolean("pharmacy");

                staff = new Staff(staffID, firstName, lastName, store_username, primaryRole,
                        admin, reception, triage, education, consultation, pharmacy);

                verificationDisplay("Verified user", "#5f8b07", "/icons/tick.png");

                showLocationOption();

            } else {
                verificationDisplay("Invalid user", "#bf1b15", "/icons/cross.png");
                return;
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Labels.showMessageLabel(loginMessageLabel, "Invalid Login. Please try again.", false);
        }
    }

    @FXML
    private void goButtonOnAction(ActionEvent e) {
        String location = locationChoiceBox.getValue();
        boolean success = DatabaseConnection.establishConnection(location); //check if valid connection established

        if (!success) {
            Labels.showMessageLabel(locationWarningLabel, "Please select a location.", false);
        } else {
            staff.setLocation(location);
            Labels.showMessageLabel(locationWarningLabel, "Connecting to database . . .", "green", 3);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {AuthDatabaseConnection.closeDatabaseConnection(); openHomePage(); close();}));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    private void hideLocationOption() {
        line.setVisible(true);
        newUserButton.setVisible(true);
        locationChoiceBox.setVisible(false);
        locationLabel.setVisible(false);
        goButton.setVisible(false);
    }
    private void showLocationOption() {
        usernameTextField.setEditable(false);
        passwordPasswordField.setEditable(false);
        line.setVisible(false);
        newUserButton.setVisible(false);
        locationChoiceBox.setVisible(true);
        locationLabel.setVisible(true);
        goButton.setVisible(true);
    }

    private void verificationDisplay(String message, String colour, String image_url) {
        Image image = new Image(MainApp.class.getResource(image_url).toExternalForm());
        loginMessageLabel.setVisible(false);
        verificationImageView.setImage(image);
        verificationLabel.setText(message);
        verificationLabel.setStyle("-fx-text-fill: " + colour + " ;");
        verificationImageView.setVisible(true);
        verificationLabel.setVisible(true);
    }
    private void openHomePage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("home-page.fxml"));
            Stage newUserStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1230 , 650);
            newUserStage.setResizable(false);
            newUserStage.setTitle("Home");
            //newUserStage.initStyle(StageStyle.UNDECORATED);
            newUserStage.setScene(scene);
            newUserStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void close() {
        Stage stage = (Stage) goButton.getScene().getWindow();
        stage.close();
    }

    public static Staff getStaffDetails() {
        return staff;
    }

}
