package org.mdjee.storemonitor.login;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.config.UserRegistrationController;
import org.mdjee.storemonitor.hibernate.entity.SampleSecurityQuestion;
import org.mdjee.storemonitor.hibernate.entity.User;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Encryptor;
import org.mdjee.storemonitor.utils.Notification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.mdjee.storemonitor.utils.Commons.getStage;

public class LoginController {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane signInPane;

    @FXML
    private JFXTextField userNameTextField;

    @FXML
    private JFXPasswordField passwordPasswordField;

    @FXML
    private JFXButton signInButton;

    @FXML
    private JFXButton forgetPasswordButton;

    @FXML
    private AnchorPane userNamePane;

    @FXML
    private JFXTextField userNameUserNamePaneTextField;

    @FXML
    private JFXButton checkUserNameButton;

    @FXML
    private JFXButton signInUserNamePaneButton;

    @FXML
    private AnchorPane question1Pane;

    @FXML
    private JFXTextField answer1TextField;

    @FXML
    private JFXButton nextQuestionQuestion1PaneButton;

    @FXML
    private Label question1Label;

    @FXML
    private JFXButton signInQuestion1PaneButton;

    @FXML
    private AnchorPane question2Pane;

    @FXML
    private JFXTextField answer2TextField;

    @FXML
    private JFXButton nextQuestionQuestion2PaneButton;

    @FXML
    private Label question2Label;

    @FXML
    private JFXButton signInQuestion2PaneButton;

    @FXML
    private AnchorPane question3Pane;

    @FXML
    private JFXTextField answer3TextField;

    @FXML
    private JFXButton nextQuestionQuestion3PaneButton;

    @FXML
    private Label question3Label;

    @FXML
    private JFXButton signInQuestion3PaneButton;

    @FXML
    private AnchorPane passwordResetPane;

    @FXML
    private JFXPasswordField confirmPasswordResetPasswordPanePasswordField;

    @FXML
    private JFXButton createPasswordPasswordResetPaneButton;

    @FXML
    private JFXPasswordField passwordResetPasswordPanePasswordField;

    @FXML
    private JFXButton signInChangePasswordPanePaneButton;

    User forgetPasswordUser;
    private static final Logger logger = LogManager.getLogger(UserRegistrationController.class);

