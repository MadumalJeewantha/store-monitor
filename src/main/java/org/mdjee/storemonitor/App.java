package org.mdjee.storemonitor;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.mdjee.storemonitor.config.ConfigInitializer;

import java.io.File;
import java.io.FileNotFoundException;

import static org.mdjee.storemonitor.utils.Commons.getStage;

public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Check Hibernate XML configuration file status
        boolean configFileStatus = ConfigInitializer.checkHibernateConfigFileStatus();

        if (configFileStatus) {
            // Check user status
            boolean userStatus = ConfigInitializer.checkUserStatus();
            if (userStatus) {
                // Show user registration
                primaryStage = getStage(primaryStage, getClass().getResource("config/UserRegistration.fxml"),
                        false, "User Registration");
                primaryStage.show();
            } else {
                // Show login scene
                primaryStage = getStage(primaryStage, getClass().getResource("login/Login.fxml"), false,
                        "Login");
                primaryStage.show();
            }


        } else {
            logger.warn("Couldn't find Hibernate configuration file. Initializing app configurations.");

            // Show WelcomeConfig scene
            primaryStage = getStage(primaryStage, getClass().getResource("config/WelcomeConfig.fxml"), false,
                    "Configuration Wizard");
            primaryStage.sizeToScene();
            primaryStage.show();

        }

    }


    public static void main(String[] args) {
        logger.info("Application started!");

        initLog4j();

        // Launch App
        launch(args);

    }

    private static void initLog4j() {
        //Read setting from configuration file

        try {
            File file = new File("./log4j2.xml");
            if(file.exists()){
                LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
                // this will force a reconfiguration
                context.setConfigLocation(file.toURI());
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }
}
