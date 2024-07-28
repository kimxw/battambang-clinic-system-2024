package com.orb.battambang.queue;

import com.orb.battambang.login.NewLoginPageController;
import com.orb.battambang.util.Labels;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.QueueManager;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;

import java.net.URL;
import java.util.HashMap;
import java.util.Queue;
import java.util.ResourceBundle;

public class QueueManagerController implements Initializable {

    @FXML
    private ListView<String> triageWaitingListView;
    @FXML
    private ListView<String> triageProgressListView;
    @FXML
    private ListView<String> educationWaitingListView;
    @FXML
    private ListView<String> educationProgressListView;
    @FXML
    private ListView<String> doctorWaitingListView;
    @FXML
    private ListView<String> doctorProgressListView;
    @FXML
    private ListView<String> pharmacyWaitingListView;
    @FXML
    private ListView<String> pharmacyProgressListView;

    @FXML
    private AnchorPane sliderAnchorPane;
    @FXML
    private Label menuLabel;
    @FXML
    private Label menuBackLabel;
    @FXML
    private Button menuHomeButton;
    @FXML
    private Button menuReceptionButton;
    @FXML
    private Button menuTriageButton;
    @FXML
    private Button menuEducationButton;
    @FXML
    private Button menuConsultationButton;
    @FXML
    private Button menuPharmacyButton;
    @FXML
    private Button menuQueueManagerButton;
    @FXML
    private Button menuAdminButton;
    @FXML
    private Button menuLogoutButton;
    @FXML
    private Button menuUserButton;
    @FXML
    private Button menuLocationButton;

    @FXML
    private Pane actionsPane;
    @FXML
    private Button moreActionsOpenButton;
    @FXML
    private Button moreActionsCloseButton;

    @FXML
    private Button triageWaitingToProgressButton;
    @FXML
    private Button triageProgressToEducationButton;
    @FXML
    private Button educationWaitingToProgressButton;
    @FXML
    private Button educationProgressToDoctorButton;
    @FXML
    private Button doctorWaitingToProgressButton;
    @FXML
    private Button doctorProgressToPharmacyButton;
    @FXML
    private Button pharmacyWaitingToProgressButton;
    @FXML
    private Button pharmacyProgressToCheckoutButton;

    private final HashMap<String, QueueManager> buttonQueueManagerMap = new HashMap<>();
    private final HashMap<String, QueueManager> choiceQueueManagerMap = new HashMap<>();


    @FXML
    private TextField addQueueNumberTextField;
    @FXML
    private TextField moveQueueNumberTextField;

    @FXML
    private ChoiceBox<String> addChoiceBox;
    @FXML
    private ChoiceBox<String> currentChoiceBox;
    @FXML
    private ChoiceBox<String> targetChoiceBox;
    @FXML
    private ChoiceBox<String> reorderChoiceBox;

    @FXML
    private Button moveUpButton;
    @FXML
    private Button moveDownButton;

    @FXML
    private Label warningLabel;
    @FXML
    private ImageView warningImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initialising MenuGallery
        MenuGallery menuGallery = new MenuGallery(sliderAnchorPane, menuLabel, menuBackLabel, menuHomeButton,
                menuReceptionButton, menuTriageButton, menuEducationButton, menuConsultationButton,
                menuPharmacyButton, menuQueueManagerButton, menuAdminButton, menuLogoutButton,
                menuUserButton, menuLocationButton);

        setUpActionsPane();
        setUpQueuePane();

