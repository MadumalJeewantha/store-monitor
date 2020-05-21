package org.mdjee.storemonitor.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.hibernate.entity.User;
import org.mdjee.storemonitor.settings.SettingsApp;
import org.mdjee.storemonitor.store.StoreApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class Commons {

    // TODO: Cannot access fromm jar
    // public static final URL ICON_COMMON = Commons.class.getResource("../images/delivery-truck-icon.png");
    // public static final InputStream ICON_COMMON = App.class.getResourceAsStream("images/delivery-truck-icon.png");
    public static String DATABASE_NAME = "store_monitor_db";

    // StoreApp object to handle singleton
    public static StoreApp storeApp = null;

    // SettingsApp object to handle singleton
    public static SettingsApp settingsApp = null;

    // Current user
    public static User currentUser = null;


    public static Stage getStage(Stage stage, URL resource, boolean resizable, String title) throws IOException {
        // Load FXML file from path
        Parent root = FXMLLoader.load(resource);
        // Set window title
        stage.setTitle(title);
        // Set resizable
        stage.setResizable(resizable);
        // Set stage icon
        InputStream image = App.class.getResourceAsStream("images/delivery-truck-icon.png");
        // Check image
        stage.getIcons().add(new Image(image));
        // Set new scene
        stage.setScene(new Scene(root));
        // Set window size
        // stage.setScene(new Scene(root, 502, 340));
        return stage;
    }

}
