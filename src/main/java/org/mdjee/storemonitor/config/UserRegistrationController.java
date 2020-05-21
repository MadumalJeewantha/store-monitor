package org.mdjee.storemonitor.config;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.hibernate.entity.SampleSecurityQuestion;
import org.mdjee.storemonitor.hibernate.entity.SecurityQuestion;
import org.mdjee.storemonitor.hibernate.entity.User;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Encryptor;
import org.mdjee.storemonitor.utils.Notification;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mdjee.storemonitor.utils.Commons.getStage;

public class UserRegistrationController {

    @FXML
    private AnchorPane pane_userRegistration;

    @FXML
    private JFXTextField txt_userName;

    @FXML
    private JFXPasswordField txt_password;

    @FXML
    private JFXPasswordField txt_confirm_password;

    @FXML
    private JFXButton btn_registerUser;

    @FXML
    private AnchorPane pane_securityQuiz1;

    @FXML
    private JFXButton pane_securityQuiz1_btn_next;

    @FXML
    private JFXComboBox<String> pane_securityQuiz1_list_question;

    @FXML
    private JFXTextField pane_securityQuiz1_txt_answer;

    @FXML
    private AnchorPane pane_securityQuiz2;

    @FXML
    private JFXButton pane_securityQuiz2_btn_next;

    @FXML
    private JFXComboBox<String> pane_securityQuiz2_list_question;

    @FXML
    private JFXTextField pane_securityQuiz2_txt_answer;

    @FXML
    private AnchorPane pane_securityQuiz3;

    @FXML
    private JFXButton pane_securityQuiz3_btn_finish;

    @FXML
    private JFXComboBox<String> pane_securityQuiz3_list_question;

    @FXML
    private JFXTextField pane_securityQuiz3_txt_answer;

    @FXML
    private StackPane stackPane;

    private User user;
    private SecurityQuestion securityQuestion;
    private ObservableList<String> sampleSecurityQuestions = FXCollections.observableArrayList();
    private static final Logger logger = LogManager.getLogger(UserRegistrationController.class);
    Map<String, Integer> sampleSecurityQuestionMap = new HashMap<>();