        String[] choiceBoxItems = new String[]{"Triage: Waiting", "Triage: In-Progress",
                                                "Education: Waiting", "Education: In-Progress",
                                                "Consultation: Waiting", "Consultation: In-Progress",
                                                "Pharmacy: Waiting", "Pharmacy: In-Progress"};
        addChoiceBox.getItems().addAll(choiceBoxItems);
        currentChoiceBox.getItems().addAll(choiceBoxItems);
        reorderChoiceBox.getItems().addAll(choiceBoxItems);
        targetChoiceBox.getItems().addAll(choiceBoxItems);
        targetChoiceBox.getItems().add("Remove");


    }

    private void setUpActionsPane() {

        actionsPane.setTranslateY(0);
        moreActionsOpenButton.setVisible(false);
        moreActionsCloseButton.setVisible(true);

        moreActionsOpenButton.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(actionsPane);

            slide.setToY(0);
            slide.play();

            actionsPane.setTranslateY(44);

            slide.setOnFinished((ActionEvent e) -> {
                moreActionsOpenButton.setVisible(false);
                moreActionsCloseButton.setVisible(true);

            });
        });

        moreActionsCloseButton.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(actionsPane);

            slide.setToY(-44);
            slide.play();

            actionsPane.setTranslateY(0);

            slide.setOnFinished((ActionEvent e) -> {
                moreActionsOpenButton.setVisible(true);
                moreActionsCloseButton.setVisible(false);

            });
        });
    }

    private void setUpQueuePane() {
        QueueManager triageWaiting = new QueueManager(triageWaitingListView, "triageWaitingTable", triageProgressListView, "triageProgressTable");
        QueueManager triageProgress = new QueueManager(triageProgressListView, "triageProgressTable", educationWaitingListView, "educationWaitingTable");
        QueueManager educationWaiting = new QueueManager(educationWaitingListView, "educationWaitingTable", educationProgressListView, "educationProgressTable");
        QueueManager educationProgress = new QueueManager(educationProgressListView, "educationProgressTable", doctorWaitingListView, "doctorWaitingTable");
        QueueManager doctorWaiting = new QueueManager(doctorWaitingListView, "doctorWaitingTable", doctorProgressListView, "doctorProgressTable");
        QueueManager doctorProgress = new QueueManager(doctorProgressListView, "doctorProgressTable", pharmacyWaitingListView, "pharmacyWaitingTable");
        QueueManager pharmacyWaiting = new QueueManager(pharmacyWaitingListView, "pharmacyWaitingTable", pharmacyProgressListView, "pharmacyProgressTable");
        QueueManager pharmacyProgress = new QueueManager(pharmacyProgressListView, "pharmacyProgressTable", null, null);

        buttonQueueManagerMap.put("triageWaitingToProgressButton", triageWaiting);
        buttonQueueManagerMap.put("triageProgressToEducationButton", triageProgress);
        buttonQueueManagerMap.put("educationWaitingToProgressButton", educationWaiting);
        buttonQueueManagerMap.put("educationProgressToDoctorButton", educationProgress);
        buttonQueueManagerMap.put("doctorWaitingToProgressButton", doctorWaiting);
        buttonQueueManagerMap.put("doctorProgressToPharmacyButton", doctorProgress);
        buttonQueueManagerMap.put("pharmacyWaitingToProgressButton", pharmacyWaiting);
        buttonQueueManagerMap.put("pharmacyProgressToCheckoutButton", pharmacyProgress);

        choiceQueueManagerMap.put("Triage: Waiting", triageWaiting);
        choiceQueueManagerMap.put("Triage: In-Progress", triageProgress);
        choiceQueueManagerMap.put("Education: Waiting", educationWaiting);
        choiceQueueManagerMap.put("Education: In-Progress", educationProgress);
        choiceQueueManagerMap.put("Consultation: Waiting", doctorWaiting);
        choiceQueueManagerMap.put("Consultation: In-Progress", doctorProgress);
        choiceQueueManagerMap.put("Pharmacy: Waiting", pharmacyWaiting);
        choiceQueueManagerMap.put("Pharmacy: In-Progress", pharmacyProgress);

    }

    @FXML
    private void nextOnAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonId = button.getId();
        buttonQueueManagerMap.get(buttonId).moveToNext();
    }

    @FXML
    private void addButtonOnAction(ActionEvent event) {
        try {
            if (addQueueNumberTextField.getText().isEmpty() || !addQueueNumberTextField.getText().matches("\\d+")) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Input a queue number", "#bf1b15", "/icons/cross.png");
                return;
            }

            int queueNumber = Integer.parseInt(addQueueNumberTextField.getText());

            String target = addChoiceBox.getSelectionModel().getSelectedItem();
            if (target == null || target.isBlank()) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Select a target queue", "#bf1b15", "/icons/cross.png");
            } else {
                QueueManager qm1 = choiceQueueManagerMap.get(target);
                QueueManager.addNew(queueNumber, qm1.getCurrentListView(), qm1.getCurrentTable());
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Patient successfully added", "#5f8b07", "/icons/tick.png");
            }
        } catch (Exception e) {
            Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#bf1b15", "/icons/cross.png");
        }
    }

    @FXML
    private void moveButtonOnAction(ActionEvent event) {

        try {
            if (moveQueueNumberTextField.getText().isEmpty() || !moveQueueNumberTextField.getText().matches("\\d+")) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Input a queue number", "#bf1b15", "/icons/cross.png");
                return;
            }

            int queueNumber = Integer.parseInt(moveQueueNumberTextField.getText());

            String current = currentChoiceBox.getSelectionModel().getSelectedItem();
            String target = targetChoiceBox.getSelectionModel().getSelectedItem();
            if (current == null || target == null) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Select an origin and target queue", "#bf1b15", "/icons/cross.png");
            } else {
                QueueManager qm1 = choiceQueueManagerMap.get(current);
                if (target.equals("Remove")) {
                    QueueManager.remove(queueNumber, qm1.getCurrentListView(), qm1.getCurrentTable());
                    Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Patient successfully removed", "#5f8b07", "/icons/tick.png");
                } else{
                    QueueManager qm2 = choiceQueueManagerMap.get(target);
                    QueueManager.move(queueNumber, qm1.getCurrentListView(), qm1.getCurrentTable(), qm2.getCurrentListView(), qm2.getCurrentTable());
                    Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Patient successfully moved", "#5f8b07", "/icons/tick.png");
                }

            }
        } catch (Exception e) {
            //Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#5f8b07", "/icons/tick.png");
            Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#bf1b15", "/icons/cross.png");
        }


    }

    @FXML
    private void reorderButtonOnAction(ActionEvent event) {
        try {
            String current = reorderChoiceBox.getSelectionModel().getSelectedItem();
            if (current == null) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Select a queue", "#bf1b15", "/icons/cross.png");
                return;
            }

            Button button = (Button) event.getSource();
            String buttonId = button.getId();

            if (buttonId.equals("moveUpButton")) {
                choiceQueueManagerMap.get(current).swapPosition(true);
            } else {
                choiceQueueManagerMap.get(current).swapPosition(false);
            }
        } catch (Exception e) {
            Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#bf1b15", "/icons/cross.png");
        }
    }

}


