package org.mdjee.storemonitor.store;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.hibernate.entity.*;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.utils.BackupRestore;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Notification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BalanceStockController {

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox progressBarVBox;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Label processStatusLabel;

    @FXML
    private VBox vehicleStoreProductDetailsVBox;

    @FXML
    private Label selectedVehicleDetailsLabel;

    @FXML
    private JFXTextField vehicleDetailsFilterTextField;

    @FXML
    private TableView<ObservableVehicleStore> vehicleDetailsTable;

    @FXML
    private TableColumn<ObservableVehicleStore, String> productIdColumnVehicleDetailsTable;

    @FXML
    private TableColumn<ObservableVehicleStore, String> batchCodeColumnVehicleDetailsTable;

    @FXML
    private TableColumn<ObservableVehicleStore, Number> quantityColumnVehicleDetailsTable;

    @FXML
    private JFXButton cancelBalanceStockButton;

    @FXML
    private JFXButton finishBalanceStockButton;

    @FXML
    private JFXComboBox<String> productIdComboBox;

    @FXML
    private JFXComboBox<String> batchCodeComboBox;

    @FXML
    private JFXTextField quantityTextField;

    @FXML
    private JFXButton addToSalesButton;

    @FXML
    private JFXButton addToReturnsButton;

    @FXML
    private TableView<ObservableVehicleStore> salesTable;

    @FXML
    private TableColumn<ObservableVehicleStore, String> productIdColumnSalesTable;

    @FXML
    private TableColumn<ObservableVehicleStore, String> batchCodeColumnSalesTable;

    @FXML
    private TableColumn<ObservableVehicleStore, Number> quantityColumnSalesTable;

    @FXML
    private TableView<ObservableVehicleStore> returnsTable;

    @FXML
    private TableColumn<ObservableVehicleStore, String> productIdColumnReturnsTable;

    @FXML
    private TableColumn<ObservableVehicleStore, String> batchCodeColumnReturnsTable;

    @FXML
    private TableColumn<ObservableVehicleStore, Number> quantityColumnReturnsTable;

    @FXML
    private AnchorPane selectVehiclePane;

    @FXML
    private JFXTextField vehicleIdFilterTextField;

    @FXML
    private JFXButton showVehicleProductButton;

    @FXML
    private TableView<ObservableVehicle> vehicleTable;

    @FXML
    private TableColumn<ObservableVehicle, String> vehicleIdColumn;

    @FXML
    private TableColumn<ObservableVehicle, String> loadedDateColumn;

    @FXML
    private TableColumn<ObservableVehicle, String> remarkColumn;

    @FXML
    private Label selectedVehicleLabel;

    /**
     * Logger instance for SendToVehicleController class.
     */
    private static final Logger logger = LogManager.getLogger(BalanceStockController.class);

    /**
     * Observable list of {@link ObservableVehicle} to store VehicleStore objects.
     */
    private ObservableList<ObservableVehicle> vehicleObservableList = FXCollections.observableArrayList();

    /**
     * Observable list of {@link ObservableVehicleStore} to store VehicleStoreProduct.
     */
    private ObservableList<ObservableVehicleStore> vehicleStoreObservableList = FXCollections.observableArrayList();

    /**
     * Observable list of {@link ObservableVehicleStore} to store sales VehicleStoreProduct.
     */
    private ObservableList<ObservableVehicleStore> salesVehicleStoreObservableList = FXCollections.observableArrayList();

    /**
     * Observable list of {@link ObservableVehicleStore} to store return VehicleStoreProduct.
     */
    private ObservableList<ObservableVehicleStore> returnVehicleStoreObservableList = FXCollections.observableArrayList();

    /**
     * DateTime formatter.
     */
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");


    @FXML
    void initialize() {
        // Initialize required field validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        // Initialize number validations
        NumberValidator numberValidator = new NumberValidator();
        numberValidator.setMessage("Only numbers allowed");

        // quantityTextField
        quantityTextField.getValidators().add(requiredFieldValidator);
        quantityTextField.getValidators().add(numberValidator);
        quantityTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                quantityTextField.validate();
            }
        });

        //productIdComboBox
        productIdComboBox.getValidators().add(requiredFieldValidator);
        //batchCodeComboBox
        batchCodeComboBox.getValidators().add(requiredFieldValidator);


        // VehicleStore - vehicleTable
        getVehicleObservableList();
        //vehicleTable.setItems(vehicleObservableList);
        vehicleIdColumn.setCellValueFactory(param -> param.getValue().vehicleIdProperty());
        loadedDateColumn.setCellValueFactory(param -> param.getValue().loadedDateProperty());
        remarkColumn.setCellValueFactory(param -> param.getValue().remarkProperty());
        // Add listener to vehicleTable selection model
        vehicleTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Populate text field
                selectedVehicleLabel.setText(newValue.getVehicleId());
            }
        });
        // Add filter
        FilteredList<ObservableVehicle> observableVehicleFilteredList =
                new FilteredList<>(vehicleObservableList, observableVehicle -> true);
        vehicleIdFilterTextField.setOnKeyReleased(event -> {
            vehicleIdFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                observableVehicleFilteredList.setPredicate((Predicate<? super ObservableVehicle>) vehicle -> {

                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    // Check for vehicleId
                    if (vehicle.getVehicleId().toLowerCase().contains(newValue.toLowerCase())) {
                        return true;
                    }

                    return false;
                });
            });
        });
        SortedList<ObservableVehicle> observableVehicleSortedList = new SortedList<>(observableVehicleFilteredList);
        observableVehicleSortedList.comparatorProperty().bind(vehicleTable.comparatorProperty());
        vehicleTable.setItems(observableVehicleSortedList);

        // vehicleDetailsTable
        vehicleDetailsTable.setItems(vehicleStoreObservableList);
        productIdColumnVehicleDetailsTable.setCellValueFactory(param -> param.getValue().productIdProperty());
        batchCodeColumnVehicleDetailsTable.setCellValueFactory(param -> param.getValue().batchCodeProperty());
        quantityColumnVehicleDetailsTable.setCellValueFactory(param -> param.getValue().availableQuantityProperty());
        // Add listener to vehicleDetailsTable selection model
        vehicleDetailsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Populate combo boxes
                productIdComboBox.getSelectionModel().select(newValue.getProductId());
                batchCodeComboBox.getSelectionModel().select(newValue.getBatchCode());
            }
        });

        // salesTable
        salesTable.setItems(salesVehicleStoreObservableList);
        productIdColumnSalesTable.setCellValueFactory(param -> param.getValue().productIdProperty());
        batchCodeColumnSalesTable.setCellValueFactory(param -> param.getValue().batchCodeProperty());
        quantityColumnSalesTable.setCellValueFactory(param -> param.getValue().availableQuantityProperty());
        // Set context menu
        MenuItem deleteMenuItemSalesTable = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItemSalesTable.setOnAction(event -> {
            System.out.println("Delete clicked");
            deleteFromSalesTable();
        });
        MenuItem closeMenuItemSalesTable = new MenuItem("Close");
        closeMenuItemSalesTable.setOnAction(event -> System.out.println("Close clicked"));
        salesTable.setContextMenu(new ContextMenu(deleteMenuItemSalesTable, closeMenuItemSalesTable));

        // returnsTable
        returnsTable.setItems(returnVehicleStoreObservableList);
        productIdColumnReturnsTable.setCellValueFactory(param -> param.getValue().productIdProperty());
        batchCodeColumnReturnsTable.setCellValueFactory(param -> param.getValue().batchCodeProperty());
        quantityColumnReturnsTable.setCellValueFactory(param -> param.getValue().availableQuantityProperty());
        // Set context menu
        MenuItem deleteMenuItemReturnsTable = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItemReturnsTable.setOnAction(event -> {
            System.out.println("Delete clicked");
            deleteFromReturnsTable();
        });
        MenuItem closeMenuItemReturnsTable = new MenuItem("Close");
        closeMenuItemReturnsTable.setOnAction(event -> System.out.println("Close clicked"));
        returnsTable.setContextMenu(new ContextMenu(deleteMenuItemReturnsTable, closeMenuItemReturnsTable));

        // Add listener to productIdComboBox to populate batchCodeComboBox
        productIdComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.isEmpty()) {
                    // Populate batchCodeComboBox
                    List<ObservableVehicleStore> collect = vehicleDetailsTable.getItems().stream()
                            .filter(vehicleStore -> vehicleStore.getProductId().equals(newValue))
                            .collect(Collectors.toList());

                    ObservableList<String> batchCodeComboBoxItems = FXCollections.observableArrayList();

                    collect.forEach(vehicleStore -> batchCodeComboBoxItems.add(vehicleStore.getBatchCode()));
                    batchCodeComboBox.setItems(batchCodeComboBoxItems);
                }
            }
        });
    }

    /**
     * Delete from sales and add quantity back to vehicle details table.
     */
    private void deleteFromSalesTable() {

        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Balance stock",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Are you sure want to remove from sales?");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            ObservableVehicleStore selectedItem = salesTable.getSelectionModel().getSelectedItem();

            // Add back to vehicleDetails table
            ObservableVehicleStore matchedObservableVehicleStore = vehicleDetailsTable.getItems().stream()
                    .filter(vehicleStore -> vehicleStore.getProductId().equals(selectedItem.getProductId()) &&
                            vehicleStore.getBatchCode().equals(selectedItem.getBatchCode()))
                    .findAny()
                    .orElse(null);

            matchedObservableVehicleStore.setAvailableQuantity(matchedObservableVehicleStore.getAvailableQuantity() +
                    selectedItem.getAvailableQuantity());

            // Remove from sales table
            salesTable.getItems().remove(selectedItem);

            // Notification
            Notification.showInfoNotification("Successfully removed from sales.", "Balance stock");
        });

        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }

    /**
     * Delete from returns and add quantity back to vehicle details table.
     */
    private void deleteFromReturnsTable() {

        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Balance stock",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Are you sure want to remove from returns?");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            ObservableVehicleStore selectedItem = returnsTable.getSelectionModel().getSelectedItem();

            // Add back to vehicleDetails table
            ObservableVehicleStore matchedObservableVehicleStore = vehicleDetailsTable.getItems().stream()
                    .filter(vehicleStore -> vehicleStore.getProductId().equals(selectedItem.getProductId()) &&
                            vehicleStore.getBatchCode().equals(selectedItem.getBatchCode()))
                    .findAny()
                    .orElse(null);

            matchedObservableVehicleStore.setAvailableQuantity(matchedObservableVehicleStore.getAvailableQuantity() +
                    selectedItem.getAvailableQuantity());

            // Remove from sales table
            returnsTable.getItems().remove(selectedItem);

            // Notification
            Notification.showInfoNotification("Successfully removed from returns.", "Balance stock");
        });

        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }


    /**
     * Get VehicleStore from database & add to vehicleObservableList.
     * Get {@link org.mdjee.storemonitor.hibernate.entity.VehicleStore} objects from database.
     */
    private void getVehicleObservableList() {

        Transaction transaction = null;
        vehicleObservableList.clear();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<VehicleStore> criteriaQuery = criteriaBuilder.createQuery(VehicleStore.class);
            Root<VehicleStore> root = criteriaQuery.from(VehicleStore.class);
            criteriaQuery.select(root);

            Query<VehicleStore> query = session.createQuery(criteriaQuery);
            List<VehicleStore> list = query.list();

            list.stream().forEach(vehicleStore -> vehicleObservableList.add(
                    new ObservableVehicle(vehicleStore.getVehicleId(), vehicleStore.getLoadedDate().format(formatter),
                            vehicleStore.getRemark(), vehicleStore.getVehicleStoreProducts())));

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }

    /**
     * Add from vehicle details table to returns table.
     *
     * @param event
     */
    @FXML
    void addToReturns(ActionEvent event) {

        // Quantity field validate
        boolean quantityValidate = quantityTextField.validate();
        boolean batchCodeValidate = batchCodeComboBox.validate();
        boolean productIdValidate = productIdComboBox.validate();

        if (quantityValidate && batchCodeValidate && productIdValidate) {
            double enteredQuantity = Double.parseDouble(quantityTextField.getText());

            // Check for available quantity
            ObservableVehicleStore matchedObservableVehicleStore = vehicleDetailsTable.getItems().stream()
                    .filter(vehicleStore -> vehicleStore.getProductId().equals(productIdComboBox.getValue()) &&
                            vehicleStore.getBatchCode().equals(batchCodeComboBox.getValue()))
                    .findAny()
                    .orElse(null);

            if (matchedObservableVehicleStore.getAvailableQuantity() < enteredQuantity) {
                // Show warning
                showWarningFXDialog("Not enough quantity");
            } else {
                // Add to returns
                // Check for duplications
                // If yes update quantity
                ObservableVehicleStore duplicateObservableVehicleStore = returnsTable.getItems().stream()
                        .filter(vehicleStore -> vehicleStore.getProductId().equals(productIdComboBox.getValue()) &&
                                vehicleStore.getBatchCode().equals(batchCodeComboBox.getValue()))
                        .findAny()
                        .orElse(null);

                if (duplicateObservableVehicleStore != null) {
                    // Duplicate found
                    // Update quantity

                    // Show Status
                    JFXDialogLayout content = Notification.showJFXDialogLayout("Balance stock",
                            FontAwesomeIcon.WARNING, "2em", "warning-icon",
                            "Product already in returns. Do you want to add again?" +
                                    "\nExisting quantity: " + duplicateObservableVehicleStore.getAvailableQuantity() +
                                    "\nNew quantity: " + enteredQuantity +
                                    "\nTotal quantity will be: " + (duplicateObservableVehicleStore.getAvailableQuantity() + enteredQuantity));

                    JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                    // Prevent closes when clicking away
                    dialog.setOverlayClose(false);

                    // Create Ok button
                    JFXButton yesButton = new JFXButton("Yes");
                    yesButton.getStyleClass().add("danger-button");
                    yesButton.setOnAction(event1 -> {
                        // Close dialog
                        dialog.close();

                        duplicateObservableVehicleStore
                                .setAvailableQuantity(duplicateObservableVehicleStore.getAvailableQuantity() + enteredQuantity);

                        // Deduct from vehicleDetails
                        matchedObservableVehicleStore
                                .setAvailableQuantity(matchedObservableVehicleStore.getAvailableQuantity() - enteredQuantity);

                        // Send notification
                        Notification.showInfoNotification("Successfully added to returns.\n" +
                                "Product ID: " + matchedObservableVehicleStore.getProductId() +
                                "\nBatch Code: " + matchedObservableVehicleStore.getBatchCode() +
                                "\nQuantity: " + enteredQuantity, "Balance stock");

                        // Clear fields
                        clearFields();

                    });

                    JFXButton noButton = new JFXButton("No");
                    noButton.setOnAction(event1 -> {
                        // Close dialog
                        dialog.close();
                    });

                    content.setActions(noButton, yesButton);
                    dialog.show();

                } else {
                    // Add to table
                    returnVehicleStoreObservableList.add(new ObservableVehicleStore(matchedObservableVehicleStore.getProductId(),
                            matchedObservableVehicleStore.getBatchCode(), enteredQuantity));

                    // Deduct from vehicleDetails
                    matchedObservableVehicleStore
                            .setAvailableQuantity(matchedObservableVehicleStore.getAvailableQuantity() - enteredQuantity);

                    // Send notification
                    Notification.showInfoNotification("Successfully added to returns.\n" +
                            "Product ID: " + matchedObservableVehicleStore.getProductId() +
                            "\nBatch Code: " + matchedObservableVehicleStore.getBatchCode() +
                            "\nQuantity: " + enteredQuantity, "Balance stock");

                    // Clear fields
                    clearFields();
                }

            }

        }
    }

    /**
     * Add from vehicle details to sales table.
     *
     * @param event
     */
    @FXML
    void addToSales(ActionEvent event) {
        // Quantity field validate
        boolean quantityValidate = quantityTextField.validate();
        boolean batchCodeValidate = batchCodeComboBox.validate();
        boolean productIdValidate = productIdComboBox.validate();

        if (quantityValidate && batchCodeValidate && productIdValidate) {
            double enteredQuantity = Double.parseDouble(quantityTextField.getText());

            // Check for available quantity
            ObservableVehicleStore matchedObservableVehicleStore = vehicleDetailsTable.getItems().stream()
                    .filter(vehicleStore -> vehicleStore.getProductId().equals(productIdComboBox.getValue()) &&
                            vehicleStore.getBatchCode().equals(batchCodeComboBox.getValue()))
                    .findAny()
                    .orElse(null);

            if (matchedObservableVehicleStore.getAvailableQuantity() < enteredQuantity) {
                // Show warning
                showWarningFXDialog("Not enough quantity");
            } else {
                // Add to sales
                // Check for duplications
                // If yes update quantity
                ObservableVehicleStore duplicateObservableVehicleStore = salesTable.getItems().stream()
                        .filter(vehicleStore -> vehicleStore.getProductId().equals(productIdComboBox.getValue()) &&
                                vehicleStore.getBatchCode().equals(batchCodeComboBox.getValue()))
                        .findAny()
                        .orElse(null);

                if (duplicateObservableVehicleStore != null) {
                    // Duplicate found
                    // Update quantity

                    // Show Status
                    JFXDialogLayout content = Notification.showJFXDialogLayout("Balance stock",
                            FontAwesomeIcon.WARNING, "2em", "warning-icon",
                            "Product already in sales. Do you want to add again?" +
                                    "\nExisting quantity: " + duplicateObservableVehicleStore.getAvailableQuantity() +
                                    "\nNew quantity: " + enteredQuantity +
                                    "\nTotal quantity will be: " + (duplicateObservableVehicleStore.getAvailableQuantity() + enteredQuantity));

                    JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
                    // Prevent closes when clicking away
                    dialog.setOverlayClose(false);

                    // Create Ok button
                    JFXButton yesButton = new JFXButton("Yes");
                    yesButton.getStyleClass().add("danger-button");
                    yesButton.setOnAction(event1 -> {
                        // Close dialog
                        dialog.close();

                        duplicateObservableVehicleStore
                                .setAvailableQuantity(duplicateObservableVehicleStore.getAvailableQuantity() + enteredQuantity);

                        // Deduct from vehicleDetails
                        matchedObservableVehicleStore
                                .setAvailableQuantity(matchedObservableVehicleStore.getAvailableQuantity() - enteredQuantity);

                        // Send notification
                        Notification.showInfoNotification("Successfully added to sales.\n" +
                                "Product ID: " + matchedObservableVehicleStore.getProductId() +
                                "\nBatch Code: " + matchedObservableVehicleStore.getBatchCode() +
                                "\nQuantity: " + enteredQuantity, "Balance stock");

                        // Clear fields
                        clearFields();

                    });

                    JFXButton noButton = new JFXButton("No");
                    noButton.setOnAction(event1 -> {
                        // Close dialog
                        dialog.close();
                    });

                    content.setActions(noButton, yesButton);
                    dialog.show();
                } else {
                    // Add to table
                    salesVehicleStoreObservableList.add(new ObservableVehicleStore(matchedObservableVehicleStore.getProductId(),
                            matchedObservableVehicleStore.getBatchCode(), enteredQuantity));

                    // Deduct from vehicleDetails
                    matchedObservableVehicleStore
                            .setAvailableQuantity(matchedObservableVehicleStore.getAvailableQuantity() - enteredQuantity);

                    // Send notification
                    Notification.showInfoNotification("Successfully added to sales.\n" +
                            "Product ID: " + matchedObservableVehicleStore.getProductId() +
                            "\nBatch Code: " + matchedObservableVehicleStore.getBatchCode() +
                            "\nQuantity: " + enteredQuantity, "Balance stock");

                    // Clear fields
                    clearFields();
                }
            }

        }
    }

    /**
     * Clear fields.
     */
    void clearFields() {
        // batchCodeComboBox.getSelectionModel().clearSelection();
        quantityTextField.clear();
    }

    /**
     * Show warning message and go back to select vehicle.
     *
     * @param event
     */
    @FXML
    void cancelBalanceStock(ActionEvent event) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Balance stock",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "There can be some unsaved changes. " +
                        "Are you sure you want to cancel?");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton yesButton = new JFXButton("Yes, cancel");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            // VehicleStore - vehicleTable
            getVehicleObservableList();

            // Clear observable list
            returnVehicleStoreObservableList.clear();
            salesVehicleStoreObservableList.clear();

            // Clear fields
            clearFields();
            batchCodeComboBox.getSelectionModel().clearSelection();
            productIdComboBox.getSelectionModel().clearSelection();

            // Change label
            selectedVehicleLabel.setText("None");
            vehicleStoreProductDetailsVBox.setVisible(false);
            selectVehiclePane.setVisible(true);
            new FadeIn(selectVehiclePane).play();

        });

        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }


    Thread thread;

    /**
     * Finish balance process and calculate profit.
     *
     * @param event
     */
    @FXML
    void finishBalanceStock(ActionEvent event) {
        JFXDialogLayout content = Notification.showJFXDialogLayout("Balance stock",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Are you sure you want to finish?");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton yesButton = new JFXButton("Yes, finish");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            vehicleStoreProductDetailsVBox.setVisible(false);
            progressBarVBox.setVisible(true);
            new FadeIn(progressBarVBox).play();

            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);

            Task Worker = finishBalanceStockWorker();
            progressBar.progressProperty().bind(Worker.progressProperty());
            Worker.messageProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println(newValue);

                // Update status in UI
                processStatusLabel.setText(newValue);

                if (newValue.equals("Successfully completed balance stock finishing process.")) {

                    progressBarVBox.setVisible(false);
                    processStatusLabel.setText("");

                    // VehicleStore - vehicleTable
                    getVehicleObservableList();

                    // Clear observable list
                    returnVehicleStoreObservableList.clear();
                    salesVehicleStoreObservableList.clear();

                    // Clear fields
                    clearFields();
                    batchCodeComboBox.getSelectionModel().clearSelection();
                    productIdComboBox.getSelectionModel().clearSelection();

                    Notification.showInfoNotification("Successfully completed balance stock finishing process for vehicle: " +
                            selectedVehicleLabel.getText() + ".", "Balance stock");

                    // Change label
                    selectedVehicleLabel.setText("None");
                    vehicleStoreProductDetailsVBox.setVisible(false);
                    selectVehiclePane.setVisible(true);
                    new FadeIn(selectVehiclePane).play();

                }
            });

            thread = new Thread(Worker);
            thread.start();

        });

        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }

    /**
     * Complete balance stock process.
     *
     * @return true
     */
    public Task finishBalanceStockWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {

                    updateMessage("Initializing balance stock finishing process.");

                    // Get product prices
                    List<Product> productList = getProductList();
                    // <ProductId-BatchCode, Price>
                    HashMap<String, Price> priceHashMap = new HashMap<>();
                    productList
                            .forEach(product -> priceHashMap
                                    .put(product.getProductId().getProductId() + "-" + product.getProductId().getBatchCode(),
                                            new Price(product.getProductId().getProductId(), product.getProductId().getBatchCode(),
                                                    product.getBuyingPrice(), product.getSellingPrice(), product.getAvailableQuantity())));
                    updateMessage("Done loading prices.");
                    updateProgress(5, 100);

                    // Balance history object
                    BalanceHistory balanceHistory = new BalanceHistory();

                    // Balanced date time
                    balanceHistory.setStockBalancedDate(LocalDateTime.now());

                    // Loaded date
                    String loadedDate = vehicleTable.getSelectionModel().getSelectedItem().getLoadedDate();
                    balanceHistory.setStockLoadedDate(LocalDateTime.parse(loadedDate, formatter));

                    // Vehicle ID
                    balanceHistory.setVehicleId(selectedVehicleLabel.getText());

                    // BalanceHistoryReturn
                    List<BalanceHistoryReturn> balanceHistoryReturns = new ArrayList<>();
                    returnsTable.getItems()
                            .forEach(vehicleStore -> {
                                balanceHistoryReturns
                                        .add(new BalanceHistoryReturn(vehicleStore.getProductId(), vehicleStore.getBatchCode(),
                                                vehicleStore.getAvailableQuantity(), balanceHistory));
                                // Update product - ADD RETURNS BACK TO STORE
                                updateQuantityFromProduct(
                                        vehicleStore.getProductId(), vehicleStore.getBatchCode(),
                                        priceHashMap.get(vehicleStore.getProductId() + "-" + vehicleStore.getBatchCode())
                                                .getAvailableQuantity() + vehicleStore.getAvailableQuantity());
                            });
                    balanceHistory.setBalanceHistoryReturns(balanceHistoryReturns);
                    // Update status
                    updateMessage("Done calculating return products.");
                    updateProgress(10, 100);

                    // BalanceHistoryNotSoldProduct
                    // Refresh product prices and quantity
                    productList = getProductList();
                    // <ProductId-BatchCode, Price>
                    // priceHashMap = new HashMap<>(); will be update from below put method
                    productList
                            .forEach(product -> priceHashMap
                                    .put(product.getProductId().getProductId() + "-" + product.getProductId().getBatchCode(),
                                            new Price(product.getProductId().getProductId(), product.getProductId().getBatchCode(),
                                                    product.getBuyingPrice(), product.getSellingPrice(), product.getAvailableQuantity())));

                    List<BalanceHistoryNotSoldProduct> balanceHistoryNotSoldProducts = new ArrayList<>();
                    vehicleDetailsTable.getItems()
                            .forEach(vehicleStore -> {
                                // To prevent from adding 0 quantity products, which can available at vehicleDetailsTable
                                if (vehicleStore.getAvailableQuantity() != 0) {
                                    // Add to balanceHistoryNotSoldProducts
                                    balanceHistoryNotSoldProducts
                                            .add(new BalanceHistoryNotSoldProduct(vehicleStore.getProductId(), vehicleStore.getBatchCode(),
                                                    vehicleStore.getAvailableQuantity(), balanceHistory));

                                    // Update product
                                    updateQuantityFromProduct(
                                            vehicleStore.getProductId(), vehicleStore.getBatchCode(),
                                            priceHashMap.get(vehicleStore.getProductId() + "-" + vehicleStore.getBatchCode())
                                                    .getAvailableQuantity() + vehicleStore.getAvailableQuantity());
                                }
                            });
                    balanceHistory.setBalanceHistoryNotSoldProducts(balanceHistoryNotSoldProducts);
                    // Update status
                    updateMessage("Done calculating not soled products. And added back to store.");
                    updateProgress(20, 100);

                    // List of profit objects.
                    List<Profit> profitList = new ArrayList<>();

                    // BalanceHistorySoldProduct
                    List<BalanceHistorySoldProduct> balanceHistorySoldProducts = new ArrayList<>();
                    salesTable.getItems()
                            .forEach(vehicleStore -> {
                                // Add to history
                                balanceHistorySoldProducts
                                        .add(new BalanceHistorySoldProduct(vehicleStore.getProductId(), vehicleStore.getBatchCode(),
                                                vehicleStore.getAvailableQuantity(), balanceHistory));

                                // Add to Profit
                                double buyingPrice = priceHashMap
                                        .get(vehicleStore.getProductId() + "-" + vehicleStore.getBatchCode()).getBuyingPrice();
                                double sellingPrice = priceHashMap
                                        .get(vehicleStore.getProductId() + "-" + vehicleStore.getBatchCode()).getSellingPrice();
                                double profitAmount = (sellingPrice - buyingPrice) * vehicleStore.getAvailableQuantity();

                                Profit profit = new Profit();
                                profit.setBatchCode(vehicleStore.getBatchCode());
                                profit.setProductId(vehicleStore.getProductId());
                                profit.setBuyingPrice(buyingPrice);
                                profit.setProfitAmount(profitAmount);
                                profit.setSoldPrice(sellingPrice);
                                profit.setQuantity(vehicleStore.getAvailableQuantity());
                                profit.setBalanceHistory(balanceHistory);
                                profitList.add(profit);
                            });
                    balanceHistory.setBalanceHistorySoldProducts(balanceHistorySoldProducts);
                    // Update status
                    updateMessage("Done calculating soled products.");
                    updateProgress(30, 100);

                    balanceHistory.setProfits(profitList);
                    // Update status
                    updateMessage("Done calculating profits.");
                    updateProgress(40, 100);

                    // Update status
                    updateMessage("Start saving balance history related details.");
                    // Save BalanceHistory
                    saveBalanceHistory(balanceHistory);
                    updateMessage("Done saving balance history related details.");
                    updateProgress(80, 100);


                    // Remove from VehicleStoreProduct
                    removeFromVehicleStoreProducts(selectedVehicleLabel.getText());
                    updateMessage("Successfully deleted Vehicle Store Products which belongs to vehicle number: " +
                            selectedVehicleLabel.getText());
                    updateProgress(90, 100);

                    // Remove from VehicleStore
                    removeFromVehicleStore(selectedVehicleLabel.getText());
                    updateMessage("Successfully deleted Vehicle Store which belongs to vehicle number: " +
                            selectedVehicleLabel.getText());
                    updateProgress(100, 100);
                    updateMessage("Successfully completed balance stock finishing process.");

                    try {
                        thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }

                return true;
            }

            /**
             * Delete from {@link VehicleStore} for selected vehicle.
             * @param vehicleId selected vehicle number.
             */
            private void removeFromVehicleStore(String vehicleId) {
                Transaction transaction = null;
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                    CriteriaDelete<VehicleStore> criteriaDelete = criteriaBuilder.createCriteriaDelete(VehicleStore.class);
                    Root<VehicleStore> root = criteriaDelete.from(VehicleStore.class);

                    javax.persistence.criteria.Predicate vehicleIdPredicate = criteriaBuilder.equal(root.get("vehicleId"),
                            vehicleId);

                    // Delete using productId and batchCode
                    criteriaDelete.where(vehicleIdPredicate);
                    transaction = session.beginTransaction();
                    session.createQuery(criteriaDelete).executeUpdate();
                    transaction.commit();

                    // Logger
                    logger.info("Successfully deleted VehicleStore which belongs to vehicle number: " + vehicleId);

                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                        logger.warn("Transaction is null, rolling back.");
                    }
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }

            /**
             * Delete from {@link VehicleStoreProduct} for selected vehicle.
             * @param vehicleId selected vehicle number.
             */
            private void removeFromVehicleStoreProducts(String vehicleId) {
                Transaction transaction = null;
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                    CriteriaDelete<VehicleStoreProduct> criteriaDelete = criteriaBuilder.createCriteriaDelete(VehicleStoreProduct.class);
                    Root<VehicleStoreProduct> root = criteriaDelete.from(VehicleStoreProduct.class);

                    javax.persistence.criteria.Predicate vehicleIdPredicate = criteriaBuilder.equal(root.get("vehicleStore").get("vehicleId"),
                            vehicleId);

                    // Delete using productId and batchCode
                    criteriaDelete.where(vehicleIdPredicate);
                    transaction = session.beginTransaction();
                    session.createQuery(criteriaDelete).executeUpdate();
                    transaction.commit();

                    // Logger
                    logger.info("Successfully deleted VehicleStoreProducts which belongs to vehicle number: " + vehicleId);

                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                        logger.warn("Transaction is null, rolling back.");
                    }
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }

            /**
             * Save BalanceHistory object.
             * @param balanceHistory
             */
            private void saveBalanceHistory(BalanceHistory balanceHistory) {
                // Save to database
                Transaction transaction = null;
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                    // start a transaction
                    transaction = session.beginTransaction();

                    // save balanceHistory
                    session.save(balanceHistory);

                    // commit transaction
                    transaction.commit();
                    logger.info("Successfully updated balance history for " + balanceHistory.getVehicleId() +
                            ". which is loaded at " + balanceHistory.getStockLoadedDate().format(formatter));


                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());

                    if (transaction != null) {
                        transaction.rollback();
                        logger.warn("Transaction is null, rolling back.");
                    }

                }
            }

            /**
             * Get all products list from database;
             * @return
             */
            private List<Product> getProductList() {
                List<Product> list = null;
                Transaction transaction = null;
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                    // Using CriteriaBuilder API
                    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                    CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
                    Root<Product> root = criteriaQuery.from(Product.class);
                    criteriaQuery.select(root);

                    Query<Product> query = session.createQuery(criteriaQuery);
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

            private void updateQuantityFromProduct(String productId, String batchCode, double newAvailableQuantity) {
                Transaction transaction = null;

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                    // Using JPA CriteriaBuilder API
                    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                    CriteriaUpdate<Product> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Product.class);
                    Root<Product> root = criteriaUpdate.from(Product.class);
                    // Create predicate
                    javax.persistence.criteria.Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId").get("productId"),
                            productId);
                    javax.persistence.criteria.Predicate batchCodePredicate = criteriaBuilder.equal(root.get("productId").get("batchCode"),
                            batchCode);


                    // Set updated values
                    criteriaUpdate.set("availableQuantity", newAvailableQuantity);
                    // Apply predicate with and
                    criteriaUpdate.where(criteriaBuilder.and(productIdPredicate, batchCodePredicate));
                    // Begin transaction
                    transaction = session.beginTransaction();
                    // Create query from criteriaUpdate
                    session.createQuery(criteriaUpdate).executeUpdate();
                    // Commit transaction
                    transaction.commit();

                    logger.info("Successfully updated quantity for product: " + productId + " with batch code: " + batchCode);

                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                        logger.warn("Transaction is null, rolling back.");
                    }
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }

        };
    }

    /**
     * Show VehicleStoreProduct details to do stock balance.
     *
     * @param event
     */
    @FXML
    void showVehicleProduct(ActionEvent event) {

        // Check for data
        if (vehicleTable.getItems().size() == 0) {
            // Show warning message
            showWarningFXDialog("You don't have any vehicle to balance.");
        } else {
            // Check for the selection
            if (selectedVehicleLabel.getText().equals("None")) {
                // Please select a vehicle
                // Show warning message
                showWarningFXDialog("Please select a vehicle.");
            } else {
                // Populate vehicleDetailsTable

                // Clear vehicleStoreObservableList
                vehicleStoreObservableList.clear();

                // productIdComboBoxItems observable string list to populate productIdComboBox
                ObservableList<String> productIdComboBoxItems = FXCollections.observableArrayList();

                vehicleTable.getSelectionModel().getSelectedItem().getVehicleStoreProduct().stream()
                        .forEach(vehicleStoreProduct -> {
                            vehicleStoreObservableList.add(new ObservableVehicleStore(vehicleStoreProduct.getProductId(),
                                    vehicleStoreProduct.getBatchCode(), vehicleStoreProduct.getAvailableQuantity()));

                            productIdComboBoxItems.add(vehicleStoreProduct.getProductId());
                        });

                // Add filter
                FilteredList<ObservableVehicleStore> observableVehicleStoreFilteredList =
                        new FilteredList<>(vehicleStoreObservableList, observableVehicle -> true);
                vehicleDetailsFilterTextField.setOnKeyReleased(event1 -> {
                    vehicleDetailsFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        observableVehicleStoreFilteredList.setPredicate((Predicate<? super ObservableVehicleStore>) vehicleStore -> {

                            if (newValue == null || newValue.isEmpty()) {
                                return true;
                            }

                            // Check for vehicleId
                            if (vehicleStore.getProductId().toLowerCase().contains(newValue.toLowerCase())) {
                                return true;
                            } else if (vehicleStore.getBatchCode().toLowerCase().contains(newValue.toLowerCase())) {
                                return true;
                            }

                            return false;
                        });
                    });
                });
                SortedList<ObservableVehicleStore> observableVehicleStoreSortedList = new SortedList<>(observableVehicleStoreFilteredList);
                observableVehicleStoreSortedList.comparatorProperty().bind(vehicleDetailsTable.comparatorProperty());
                vehicleDetailsTable.setItems(observableVehicleStoreSortedList);

                // Populate selectedVehicleDetailsLabel
                String vehicleId = vehicleTable.getSelectionModel().getSelectedItem().getVehicleId();
                String loadedDate = vehicleTable.getSelectionModel().getSelectedItem().getLoadedDate();
                selectedVehicleDetailsLabel.setText("Selected vehicle: " + vehicleId + " which is loaded at " + loadedDate);

                // Populate productIdComboBox
                productIdComboBox.setItems(productIdComboBoxItems);

                // Show vehicleStoreProductDetailsVBox
                selectVehiclePane.setVisible(false);
                vehicleStoreProductDetailsVBox.setVisible(true);
                new FadeIn(vehicleStoreProductDetailsVBox).play();
            }
        }
    }

    /**
     * Show warning dialog with given message.
     *
     * @param message message to be shown in dialog
     */
    private void showWarningFXDialog(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Balance stock",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", message);

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
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
