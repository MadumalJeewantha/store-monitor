package org.mdjee.storemonitor.store;

import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.hibernate.entity.Product;
import org.mdjee.storemonitor.hibernate.entity.ProductId;
import org.mdjee.storemonitor.hibernate.entity.Store;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.utils.Notification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddToStoreController {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField productIdJFXTextField;

    @FXML
    private JFXTextField batchCodeJFXTextField;

    @FXML
    private JFXTextArea productDescriptionJFXTextArea;

    @FXML
    private JFXTextField reOrderLevelJFXTextField;

    @FXML
    private JFXTextField quantityJFXTextField;

    @FXML
    private JFXTextField buyingPriceJFXTextField;

    @FXML
    private JFXTextField sellingPriceJFXTextField;

    @FXML
    private JFXDatePicker manufacturedDateDatePicker;

    @FXML
    private JFXDatePicker expiryDateDatePicker;

    @FXML
    private JFXButton addToStoreButton;

    @FXML
    private JFXComboBox<String> productIdComboBox;

    @FXML
    private JFXComboBox<String> batchCodeComboBox;

    @FXML
    private JFXTextArea productDescriptionJFXTextAreaAsLabel;

    @FXML
    private JFXTextField reOrderLevelJFXTextFieldAsLabel;

    @FXML
    private JFXTextField quantityJFXTextFieldAsLabel;

    @FXML
    private JFXTextField buyingPriceJFXTextFieldAsLabel;

    @FXML
    private JFXTextField sellingPriceJFXTextFieldAsLabel;

    @FXML
    private JFXTextField manufacturedDateJFXTextFieldAsLabel;

    @FXML
    private JFXTextField expiryDateJFXTextFieldAsLabel;

    @FXML
    private JFXTextField newQuantityJFXTextField;

    @FXML
    private JFXButton addToExistingStoreButton;

    private static final Logger logger = LogManager.getLogger(AddToStoreController.class);
    // Store List
    private ObservableList<Store> observableStoreList = FXCollections.observableArrayList();
    // ProductIds List
    private ObservableList<String> observableProductIdList = FXCollections.observableArrayList();
    // batchCode List
    private ObservableList<String> observableBatchCodeComboBoxList = FXCollections.observableArrayList();
    // TODO: Auto complete batchCodeJFXTextField
    // private ObservableList<String> observableBatchTextFieldBoxList = FXCollections.observableArrayList();

    // AutoCompletionBinding for productIdJFXText
    private AutoCompletionBinding productIdJFXTextFieldAutoCompletionBinding;

    @FXML
    void initialize() {

        // Initialize required field validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");
        // Initialize number validations
        NumberValidator numberValidator = new NumberValidator();
        numberValidator.setMessage("Only numbers allowed");

        // Get existing store objects with products
        // initialize observableStoreList
        getStoreList();

        // Product Id related UI components
        // Update observableProductIds
        productIdJFXTextFieldAutoCompletionBinding = initializeProductIdAutoCompletion();

        // Bind to productIdComboBox
        productIdComboBox.setItems(observableProductIdList);

        // productIdComboBox validation
        productIdComboBox.getValidators().add(requiredFieldValidator);

        // batchCodeComboBox validation
        batchCodeComboBox.getValidators().add(requiredFieldValidator);

        // productIdJFXTextField
        productIdJFXTextField.getValidators().add(requiredFieldValidator);
        productIdJFXTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                productIdJFXTextField.validate();
            }
        });

        // batchCodeJFXTextField
        batchCodeJFXTextField.getValidators().add(requiredFieldValidator);
        batchCodeJFXTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                batchCodeJFXTextField.validate();
            }
        });

        // quantityJFXTextField
        quantityJFXTextField.getValidators().add(requiredFieldValidator);
        quantityJFXTextField.getValidators().add(numberValidator);
        quantityJFXTextField.focusedProperty()
                .addListener((observable, oldValue, newValue) -> quantityJFXTextField.validate());
        // Replace other characters except numbers and dot
        /**
         quantityJFXTextField.textProperty().addListener((observable, oldValue, newValue) -> {
         // Match floating point number
         if (!newValue.matches("[-+]?[0-9]*\\.?[0-9]*")) {
         quantityJFXTextField.validate();

         // Run later to prevent java.lang.IllegalArgumentException: The start must be <= the end
         Platform.runLater(() -> {
         quantityJFXTextField.setText(newValue.replaceAll("[^\\d]", ""));
         quantityJFXTextField.positionCaret(quantityJFXTextField.getLength());
         });
         }
         });
         **/

        // buyingPriceJFXTextField
        buyingPriceJFXTextField.getValidators().add(requiredFieldValidator);
        buyingPriceJFXTextField.getValidators().add(numberValidator);
        buyingPriceJFXTextField.focusedProperty()
                .addListener((observable, oldValue, newValue) -> buyingPriceJFXTextField.validate());

        // sellingPriceJFXTextField
        sellingPriceJFXTextField.getValidators().add(numberValidator);
        sellingPriceJFXTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                sellingPriceJFXTextField.validate();
            }
        });

        // reOrderLevelJFXTextField
        reOrderLevelJFXTextField.getValidators().add(requiredFieldValidator);
        reOrderLevelJFXTextField.getValidators().add(numberValidator);
        reOrderLevelJFXTextField.focusedProperty()
                .addListener((observable, oldValue, newValue) -> reOrderLevelJFXTextField.validate());

        // newQuantityJFXTextField
        newQuantityJFXTextField.getValidators().add(numberValidator);
        newQuantityJFXTextField.getValidators().add(requiredFieldValidator);
        newQuantityJFXTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                newQuantityJFXTextField.validate();
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
    }

    private void clearLabels() {
        // Clear labels
        productDescriptionJFXTextAreaAsLabel.clear();
        reOrderLevelJFXTextFieldAsLabel.clear();
        quantityJFXTextFieldAsLabel.clear();
        buyingPriceJFXTextFieldAsLabel.clear();
        sellingPriceJFXTextFieldAsLabel.clear();
        manufacturedDateJFXTextFieldAsLabel.clear();
        expiryDateJFXTextFieldAsLabel.clear();
        // Clear textfield
        newQuantityJFXTextField.clear();
    }

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
                    if(product.getRemark() != null){
                        productDescriptionJFXTextAreaAsLabel.setText(product.getRemark() + "");
                    }else{
                        productDescriptionJFXTextAreaAsLabel.clear();
                    }
                    // reOrderLevelJFXTextFieldAsLabel
                    reOrderLevelJFXTextFieldAsLabel.setText(product.getReorderLevel() + "");
                    // quantityJFXTextFieldAsLabel
                    quantityJFXTextFieldAsLabel.setText(product.getAvailableQuantity() + "");
                    // buyingPriceJFXTextFieldAsLabel
                    buyingPriceJFXTextFieldAsLabel.setText(product.getBuyingPrice() + "");
                    // sellingPriceJFXTextFieldAsLabel
                    if(product.getSellingPrice() != 0.0){
                        sellingPriceJFXTextFieldAsLabel.setText(product.getSellingPrice() + "");
                    }else{
                        sellingPriceJFXTextFieldAsLabel.setText("No selling price");
                    }
                    // manufacturedDateJFXTextFieldAsLabel
                    if (product.getManufacturedDate() != null) {
                        manufacturedDateJFXTextFieldAsLabel.setText(product.getManufacturedDate().toString());
                    } else {
                        manufacturedDateJFXTextFieldAsLabel.clear();
                    }
                    // expiryDateJFXTextFieldAsLabel
                    if (product.getExpiryDate() != null) {
                        expiryDateJFXTextFieldAsLabel.setText(product.getExpiryDate().toString());
                    } else {
                        expiryDateJFXTextFieldAsLabel.clear();
                    }
                }
            });
        }
    }

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

    @FXML
    void addToExistingStore(ActionEvent event) {
        // Validation
        boolean productIdComboBoxValidate = productIdComboBox.validate();
        boolean batchCOdeComboBoxValidate = batchCodeComboBox.validate();
        boolean newQuantityJFXTextFieldValidate = newQuantityJFXTextField.validate();

        if(productIdComboBoxValidate && batchCOdeComboBoxValidate && newQuantityJFXTextFieldValidate){
            // Get values from ComboBoxes
            String productId = productIdComboBox.getSelectionModel().getSelectedItem();
            String batchCode = batchCodeComboBox.getSelectionModel().getSelectedItem();
            double newQuantity = Double.parseDouble(newQuantityJFXTextField.getText());

            // Get matching Store which has productIdComboBox value
            Store storeWithMatchingProductId = observableStoreList.stream()
                    .filter(storeList -> productId.equals(storeList.getProductId()))
                    .findAny()
                    .orElse(null);

            if (storeWithMatchingProductId != null){
                // There is a matching productId
                // Check for the batchCode
                Product productWithMatchingBatchCode = storeWithMatchingProductId.getProducts().stream()
                        .filter(productList -> batchCode.equals(productList.getProductId().getBatchCode()))
                        .findAny()
                        .orElse(null);
                if (productWithMatchingBatchCode != null) {
                    // There is matching productId and batchCode
                    // Add new quantity
                    addNewProductToExistingStore(productWithMatchingBatchCode, newQuantity);

                    // Clear labels
                    clearLabels();

                    // Clear selection
                     batchCodeComboBox.getSelectionModel().clearSelection();
                     productIdComboBox.getSelectionModel().clearSelection();

                    // Refresh observableStoreList
                    getStoreList();
                }
            }
        }
    }

    @FXML
    void addToStore(ActionEvent event) {

        boolean productIdValidate = productIdJFXTextField.validate();
        boolean batchCodeValidate = batchCodeJFXTextField.validate();
        boolean productDescriptionValidate = productDescriptionJFXTextArea.validate();
        boolean reOrderLevelValidate = reOrderLevelJFXTextField.validate();
        boolean quantityValidate = quantityJFXTextField.validate();
        boolean buyingPriceValidate = buyingPriceJFXTextField.validate();
        boolean sellingPriceValidate = sellingPriceJFXTextField.validate();
        boolean manufacturedDateValidate = manufacturedDateDatePicker.validate();
        boolean expiryDateValidate = expiryDateDatePicker.validate();

        if (productIdValidate && batchCodeValidate && productDescriptionValidate && reOrderLevelValidate
                && quantityValidate && buyingPriceValidate && sellingPriceValidate && manufacturedDateValidate
                && expiryDateValidate) {

            // Get values
            String productId = productIdJFXTextField.getText();
            String batchCode = batchCodeJFXTextField.getText();
            double quantity = Double.parseDouble(quantityJFXTextField.getText());
            double buyingPrice = Double.parseDouble(buyingPriceJFXTextField.getText());
            double sellingPrice = 0;
            if (!sellingPriceJFXTextField.getText().isEmpty()) {
                sellingPrice = Double.parseDouble(sellingPriceJFXTextField.getText());
            }
            LocalDate manufacturedDate = manufacturedDateDatePicker.getValue();
            LocalDate expiryDate = expiryDateDatePicker.getValue();
            String description = productDescriptionJFXTextArea.getText();
            double reOrderLevel = Double.parseDouble(reOrderLevelJFXTextField.getText());

            // Check for duplication
            Store storeWithMatchingProductId = observableStoreList.stream()
                    .filter(storeList -> productId.equals(storeList.getProductId()))
                    .findAny()
                    .orElse(null);

            if (storeWithMatchingProductId == null) {
                // There is no matching productId
                // Create new product
                addNewProduct(productId, batchCode, quantity, buyingPrice, sellingPrice, manufacturedDate, expiryDate,
                        description, reOrderLevel);

                // Clear fields
                clearAddNewProductFields();

                // Refresh observableStoreList
                getStoreList();
                // Refresh observableProductIds
                // stringAutoCompletionBinding.dispose() -> dispose previous buinding
                productIdJFXTextFieldAutoCompletionBinding.dispose();
                productIdJFXTextFieldAutoCompletionBinding = initializeProductIdAutoCompletion();


            } else {
                // There is a matching productId
                // Check for the batchCode
                Product productWithMatchingBatchCode = storeWithMatchingProductId.getProducts().stream()
                        .filter(productList -> batchCode.equals(productList.getProductId().getBatchCode()))
                        .findAny()
                        .orElse(null);
                if (productWithMatchingBatchCode == null) {
                    // There is no matching productId and batchCode
                    // Create new product
                    addNewProductToExistingStore(storeWithMatchingProductId, productId, batchCode, quantity, buyingPrice,
                            sellingPrice, manufacturedDate, expiryDate, description, reOrderLevel);

                    // Clear fields
                    clearAddNewProductFields();

                    // Refresh observableStoreList
                    getStoreList();
                    // Refresh observableProductIds
                    // stringAutoCompletionBinding.dispose() -> dispose previous buinding
                    productIdJFXTextFieldAutoCompletionBinding.dispose();
                    productIdJFXTextFieldAutoCompletionBinding = initializeProductIdAutoCompletion();
                } else {
                    // There is matching productId and batchCode
                    // Show error message -> please add to existing product
                    showWarningFXDialog("Product already exist. Please use 'Add to existing product' to add new quantity to existing product.");

                    // Clear fields
                    clearAddNewProductFields();

                }
            }
        }


    }

    private void addNewProductToExistingStore(Store storeWithMatchingProductId, String productId, String batchCode,
                                              double quantity, double buyingPrice, double sellingPrice,
                                              LocalDate manufacturedDate, LocalDate expiryDate,
                                              String description, double reOrderLevel) {
        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // Create new store
            Store store = storeWithMatchingProductId;
            Product product = new Product();
            ProductId productIdEntity = new ProductId();

            // productId
            productIdEntity.setBatchCode(batchCode);
            // batchCode
            productIdEntity.setProductId(productId);

            // Product entity related
            // store
            product.setStore(store);
            // productId
            product.setProductId(productIdEntity);
            // quantity
            product.setAvailableQuantity(quantity);
            // buyingPrice
            product.setBuyingPrice(buyingPrice);
            // reOrderLevel
            if (!reOrderLevelJFXTextField.getText().isEmpty()) {
                product.setReorderLevel(reOrderLevel);
            }
            // sellingPrice
            if (!sellingPriceJFXTextField.getText().isEmpty()) {
                product.setSellingPrice(sellingPrice);
            }
            // manufacturedDate
            if (manufacturedDate != null) {
                product.setManufacturedDate(manufacturedDate);
            }
            // expiryDate
            if (expiryDate != null) {
                product.setExpiryDate(expiryDate);
            }
            // description
            if (!description.isEmpty()) {
                product.setRemark(description);
            }

            // Store entity related
            // Products
            List<Product> inputProduct = new ArrayList<>();
            inputProduct.add(product);
            store.setProducts(inputProduct);

            // save Store
            session.update(store);

            // commit transaction
            transaction.commit();

            logger.info("Successfully saved new product: " + store.getProductId()
                    + " with batch code: " + product.getProductId().getBatchCode(), "Add to store");

            // Send notification
            Notification.showInfoNotification("Successfully saved new product: "
                    + store.getProductId() + " with batch code: " + product.getProductId().getBatchCode(), "Add to store");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private void addNewProductToExistingStore(Product productWithMatchingBatchCode, double newQuantity) {
        // Get existing quantity + newQuantity
        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // Get store
            Store store = productWithMatchingBatchCode.getStore();
            // Available quantity
            double availableQuantity = productWithMatchingBatchCode.getAvailableQuantity();
            // Set new quantity
            productWithMatchingBatchCode.setAvailableQuantity(availableQuantity + newQuantity);

            // Attach Products -> productWithMatchingBatchCode
            List<Product> inputProduct = new ArrayList<>();
            inputProduct.add(productWithMatchingBatchCode);

            // Set product to store
            store.setProducts(inputProduct);

            // save Store
            session.update(store);

            // commit transaction
            transaction.commit();

            logger.info("Successfully added new " + newQuantity + " quantity to: " + store.getProductId() +
                    " with batch code: " + productWithMatchingBatchCode.getProductId().getBatchCode(), "Add to store");

            // Send notification
            Notification.showInfoNotification("Successfully added new " + newQuantity + " quantity to: " +
                    store.getProductId() + " with batch code: " + productWithMatchingBatchCode.getProductId().getBatchCode(),
                    "Add to store");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private void addNewProduct(String productId, String batchCode, double quantity, double buyingPrice,
                               double sellingPrice, LocalDate manufacturedDate, LocalDate expiryDate,
                               String description, double reOrderLevel) {

        // Save to database
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            // Create new store
            Store store = new Store();
            Product product = new Product();
            ProductId productIdEntity = new ProductId();

            productIdEntity.setBatchCode(batchCode);
            productIdEntity.setProductId(productId);

            // Product entity related
            // store
            product.setStore(store);
            // productId
            product.setProductId(productIdEntity);
            // batchCode
            //product.getProductId().setBatchCode(batchCode);
            // quantity
            product.setAvailableQuantity(quantity);
            // buyingPrice
            product.setBuyingPrice(buyingPrice);
            // reOrderLevel
            if (!reOrderLevelJFXTextField.getText().isEmpty()) {
                product.setReorderLevel(reOrderLevel);
            }
            // sellingPrice
            if (!sellingPriceJFXTextField.getText().isEmpty()) {
                product.setSellingPrice(sellingPrice);
            }
            // manufacturedDate
            if (manufacturedDate != null) {
                product.setManufacturedDate(manufacturedDate);
            }
            // expiryDate
            if (expiryDate != null) {
                product.setExpiryDate(expiryDate);
            }
            // description
            if (!description.isEmpty()) {
                product.setRemark(description);
            }

            // Store entity related
            // productId
            store.setProductId(productId);
            // Products
            List<Product> inputProduct = new ArrayList<>();
            inputProduct.add(product);
            store.setProducts(inputProduct);

            // save Store
            session.save(store);

            // commit transaction
            transaction.commit();

            logger.info("Successfully saved new product: " + store.getProductId()
                    + store.getProductId() + " with batch code: " + product.getProductId().getBatchCode(), "Add to store");

            // Send notification
            Notification.showInfoNotification("Successfully saved new product: "
                    + store.getProductId() + " with batch code: " + product.getProductId().getBatchCode(), "Add to store");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    // Clear Add New Product related fields
    private void clearAddNewProductFields() {
        productIdJFXTextField.clear();
        batchCodeJFXTextField.clear();
        productDescriptionJFXTextArea.clear();
        reOrderLevelJFXTextField.clear();
        quantityJFXTextField.clear();
        buyingPriceJFXTextField.clear();
        sellingPriceJFXTextField.clear();
        manufacturedDateDatePicker.setValue(null);
        expiryDateDatePicker.setValue(null);

    }

    private void showWarningFXDialog(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Add to store",
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

    private AutoCompletionBinding initializeProductIdAutoCompletion() {
        // Get Product IDs
        observableProductIdList.clear();
        observableStoreList.stream().forEach((store) -> observableProductIdList.add(store.getProductId()));

        // Bind auto completion for productIdJFXTextField
        AutoCompletionBinding stringAutoCompletionBinding = TextFields
                .bindAutoCompletion(productIdJFXTextField, observableProductIdList);
        return stringAutoCompletionBinding;
    }


}