    @FXML
    void initialize() {

        // Initialize validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        // userNameTextField
        userNameTextField.getValidators().add(requiredFieldValidator);
        userNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                userNameTextField.validate();
            }
        });

        // passwordPasswordField
        passwordPasswordField.getValidators().add(requiredFieldValidator);
        passwordPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                passwordPasswordField.validate();
            }
        });

        // userNameUserNamePaneTextField
        userNameUserNamePaneTextField.getValidators().add(requiredFieldValidator);
        userNameUserNamePaneTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                userNameUserNamePaneTextField.validate();
            }
        });

        // answer1TextField
        answer1TextField.getValidators().add(requiredFieldValidator);
        answer1TextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                answer1TextField.validate();
            }
        });

        // answer2TextField
        answer2TextField.getValidators().add(requiredFieldValidator);
        answer2TextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                answer2TextField.validate();
            }
        });

        // answer3TextField
        answer3TextField.getValidators().add(requiredFieldValidator);
        answer3TextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                answer3TextField.validate();
            }
        });

        // passwordResetPasswordPanePasswordField
        passwordResetPasswordPanePasswordField.getValidators().add(requiredFieldValidator);
        passwordResetPasswordPanePasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                passwordResetPasswordPanePasswordField.validate();
            }
        });

        // passwordResetPasswordPanePasswordField
        confirmPasswordResetPasswordPanePasswordField.getValidators().add(requiredFieldValidator);
        confirmPasswordResetPasswordPanePasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                confirmPasswordResetPasswordPanePasswordField.validate();
            }
        });
    }

    @FXML
    void createPassword(ActionEvent event) {
        // Text fields
        String password = passwordResetPasswordPanePasswordField.getText();
        String confirmPassword = confirmPasswordResetPasswordPanePasswordField.getText();

        // Validations
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            passwordResetPasswordPanePasswordField.validate();
            confirmPasswordResetPasswordPanePasswordField.validate();
        } else {
            if (password.equals(confirmPassword)) {
                // Set user data
                Encryptor encryptor = new Encryptor();

                forgetPasswordUser.setPassword(encryptor.encrypt(password));
                // Update user
                updateUser();
                // Show message and load Home
                showInfoMessage("Successfully changed password.");


            } else {
                // Show password mismatch message
                showWarningMessage("Password mismatch. Please confirm your password.");
            }
        }
    }

    private void updateUser() {
        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // update User
            session.update(forgetPasswordUser);

            // commit transaction
            transaction.commit();

            logger.info("Successfully changed password.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    @FXML
    void forgetPassword(ActionEvent event) {
        signInPane.setVisible(false);
        userNamePane.setVisible(true);
        new FadeIn(userNamePane).play();
    }

    @FXML
    void showCreateNewPasswordPane(ActionEvent event) {
        String answer3 = answer3TextField.getText();

        if (answer3.isEmpty()) {
            answer3TextField.validate();
        } else {
            // Check Answer3
            Encryptor encryptor = new Encryptor();
            if (answer3.equals(encryptor.decrypt(forgetPasswordUser.getSecurityQuestion().getAnswer3()))) {
                // Show passwordResetPane
                question3Pane.setVisible(false);
                passwordResetPane.setVisible(true);
                new FadeIn(passwordResetPane).play();
            } else {
                // Wrong answer
                showWarningMessage("Wrong answer.");
                answer3TextField.clear();
            }

        }
    }

    @FXML
    void showQuestion1Pane(ActionEvent event) {
        String username = userNameUserNamePaneTextField.getText();

        if (username.isEmpty()) {
            userNameUserNamePaneTextField.validate();
        } else {
            // Search for username
            List<User> list = getUser(username);

            if (list.isEmpty()) {
                // Username not found
                showWarningMessage("Username not found. Please check your username.");
                userNameUserNamePaneTextField.clear();
            } else {
                forgetPasswordUser = list.get(0);

                // Load questions
                String question1 = getSecurityQuestion(forgetPasswordUser.getSecurityQuestion().getQuestion1());
                String question2 = getSecurityQuestion(forgetPasswordUser.getSecurityQuestion().getQuestion2());
                String question3 = getSecurityQuestion(forgetPasswordUser.getSecurityQuestion().getQuestion3());

                // Set to labels
                question1Label.setText(question1);
                question2Label.setText(question2);
                question3Label.setText(question3);

                // Show Question1 Pane
                userNamePane.setVisible(false);
                question1Pane.setVisible(true);
                new FadeIn(question1Pane).play();
            }
        }
    }

    private String getSecurityQuestion(int questionId) {
        Transaction transaction = null;
        String question = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from SampleSecurityQuestion where id = :questionId", SampleSecurityQuestion.class);
            query.setParameter("questionId", questionId);
            SampleSecurityQuestion result = (SampleSecurityQuestion) query.uniqueResult();
            question = result.getQuestion();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return question;
    }

    @FXML
    void showQuestion2Pane(ActionEvent event) {
        String answer1 = answer1TextField.getText();

        if (answer1.isEmpty()) {
            answer1TextField.validate();
        } else {
            // Check Answer1
            Encryptor encryptor = new Encryptor();
            if (answer1.equals(encryptor.decrypt(forgetPasswordUser.getSecurityQuestion().getAnswer1()))) {
                // Show showQuestion2Pane
                question1Pane.setVisible(false);
                question2Pane.setVisible(true);
                new FadeIn(question2Pane).play();
            } else {
                // Wrong answer
                showWarningMessage("Wrong answer.");
                answer1TextField.clear();
            }

        }
    }

    @FXML
    void showQuestion3Pane(ActionEvent event) {
        String answer2 = answer2TextField.getText();

        if (answer2.isEmpty()) {
            answer2TextField.validate();
        } else {
            // Check Answer1
            Encryptor encryptor = new Encryptor();
            if (answer2.equals(encryptor.decrypt(forgetPasswordUser.getSecurityQuestion().getAnswer2()))) {
                // Show showQuestion2Pane
                question2Pane.setVisible(false);
                question3Pane.setVisible(true);
                new FadeIn(question3Pane).play();
            } else {
                // Wrong answer
                showWarningMessage("Wrong answer.");
                answer2TextField.clear();
            }

        }
    }

    @FXML
    void showSignInPane(ActionEvent event) {
        userNamePane.setVisible(false);
        passwordResetPane.setVisible(false);
        question1Pane.setVisible(false);
        question2Pane.setVisible(false);
        question3Pane.setVisible(false);

        signInPane.setVisible(true);
        new FadeIn(signInPane).play();
    }

    // Sign in
    @FXML
    void signIn(ActionEvent event) {

        String username = userNameTextField.getText();
        String password = passwordPasswordField.getText();

        // Validations
        if (password.isEmpty() || username.isEmpty()) {
            userNameTextField.validate();
            passwordPasswordField.validate();
        } else {
            // Check username
            List<User> list = getUser(username);
            if (list.isEmpty()) {
                // Show message
                // Wrong username please try again
                showWarningMessage("Username not found. Please check your username.");
                // Clear fields
                userNameTextField.clear();
                passwordPasswordField.clear();
            } else {
                // Check password
                User user = list.get(0);
                Encryptor encryptor = new Encryptor();
                if (password.equals(encryptor.decrypt(user.getPassword())) && username.equals(user.getUserName())) {
                    // Update last login datetime
                    updateLastLogIn(user);

                    // Update current user
                    Commons.currentUser = user;

                    // Show Home
                    // Close current window
                    showHome();
                } else {
                    // Show password doesn't match with your username
                    showWarningMessage("Password doesn't match with your username.");
                    // Clear fields
                    userNameTextField.clear();
                    passwordPasswordField.clear();
                }
            }

        }
    }

    private void updateLastLogIn(User user) {
        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Change lastlogin
            user.setLastLogin(new Date());
            // start a transaction
            transaction = session.beginTransaction();

            // update User
            session.update(user);

            // commit transaction
            transaction.commit();

            logger.info("Successfully logged in user:" + user.getUserName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private void showHome() {
        ((Stage) userNameTextField.getScene().getWindow()).close();

        // Show Config window
        Stage stage = new Stage();
        try {
            stage = getStage(stage, App.class.getResource("home/Home.fxml"), true,
                    "Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setMaximized(true);
        stage.show();
    }

    private void showWarningMessage(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Login",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        JFXButton okButton = new JFXButton("Ok");
        okButton.getStyleClass().add("common-button");

        okButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        content.setActions(okButton);
        dialog.show();
    }

    private void showInfoMessage(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Login",
                FontAwesomeIcon.INFO_CIRCLE, "2em",
                "common-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        JFXButton okButton = new JFXButton("Ok");
        okButton.getStyleClass().add("common-button");

        okButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
            // Show Home
            showHome();
        });

        content.setActions(okButton);
        dialog.show();
    }

    private List getUser(String username) {
        Transaction transaction = null;
        List<User> list = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from User where userName =:username", User.class);
            query.setParameter("username", username);
            list = query.list();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return list;
    }

}