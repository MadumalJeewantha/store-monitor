package org.mdjee.storemonitor.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Notification;

import java.io.IOException;

public class SettingsController {

    private String fileName;

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXButton aboutButton;

    @FXML
    private JFXButton userAccountsButton;

    @FXML
    private JFXButton securityButton;

    @FXML
    private JFXButton vehicleButton;

    @FXML
    private JFXButton reportsButton;

    @FXML
    void closeWindow(ActionEvent event) {
        // Invoke to  WindowEvent.WINDOW_CLOSE_REQUEST
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    @FXML
    void showAbout(ActionEvent event) {
        setUI("About.fxml");
    }

    @FXML
    void showReports(ActionEvent event) {
        setUI("Reports.fxml");
    }

    @FXML
    void showSecurity(ActionEvent event) {
        setUI("Security.fxml");
    }

    @FXML
    void showUserAccounts(ActionEvent event) {
        setUI("UserAccounts.fxml");
    }

    @FXML
    void showVehicles(ActionEvent event) {
        setUI("Vehicles.fxml");
    }

    @FXML
    void initialize() {

        // Set value after loading
        Platform.runLater(() -> {
            // Set borderPane center layout
            setUI();
            // Bind window close event
            stackPane.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
        });

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUI() {

        if (!fileName.isEmpty()) {
            Parent root = null;
            try {
                root = FXMLLoader.load(App.class.getResource("settings/" + this.fileName));
                setSliderStyle(this.fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
            borderPane.setCenter(root);
        }
    }

    public void setUI(String fileName) {
        System.out.println("Changing stage into: " + fileName);
        Parent root = null;
        try {
            root = FXMLLoader.load(App.class.getResource("settings/" + fileName));
            setSliderStyle(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        borderPane.setCenter(root);
    }

    private void setSliderStyle(String str) {
        switch (str) {
            case "About.fxml":
                aboutButton.getStyleClass().add("selected-button");
                reportsButton.getStyleClass().remove("selected-button");
                securityButton.getStyleClass().remove("selected-button");
                userAccountsButton.getStyleClass().remove("selected-button");
                vehicleButton.getStyleClass().remove("selected-button");
                break;
            case "Reports.fxml":
                aboutButton.getStyleClass().remove("selected-button");
                reportsButton.getStyleClass().add("selected-button");
                securityButton.getStyleClass().remove("selected-button");
                userAccountsButton.getStyleClass().remove("selected-button");
                vehicleButton.getStyleClass().remove("selected-button");
                break;
            case "Security.fxml":
                aboutButton.getStyleClass().remove("selected-button");
                reportsButton.getStyleClass().remove("selected-button");
                securityButton.getStyleClass().add("selected-button");
                userAccountsButton.getStyleClass().remove("selected-button");
                vehicleButton.getStyleClass().remove("selected-button");
                break;
            case "UserAccounts.fxml":
                aboutButton.getStyleClass().remove("selected-button");
                reportsButton.getStyleClass().remove("selected-button");
                securityButton.getStyleClass().remove("selected-button");
                userAccountsButton.getStyleClass().add("selected-button");
                vehicleButton.getStyleClass().remove("selected-button");
                break;
            case "Vehicles.fxml":
                aboutButton.getStyleClass().remove("selected-button");
                reportsButton.getStyleClass().remove("selected-button");
                securityButton.getStyleClass().remove("selected-button");
                userAccountsButton.getStyleClass().remove("selected-button");
                vehicleButton.getStyleClass().add("selected-button");
                break;
        }

    }

    private void closeWindowEvent(WindowEvent event) {
        // Prevent from closing window
        event.consume();

        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Store monitor",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "There can be some unsaved changes.\n" +
                        "Are you sure you want to close Settings window?");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("danger-button");
        JFXButton noButton = new JFXButton("No");

        noButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        yesButton.setOnAction(event1 -> {
            // Exit from Store App
            ((Stage) stackPane.getScene().getWindow()).close();

            System.out.println("You are closing Settings App");
            Commons.settingsApp = null;
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }

}