    @FXML
    void initialize() {
        // Initialize validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        // Password
        txt_password.getValidators().add(requiredFieldValidator);
        txt_password.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                txt_password.validate();
            }
        });

        txt_confirm_password.getValidators().add(requiredFieldValidator);
        txt_confirm_password.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                txt_confirm_password.validate();
            }
        });

        // Username
        txt_userName.getValidators().add(requiredFieldValidator);
        txt_userName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                txt_userName.validate();
            }
        });

        // pane_securityQuiz1_txt_answer
        pane_securityQuiz1_txt_answer.getValidators().add(requiredFieldValidator);
        pane_securityQuiz1_txt_answer.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pane_securityQuiz1_txt_answer.validate();
            }
        });

        // pane_securityQuiz2_txt_answer
        pane_securityQuiz2_txt_answer.getValidators().add(requiredFieldValidator);
        pane_securityQuiz2_txt_answer.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pane_securityQuiz2_txt_answer.validate();
            }
        });

        // pane_securityQuiz3_txt_answer
        pane_securityQuiz3_txt_answer.getValidators().add(requiredFieldValidator);
        pane_securityQuiz3_txt_answer.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pane_securityQuiz3_txt_answer.validate();
            }
        });

        // Load sample security questions
        loadSampleSecurityQuestions();
        // Set to dropdown
        pane_securityQuiz1_list_question.setItems(sampleSecurityQuestions);
        pane_securityQuiz2_list_question.setItems(sampleSecurityQuestions);
        pane_securityQuiz3_list_question.setItems(sampleSecurityQuestions);

        // pane_securityQuiz1_list_question
        pane_securityQuiz1_list_question.getValidators().add(requiredFieldValidator);
        pane_securityQuiz1_list_question.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pane_securityQuiz1_list_question.validate();
            }
        });

        // pane_securityQuiz2_list_question
        pane_securityQuiz2_list_question.getValidators().add(requiredFieldValidator);
        pane_securityQuiz2_list_question.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pane_securityQuiz2_list_question.validate();
            }
        });

        // pane_securityQuiz3_list_question
        pane_securityQuiz3_list_question.getValidators().add(requiredFieldValidator);
        pane_securityQuiz3_list_question.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pane_securityQuiz3_list_question.validate();
            }
        });

    }

    private void loadSampleSecurityQuestions() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<SampleSecurityQuestion> list = session.createQuery("from SampleSecurityQuestion ",
                    SampleSecurityQuestion.class).list();

            list.forEach(s -> {
                sampleSecurityQuestions.add(s.getQuestion());
                sampleSecurityQuestionMap.put(s.getQuestion(), s.getQuestionId());
            });

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
    void registerUser(ActionEvent event) {
        // Text fields
        String username = txt_userName.getText();
        String password = txt_password.getText();
        String confirmPassword = txt_confirm_password.getText();

        // Validations
        if (password.isEmpty() || username.isEmpty() || confirmPassword.isEmpty()) {
            txt_password.validate();
            txt_userName.validate();
            txt_confirm_password.validate();
        } else {
            if (password.equals(confirmPassword)) {
                // Set user data
                user = new User();

                Encryptor encryptor = new Encryptor();

                user.setPassword(encryptor.encrypt(password));
                user.setUserName(username);

                // Show Security Question 1
                pane_userRegistration.setVisible(false);
                pane_securityQuiz1.setVisible(true);
                new FadeIn(pane_securityQuiz1).play();
            } else {
                // Show password mismatch message
                showPasswordMismatchMessage();
            }
        }
    }

    private void showPasswordMismatchMessage() {
        // Show Status
        String message = "Password mismatch. Please confirm your password.";
        JFXDialogLayout content = Notification.showJFXDialogLayout("User Registration",
                FontAwesomeIcon.INFO_CIRCLE, "2em",
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

        // Clear textfield
        txt_confirm_password.clear();

        dialog.show();
    }

    @FXML
    void showSecurityQuiz2Pane(ActionEvent event) {
        // Add security question
        String answer = pane_securityQuiz1_txt_answer.getText();
        String question = pane_securityQuiz1_list_question.getSelectionModel().getSelectedItem();
        int index = pane_securityQuiz1_list_question.getSelectionModel().getSelectedIndex();

        // Validations
        if (answer.isEmpty() || pane_securityQuiz1_list_question.getSelectionModel().isSelected(-1) ) {
            pane_securityQuiz1_txt_answer.validate();
            pane_securityQuiz1_list_question.validate();
        } else {
            // Assign new object
            securityQuestion = new SecurityQuestion();
            // Set data to the object
            Encryptor encryptor = new Encryptor();
            securityQuestion.setQuestion1(sampleSecurityQuestionMap.get(question));
            securityQuestion.setAnswer1(encryptor.encrypt(answer));

            // Remove selected question from Observable list
            sampleSecurityQuestions.remove(index);

            // Show Security Question 2
            pane_securityQuiz1.setVisible(false);
            pane_securityQuiz2.setVisible(true);
            new FadeIn(pane_securityQuiz2).play();
        }

    }

    @FXML
    void showSecurityQuiz3Pane(ActionEvent event) {
        // Add security question
        String answer = pane_securityQuiz2_txt_answer.getText();
        String question = pane_securityQuiz2_list_question.getSelectionModel().getSelectedItem();
        int index = pane_securityQuiz2_list_question.getSelectionModel().getSelectedIndex();

        // Validations
        if (answer.isEmpty() || pane_securityQuiz2_list_question.getSelectionModel().isSelected(-1) ) {
            pane_securityQuiz2_txt_answer.validate();
            pane_securityQuiz2_list_question.validate();
        } else {
            Encryptor encryptor = new Encryptor();
            securityQuestion.setQuestion2(sampleSecurityQuestionMap.get(question));
            securityQuestion.setAnswer2(encryptor.encrypt(answer));

            // Remove selected question from Observable list
            sampleSecurityQuestions.remove(index);

            // Show Security Question 3
            pane_securityQuiz2.setVisible(false);
            pane_securityQuiz3.setVisible(true);
            new FadeIn(pane_securityQuiz2).play();
        }

    }

    @FXML
    void finishUserRegistration(ActionEvent event) {

        // Add security question
        String answer = pane_securityQuiz3_txt_answer.getText();
        String question = pane_securityQuiz3_list_question.getSelectionModel().getSelectedItem();

        // Validations
        if (answer.isEmpty() || pane_securityQuiz3_list_question.getSelectionModel().isSelected(-1) ) {
            pane_securityQuiz3_txt_answer.validate();
            pane_securityQuiz3_list_question.validate();
        } else {
            Encryptor encryptor = new Encryptor();
            securityQuestion.setQuestion3(sampleSecurityQuestionMap.get(question));
            securityQuestion.setAnswer3(encryptor.encrypt(answer));

            user.setSecurityQuestion(securityQuestion);

            // Save to database
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                // start a transaction
                transaction = session.beginTransaction();

                // Change last login
                user.setLastLogin(new Date());

                // save User
                session.save(user);

                // commit transaction
                transaction.commit();
                logger.info("Successfully created a new user.");

                // Update current user
                Commons.currentUser = user;

                showSuccessfulMessage();

            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                    logger.warn("Transaction is null, rolling back.");
                }
                e.printStackTrace();
                logger.error(e.getMessage());
            }

        }
    }

    public void showSuccessfulMessage(){
        // Show Status
        String message = "You have successfully created a new user.\n" +
                "Let's start doing some sales!";
        JFXDialogLayout content = Notification.showJFXDialogLayout("User registration",
                FontAwesomeIcon.INFO_CIRCLE, "2em",
                "common-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        JFXButton okButton = new JFXButton("Ok");
        okButton.getStyleClass().add("common-button");

        okButton.setOnAction(event1 -> {
            // Close current window
            ((Stage) pane_securityQuiz3_btn_finish.getScene().getWindow()).close();

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
        });

        content.setActions(okButton);
        dialog.show();
    }


}
