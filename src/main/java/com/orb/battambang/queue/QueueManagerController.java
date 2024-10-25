package com.orb.battambang.queue;

import com.orb.battambang.util.Labels;
import com.orb.battambang.util.MenuGallery;
import com.orb.battambang.util.QueueManager;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
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
    private ListView<String> physioWaitingListView;
    @FXML
    private ListView<String> physioProgressListView;
    @FXML
    private ListView<String> pharmacyWaitingListView;
    @FXML
    private ListView<String> pharmacyProgressListView;

    @FXML
    private ListView<String> triageWaitingTagListView; //not used yet
    @FXML
    private ListView<String> triageProgressTagListView; //not used yet
    @FXML
    private ListView<String> educationWaitingTagListView; //not used yet
    @FXML
    private ListView<String> educationProgressTagListView; //not used yet
    @FXML
    private ListView<String> doctorWaitingTagListView; //not used yet
    @FXML
    private ListView<String> doctorProgressTagListView; //not used yet
    @FXML
    private ListView<String> physioWaitingTagListView; //not used yet
    @FXML
    private ListView<String> physioProgressTagListView; //not used yet
    @FXML
    private ListView<String> pharmacyWaitingTagListView; //not used yet
    @FXML
    private ListView<String> pharmacyProgressTagListView; //not used yet

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

    /*
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
    private Button physioWaitingToProgressButton;
    @FXML
    private Button physioProgressToPharmacyButton;
    @FXML
    private Button pharmacyWaitingToProgressButton;
    @FXML
    private Button pharmacyProgressToCheckoutButton;
    */

    private final HashMap<String, QueueManager> buttonQueueManagerMap = new HashMap<>();
    private final HashMap<String, QueueManager> choiceQueueManagerMap = new HashMap<>();


    @FXML
    private TextField addQueueNumberTextField;
    @FXML
    private TextField moveQueueNumberTextField;

    @FXML
    private ChoiceBox<String> addTargetChoiceBox;
    @FXML
    private ChoiceBox<String> moveTargetChoiceBox;

    @FXML
    private Button moveUpButton;
    @FXML
    private Button moveDownButton;

    @FXML
    private Label warningLabel;
    @FXML
    private ImageView warningImageView;

    QueueManager lastSelectedQM = null;

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
                "Physiotherapy: Waiting", "Physiotherapy: In-Progress",
                "Pharmacy: Waiting", "Pharmacy: In-Progress"};
        addTargetChoiceBox.getItems().addAll(choiceBoxItems);
        moveTargetChoiceBox.getItems().addAll(choiceBoxItems);
        moveTargetChoiceBox.getItems().add("Remove");

        for(QueueManager qm : buttonQueueManagerMap.values()) {
            qm.getCurrentListView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    lastSelectedQM = qm; // Update the most recently selected ListView
                } else {
                    lastSelectedQM = null;
                }
            });
        }
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

        QueueManager pharmacyProgress = new QueueManager(pharmacyProgressListView, "pharmacyProgressTable", pharmacyProgressTagListView,null);
        QueueManager pharmacyWaiting = new QueueManager(pharmacyWaitingListView, "pharmacyWaitingTable", pharmacyWaitingTagListView, pharmacyProgress);
        QueueManager physioProgress = new QueueManager(physioProgressListView, "physioProgressTable", physioProgressTagListView, pharmacyWaiting);
        QueueManager physioWaiting = new QueueManager(physioWaitingListView, "physioWaitingTable", physioWaitingTagListView, physioProgress);
        QueueManager doctorProgress = new QueueManager(doctorProgressListView, "doctorProgressTable", doctorProgressTagListView, pharmacyWaiting, physioWaiting, "Physiotherapist");
        QueueManager doctorWaiting = new QueueManager(doctorWaitingListView, "doctorWaitingTable", doctorWaitingTagListView, doctorProgress);
        QueueManager educationProgress = new QueueManager(educationProgressListView, "educationProgressTable", educationProgressTagListView, doctorWaiting);
        QueueManager educationWaiting = new QueueManager(educationWaitingListView, "educationWaitingTable", educationWaitingTagListView, educationProgress);
        QueueManager triageProgress = new QueueManager(triageProgressListView, "triageProgressTable", triageProgressTagListView, educationWaiting);
        QueueManager triageWaiting = new QueueManager(triageWaitingListView, "triageWaitingTable", triageWaitingTagListView, triageProgress);

        buttonQueueManagerMap.put("triageWaitingToProgressButton", triageWaiting);
        buttonQueueManagerMap.put("triageProgressToEducationButton", triageProgress);
        buttonQueueManagerMap.put("educationWaitingToProgressButton", educationWaiting);
        buttonQueueManagerMap.put("educationProgressToDoctorButton", educationProgress);
        buttonQueueManagerMap.put("doctorWaitingToProgressButton", doctorWaiting);
        buttonQueueManagerMap.put("doctorProgressToPharmacyButton", doctorProgress);
        buttonQueueManagerMap.put("physioWaitingToProgressButton", physioWaiting);
        buttonQueueManagerMap.put("physioProgressToPharmacyButton", physioProgress);
        buttonQueueManagerMap.put("pharmacyWaitingToProgressButton", pharmacyWaiting);
        buttonQueueManagerMap.put("pharmacyProgressToCheckoutButton", pharmacyProgress);

        choiceQueueManagerMap.put("Triage: Waiting", triageWaiting);
        choiceQueueManagerMap.put("Triage: In-Progress", triageProgress);
        choiceQueueManagerMap.put("Education: Waiting", educationWaiting);
        choiceQueueManagerMap.put("Education: In-Progress", educationProgress);
        choiceQueueManagerMap.put("Consultation: Waiting", doctorWaiting);
        choiceQueueManagerMap.put("Consultation: In-Progress", doctorProgress);
        choiceQueueManagerMap.put("Physiotherapy: Waiting", physioWaiting);
        choiceQueueManagerMap.put("Physiotherapy: In-Progress", physioProgress);
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

            String current = searchQueueNumber(queueNumber);
            String target = addTargetChoiceBox.getSelectionModel().getSelectedItem();
            if (current != null) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Patient already exists in " + current, "#bf1b15", "/icons/cross.png");
                clearAddFields();
                return; //patient already exists in queue
            }
            if (target == null || target.isBlank()) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Select a target queue", "#bf1b15", "/icons/cross.png");
                clearAddFields();
                return;
            }

            QueueManager qm1 = choiceQueueManagerMap.get(target);
            QueueManager.addNew(queueNumber, qm1);

            clearAddFields();

            Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Patient successfully added", "#5f8b07", "/icons/tick.png");

        } catch (Exception e) {
            Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#bf1b15", "/icons/cross.png");
        }
    }

    @FXML
    private void moveButtonOnAction(ActionEvent event) {

        QueueManager sourceQM;
        QueueManager targetQM;
        try {
            if (moveQueueNumberTextField.getText().isEmpty() || !moveQueueNumberTextField.getText().matches("\\d+")) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Input a queue number", "#bf1b15", "/icons/cross.png");
                return;
            }

            int queueNumber = Integer.parseInt(moveQueueNumberTextField.getText());

            String current = searchQueueNumber(queueNumber); //currentChoiceBox.getSelectionModel().getSelectedItem();
            String target = moveTargetChoiceBox.getSelectionModel().getSelectedItem();
            if (current == null) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Queue number needs to be added first", "#bf1b15", "/icons/cross.png");
                clearMoveFields();
                return;
            }
            if (target == null) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Select a target queue", "#bf1b15", "/icons/cross.png");
                clearMoveFields();
                return;
            }

            sourceQM = choiceQueueManagerMap.get(current);
            if (target.equals("Remove")) {
                QueueManager.remove(queueNumber, sourceQM);
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Patient successfully removed", "#5f8b07", "/icons/tick.png");
            } else {
                targetQM = choiceQueueManagerMap.get(target);
                QueueManager.move(queueNumber, sourceQM, targetQM);
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Patient successfully moved", "#5f8b07", "/icons/tick.png");
            }

            clearMoveFields();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#5f8b07", "/icons/tick.png");
            Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#bf1b15", "/icons/cross.png");
        }
    }

    @FXML
    private void reorderButtonOnAction(ActionEvent event) {
        try {

            if (this.lastSelectedQM == null) {
                Labels.iconWithMessageDisplay(warningLabel, warningImageView, "Select a queue", "#bf1b15", "/icons/cross.png");
                return;
            }

            Button button = (Button) event.getSource();
            String buttonId = button.getId();

            if (buttonId.equals("moveUpButton")) {
                this.lastSelectedQM.swapPosition(true);
            } else {
                this.lastSelectedQM.swapPosition(false);
            }
        } catch (Exception e) {
            Labels.iconWithMessageDisplay(warningLabel, warningImageView, e.getMessage(), "#bf1b15", "/icons/cross.png");
        }
    }

    private String searchQueueNumber(int queueNumber) {
        for (Entry entry : choiceQueueManagerMap.entrySet()) {
            if (((QueueManager)entry.getValue()).search(queueNumber)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    private void clearAddFields() {
        addQueueNumberTextField.setText("");
        addTargetChoiceBox.setValue(null);
    }

    private void clearMoveFields() {
        moveQueueNumberTextField.setText("");
        moveTargetChoiceBox.setValue(null);
    }

}


