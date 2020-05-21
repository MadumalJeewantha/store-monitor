package org.mdjee.storemonitor.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.mdjee.storemonitor.utils.Notification;

import java.awt.*;
import java.net.URI;

public class AboutController {

    @FXML
    private StackPane stackPane;

    /**
     * Open my linked-in profile
     * @param event
     */
    @FXML
    void openBrowser(ActionEvent event) {

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("https://www.linkedin.com/in/madumal-jeewantha/"));
        } catch (Exception e) {
            showInfoFXDialog("Something went wrong while opening web browser. Please use below link.\n" +
                    "https://www.linkedin.com/in/madumal-jeewantha/");
            e.printStackTrace();
        }
    }

    private void showInfoFXDialog(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("About",
                FontAwesomeIcon.INFO_CIRCLE, "2em",
                "common-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton okButton = new JFXButton("Ok");
        okButton.getStyleClass().add("common-button");
        okButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        content.setActions(okButton);
        dialog.show();
    }

}
