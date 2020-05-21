package org.mdjee.storemonitor.utils;

import com.jfoenix.controls.JFXDialogLayout;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.mdjee.storemonitor.App;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class Notification {

    public static void showTrayMessage(String message, String title, TrayIcon.MessageType messageType) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            InputStream imageInputStream = App.class.getResourceAsStream("images/delivery-truck-icon.png");
            BufferedImage image = ImageIO.read(imageInputStream);
            TrayIcon trayIcon = new TrayIcon(image, "Store Monitor");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Store Monitor");
            tray.add(trayIcon);
            //  messageType -> TrayIcon.MessageType.INFO
            trayIcon.displayMessage(title, message, messageType);
            tray.remove(trayIcon);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static JFXDialogLayout showJFXDialogLayout(String heading, FontAwesomeIcon fontAwesomeIcon,
                                                      String fontAwesomeIconSize, String fontAwesomeIconClass, String message) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(heading));

        FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView(fontAwesomeIcon);
        fontAwesomeIconView.setSize(fontAwesomeIconSize);
        fontAwesomeIconView.setStyleClass(fontAwesomeIconClass);

        HBox hBox = new HBox(20, fontAwesomeIconView, new Text(message));
        content.setBody(hBox);

        return content;
    }

    public static void showInfoNotification(String message, String title) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(message)
                .graphic(null)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .onAction(event -> {
                });
        // Set dark theme
        // notification.darkStyle();
        notification.showInformation();
    }

}
