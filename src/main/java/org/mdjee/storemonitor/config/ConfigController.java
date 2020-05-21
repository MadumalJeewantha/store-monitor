package org.mdjee.storemonitor.config;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.utils.BackupRestore;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Notification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

import static org.mdjee.storemonitor.config.ConfigInitializer.populateSampleSecurityQuestion;
import static org.mdjee.storemonitor.utils.Commons.getStage;

public class ConfigController {
    private static final Logger logger = LogManager.getLogger(ConfigInitializer.class);
    @FXML
    private JFXTextField txt_severHost;

    @FXML
    private JFXTextField txt_userName;

    @FXML
    private JFXPasswordField txt_password;

    @FXML
    private JFXTextField txt_port;

    @FXML
    private JFXButton btn_cancel;

    @FXML
    private JFXButton btn_execute;

    @FXML
    private JFXButton btn_back;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private StackPane stackPane;

    private boolean isConfigurationInProgress = false;

    @FXML
    void initialize() {
        // Initialize validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        txt_password.getValidators().add(requiredFieldValidator);
        txt_password.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                txt_password.validate();
            }
        });

        txt_port.getValidators().add(requiredFieldValidator);
        txt_port.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                txt_port.validate();
            }
        });

        txt_severHost.getValidators().add(requiredFieldValidator);
        txt_severHost.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                txt_severHost.validate();
            }
        });

        txt_userName.getValidators().add(requiredFieldValidator);
        txt_userName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                txt_userName.validate();
            }
        });

        // Set value after loading
        Platform.runLater(() -> {
            // Bind window close event
            stackPane.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

        });

    }

    @FXML
    void cancel(ActionEvent event) {

        // Show confirmation dialog
        String message = "Do you want to cancel configuration process?";
        JFXDialogLayout content = Notification.showJFXDialogLayout("Configuration wizard",
                FontAwesomeIcon.WARNING, "2em",
                "common-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("danger-button");

        JFXButton noButton = new JFXButton("No");

        noButton.setOnAction(event1 -> dialog.close());
        yesButton.setOnAction(event1 -> System.exit(0));

        content.setActions(noButton, yesButton);
        dialog.show();
    }

    @FXML
    void execute(ActionEvent event) throws FileNotFoundException, SQLException, ClassNotFoundException {

        // Text fields
        String password = txt_password.getText();
        String username = txt_userName.getText();
        String host = txt_severHost.getText();
        String port = txt_port.getText();

        // Validations
        if (password.isEmpty() || username.isEmpty() || host.isEmpty() || port.isEmpty()) {
            txt_password.validate();
            txt_userName.validate();
            txt_severHost.validate();
            txt_port.validate();
        } else {
            // TODO: call to worker
            btn_back.setDisable(true);
            btn_cancel.setDisable(true);
            btn_execute.setDisable(true);

            isConfigurationInProgress = true;

            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            Task initializeExecuteWorker = initializeExecute(password, username, host, port);
            progressBar.progressProperty().bind(initializeExecuteWorker.progressProperty());
            initializeExecuteWorker.messageProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println(newValue);
                if (newValue.equals("Configuration completed.")) {
                    btn_back.setDisable(false);
                    btn_cancel.setDisable(false);
                    btn_execute.setDisable(false);

                    isConfigurationInProgress = false;
                }
            });

            new Thread(initializeExecuteWorker).start();
            Platform.setImplicitExit(false);
        }

    }

    public Task initializeExecute(String password, String username, String host, String port) {
        return new Task() {
            @Override
            protected Object call() throws Exception {

                updateMessage("Initializing  configurations.");

                // Generate hibernate config file
                ConfigInitializer.createHibernateConfigFile(password, username, host, port);
                updateMessage("Configuration file created.");
                updateProgress(10, 100);

                // Initialize database
                try {
                    // Create connection using standard database
                    updateMessage("Creating database connection.");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection testConn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/mysql", username, password);
                    Statement statement = testConn.createStatement();
                    updateProgress(15, 100);


                    updateMessage("Checking database status.");
                    // Database status
                    boolean isExistDatabase = false;
                    ResultSet resultSet = testConn.getMetaData().getCatalogs();
                    // iterate each catalog in the ResultSet
                    while (resultSet.next()) {
                        // Get the database name, which is at position 1
                        String databaseName = resultSet.getString(1);
                        if (databaseName.equals(Commons.DATABASE_NAME)) {
                            isExistDatabase = true;
                            logger.info("Database already exists: " + Commons.DATABASE_NAME);
                            break;
                        }
                    }
                    resultSet.close();
                    updateMessage("Done checking database status.");
                    updateProgress(30, 100);


                    // If database exists
                    if (isExistDatabase) {
                        // Show confirmation dialog
                        String message = "Database already exists. Do you want to replace?\n This will remove your existing data.";
                        JFXDialogLayout content = Notification.showJFXDialogLayout("Configuration wizard",
                                FontAwesomeIcon.WARNING, "2em",
                                "warning-icon", message);

                        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                        // Prevent closes when clicking away
                        dialog.setOverlayClose(false);

                        JFXButton yesButton = new JFXButton("Yes");
                        yesButton.getStyleClass().add("danger-button");

                        JFXButton noButton = new JFXButton("No");

                        // No button
                        noButton.setOnAction(event1 -> {
                            // Close dialog
                            dialog.close();
                            // Skip database replace
                            logger.info("Skip existing database replace.");
                            updateMessage("Skip existing database replace.");
                            // Close resources
                            try {
                                statement.close();
                                testConn.close();
                            } catch (SQLException e) {
                                logger.error(e.getMessage());
                                e.printStackTrace();
                            }
                            // Show confirmation message and load sign in
                            showLogInWindow();
                        });
                        // Yes button
                        yesButton.setOnAction(event1 -> {
                            // Close dialog
                            dialog.close();

                            // Replace existing database
                            try {

                                updateMessage("Start replacing database.");
                                statement.executeUpdate("DROP SCHEMA IF EXISTS " + Commons.DATABASE_NAME);
                                statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + Commons.DATABASE_NAME);
                                // Close resources
                                statement.close();
                                testConn.close();
                                logger.info("Database replaced successfully.");

                                updateMessage("Done replacing database.");
                                updateProgress(80, 100);


                                // Populate SampleSecurityQuestions
                                populateSampleSecurityQuestion();

                                updateMessage("Configuration completed.");
                                updateProgress(100, 100);
                                // Show confirmation message and load user registration
                                showSuccessfulMessage();
                            } catch (SQLException e) {
                                logger.error(e.getMessage());
                                e.printStackTrace();
                            }
                        });

                        content.setActions(noButton, yesButton);

                        // Show in another method to prevent from : Not on FX application thread
                        showDatabaseAlreadyExistsDialog(dialog);
                        // dialog.show();
                    } else {
                        // Create database
                        statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + Commons.DATABASE_NAME);
                        logger.info(("Database created: " + Commons.DATABASE_NAME));

                        updateMessage("Database created: " + Commons.DATABASE_NAME);
                        updateProgress(80, 100);

                        // Populate SampleSecurityQuestions
                        populateSampleSecurityQuestion();
                        // Close resources
                        statement.close();
                        testConn.close();

                        updateMessage("Configuration completed.");
                        updateProgress(100, 100);

                        // Show confirmation message and load user registration
                        showSuccessfulMessage();
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }

                return true;
            }

            private void showDatabaseAlreadyExistsDialog(JFXDialog dialog) {
                Platform.runLater(() -> dialog.show());
            }
        };
    }

    @FXML
    void showPrevious(ActionEvent event) throws IOException {
        // Close current window
        ((Stage) btn_back.getScene().getWindow()).close();

        // Show Config window
        Stage stage = new Stage();
        stage = getStage(stage, App.class.getResource("config/WelcomeConfig.fxml"), false,
                "Configuration Wizard");
        stage.sizeToScene();
        stage.show();
    }

    public void showSuccessfulMessage() {

        Platform.runLater(() -> {
            // Show Status
            String message = "Congratulations, you have completed all the configurations.\n" +
                    "Let's create a username and password.";
            JFXDialogLayout content = Notification.showJFXDialogLayout("Configuration wizard",
                    FontAwesomeIcon.INFO_CIRCLE, "2em",
                    "common-icon", message);

            JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
            // Prevent closes when clicking away
            dialog.setOverlayClose(false);

            JFXButton okButton = new JFXButton("Ok");
            okButton.getStyleClass().add("common-button");

            okButton.setOnAction(event1 -> {
                // Close current window
                ((Stage) btn_back.getScene().getWindow()).close();

                // Show Config window
                Stage stage = new Stage();
                try {
                    stage = getStage(stage, App.class.getResource("config/UserRegistration.fxml"), false,
                            "User Registration");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.sizeToScene();
                stage.show();
            });

            content.setActions(okButton);
            dialog.show();
        });

    }

    public void showLogInWindow() {
        // Update progressbar
        progressBar.setProgress(100);

        // Show Status
        String message = "Congratulations, you have completed all the configurations.\n" +
                "Please use your latest username and password to sign in.";
        JFXDialogLayout content = Notification.showJFXDialogLayout("Configuration wizard",
                FontAwesomeIcon.INFO_CIRCLE, "2em",
                "common-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        JFXButton okButton = new JFXButton("Ok");
        okButton.getStyleClass().add("common-button");

        okButton.setOnAction(event1 -> {
            // Close current window
            ((Stage) btn_back.getScene().getWindow()).close();

            // Show Config window
            Stage stage = new Stage();
            try {
                stage = getStage(stage, App.class.getResource("login/Login.fxml"), false,
                        "User Registration");
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.sizeToScene();
            stage.show();
        });

        content.setActions(okButton);
        dialog.show();
    }

    private void closeWindowEvent(WindowEvent event) {
        // Prevent from closing window
        event.consume();

        if (isConfigurationInProgress) {
            // Show Status
            JFXDialogLayout content = Notification.showJFXDialogLayout("Store monitor",
                    FontAwesomeIcon.WARNING, "2em",
                    "warning-icon", "Please wait, configuration in progress.");

            JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
            // Prevent closes when clicking away
            dialog.setOverlayClose(false);

            // Create Yes button
            JFXButton okButton = new JFXButton("Ok");
            okButton.getStyleClass().add("danger-button");

            okButton.setOnAction(event1 -> {
                // Close dialog
                dialog.close();
            });

            content.setActions(okButton);
            dialog.show();

        } else {
            // Show Status
            JFXDialogLayout content = Notification.showJFXDialogLayout("Store monitor",
                    FontAwesomeIcon.WARNING, "2em",
                    "warning-icon", "There can be some unsaved changes.\n" +
                            "Are you sure you want to close Store Monitor?");

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

            });

            content.setActions(noButton, yesButton);
            dialog.show();
        }


    }
}
