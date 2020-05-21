package org.mdjee.storemonitor.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.hibernate.entity.Vehicle;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.store.AddToStoreController;
import org.mdjee.storemonitor.utils.Notification;

import java.util.ArrayList;
import java.util.List;

public class VehiclesController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField vehicleIdTextField;

    @FXML
    private JFXTextField nameTextField;

    @FXML
    private JFXTextField descriptionTextField;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton updateButton;

    @FXML
    private TableView<ObservableVehicle> tableView;

    @FXML
    private TableColumn<ObservableVehicle, String> vehicleIdColumn;

    @FXML
    private TableColumn<ObservableVehicle, String> nameColumn;

    @FXML
    private TableColumn<ObservableVehicle, String> descriptionColumn;

    @FXML
    private JFXButton clearFieldsButton;

    private static final Logger logger = LogManager.getLogger(AddToStoreController.class);
    private ObservableList<ObservableVehicle> vehicleObservableList = FXCollections.observableArrayList();
    private List<Vehicle> vehicleList = new ArrayList<>();


    @FXML
    void addToVehicle(ActionEvent event) {

        // Vehicle ID validation
        boolean vehicleIdValidate = vehicleIdTextField.validate();

        if (vehicleIdValidate) {

            // Add new vehicle
            String vehicleId = vehicleIdTextField.getText();
            String name = nameTextField.getText();
            String description = descriptionTextField.getText();

            // Check for duplication
            ObservableVehicle matchingVehicle = vehicleObservableList.stream()
                    .filter(vehicle -> vehicle.getVehicleId().equals(vehicleId))
                    .findAny()
                    .orElse(null);

            if (matchingVehicle != null) {
                // Show warning message
                // Vehicle number already exists
                showWarningFXDialog("Vehicle number already exists.");
                // Clear fields
                clearFields();
            } else {
                // Save to database
                createNewVehicle(vehicleId, description, name);
                //Clear fields
                clearFields();
            }
        }
    }

    @FXML
    private void clearFields() {
        // Clear input fields
        vehicleIdTextField.clear();
        descriptionTextField.clear();
        nameTextField.clear();

        // Clear selection
        tableView.getSelectionModel().clearSelection();
    }

    private void createNewVehicle(String vehicleId, String description, String name) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // Create new vehicle
            Vehicle vehicle = new Vehicle();
            // vehicleId
            vehicle.setVehicleID(vehicleId);
            // description
            vehicle.setDescription(description);
            // name
            vehicle.setName(name);

            // save vehicle
            session.save(vehicle);

            // commit transaction
            transaction.commit();

            //Refresh observableVehicleList
            getVehicleDetails();

            logger.info("Successfully saved new vehicle: " + vehicle.getVehicleID(), "Vehicles");

            // Send notification
            Notification.showInfoNotification("Successfully saved new vehicle: " + vehicle.getVehicleID(),
                    "Vehicles");

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
    void deleteVehicle(ActionEvent event) {
        // Vehicle ID validation
        boolean vehicleIdValidate = vehicleIdTextField.validate();

        if (vehicleIdValidate) {

            // Delete vehicle
            String vehicleId = vehicleIdTextField.getText();

            // Check for duplication
            Vehicle matchingVehicle = vehicleList.stream()
                    .filter(vehicle -> vehicle.getVehicleID().equals(vehicleId))
                    .findAny()
                    .orElse(null);

            if (matchingVehicle == null) {
                // Show warning message
                // Vehicle number already exists
                showWarningFXDialog("Vehicle number not found.");
                // Clear fields
                clearFields();
            } else {
                // Show confirmation
                JFXDialogLayout content = Notification.showJFXDialogLayout("Vehicles",
                        FontAwesomeIcon.WARNING, "2em",
                        "warning-icon", "Are you sure you want to delete vehicle: " +
                                matchingVehicle.getVehicleID());

                JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                // Prevent closes when clicking away
                dialog.setOverlayClose(false);

                // Create Yes button
                JFXButton yesButton = new JFXButton("Yes");
                yesButton.getStyleClass().add("danger-button");
                yesButton.setOnAction(event1 -> {
                    // Delete
                    deleteVehicle(matchingVehicle);
                    // Close dialog
                    dialog.close();
                    // Clear fields
                    clearFields();
                    // Refresh table
                    getVehicleDetails();
                });

                JFXButton noButton = new JFXButton("No");
                noButton.setOnAction(event1 -> dialog.close());

                content.setActions(noButton, yesButton);
                dialog.show();
            }
        }
    }

    private void deleteVehicle(Vehicle vehicle) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // delete vehicle
            session.delete(vehicle);

            // commit transaction
            transaction.commit();

            //Refresh observableVehicleList
            getVehicleDetails();

            logger.info("Successfully deleted vehicle: " + vehicle.getVehicleID(), "Vehicles");

            // Send notification
            Notification.showInfoNotification("Successfully deleted vehicle: " + vehicle.getVehicleID(),
                    "Vehicles");

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
    void updateVehicle(ActionEvent event) {
        // Vehicle ID validation
        boolean vehicleIdValidate = vehicleIdTextField.validate();

        if (vehicleIdValidate) {

            // Add new vehicle
            String vehicleId = vehicleIdTextField.getText();
            String name = nameTextField.getText();
            String description = descriptionTextField.getText();

            // Check for duplication
            Vehicle matchingVehicle = vehicleList.stream()
                    .filter(vehicle -> vehicle.getVehicleID().equals(vehicleId))
                    .findAny()
                    .orElse(null);

            if (matchingVehicle == null) {
                // Show warning message
                // Vehicle number already exists
                showWarningFXDialog("Vehicle number not found.");
                // Clear fields
                clearFields();
            } else {
                // Update to database
                updateVehicle(matchingVehicle, description, name);
                // Clear fields
                clearFields();
                // Refresh table
                getVehicleDetails();
            }
        }
    }

    private void updateVehicle(Vehicle vehicle, String description, String name) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // description
            vehicle.setDescription(description);
            // name
            vehicle.setName(name);

            // save vehicle
            session.update(vehicle);

            // commit transaction
            transaction.commit();

            //Refresh observableVehicleList
            getVehicleDetails();

            logger.info("Successfully updated vehicle: " + vehicle.getVehicleID(), "Vehicles");

            // Send notification
            Notification.showInfoNotification("Successfully updated vehicle: " + vehicle.getVehicleID(),
                    "Vehicles");

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
    void initialize() {
        // Set validations
        // Initialize required field validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        // vehicleIdTextField
        vehicleIdTextField.getValidators().add(requiredFieldValidator);
        vehicleIdTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                vehicleIdTextField.validate();
            }
        });

        // Load vehicle details to tableView
        getVehicleDetails();
        tableView.setItems(vehicleObservableList);
        vehicleIdColumn.setCellValueFactory(param -> param.getValue().vehicleIdProperty());
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());

        // Add listener to table selection model
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Populate text field
                vehicleIdTextField.setText(newValue.getVehicleId());
                nameTextField.setText(newValue.getName());
                descriptionTextField.setText(newValue.getDescription());
            }
        });
    }

    private void getVehicleDetails() {
        Transaction transaction = null;
        vehicleObservableList.clear();

        // Get data
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from Vehicle", Vehicle.class);
            vehicleList = query.list();

            // Add to Observable Store List
            vehicleList.stream().forEach((vehicleItem) -> {
                // Create ObservableVehicle object fro Vehicle
                vehicleObservableList.add(new ObservableVehicle(vehicleItem.getVehicleID(),
                        vehicleItem.getName(), vehicleItem.getDescription()));
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

    private void showWarningFXDialog(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Vehicles",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", message);

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
    }
}
