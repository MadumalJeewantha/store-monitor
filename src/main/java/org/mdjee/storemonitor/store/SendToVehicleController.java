package org.mdjee.storemonitor.store;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.hibernate.entity.*;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.settings.SettingsApp;
import org.mdjee.storemonitor.utils.Commons;
import org.mdjee.storemonitor.utils.Notification;

import javax.persistence.criteria.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SendToVehicleController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox addProductsVBox;

    @FXML
    private TitledPane titledPane;

    @FXML
    private AnchorPane selectVehiclePane;

    @FXML
    private JFXComboBox<String> vehicleComboBox;

    @FXML
    private JFXComboBox<String> productIdComboBox;

    @FXML
    private JFXComboBox<String> batchCodeComboBox;

    @FXML
    private Label vehicleIdLabel;

    @FXML
    private JFXTextField availableQuantityTextField;

    @FXML
    private JFXTextField descriptionTextField;

    @FXML
    private JFXTextField quantityTextField;

    @FXML
    private JFXTextField sellingPriceTextField;

    @FXML
    private JFXButton addToStoreButton;

    @FXML
    private JFXButton finishLoadingButton;

    @FXML
    private JFXButton nextButton;

    @FXML
    private JFXButton backToSelectVehicleButton;

    @FXML
    private TableView<ObservableSendToVehicleProduct> tableView;

    @FXML
    private TableColumn<ObservableSendToVehicleProduct, String> productIdColumn;

    @FXML
    private TableColumn<ObservableSendToVehicleProduct, String> batchCodeColumn;

    @FXML
    private TableColumn<ObservableSendToVehicleProduct, Number> quantityColumn;


    /**
     * Logger instance for SendToVehicleController class.
     */
    private static final Logger logger = LogManager.getLogger(SendToVehicleController.class);

    /**
     * List of Store objects.
     */
    private ObservableList<Store> observableStoreList = FXCollections.observableArrayList();

    /**
     * List of String productIds.
     */
    private ObservableList<String> observableProductIdList = FXCollections.observableArrayList();

    /**
     * List of String batchCodes.
     */
    private ObservableList<String> observableBatchCodeComboBoxList = FXCollections.observableArrayList();

    /**
     * List of Vehicle objects.
     */
    private ObservableList<String> observableVehicleList = FXCollections.observableArrayList();

    /**
     * List to store Products which shown in tableView.
     */
    private ObservableList<ObservableSendToVehicleProduct> observableSendToVehicleProductsList = FXCollections.observableArrayList();

    /**
     * True if already loaded for selected vehicle.
     */
    private boolean isAlreadyLoaded = false;

    /**
     * If isAlreadyLoaded is true, assign VehicleStore.
     */
    private VehicleStore alreadyLoadedVehicleStore;

    /**
     * Check for available vehicles and input fields validations.
     */
    @FXML
    void initialize() {

        // Set value after loading
        Platform.runLater(() -> {
            // Check available vehicles
            if (isVehicleEmpty()) {
                showSettings("Vehicles.fxml");
            }
        });

        // Get current Store objects
        getStoreList();

        // Initialize vehicleId auto completion
        initializeVehicleIdAutoCompletion();

        // Initialize productId auto completion
        initializeProductIdAutoCompletion();

        // Validators
        // Initialize required field validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        // Initialize number validations
        NumberValidator numberValidator = new NumberValidator();
        numberValidator.setMessage("Only numbers allowed");

        // vehicleComboBox
        vehicleComboBox.getValidators().add(requiredFieldValidator);

        // productIdComboBox
        productIdComboBox.getValidators().add(requiredFieldValidator);

        // batchCodeComboBox
        batchCodeComboBox.getValidators().add(requiredFieldValidator);

        // quantityTextField
        quantityTextField.getValidators().add(requiredFieldValidator);
        quantityTextField.getValidators().add(numberValidator);
        quantityTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                quantityTextField.validate();
            }
        });

        // Add listener to productIdComboBox
        productIdComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.isEmpty()) {
                    System.out.println(newValue);
                    // Refresh observableBatchCodeList
                    initializeBatchCodeComboBox(newValue);
                }
            } else {
                // Clear observableBatchCodeComboBoxList to remove items from comboBox
                observableBatchCodeComboBoxList.clear();
                // Clear labels
                clearLabels();
            }
        });

        // Add listener to batchCodeComboBox
        batchCodeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.isEmpty() && !productIdComboBox.getSelectionModel().isSelected(-1)) {
                    System.out.println(newValue);
                    // Refresh observableBatchCodeList
                    populateTextFields(newValue, productIdComboBox.getSelectionModel().getSelectedItem());
                }
            } else {
                // Clear labels
                clearLabels();
            }
        });

        // Initialize tableView
        initializeTableView();

    }

    /**
     * Initialize tableView
     */
    private void initializeTableView() {
        // Set observableSendToVehicleProductsList
        tableView.setItems(observableSendToVehicleProductsList);
        // Bind cell values
        productIdColumn.setCellValueFactory(param -> param.getValue().productIdProperty());
        batchCodeColumn.setCellValueFactory(param -> param.getValue().batchCodeProperty());
        quantityColumn.setCellValueFactory(param -> param.getValue().quantityProperty());

        // Set context menu
        MenuItem deleteMenuItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItem.setOnAction(event -> {

            System.out.println("Delete clicked");
            // Delete from table
            deleteProductFromTable();
        });

        MenuItem closeMenuItem = new MenuItem("Close");
        closeMenuItem.setOnAction(event -> System.out.println("Close clicked"));

        tableView.setContextMenu(new ContextMenu(deleteMenuItem, closeMenuItem));
    }

    /**
     * Delete selected row from table.
     * Validated isAlreadyLoaded.
     */
    private void deleteProductFromTable() {
        // Get selected item
        ObservableSendToVehicleProduct selectedItem = tableView.getSelectionModel().getSelectedItem();

        if (isAlreadyLoaded) {

            // Check this product stores in alreadyLoadedVehicleStore
            VehicleStoreProduct matchingVehicleStoreProduct = alreadyLoadedVehicleStore.getVehicleStoreProducts().stream()
                    .filter(vehicleStoreProduct ->
                            vehicleStoreProduct.getProductId().equals(selectedItem.getProductId()) &&
                                    vehicleStoreProduct.getBatchCode().equals(selectedItem.getBatchCode()))
                    .findAny()
                    .orElse(null);

            if (matchingVehicleStoreProduct != null) {
                // Should check quantity as well
                // If some one added new quantity to already loaded product
                // Special Note: DONE Or this can be done while adding to vehicle > if product exists - make database changes on the fly
                //------------------------------------------------------------------------------------------------------
                // Found matching product
                // If product exists in alreadyLoadedVehicleStore
                // Remove quantity from vehicleStore, Add that back to Product
                // Refresh observableStoreList + alreadyLoadedVehicleStore
                removeProductIfAlreadyLoadedProduct(selectedItem, matchingVehicleStoreProduct);
            } else {
                // No any matching product found
                // If not, this product has added by user to already loaded vehicle
                // So no need to deal with vehicle
                // Remove from table
                // Add quantity back to observableStoreList product
                removeProductIfNotAlreadyLoadedProduct(selectedItem);
            }

        } else {
            // Not already loaded vehicle
            // Remove from table
            // Add quantity back to observableStoreList product
            removeProductIfNotAlreadyLoadedProduct(selectedItem);
        }
    }

    /**
     * If already loaded product: Remove product from vehicle and add quantity back to store.
     *
     * @param selectedItem                selected table row {@link ObservableSendToVehicleProduct} object
     * @param matchingVehicleStoreProduct already loaded vehicle store product {@link VehicleStoreProduct}
     */
    private void removeProductIfAlreadyLoadedProduct(ObservableSendToVehicleProduct selectedItem,
                                                     VehicleStoreProduct matchingVehicleStoreProduct) {
        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Please be careful this is an already loaded product. " +
                        "Are you sure you want to remove product: " +
                        selectedItem.getProductId() + " with \nbatch code " + selectedItem.getBatchCode() + " from vehicle?\n" +
                        "Quantity loaded: " + selectedItem.getQuantity() +
                        "\n\nRemoving this item will be deleted from your vehicle: " + selectedItem.getVehicleId() +
                        " and quantity added back to your store!");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton yesButton = new JFXButton("Yes, remove");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            // Found matching product
            // If product exists in alreadyLoadedVehicleStore
            // Remove quantity from vehicleStore, Add that back to Product
            // Refresh observableStoreList + alreadyLoadedVehicleStore

            // Add quantity back to product and refresh observableStoreList
            Product matchingProduct = observableStoreList.stream()
                    .filter(store -> store.getProductId().equals(selectedItem.getProductId()))
                    .findAny()
                    .orElse(null)
                    .getProducts().stream()
                    .filter(product -> product.getProductId().getBatchCode().equals(selectedItem.getBatchCode()))
                    .findAny()
                    .orElse(null);

            // update quantity
            // Added selectedItem quantity back to observableStoreList
            matchingProduct.setAvailableQuantity(matchingProduct.getAvailableQuantity() + selectedItem.getQuantity());

            // Update to DB
            updateQuantityFromProduct(selectedItem, matchingProduct.getAvailableQuantity());

            // Delete from VehicleStoreProduct
            deleteFromVehicleStoreProduct(matchingVehicleStoreProduct);
            // Refresh alreadyLoadedVehicleStore
            alreadyLoadedVehicleStore = null;
            alreadyLoadedVehicleStore = isAlreadyLoaded(vehicleIdLabel.getText());

            // Remove selected item
            tableView.getItems().remove(selectedItem);

            // Show notification
            Notification.showInfoNotification("Successfully removed from vehicle: " + selectedItem.getVehicleId() + ".\n" +
                            "Product ID: " + selectedItem.getVehicleId() +
                            "\nBatch code: " + selectedItem.getBatchCode() +
                            "\nQuantity: " + selectedItem.getQuantity() +
                            "\nNote: This is a already loaded product.",
                    "Send to vehicle");

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
     * Delete from {@link VehicleStoreProduct} with given productId and batchCode.
     *
     * @param matchingVehicleStoreProduct product to be removed
     */
    private void deleteFromVehicleStoreProduct(VehicleStoreProduct matchingVehicleStoreProduct) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaDelete<VehicleStoreProduct> criteriaDelete = criteriaBuilder.createCriteriaDelete(VehicleStoreProduct.class);
            Root<VehicleStoreProduct> root = criteriaDelete.from(VehicleStoreProduct.class);

            // Apply for productId and batchCode
            Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId"),
                    matchingVehicleStoreProduct.getProductId());
            Predicate batchCodePredicate = criteriaBuilder.equal(root.get("batchCode"),
                    matchingVehicleStoreProduct.getBatchCode());
            Predicate vehicleIdPredicate = criteriaBuilder.equal(root.get("vehicleStore").get("vehicleId"),
                    matchingVehicleStoreProduct.getVehicleStore().getVehicleId());

            // Delete using productId and batchCode
            criteriaDelete.where(criteriaBuilder.and(productIdPredicate, batchCodePredicate, vehicleIdPredicate));
            transaction = session.beginTransaction();
            session.createQuery(criteriaDelete).executeUpdate();
            transaction.commit();

            String message = "Successfully deleted product: " + matchingVehicleStoreProduct.getProductId() + " with " +
                    "batch code: " + matchingVehicleStoreProduct.getBatchCode() + " from vehicle: " +
                    matchingVehicleStoreProduct.getVehicleStore().getVehicleId() +
                    "\nAvailable quantity: " + matchingVehicleStoreProduct.getAvailableQuantity();

            // Logger
            logger.info(message, "Send to vehicle");

            // Send notification
            //Notification.showInfoNotification(message, "Send to vehicle");

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
     * Remove product from table if not already loaded product.
     *
     * @param selectedItem selected table row
     */
    private void removeProductIfNotAlreadyLoadedProduct(ObservableSendToVehicleProduct selectedItem) {
        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Are you sure you want to remove product: " +
                        selectedItem.getProductId() + " with batch code " + selectedItem.getBatchCode() + " from vehicle?\n" +
                        "Quantity loaded: " + selectedItem.getQuantity());

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton yesButton = new JFXButton("Yes, remove");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {

            // Close dialog
            dialog.close();

            // Remove from table
            // Add quantity back to observableStoreList
            Product matchingProduct = observableStoreList.stream()
                    .filter(store -> store.getProductId().equals(selectedItem.getProductId()))
                    .findAny()
                    .orElse(null)
                    .getProducts().stream()
                    .filter(product -> product.getProductId().getBatchCode().equals(selectedItem.getBatchCode()))
                    .findAny()
                    .orElse(null);

            // update quantity
            // Added selectedItem quantity back
            matchingProduct.setAvailableQuantity(matchingProduct.getAvailableQuantity() + selectedItem.getQuantity());

            // Remove selected item
            tableView.getItems().remove(selectedItem);

            // Show notification
            Notification.showInfoNotification("Successfully removed from vehicle: " + selectedItem.getVehicleId() + ".\n" +
                            "Product ID: " + selectedItem.getVehicleId() +
                            "\nBatch code: " + selectedItem.getBatchCode() +
                            "\nQuantity: " + selectedItem.getQuantity() +
                            "\nNote: This is not already loaded product.",
                    "Send to vehicle");

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
     * Add selected products to the tableView.
     * Validate quantity before adding to tableView.
     *
     * @param event
     */
    @FXML
    void addToTableView(ActionEvent event) {

        // validations
        boolean productIdValidate = productIdComboBox.validate();
        boolean batchCodeValidate = batchCodeComboBox.validate();
        boolean quantityValidate = quantityTextField.validate();

        // Validate
        if (productIdValidate && batchCodeValidate && quantityValidate) {
            String productId = productIdComboBox.getSelectionModel().getSelectedItem();
            String batchCode = batchCodeComboBox.getSelectionModel().getSelectedItem();
            Double quantity = Double.parseDouble(quantityTextField.getText());
            Double availableQuantity = Double.parseDouble(availableQuantityTextField.getText());
            String vehicleId = vehicleIdLabel.getText();
            boolean quantityCheck = availableQuantity >= quantity;

            // check selling price
            if (sellingPriceTextField.getText().equals("No selling price")) {
                // Show message
                showWarningFXDialog("You haven't decided selling price of selected product: " +
                        productId + " with batch code: " + batchCode + ".\n In order to send to vehicle, product should " +
                        "have selling price.");
                // Clear fields
                clearFields();
                return;
            }

            // Show warning message if there aren't enough quantity
            if (!quantityCheck) {
                // Show message
                showWarningFXDialog("You don't have enough quantity.\n" +
                        "Available quantity : " + availableQuantityTextField.getText());
                // Clear quantity textField
                quantityTextField.clear();

            } else {

                // Check productId and batchCode in table for duplications
                ObservableSendToVehicleProduct matchingObservableSendToVehicleProduct =
                        observableSendToVehicleProductsList.stream()
                                .filter(product -> product.getProductId().equals(productId) && product.getBatchCode().equals(batchCode))
                                .findAny()
                                .orElse(null);

                if (matchingObservableSendToVehicleProduct != null) {
                    // Show warning message
                    // Product already loaded to vehicle. Do you want to add {qty} again?
                    // Existing quantity:
                    // New quantity:
                    // Total quantity will be:
                    addProductToTableWithExistingProduct(matchingObservableSendToVehicleProduct, vehicleId, productId,
                            batchCode, quantity, availableQuantity);

                } else {
                    // Add new product to tableView
                    observableSendToVehicleProductsList.add(
                            new ObservableSendToVehicleProduct(vehicleId, productId, batchCode, quantity));

                    // Update quantity in observableStoreList
                    observableStoreList.stream()
                            .filter(store -> store.getProductId().equals(productId))
                            .findAny()
                            .orElse(null)
                            .getProducts().stream()
                            .filter(product -> product.getProductId().getBatchCode().equals(batchCode))
                            .findAny()
                            .orElse(null)
                            .setAvailableQuantity(availableQuantity - quantity);

                    // Clear fields
                    clearFields();

                    // Show notification
                    Notification.showInfoNotification("Successfully added new product to vehicle: " + vehicleId + ".\n" +
                                    "Product ID: " + productId +
                                    "\nBatch code: " + batchCode +
                                    "\nQuantity: " + quantity,
                            "Send to vehicle");
                }
            }

        }

    }

    /**
     * Update quantity if selected product already exists in treeTable.
     * Updated lists: with new quantity
     * observableSendToVehicleProductsList
     * observableStoreList
     *
     * @param matchingObservableSendToVehicleProduct matched ObservableSendToVehicleProduct
     * @param vehicleId                              selected vehicleId
     * @param productId                              selected productId
     * @param batchCode                              selected batchCode
     * @param quantity                               new quantity
     * @param availableQuantity                      available quantity
     */
    private void addProductToTableWithExistingProduct(ObservableSendToVehicleProduct matchingObservableSendToVehicleProduct,
                                                      String vehicleId, String productId, String batchCode, Double quantity,
                                                      Double availableQuantity) {
        // Update new quantity to observableSendToVehicleProductsList
        // Update to observableStoreList


        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Product: " + productId + " with batch code: " + batchCode +
                        " already loaded to vehicle: " + vehicleId + ". Do you want to add more " + quantity + " quantity again?\n" +
                        "Existing quantity in vehicle: " + matchingObservableSendToVehicleProduct.getQuantity() + "\n" +
                        "New quantity: " + quantity + "\n" +
                        "Total quantity will be: " + (quantity + matchingObservableSendToVehicleProduct.getQuantity()));

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {

            // Update quantity in observableStoreList
            observableStoreList.stream()
                    .filter(store -> store.getProductId().equals(productId))
                    .findAny()
                    .orElse(null)
                    .getProducts().stream()
                    .filter(product -> product.getProductId().getBatchCode().equals(batchCode))
                    .findAny()
                    .orElse(null)
                    .setAvailableQuantity(availableQuantity - quantity);

            // UPDATE TO PRODUCT DATABASE
            updateQuantityFromProduct(matchingObservableSendToVehicleProduct, availableQuantity - quantity);

            // UPDATE TO alreadyLoadedVehicleStore
            VehicleStoreProduct vehicleStoreProductToBeUpdate = alreadyLoadedVehicleStore.getVehicleStoreProducts().stream()
                    .filter(vehicleStoreProduct ->
                            vehicleStoreProduct.getProductId().equals(productId) &&
                                    vehicleStoreProduct.getBatchCode().equals(batchCode)
                    ).findAny()
                    .orElse(null);

            vehicleStoreProductToBeUpdate.setAvailableQuantity(vehicleStoreProductToBeUpdate.getAvailableQuantity() + quantity);

            // UPDATE TO VEHICLESTOREPRODUCT DATABASE
            updateVehicleStoreProductQuantity(matchingObservableSendToVehicleProduct,
                    vehicleStoreProductToBeUpdate.getAvailableQuantity());


            // Update tableView
            // Update quantity in observableSendToVehicleProductsList
            observableSendToVehicleProductsList.stream().filter(product -> product.getProductId().equals(productId) &&
                    product.getBatchCode().equals(batchCode))
                    .findAny()
                    .orElse(null)
                    .setQuantity(quantity + matchingObservableSendToVehicleProduct.getQuantity());

            // Close dialog
            dialog.close();

            // Clear fields
            clearFields();

            // Show notification
            Notification.showInfoNotification("Successfully updated product of vehicle: " + vehicleId + ".\n" +
                            "Product ID: " + productId +
                            "\nBatch code: " + batchCode +
                            "\nQuantity: " + matchingObservableSendToVehicleProduct.getQuantity(),
                    "Send to vehicle");
        });

        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            // Clear fields
            clearFields();
        });

        content.setActions(noButton, yesButton);
        dialog.show();
    }

    /**
     * Update {@link VehicleStoreProduct} available quantity.
     *
     * @param observableSendToVehicleProduct selected ObservableSendToVehicleProduct
     * @param newAvailableQuantity           new available quantity
     */
    private void updateVehicleStoreProductQuantity(ObservableSendToVehicleProduct observableSendToVehicleProduct,
                                                   double newAvailableQuantity) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using JPA CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaUpdate<VehicleStoreProduct> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(VehicleStoreProduct.class);
            Root<VehicleStoreProduct> root = criteriaUpdate.from(VehicleStoreProduct.class);
            // Create predicate
            Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId"),
                    observableSendToVehicleProduct.getProductId());
            Predicate batchCodePredicate = criteriaBuilder.equal(root.get("batchCode"),
                    observableSendToVehicleProduct.getBatchCode());
            Predicate vehicleIdPredicate = criteriaBuilder.equal(root.get("vehicleStore").get("vehicleId"),
                    observableSendToVehicleProduct.getVehicleId());


            // Set updated values
            criteriaUpdate.set("availableQuantity", newAvailableQuantity);
            // Apply predicate with and
            criteriaUpdate.where(criteriaBuilder.and(productIdPredicate, batchCodePredicate, vehicleIdPredicate));
            // Begin transaction
            transaction = session.beginTransaction();
            // Create query from criteriaUpdate
            session.createQuery(criteriaUpdate).executeUpdate();
            // Commit transaction
            transaction.commit();

            logger.info("Successfully added new quantity for product: " + observableSendToVehicleProduct.getProductId()
                    + " with batch code: " + observableSendToVehicleProduct.getBatchCode() +
                    " Which is related to vehicle: " + observableSendToVehicleProduct.getVehicleId(), "Send to vehicle");

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
     * Clear input fields and comboBoxes.
     */
    private void clearFields() {
        quantityTextField.clear();
        productIdComboBox.getSelectionModel().clearSelection();
        batchCodeComboBox.getSelectionModel().clearSelection();
        descriptionTextField.clear();
        availableQuantityTextField.clear();
        sellingPriceTextField.clear();
    }

    /**
     * Finish loading to vehicle.
     * Managed already loaded vehicle and also fresh vehicle.
     *
     * @param event
     */
    @FXML
    void finishLoading(ActionEvent event) {
        // Check for table data
        ObservableList<ObservableSendToVehicleProduct> tableItems = tableView.getItems();
        if (tableItems.size() == 0) {
            // Show warning message
            showWarningFXDialog("You don't have any products added to vehicle.");
            return;
        }

        // Get confirmation to FinishLoading
        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
                FontAwesomeIcon.INFO_CIRCLE, "2em",
                "common-icon", "Are you sure you want to finish loading to vehicle: " +
                        vehicleIdLabel.getText() +
                        ".\nThis will deduct quantities you have added from your store and send to the vehicle.");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton yesButton = new JFXButton("Yes, Finish loading");
        yesButton.getStyleClass().add("common-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            // Proceed
            proceedFinishLoading(tableItems);
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
     * Save products to VehicleStore and deduct items from Store.
     *
     * @param tableItems table data which contains user added products
     */
    private void proceedFinishLoading(ObservableList<ObservableSendToVehicleProduct> tableItems) {

        // Check for isAlreadyLoaded
        if (isAlreadyLoaded) {
            // To be checked with alreadyLoadedVehicleStore
            // Check quantity changes


            // Get VehicleStoreProducts from already loaded vehicle
            List<VehicleStoreProduct> vehicleStoreProductList = alreadyLoadedVehicleStore.getVehicleStoreProducts();

            // Loop through table data
            tableItems.forEach(observableSendToVehicleProduct -> {
                // Check for isAlreadyLoaded
                // Search in alreadyLoadedVehicleStore
                VehicleStoreProduct matchingVehicleStoreProduct = alreadyLoadedVehicleStore.getVehicleStoreProducts().stream()
                        .filter(vehicleStoreProduct ->
                                vehicleStoreProduct.getProductId().equals(observableSendToVehicleProduct.getProductId()) &&
                                        vehicleStoreProduct.getBatchCode().equals(observableSendToVehicleProduct.getBatchCode()))
                        .findAny()
                        .orElse(null);

                if (matchingVehicleStoreProduct != null) {
                    // Already loaded product

                    // Special Note: There will not be any quantity changes any more. NO NEED TO CHECK QUANTITY CHANGES
                    // Because while ADDING & DELETING to existing loaded vehicle product -> BD changes will happen on the fly

                    /**
                     // Check for quantity changes > || <
                     // Remove from vehicle || Add to store
                     if (matchingVehicleStoreProduct.getAvailableQuantity() != observableSendToVehicleProduct.getQuantity()) {
                     // If quantity mismatched
                     if (matchingVehicleStoreProduct.getAvailableQuantity() > observableSendToVehicleProduct.getQuantity()) {
                     // This means some items removed from loaded product
                     // Never happen this cause not supporting for partial item removals from vehicle
                     // Support only for total product removal for prevent from user mistakes
                     }

                     if (matchingVehicleStoreProduct.getAvailableQuantity() < observableSendToVehicleProduct.getQuantity()) {
                     // This means user has added new amount of quantity to already loaded product.
                     // TO be add new amount to vehicle
                     // TO be remove that new amount from store

                     // Update quantity of matchingVehicleStoreProduct
                     Double quantityDifference = observableSendToVehicleProduct.getQuantity() -
                     matchingVehicleStoreProduct.getAvailableQuantity();
                     Double newQuantity = quantityDifference + matchingVehicleStoreProduct.getAvailableQuantity();

                     matchingVehicleStoreProduct.setAvailableQuantity(newQuantity);

                     // Update product
                     // Get available quantity from storeList
                     Double newAvailableQuantity = getAvailableQuantity(observableStoreList,
                     observableSendToVehicleProduct.getProductId(),
                     observableSendToVehicleProduct.getBatchCode());
                     updateQuantityFromProduct(observableSendToVehicleProduct, newAvailableQuantity);
                     }

                     } else {
                     // No any mismatched found
                     // Good to skip from deducting again.
                     }
                     */

                } else {
                    // Not already loaded product
                    // Go same as normal process

                    // Add new product to vehicleStoreProductsList
                    // create new VehicleStoreProduct
                    VehicleStoreProduct vehicleStoreProduct = new VehicleStoreProduct();
                    // availableQuantity
                    vehicleStoreProduct.setAvailableQuantity(observableSendToVehicleProduct.getQuantity());
                    // batchCode
                    vehicleStoreProduct.setBatchCode(observableSendToVehicleProduct.getBatchCode());
                    // productId
                    vehicleStoreProduct.setProductId(observableSendToVehicleProduct.getProductId());
                    // vehicleId
                    vehicleStoreProduct.setVehicleStore(alreadyLoadedVehicleStore);
                    // Add to vehicleStoreProductsList
                    vehicleStoreProductList.add(vehicleStoreProduct);

                    // Deduct from Products
                    // DB operation
                    // Get available quantity from storeList
                    Double newAvailableQuantity = getAvailableQuantity(observableStoreList,
                            observableSendToVehicleProduct.getProductId(),
                            observableSendToVehicleProduct.getBatchCode());
                    updateQuantityFromProduct(observableSendToVehicleProduct, newAvailableQuantity);
                }

            });


            // Manage loadedDate if there is no any loaded products
            // User may deleted all the products from VehicleStoreProducts, but exists in VehicleStore
            if (alreadyLoadedVehicleStore.getVehicleStoreProducts().size() == 0) {
                alreadyLoadedVehicleStore.setLoadedDate(LocalDateTime.now());
            }

            // update vehicleStore to DB
            updateVehicleStore(alreadyLoadedVehicleStore);

            // Get current Store objects
            getStoreList();

            // show select vehicle pane
            showSelectVehiclePane();


        } else {
            // Send quantity to selected vehicle
            // Deduct quantity from store

            // Create vehicle store
            VehicleStore vehicleStore = new VehicleStore();
            // vehicleId
            vehicleStore.setVehicleId(vehicleIdLabel.getText());
            // loadedDate
            vehicleStore.setLoadedDate(LocalDateTime.now());
            // TODO: remark
            // TODO: Ask for any comments DialogBox
            vehicleStore.setRemark("");

            // Create vehicle store products
            List<VehicleStoreProduct> vehicleStoreProductList = new ArrayList<>();

            tableItems.forEach(observableSendToVehicleProduct -> {
                // create new VehicleStoreProduct
                VehicleStoreProduct vehicleStoreProduct = new VehicleStoreProduct();
                // availableQuantity
                vehicleStoreProduct.setAvailableQuantity(observableSendToVehicleProduct.getQuantity());
                // batchCode
                vehicleStoreProduct.setBatchCode(observableSendToVehicleProduct.getBatchCode());
                // productId
                vehicleStoreProduct.setProductId(observableSendToVehicleProduct.getProductId());
                // vehicleId
                vehicleStoreProduct.setVehicleStore(vehicleStore);
                // Add to vehicleStoreProductList
                vehicleStoreProductList.add(vehicleStoreProduct);

                // Deduct from Products
                // DB operation
                // Get available quantity from storeList
                Double newAvailableQuantity = getAvailableQuantity(observableStoreList,
                        observableSendToVehicleProduct.getProductId(),
                        observableSendToVehicleProduct.getBatchCode());
                // Update available quantity in product table
                updateQuantityFromProduct(observableSendToVehicleProduct, newAvailableQuantity);
            });

            // Set VehicleStoreProducts to Vehicle Store
            vehicleStore.setVehicleStoreProducts(vehicleStoreProductList);

            // Save vehicleStore to DB
            saveVehicleStore(vehicleStore);

            // Get current Store objects
            getStoreList();

            // show select vehicle pane
            showSelectVehiclePane();
        }
    }

    /**
     * Update given VehicleStore.
     *
     * @param alreadyLoadedVehicleStore {@link VehicleStore} object to be update
     */
    private void updateVehicleStore(VehicleStore alreadyLoadedVehicleStore) {
        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // save VehicleStore
            session.update(alreadyLoadedVehicleStore);

            // commit transaction
            transaction.commit();

            // Show notification
            Notification.showInfoNotification("Successfully updated product(s) of vehicle: " +
                    alreadyLoadedVehicleStore.getVehicleId(), "Send to vehicle");

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
     * Hide table and product details pane and show select vehicle pane.
     */
    private void showSelectVehiclePane() {
        // Clear fields
        clearLabels();

        titledPane.setText("Select vehicle");

        productIdComboBox.getSelectionModel().clearSelection();
        batchCodeComboBox.getSelectionModel().clearSelection();

        tableView.setVisible(false);
        addProductsVBox.setVisible(false);
        selectVehiclePane.setVisible(true);
        new FadeIn(selectVehiclePane).play();
    }

    /**
     * Save VehicleStore object to database.
     *
     * @param vehicleStore {@link VehicleStore} object to be saved
     */
    private void saveVehicleStore(VehicleStore vehicleStore) {
        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // save VehicleStore
            session.save(vehicleStore);

            // commit transaction
            transaction.commit();

            // Show notification
            Notification.showInfoNotification("Successfully loaded to vehicle: " + vehicleStore.getVehicleId(),
                    "Send to vehicle");

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
     * Update quantity from Product.
     *
     * @param observableSendToVehicleProduct table row ObservableSendToVehicleProduct
     * @param newAvailableQuantity           new quantity to be update
     */
    private void updateQuantityFromProduct(ObservableSendToVehicleProduct observableSendToVehicleProduct,
                                           Double newAvailableQuantity) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using JPA CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaUpdate<Product> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Product.class);
            Root<Product> root = criteriaUpdate.from(Product.class);
            // Create predicate
            Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId").get("productId"),
                    observableSendToVehicleProduct.getProductId());
            Predicate batchCodePredicate = criteriaBuilder.equal(root.get("productId").get("batchCode"),
                    observableSendToVehicleProduct.getBatchCode());


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

            logger.info("Successfully updated quantity for product: " + observableSendToVehicleProduct.getProductId()
                    + " with batch code: " + observableSendToVehicleProduct.getBatchCode() +
                    " Which is related to vehicle: " + observableSendToVehicleProduct.getVehicleId(), "Send to vehicle");

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
     * Find quantity from Store object with given productId and batchCode.
     *
     * @param observableStoreList list of Store objects
     * @param productId           product id for selected product
     * @param batchCode           batch code for selected product
     * @return double available quantity
     */
    private Double getAvailableQuantity(ObservableList<Store> observableStoreList, String productId, String batchCode) {
        double availableQuantity = observableStoreList.stream()
                .filter(store -> store.getProductId().equals(productId))
                .findAny()
                .orElse(null)
                .getProducts().stream()
                .filter(product -> product.getProductId().getBatchCode().equals(batchCode))
                .findAny()
                .orElse(null)
                .getAvailableQuantity();

        return availableQuantity;
    }

    /**
     * Select vehicleId and show add products components.
     *
     * @param event
     */
    @FXML
    void showProductDetails(ActionEvent event) {

        boolean validate = vehicleComboBox.validate();
        if (validate) {
            // Selected vehicleID
            String vehicleId = vehicleComboBox.getSelectionModel().getSelectedItem();

            // Clear previous VehicleProducts
            observableSendToVehicleProductsList.clear();

            // Check for VehicleStoreProduct
            alreadyLoadedVehicleStore = isAlreadyLoaded(vehicleId);
            if (isAlreadyLoaded) {
                // Populate tableView
                alreadyLoadedVehicleStore.getVehicleStoreProducts().stream().forEach(vehicleStoreProduct -> {
                    observableSendToVehicleProductsList.add(
                            new ObservableSendToVehicleProduct(vehicleStoreProduct.getVehicleStore().getVehicleId(),
                                    vehicleStoreProduct.getProductId(), vehicleStoreProduct.getBatchCode(),
                                    vehicleStoreProduct.getAvailableQuantity()));
                });
            }

            // Hide selectVehiclePane
            selectVehiclePane.setVisible(false);
            // Show addProductsVBox
            addProductsVBox.setVisible(true);
            new FadeIn(addProductsVBox).play();
            // Show tableView
            tableView.setVisible(true);
            new FadeIn(tableView).play();

            // Set title of titled pane
            titledPane.setText("Select products");
            // Set vehicleId label
            vehicleIdLabel.setText(vehicleId);
        }

    }

    /**
     * Returns VehicleStore for given vehicleId and isAlreadyLoaded will be true if there any matching results.
     *
     * @param vehicleId selected vehicleId String value
     * @return matching VehicleStore for selected vehicleId
     */
    private VehicleStore isAlreadyLoaded(String vehicleId) {
        VehicleStore vehicleStore = null;
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<VehicleStore> criteriaQuery = criteriaBuilder.createQuery(VehicleStore.class);
            Root<VehicleStore> root = criteriaQuery.from(VehicleStore.class);
            criteriaQuery.select(root)
                    .where(criteriaBuilder.equal(root.get("vehicleId"), vehicleId));

            Query<VehicleStore> query = session.createQuery(criteriaQuery);
            vehicleStore = query.uniqueResult();

            // isAlreadyLoaded true if there any results
            if (vehicleStore != null) {
                isAlreadyLoaded = true;
            } else {
                isAlreadyLoaded = false;
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return vehicleStore;
    }

    /**
     * Go back to select vehicle pane.
     * Check before going back to prevent from unsaved changes.
     *
     * @param event
     */
    @FXML
    void backToSelectVehicle(ActionEvent event) {

        ObservableList<ObservableSendToVehicleProduct> tableViewItems = tableView.getItems();
        // Check table data
        if (tableViewItems.size() == 0) {
            // Refresh storeList
            showSelectVehiclePaneWithoutUnsavedChanges();
        } else {
            if (isAlreadyLoaded) {

                // Special Note: No need to check quantity differences
                // See if isAlreadyLoaded = true -> Add new quantity
                //                               -> Delete quantity

                if (alreadyLoadedVehicleStore.getVehicleStoreProducts().size() < tableViewItems.size()) {
                    // This means, user has added new product to table
                    // There are some unsaved changes
                    // Show warning
                    showSelectVehiclePaneWithUnsavedChanges();
                }else{
                    // Refresh storeList
                    showSelectVehiclePaneWithoutUnsavedChanges();
                }

            } else {
                // Show warning
                // There are some unsaved changes
                // If user want to go back -> Refresh storeList
                showSelectVehiclePaneWithUnsavedChanges();
            }
        }
    }

    /**
     * Show info message to go back to select vehicle.
     */
    private void showSelectVehiclePaneWithoutUnsavedChanges() {
        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
                FontAwesomeIcon.INFO_CIRCLE, "2em",
                "common-icon", "There is no any unsaved changes. Do you want to go " +
                        "back to select different vehicle?");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("common-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            // Get current Store objects
            getStoreList();

            // show select vehicle pane
            showSelectVehiclePane();

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
     * Show warning if there any unsaved changes in table.
     * If yes show select vehicle pane.
     */
    private void showSelectVehiclePaneWithUnsavedChanges() {
        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "There are some unsaved changes. Do you want to go " +
                        "back to select different vehicle?");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Ok button
        JFXButton yesButton = new JFXButton("Yes, go back");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Close dialog
            dialog.close();

            // Get current Store objects
            getStoreList();

            // show select vehicle pane
            showSelectVehiclePane();
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
     * Change batchCodes according to selected productId.
     *
     * @param productId selected productId from productIdComboBox
     */
    private void initializeBatchCodeComboBox(String productId) {
        // Get all the batchCodes from selected productId
        // Add to observableBatchCodeList
        // Remove previous data
        observableBatchCodeComboBoxList.clear();

        Store store = observableStoreList.stream()
                .filter(storeList -> productId.equals(storeList.getProductId()))
                .findAny()
                .orElse(null);

        // If there any match Product ID
        // Find Batch Codes
        if (store != null) {
            // Get product list from store
            List<Product> productList = store.getProducts();

            // Add to BatchCodeList
            productList.stream().forEach((product) -> observableBatchCodeComboBoxList.add(product.getProductId().getBatchCode()));

        }

        // Bind auto completion
        batchCodeComboBox.setItems(observableBatchCodeComboBoxList);
    }

    /**
     * Populate text fields according to the selected productId and batchCode.
     *
     * @param batchCode selected batchCode from batchCodeComboBox
     * @param productId selected productId from productIdComboBox
     */
    private void populateTextFields(String batchCode, String productId) {
        // Get store object which has relevant productId
        Store storeResult = observableStoreList.stream()
                .filter(store -> productId.equals(store.getProductId()))
                .findAny()
                .orElse(null);

        if (storeResult != null) {
            // Found matching store with productId
            // Get Products from store
            List<Product> products = storeResult.getProducts();
            // Populate
            products.stream().forEach((product) -> {
                // If there any matching batch code
                if (batchCode.equals(product.getProductId().getBatchCode())) {

                    // productDescriptionJFXTextAreaAsLabel
                    if (product.getRemark() != null) {
                        descriptionTextField.setText(product.getRemark() + "");
                    } else {
                        descriptionTextField.clear();
                    }

                    // quantityJFXTextFieldAsLabel
                    availableQuantityTextField.setText(product.getAvailableQuantity() + "");

                    // sellingPriceTextField
                    if (product.getSellingPrice() == 0) {
                        sellingPriceTextField.setText("No selling price");
                    } else {
                        sellingPriceTextField.setText(product.getSellingPrice() + "");
                    }

                }
            });
        }
    }

    /**
     * Clear input fields.
     */
    private void clearLabels() {
        availableQuantityTextField.clear();
        descriptionTextField.clear();
        quantityTextField.clear();
        sellingPriceTextField.clear();
    }

    /**
     * Initialize vehicleId auto completion
     */
    private void initializeVehicleIdAutoCompletion() {
        // Bind to productIdComboBox
        vehicleComboBox.setItems(observableVehicleList);
    }

    /**
     * Check vehicle availability and if there any results found assign to vehicleList.
     *
     * @return true if there is no any vehicles
     */
    public boolean isVehicleEmpty() {
        Transaction transaction = null;
        boolean status = false;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from Vehicle ", Vehicle.class);
            List<Vehicle> list = query.list();

            // Assign status
            status = list.isEmpty();
            // Add to observableVehicleList
            list.stream().forEach(vehicle -> observableVehicleList.add(vehicle.getVehicleID()));

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return status;
    }

    /**
     * Show dialog message and open Settings with Vehicle details.
     *
     * @param filename fxml file name to be load
     */
    public void showSettings(String filename) {
        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "There is no any vehicle found. Please add a vehicle to proceed.");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton okButton = new JFXButton("Ok");
        okButton.getStyleClass().add("danger-button");
        okButton.setOnAction(event1 -> {

            // Close dialog
            dialog.close();

            // Close current window
            ((Stage) stackPane.getScene().getWindow()).close();

            // Manually clear static variable related to Store
            Commons.storeApp = null;

            // Show settings > vehicle
            try {
                showSettingsUI(filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        content.setActions(okButton);
        dialog.show();
    }

    /**
     * Show settings window with given fxml as center content.
     *
     * @param filename fxml file name to show as default pane
     * @throws Exception
     */
    private void showSettingsUI(String filename) throws Exception {
        // Make this as singleton
        if (Commons.settingsApp == null) {
            Commons.settingsApp = new SettingsApp(filename);
            Commons.settingsApp.start(new Stage());
        } else {
            Commons.settingsApp.getPrimaryStageReference().requestFocus();
            // TrayNotification
            Notification.showTrayMessage("Settings window is already opened.", "Store Monitor",
                    TrayIcon.MessageType.INFO);
        }
    }

    /**
     * Returns list of store objects from database.
     */
    private void getStoreList() {
        Transaction transaction = null;
        observableStoreList.clear();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from Store", Store.class);
            List<Store> list = null;
            list = query.list();

            // TODO: If not retrieve Product object
            list.stream().forEach((store) -> System.out.println("Found products: " + store.getProducts().size()));

            // Add to Observable Store List
            list.stream().forEach((store) -> observableStoreList.add(store));

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
     * Add productIds to observableProductIdList and bind to productIdComboBox.
     */
    private void initializeProductIdAutoCompletion() {
        // Get Product IDs
        observableStoreList.stream().forEach((store) -> observableProductIdList.add(store.getProductId()));

        // Bind to productIdComboBox
        productIdComboBox.setItems(observableProductIdList);
    }

    /**
     * Show warning dialog with given message.
     *
     * @param message message to be shown in dialog
     */
    private void showWarningFXDialog(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Send to vehicle",
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
