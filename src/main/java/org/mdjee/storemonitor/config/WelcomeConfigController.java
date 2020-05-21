package org.mdjee.storemonitor.config;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.mdjee.storemonitor.App;

import java.io.IOException;

import static org.mdjee.storemonitor.utils.Commons.getStage;

public class WelcomeConfigController {

    @FXML
    private JFXButton btn_next;

    @FXML
    private JFXButton btn_cancel;

    @FXML
    void cancel(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void showNext(ActionEvent event) throws IOException {

        // Close current window
        ((Stage) btn_next.getScene().getWindow()).close();

        // Show Config window
        Stage stage = new Stage();
        stage = getStage(stage, App.class.getResource("config/Config.fxml"), false,
                "Configuration Wizard");
        stage.sizeToScene();
        stage.show();

    }
}