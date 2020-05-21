package org.mdjee.storemonitor.home;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mdjee.storemonitor.settings.SettingsApp;
import org.mdjee.storemonitor.store.StoreApp;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Notification;

import java.awt.*;

public class HomeController {
    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton btn_store;

    @FXML
    private JFXButton btn_balanceStock;

    @FXML
    private JFXButton btn_sendToVehicle;

    @FXML
    private JFXButton btn_addToStore;

    @FXML
    private JFXButton btn_settings;

    @FXML
    private JFXButton btn_reports;

    @FXML
    void showAddToStore(ActionEvent event) throws Exception {
        showStoreUI("AddToStore.fxml");
    }

    @FXML
    void showBalanceStock(ActionEvent event) throws Exception {
        showStoreUI("BalanceStock.fxml");
    }

    @FXML
    void showReports(ActionEvent event) throws Exception {
        showSettingsUI("Reports.fxml");
    }

    @FXML
    void showSendToVehicle(ActionEvent event) throws Exception {
        showStoreUI("SendToVehicle.fxml");
    }

    @FXML
    void showSettings(ActionEvent event) throws Exception {
        showSettingsUI("About.fxml");
    }

    @FXML
    void showStore(ActionEvent event) throws Exception {
        showStoreUI("StoreDetails.fxml");
    }

    @FXML
    void initialize() {
        // Set value after loading
        Platform.runLater(() -> {
            stackPane.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
        });

    }

    private void closeWindowEvent(WindowEvent event) {
        // Prevent from closing window
        event.consume();

        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Store monitor",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", " Are you sure you want to exit Store Monitor?\n" +
                        "Please make sure to save all the works before exit.");

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
            // Exit from App
            System.exit(0);
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }


    private void showStoreUI(String filename) throws Exception {
        // Make this as singleton
        if (Commons.storeApp == null) {
            Commons.storeApp = new StoreApp(filename);
            Commons.storeApp.start(new Stage());
        } else {
            Commons.storeApp.getPrimaryStageReference().requestFocus();
            // TrayNotification
            Notification.showTrayMessage("Store window is already opened.", "Store Monitor",
                    TrayIcon.MessageType.INFO);
        }
    }

    private void showSettingsUI(String filename) throws Exception {
        // Make this as singleton
        if (Commons.settingsApp == null) {
            Commons.settingsApp = new SettingsApp(filename);
            Commons.settingsApp.start(new Stage());
        } else {
            Commons.settingsApp.getPrimaryStageReference().requestFocus();
            // TrayNotification
            Notification.showTrayMessage("Settings window is already opened.", "Store Monitor",
                    TrayIcon.MessageType.INFO);
        }
    }

}