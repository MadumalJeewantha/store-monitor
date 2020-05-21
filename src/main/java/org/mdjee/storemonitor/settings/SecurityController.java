package org.mdjee.storemonitor.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mdjee.storemonitor.utils.BackupRestore;
import org.mdjee.storemonitor.utils.Notification;

import java.io.File;

public class SecurityController {

    Task restoreWorker;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXProgressBar backupProgressBar;

    @FXML
    private JFXProgressBar restoreProgressBar;

    @FXML
    private JFXButton backupButton;

    @FXML
    private JFXButton restoreButton;

    private FileChooser fileChooser = new FileChooser();
    DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    void backup(ActionEvent event) {
        // Set title for DirectoryChooser
        directoryChooser.setTitle("Select backup directory");
        // Set Initial Directory
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File dir = directoryChooser.showDialog(stackPane.getScene().getWindow());
        if (dir != null) {

            backupButton.setDisable(true);

            backupProgressBar.progressProperty().unbind();
            backupProgressBar.setProgress(0);
            BackupRestore backupRestore = new BackupRestore();
            Task backupWorker = backupRestore.backupWorker(dir.getAbsolutePath());
            backupProgressBar.progressProperty().bind(backupWorker.progressProperty());
            backupWorker.messageProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println(newValue);
                if (newValue.equals("Backup completed successfully.")) {
                    backupButton.setDisable(false);
                    Notification.showInfoNotification("Backup completed successfully.", "Security center");
                }
            });

            new Thread(backupWorker).start();

        } else {
            Notification.showInfoNotification("You have not selected any directory.", "Security center");
        }


    }

    @FXML
    void restore(ActionEvent event) {
        // Set title for DirectoryChooser
        fileChooser.setTitle("Select your backup file");
        // Set Initial Directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SQL", "*.sql"));

        File file = fileChooser.showOpenDialog(stackPane.getScene().getWindow());
        if (file != null) {
            restoreButton.setDisable(true);

            restoreProgressBar.progressProperty().unbind();
            restoreProgressBar.setProgress(0);
            BackupRestore backupRestore = new BackupRestore();
            Task restoreWorker = backupRestore.restoreWorker(file.getAbsolutePath());
            restoreProgressBar.progressProperty().bind(restoreWorker.progressProperty());
            restoreWorker.messageProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println(newValue);
                if (newValue.equals("Restore completed successfully.")) {
                    restoreButton.setDisable(false);
                    
                    // Show Status
                    JFXDialogLayout content = Notification.showJFXDialogLayout("Security center",
                            FontAwesomeIcon.INFO_CIRCLE, "2em",
                            "common-icon", "Restore completed successfully.\n" +
                                    "Please restart Store Monitor for the applied changes to take effect.");

                    JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                    // Prevent closes when clicking away
                    dialog.setOverlayClose(false);

                    // Create Yes button
                    JFXButton yesButton = new JFXButton("Ok");
                    yesButton.getStyleClass().add("common-button");

                    yesButton.setOnAction(event1 -> {
                        // Exit from App
                        System.exit(0);
                    });

                    content.setActions(yesButton);
                    dialog.show();
                }
            });
            new Thread(restoreWorker).start();
        } else {
            Notification.showInfoNotification("You have not selected any file.", "Security center");
        }


    }

}
