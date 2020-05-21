package org.mdjee.storemonitor.settings;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.utils.Commons;

import java.io.InputStream;
import java.net.URL;


public class SettingsApp extends Application {

    private static final Logger logger = LogManager.getLogger(SettingsApp.class);
    private String fileName;
    private Stage primaryStageReference;

    public SettingsApp(String fileName) {
        this.fileName = fileName;
    }

    public Stage getPrimaryStageReference(){
        return primaryStageReference;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Create FXMLLoader
        FXMLLoader loader = new FXMLLoader();
        URL resource = App.class.getResource("settings/Settings.fxml");
        loader.setLocation(resource);
        // Get parent from loader
        Parent root = (Parent) loader.load();
        // Get controller
        SettingsController controller = loader.<SettingsController>getController();
        // Change file name
        controller.setFileName(this.fileName);
        // Set window title
        primaryStage.setTitle("Settings");
        // Set resizable
        primaryStage.setResizable(true);
        // Set stage icon
        InputStream image = App.class.getResourceAsStream("images/delivery-truck-icon.png");
        primaryStage.getIcons().add(new Image(image));
        // Sst Maximize
        primaryStage.setMaximized(true);
        // Set new scene
        primaryStage.setScene(new Scene(root));
        // Add close event listener
        // primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
        // Set primaryStageReference
        primaryStageReference = primaryStage;

        // Show
        primaryStage.show();

    }

    private void closeWindowEvent(WindowEvent event) {
        System.out.println("You are closing Settings App");
        Commons.settingsApp = null;
    }

    public static void main(String[] args) {
        logger.info("Settings app started.");
        // Launch App
        launch(args);
    }
}
