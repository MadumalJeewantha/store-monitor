package org.mdjee.storemonitor.settings;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mdjee.storemonitor.hibernate.entity.User;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.store.SendToVehicleController;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Encryptor;
import org.mdjee.storemonitor.utils.Notification;

public class UserAccountsController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane newPasswordPane;

    @FXML
    private JFXPasswordField newPasswordPasswordField;

    @FXML
    private JFXPasswordField confirmPasswordPasswordField;

    @FXML
    private JFXButton changePasswordButton;

    @FXML
    private VBox currentPasswordVBox;

    @FXML
    private JFXPasswordField currentPasswordPasswordField;

    @FXML
    private JFXButton nextButton;

    /**
     * Logger instance for SendToVehicleController class.
     */
    private static final Logger logger = LogManager.getLogger(SendToVehicleController.class);

    /**
     * Change password button event.
     *
     * @param event
     */
    @FXML
    void changePassword(ActionEvent event) {
        boolean newPasswordValidate = newPasswordPasswordField.validate();
        boolean confirmPasswordValidate = confirmPasswordPasswordField.validate();

        if (newPasswordValidate && confirmPasswordValidate) {
            // Check passwords
            if (newPasswordPasswordField.getText().equals(confirmPasswordPasswordField.getText())) {
                // Change password
                saveNewPassword(Commons.currentUser, newPasswordPasswordField.getText());

                // Clear fields
                currentPasswordPasswordField.clear();
                newPasswordPasswordField.clear();
                confirmPasswordPasswordField.clear();
                // Hide panes
                newPasswordPane.setVisible(false);
                currentPasswordVBox.setVisible(true);
                new FadeIn(currentPasswordVBox).play();

            } else {
                // Show warning
                showWarningFXDialog("Password mismatch. Please confirm your password.");

                // Clear fields
                newPasswordPasswordField.clear();
                confirmPasswordPasswordField.clear();
            }
        }
    }

    /**
     * Save new password.
     *
     * @param user     current user object
     * @param password new password
     */
    private void saveNewPassword(User user, String password) {
        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Change password
            Encryptor encryptor = new Encryptor();
            user.setPassword(encryptor.encrypt(password));
            // start a transaction
            transaction = session.beginTransaction();

            // update User
            session.update(user);

            // commit transaction
            transaction.commit();

            logger.info("Successfully updated password for user: " + user.getUserName());

            // Show notification
            Notification.showInfoNotification("Successfully updated password for user: " + user.getUserName(),
                    "User accounts");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }

    /**
     * Check current password.
     *
     * @param event
     */
    @FXML
    void checkCurrentPassword(ActionEvent event) {
        // Validation
        boolean validate = currentPasswordPasswordField.validate();
        // Validate
        Encryptor encryptor = new Encryptor();
        if (validate) {

            if (currentPasswordPasswordField.getText().equals(encryptor.decrypt(Commons.currentUser.getPassword()))) {
                // Hide current pane
                currentPasswordVBox.setVisible(false);
                newPasswordPane.setVisible(true);
                new FadeIn(newPasswordPane).play();
            }else {
                // Show warning message
                showWarningFXDialog("Password doesn't match with your username.");
                currentPasswordPasswordField.clear();
            }
        }
    }

    /**
     * Make validations and bind event listeners.
     */
    @FXML
    void initialize() {
        // Validators
        // Initialize required field validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        currentPasswordPasswordField.getValidators().add(requiredFieldValidator);
        currentPasswordPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                currentPasswordPasswordField.validate();
            }
        });

        newPasswordPasswordField.getValidators().add(requiredFieldValidator);
        newPasswordPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                newPasswordPasswordField.validate();
            }
        });

        confirmPasswordPasswordField.getValidators().add(requiredFieldValidator);
        confirmPasswordPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                confirmPasswordPasswordField.validate();
            }
        });
    }

    /**
     * Show warning dialog with given message.
     *
     * @param message message to be shown in dialog
     */
    private void showWarningFXDialog(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("User accounts",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton okButton = new JFXButton("Ok");
        okButton.getStyleClass().add("danger-button");
        okButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        content.setActions(okButton);
        dialog.show();
    }
}
