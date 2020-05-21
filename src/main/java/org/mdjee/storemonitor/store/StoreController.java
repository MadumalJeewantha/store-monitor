package org.mdjee.storemonitor.store;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Notification;

import java.io.IOException;

public class StoreController {

    private String fileName;

    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXButton storeDetailsButton;

    @FXML
    private JFXButton vehicleDetailsButton;

    @FXML
    private JFXButton productDetailsButton;

    @FXML
    private JFXButton sendToVehicleButton;

    @FXML
    private JFXButton balanceStockButton;

    @FXML
    private JFXButton addToStockButton;

    @FXML
    private StackPane stackPane;

    @FXML
    void closeWindow(ActionEvent event) {
        // Invoke to  WindowEvent.WINDOW_CLOSE_REQUEST
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    @FXML
    void showAddToStore(ActionEvent event) {
        setUI("AddToStore.fxml");
    }

    @FXML
    void showBalanceStock(ActionEvent event) {
        setUI("BalanceStock.fxml");
    }

    @FXML
    void showProductDetails(ActionEvent event) {
        setUI("ProductDetails.fxml");
    }

    @FXML
    void showSendToVehicle(ActionEvent event) {
        setUI("SendToVehicle.fxml");
    }

    @FXML
    void showStoreDetails(ActionEvent event) {
        setUI("StoreDetails.fxml");
    }

    @FXML
    void showVehicleDetails(ActionEvent event) {
        setUI("VehicleDetails.fxml");
    }

    @FXML
    void initialize() {

        // Set value after loading
        Platform.runLater(() -> {
            // Set borderPane center layout
            setUI();
            // Bind window close event
            stackPane.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

        });

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUI() {

        if (!fileName.isEmpty()) {
            Parent root = null;
            try {
                root = FXMLLoader.load(App.class.getResource("store/" + this.fileName));
                setSliderStyle(this.fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
            borderPane.setCenter(root);
        }
    }

    public void setUI(String fileName) {
        System.out.println("Changing stage into: " + fileName);
        Parent root = null;
        try {
            root = FXMLLoader.load(App.class.getResource("store/" + fileName));
            setSliderStyle(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        borderPane.setCenter(root);
    }

    private void setSliderStyle(String str) {
        switch (str) {
            case "AddToStore.fxml":
                addToStockButton.getStyleClass().add("selected-button");
                balanceStockButton.getStyleClass().remove("selected-button");
                productDetailsButton.getStyleClass().remove("selected-button");
                sendToVehicleButton.getStyleClass().remove("selected-button");
                storeDetailsButton.getStyleClass().remove("selected-button");
                vehicleDetailsButton.getStyleClass().remove("selected-button");
                break;
            case "BalanceStock.fxml":
                addToStockButton.getStyleClass().remove("selected-button");
                balanceStockButton.getStyleClass().add("selected-button");
                productDetailsButton.getStyleClass().remove("selected-button");
                sendToVehicleButton.getStyleClass().remove("selected-button");
                storeDetailsButton.getStyleClass().remove("selected-button");
                vehicleDetailsButton.getStyleClass().remove("selected-button");
                break;
            case "ProductDetails.fxml":
                addToStockButton.getStyleClass().remove("selected-button");
                balanceStockButton.getStyleClass().remove("selected-button");
                productDetailsButton.getStyleClass().add("selected-button");
                sendToVehicleButton.getStyleClass().remove("selected-button");
                storeDetailsButton.getStyleClass().remove("selected-button");
                vehicleDetailsButton.getStyleClass().remove("selected-button");
                break;
            case "SendToVehicle.fxml":
                addToStockButton.getStyleClass().remove("selected-button");
                balanceStockButton.getStyleClass().remove("selected-button");
                productDetailsButton.getStyleClass().remove("selected-button");
                sendToVehicleButton.getStyleClass().add("selected-button");
                storeDetailsButton.getStyleClass().remove("selected-button");
                vehicleDetailsButton.getStyleClass().remove("selected-button");
                break;
            case "StoreDetails.fxml":
                addToStockButton.getStyleClass().remove("selected-button");
                balanceStockButton.getStyleClass().remove("selected-button");
                productDetailsButton.getStyleClass().remove("selected-button");
                sendToVehicleButton.getStyleClass().remove("selected-button");
                storeDetailsButton.getStyleClass().add("selected-button");
                vehicleDetailsButton.getStyleClass().remove("selected-button");
                break;
            case "VehicleDetails.fxml":
                addToStockButton.getStyleClass().remove("selected-button");
                balanceStockButton.getStyleClass().remove("selected-button");
                productDetailsButton.getStyleClass().remove("selected-button");
                sendToVehicleButton.getStyleClass().remove("selected-button");
                storeDetailsButton.getStyleClass().remove("selected-button");
                vehicleDetailsButton.getStyleClass().add("selected-button");
                break;
        }

    }

    private void closeWindowEvent(WindowEvent event) {
        // Prevent from closing window
        event.consume();

        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Store monitor",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "There can be some unsaved changes.\n" +
                        "Are you sure you want to close Store window?");

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

            System.out.println("You are closing Store App");
            Commons.storeApp = null;
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }

}
