package org.mdjee.storemonitor.store;

import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.hibernate.entity.Product;
import org.mdjee.storemonitor.hibernate.entity.Store;
import org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.utils.Notification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDetailsController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXToggleButton sellingPriceNotSavedToggleButton;

    @FXML
    private JFXToggleButton belowReOrderLevelToggleButton;

    @FXML
    private JFXToggleButton productIdToggleButton;

    @FXML
    private JFXTextField productIdToggleButtonTextField;

    @FXML
    private JFXToggleButton batchCodeToggleButton;

    @FXML
    private JFXTextField batchCodeToggleButtonTextField;

    @FXML
    private JFXToggleButton expiredToggleButton;

    @FXML
    private JFXDatePicker expiredToggleButtonDatePicker;

    @FXML
    private JFXButton applyFilterButton;

    @FXML
    private JFXTextField sellingPriceTextField;

    @FXML
    private JFXTextField buyingPriceTextField;

    @FXML
    private JFXTextField reOrderLevelTextField;

    @FXML
    private JFXDatePicker manufacturedDateDatePicker;

    @FXML
    private JFXDatePicker expiryDateDatePicker;

    @FXML
    private JFXTextArea remarkTextArea;

    @FXML
    private JFXButton updateButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton clearButton;

    @FXML
    private TreeTableView<ObservableProduct> treeTableView;

    @FXML
    private TreeTableColumn<ObservableProduct, String> productIdColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> batchCodeColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> remarkColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> availableQuantityColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> reorderLevelColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> buyingPriceColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> sellingPriceColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> expiryDateColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> manufacturedDateColumn;

    /**
     * Apply Filter button event listener.
     *
     * @param event
     */
    @FXML
    void applyTreeTableViewFilter(ActionEvent event) {
        // Check filters
        if (!(sellingPriceNotSavedToggleButton.isSelected() || belowReOrderLevelToggleButton.isSelected() ||
                productIdToggleButton.isSelected() || batchCodeToggleButton.isSelected() || expiredToggleButton.isSelected())) {
            // Show warning message
            showWarningFXDialog("You have not selected any filter.");
            return;
        }

        boolean productIdValidate = true;
        boolean batchCodeValidate = true;
        boolean expiryDateValidate = true;

        // Check validations
        if (productIdToggleButton.isSelected()) {
            productIdValidate = productIdToggleButtonTextField.validate();
        }

        if (batchCodeToggleButton.isSelected()) {
            batchCodeValidate = batchCodeToggleButtonTextField.validate();
        }

        if (expiredToggleButton.isSelected()) {
            expiryDateValidate = expiredToggleButtonDatePicker.validate();
        }


        // Validate
        if (productIdValidate && batchCodeValidate && expiryDateValidate) {
            if (applyFilterButton.getText().equals("Apply Filter")) {
                // Check for the filter

                // Apply other filters
                storeList.stream().forEach(store -> store.setProducts(
                        store.getProducts().stream()
                                .filter(product ->
                                        getResults(product))
                                .collect(Collectors.toList())));

                // Refresh TreeTableView
                initializeTreeTableView(storeList);

                // Set button text
                applyFilterButton.setText("Clear Filter");
            } else {
                // Clear filter
                // Clear TextFields and ToggleButtons
                clearFilter();

                // Set button text
                applyFilterButton.setText("Apply Filter");
            }
        }
    }

    /**
     * Clear filters,input fields and refresh treeTableView.
     */
    private void clearFilter() {
        // Refresh data
        storeList = getStoreList();
        // Refresh TreeViewTable
        initializeTreeTableView(storeList);
        // Clear TextFields and ToggleButtons
        // Selling price
        sellingPriceNotSavedToggleButton.setSelected(false);
        // Re-order level
        belowReOrderLevelToggleButton.setSelected(false);
        // ProductID
        productIdToggleButton.setSelected(false);
        productIdToggleButtonTextField.clear();
        // BatchCode
        batchCodeToggleButton.setSelected(false);
        batchCodeToggleButtonTextField.clear();
        // Expired
        expiredToggleButton.setSelected(false);
        expiredToggleButtonDatePicker.setValue(null);
    }

    /**
     * Apply filters according the business logic.
     *
     * @param product product object which is selected by user
     * @return true if there are any matching filters
     */
    private boolean getResults(Product product) {
        boolean productIdStatus = true;
        boolean batchCodeStatus = true;
        boolean sellingPriceStatus = true;
        boolean reOrderLevelStatus = true;
        boolean expiredStatus = true;
        // productId
        if (productIdToggleButton.isSelected() && !productIdToggleButtonTextField.getText().isEmpty()) {
            productIdStatus = product.getProductId().getProductId().contains(productIdToggleButtonTextField.getText());
        }

        // batchCode
        if (batchCodeToggleButton.isSelected() && !batchCodeToggleButtonTextField.getText().isEmpty()) {
            batchCodeStatus = product.getProductId().getBatchCode().contains(batchCodeToggleButtonTextField.getText());
        }

        // selling price not saved
        if (sellingPriceNotSavedToggleButton.isSelected()) {
            sellingPriceStatus = product.isSellingPriceEmpty();
        }

        // below re-order level
        if (belowReOrderLevelToggleButton.isSelected()) {
            reOrderLevelStatus = product.isBelowReOrderLevel();
        }
        // expired
        if (expiredToggleButton.isSelected()) {
            expiredStatus = product.isExpired(expiredToggleButtonDatePicker.getValue());
        }


        return productIdStatus && batchCodeStatus && sellingPriceStatus && reOrderLevelStatus && expiredStatus;
    }

    /**
     * Delete product event listener.
     *
     * @param event
     */
    @FXML
    void deleteProduct(ActionEvent event) {
        // Validate and initiate delete operation
        validateToDelete();
    }

    /**
     * Update button event listener.
     *
     * @param event
     */
    @FXML
    void updateProduct(ActionEvent event) {
        // Validate
        boolean sellingPriceValidate = sellingPriceTextField.validate();
        boolean buyingPriceValidate = buyingPriceTextField.validate();
        boolean reOrderLevelValidate = reOrderLevelTextField.validate();

        // Check validations
        if (sellingPriceValidate && buyingPriceValidate && reOrderLevelValidate) {
            // Get ObservableProduct
            ObservableProduct product = treeTableView.getSelectionModel().getSelectedItem().getValue();
            // Get new values
            // sellingPrice
            double sellingPrice = 0.0;
            if (!sellingPriceTextField.getText().isEmpty()) {
                sellingPrice = Double.parseDouble(sellingPriceTextField.getText());
            }
            // buyingPrice
            double buyingPrice = Double.parseDouble(buyingPriceTextField.getText());
            // reOrderLevel
            double reOrderLevel = Double.parseDouble(reOrderLevelTextField.getText());
            // manufacturedDate
            LocalDate manufacturedDate = manufacturedDateDatePicker.getValue();
            // expiryDate
            LocalDate expiryDate = expiryDateDatePicker.getValue();
            // description
            String remark = remarkTextArea.getText();

            // Update
            updateProduct(product, sellingPrice, buyingPrice, reOrderLevel, manufacturedDate, expiryDate, remark);

            // Clear fields
            clearFields();

            // Get current Store objects
            storeList = getStoreList();

            // Refresh TreeTableView
            initializeTreeTableView(storeList);

        }

    }

    /**
     * Update product with relevant fields.
     *
     * @param selectedProduct  ObservableProduct object which contains selected product
     * @param sellingPrice     selling price of product
     * @param buyingPrice      buyingPrice of product
     * @param reOrderLevel     reorderLevel of product
     * @param manufacturedDate manufacturedDate of product
     * @param expiryDate       expiryDate of product
     * @param remark           remark of product
     */
    private void updateProduct(ObservableProduct selectedProduct, double sellingPrice, double buyingPrice,
                               double reOrderLevel, LocalDate manufacturedDate, LocalDate expiryDate, String remark) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Query query = session.createQuery("from Product where product_id = :product_id and batch_code =:batch_code",
            //       Product.class);
            // query.setParameter("product_id", selectedProduct.getProductId());
            // query.setParameter("batch_code", selectedProduct.getBatchCode());
            // Product result = (Product) query.uniqueResult();
            // // Set new values to result object and update
            // result.setSellingPrice(sellingPrice);
            // result.setBuyingPrice(buyingPrice);
            // result.setReorderLevel(reOrderLevel);
            // result.setRemark(remark);
            // if (manufacturedDate != null) {
            //    result.setManufacturedDate(manufacturedDate);
            // }
            // if (expiryDate != null) {
            //    result.setExpiryDate(expiryDate);
            // }
            // // start a transaction
            // transaction = session.beginTransaction();
            // // update Product
            // session.update(result);
            // // commit transaction
            // transaction.commit();

            // Using JPA CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaUpdate<Product> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Product.class);
            Root<Product> root = criteriaUpdate.from(Product.class);
            // Create predicate
            Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId").get("productId"), selectedProduct.getProductId());
            Predicate batchCodePredicate = criteriaBuilder.equal(root.get("productId").get("batchCode"), selectedProduct.getBatchCode());
            // Set updated values
            criteriaUpdate.set("sellingPrice", sellingPrice);
            criteriaUpdate.set("buyingPrice", buyingPrice);
            criteriaUpdate.set("reorderLevel", reOrderLevel);
            criteriaUpdate.set("manufacturedDate", manufacturedDate);
            criteriaUpdate.set("expiryDate", expiryDate);
            criteriaUpdate.set("remark", remark);
            // Apply predicate with and
            criteriaUpdate.where(criteriaBuilder.and(productIdPredicate, batchCodePredicate));
            // Begin transaction
            transaction = session.beginTransaction();
            // Create query from criteriaUpdate
            session.createQuery(criteriaUpdate).executeUpdate();
            // Commit transaction
            transaction.commit();


            logger.info("Successfully updated product: " + selectedProduct.getProductId()
                    + " with batch code: " + selectedProduct.getBatchCode(), "Product details");

            // Send notification
            Notification.showInfoNotification("Successfully updated product: " + selectedProduct.getProductId()
                    + " with batch code: " + selectedProduct.getBatchCode(), "Product details");


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
     * Clear button event listener.
     * This is calling to clearFields() method.
     *
     * @param event
     */
    @FXML
    void clearProductDetails(ActionEvent event) {
        // Clear fields
        clearFields();
    }

    /**
     * Clear product details related fields.
     */
    private void clearFields() {
        sellingPriceTextField.clear();
        buyingPriceTextField.clear();
        reOrderLevelTextField.clear();
        manufacturedDateDatePicker.setValue(null);
        expiryDateDatePicker.setValue(null);
        remarkTextArea.clear();
    }

    /**
     * Logger instance for AddToStoreController class
     */
    private static final Logger logger = LogManager.getLogger(AddToStoreController.class);

    /**
     * List of Store objects to populate TreeTableView
     */
    private List<Store> storeList;

    /**
     * Initialize validations for components and bind event listeners.
     * Added tooltips accordingly to some components.
     */
    @FXML
    void initialize() {

        // Get current Store objects
        storeList = getStoreList();

        // Initialize table
        initializeTreeTableView(storeList);

        // Validators
        // Initialize required field validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        // Initialize number validations
        NumberValidator numberValidator = new NumberValidator();
        numberValidator.setMessage("Only numbers allowed");

        // Product ID Toggle
        productIdToggleButtonTextField.getValidators().add(requiredFieldValidator);
        productIdToggleButtonTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && productIdToggleButton.isSelected()) {
                productIdToggleButtonTextField.validate();
            }
        });

        // Batch Code Toggle
        batchCodeToggleButtonTextField.getValidators().add(requiredFieldValidator);
        batchCodeToggleButtonTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && batchCodeToggleButton.isSelected()) {
                batchCodeToggleButtonTextField.validate();
            }
        });

        // Expired Toggle
        expiredToggleButtonDatePicker.getValidators().add(requiredFieldValidator);
        expiredToggleButtonDatePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && expiredToggleButton.isSelected()) {
                expiredToggleButtonDatePicker.validate();
            }
        });

        // Buying Price
        buyingPriceTextField.getValidators().add(requiredFieldValidator);
        buyingPriceTextField.getValidators().add(numberValidator);
        buyingPriceTextField.focusedProperty().addListener((observable, oldValue, newValue) -> buyingPriceTextField.validate());

        // Re-order level
        reOrderLevelTextField.getValidators().add(requiredFieldValidator);
        reOrderLevelTextField.getValidators().add(numberValidator);
        reOrderLevelTextField.focusedProperty().addListener((observable, oldValue, newValue) -> reOrderLevelTextField.validate());

        // Selling price
        sellingPriceTextField.getValidators().add(numberValidator);
        sellingPriceTextField.focusedProperty().addListener((observable, oldValue, newValue) -> sellingPriceTextField.validate());

        // Add listener to treeTableView
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Clear fields
                clearFields();

                // Populate fields
                // sellingPrice
                if (newValue.getValue().getSellingPrice().equals("No selling price")) {
                    sellingPriceTextField.setText("0.0");
                } else {
                    sellingPriceTextField.setText(newValue.getValue().getSellingPrice());
                }
                // buyingPrice
                buyingPriceTextField.setText(newValue.getValue().getBuyingPrice());
                // reOrderLevel
                reOrderLevelTextField.setText(newValue.getValue().getReorderLevel());
                // manufacturedDate
                if (!newValue.getValue().getManufacturedDate().isEmpty()) {
                    manufacturedDateDatePicker.setValue(LocalDate.parse(newValue.getValue().getManufacturedDate()));
                }
                // expiryDate
                if (!newValue.getValue().getExpiryDate().isEmpty()) {
                    expiryDateDatePicker.setValue(LocalDate.parse(newValue.getValue().getExpiryDate()));
                }
                // description
                remarkTextArea.setText(newValue.getValue().getRemark());
            }
        });

        // Set tooltip
        manufacturedDateDatePicker.setTooltip(new Tooltip("Manufactured Date"));
        expiryDateDatePicker.setTooltip(new Tooltip("Expiry Date"));
        expiredToggleButtonDatePicker.setTooltip(new Tooltip("Expired"));
    }

    /**
     * Initialize TreeTableView with list of Store objects.
     * Create TreeItems dynamically according to number of products
     *
     * @param storeList contains list of Store objects
     */
    private void initializeTreeTableView(List<Store> storeList) {

        // Dummy root
        // This will be root for all the elements
        TreeItem<ObservableProduct> root = new TreeItem<>(new ObservableProduct("", "",
                "", "", "", "",
                "", "", ""));

        // Create Store item array
        TreeItem[] storeItemArray = new TreeItem[storeList.size()];
        // Loop through storeList to populate storeItemList
        for (int i = 0; i < storeList.size(); i++) {
            // Create new TreeItem and add to storeItemArray
            storeItemArray[i] = new TreeItem<>(new ObservableProduct(storeList.get(i).getProductId(), "",
                    "", "", "", "", "", "",
                    storeList.get(i).getDescription()));

            // Create related product
            List<Product> productList = storeList.get(i).getProducts();
            TreeItem[] productTreeItemArray = new TreeItem[productList.size()];
            for (int j = 0; j < productList.size(); j++) {

                // Get values
                // productId
                String productId = productList.get(j).getProductId().getProductId();
                // batchCode
                String batchCode = productList.get(j).getProductId().getBatchCode();
                // availableQuantity
                String availableQuantity = String.valueOf(productList.get(j).getAvailableQuantity());
                // buyingPrice
                String buyingPrice = String.valueOf(productList.get(j).getBuyingPrice());
                // sellingPrice
                double sellingPriceValue = productList.get(j).getSellingPrice();
                String sellingPrice = "";
                if (sellingPriceValue == 0) {
                    sellingPrice = String.valueOf("No selling price");
                } else {
                    sellingPrice = String.valueOf(sellingPriceValue);
                }
                // manufacturedDate
                String manufacturedDate = "";
                if (productList.get(j).getManufacturedDate() != null) {
                    manufacturedDate = productList.get(j).getManufacturedDate().toString();
                }
                // expiryDate
                String expiryDate = "";
                if (productList.get(j).getExpiryDate() != null) {
                    expiryDate = productList.get(j).getExpiryDate().toString();
                }
                // description
                String remark = productList.get(j).getRemark();
                // reOrderLevel
                String reOrderLevel = String.valueOf(productList.get(j).getReorderLevel());

                productTreeItemArray[j] = new TreeItem<>(new ObservableProduct(productId, batchCode, manufacturedDate,
                        expiryDate, availableQuantity, reOrderLevel, buyingPrice, sellingPrice, remark));
            }

            // Add TreeItems to secondary root (Store)
            storeItemArray[i].getChildren().setAll(productTreeItemArray);
        }

        root.getChildren().setAll(storeItemArray);

        productIdColumn.setCellValueFactory(param -> param.getValue().getValue().productIdProperty());
        batchCodeColumn.setCellValueFactory(param -> param.getValue().getValue().batchCodeProperty());
        remarkColumn.setCellValueFactory(param -> param.getValue().getValue().remarkProperty());
        manufacturedDateColumn.setCellValueFactory(param -> param.getValue().getValue().manufacturedDateProperty());
        expiryDateColumn.setCellValueFactory(param -> param.getValue().getValue().expiryDateProperty());
        availableQuantityColumn.setCellValueFactory(param -> param.getValue().getValue().availableQuantityProperty());
        reorderLevelColumn.setCellValueFactory(param -> param.getValue().getValue().reorderLevelProperty());
        buyingPriceColumn.setCellValueFactory(param -> param.getValue().getValue().buyingPriceProperty());
        sellingPriceColumn.setCellValueFactory(param -> param.getValue().getValue().sellingPriceProperty());

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);

        // Set context menu
        MenuItem deleteMenuItem = new MenuItem("Delete", new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItem.setOnAction(event -> {

            System.out.println("Delete clicked");
            // Start delete by validating
            validateToDelete();
        });
        MenuItem closeMenuItem = new MenuItem("Close");
        closeMenuItem.setOnAction(event -> System.out.println("Close clicked"));

        treeTableView.setContextMenu(new ContextMenu(deleteMenuItem, closeMenuItem));
    }

    /**
     * Validate before delete Product/Store selected item in treeTableView.
     */
    private void validateToDelete() {
        if (treeTableView.getSelectionModel().selectedItemProperty().getValue() == null) {
            // Show warning message
            // Oops It seems like you haven't selected any product.
            // Option one:
            // Please select a product and click delete button.
            // Option two:
            // Right click on the table and click delete
            showWarningFXDialog("It seems like you haven't selected any product.\n\n" +
                    "Option one:\n" +
                    "Select a product and click delete button.\n\n" +
                    "Option two:\n" +
                    "Right click on the table and click delete.");
            return;
        }
        // Get ObservableProduct
        ObservableProduct observableProduct = treeTableView.getSelectionModel().
                selectedItemProperty().getValue().getValue();
        if (observableProduct != null) {
            // Check for batch code
            // If there no any batch code treat as a Store element
            // Then delete accordingly
            if (observableProduct.getBatchCode() == null || observableProduct.getBatchCode().isEmpty()) {
                // Get confirmation message to delete Store
                getConfirmationToDeleteStore(observableProduct);
            } else {
                // Get confirmation to delete product
                getConfirmationToDeleteProduct(observableProduct);
            }
        } else {
            // Show error message
            // You haven't selected any product
            showWarningFXDialog("You haven't selected any product");
        }
    }

    /**
     * Show confirmation window before delete Product and check in {@link VehicleStoreProduct} for particular productId.
     *
     * @param observableProduct selected product object as a observable
     */
    private void getConfirmationToDeleteProduct(ObservableProduct observableProduct) {
        // Check in Vehicle Store before delete
        JFXDialogLayout content = Notification.showJFXDialogLayout("Product details",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Are you sure you want to delete product: " +
                        observableProduct.getProductId() + " with batch code " + observableProduct.getBatchCode() +
                        "\nYou will not be able to restore anything after deletion.");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Check in VehicleStoreProduct Store before delete
            boolean status = isStoreExistsInVehicleStoreProduct(observableProduct);

            if (status) {
                // Show warning message
                // Can't delete product with collection of batches because product already loaded to vehicle(s)
                showWarningFXDialog("Can't delete product with collection of batches because product already loaded to vehicle(s).\n" +
                        "First you have to balance particular vehicle(s) in order to release for deletion.");
            } else {
                // Delete from Store
                deleteProduct(observableProduct);

                // Clear fields
                clearFields();

                // Get current Store objects
                storeList = getStoreList();

                // Refresh TreeTableView
                initializeTreeTableView(storeList);
            }
            // Close dialog
            dialog.close();
        });

        // Create No button
        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event -> dialog.close());

        content.setActions(noButton, yesButton);
        dialog.show();

    }

    /**
     * Show confirmation window before delete Store and check in {@link VehicleStoreProduct} for particular productId.
     *
     * @param observableProduct selected product object as a observable
     */
    private void getConfirmationToDeleteStore(ObservableProduct observableProduct) {
        JFXDialogLayout content = Notification.showJFXDialogLayout("Product details",
                FontAwesomeIcon.WARNING, "2em",
                "warning-icon", "Are you sure you want to delete entire product: " +
                        observableProduct.getProductId() + " This may include several batch(es).\n " +
                        "You will not be able to restore anything after deletion.");

        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        // Prevent closes when clicking away
        dialog.setOverlayClose(false);

        // Create Yes button
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.getStyleClass().add("danger-button");
        yesButton.setOnAction(event1 -> {
            // Check in VehicleStoreProduct Store before delete
            boolean status = isStoreExistsInVehicleStoreProduct(observableProduct);

            if (status) {
                // Show warning message
                // Can't delete product with collection of batches because product already loaded to vehicle(s)
                showWarningFXDialog("Can't delete product with collection of batches because product already loaded to vehicle(s).\n" +
                        "First you have to balance particular vehicle(s) in order to release for deletion.");
            } else {
                // Delete from Store
                deleteProduct(observableProduct);
                deleteStore(observableProduct);

                // Clear fields
                clearFields();

                // Get current Store objects
                storeList = getStoreList();

                // Refresh TreeTableView
                initializeTreeTableView(storeList);
            }
            // Close dialog
            dialog.close();
        });

        // Create No button
        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event -> dialog.close());

        content.setActions(noButton, yesButton);
        dialog.show();

    }

    /**
     * Delete Product from database if matching productId and batchCode.
     *
     * @param observableProduct selected product by user
     */
    private void deleteStore(ObservableProduct observableProduct) {
        Transaction transaction = null;
        String message = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaDelete<Store> criteriaDelete = criteriaBuilder.createCriteriaDelete(Store.class);
            Root<Store> root = criteriaDelete.from(Store.class);

            // Delete using productId and batchCode
            criteriaDelete.where(criteriaBuilder.equal(root.get("productId"), observableProduct.getProductId()));

            message = "Successfully deleted product: " + observableProduct.getProductId() + " with all the related batch(es)";

            transaction = session.beginTransaction();
            session.createQuery(criteriaDelete).executeUpdate();
            transaction.commit();

            // Logger
            logger.info(message, "Product details");

            // Send notification
            Notification.showInfoNotification(message, "Product details");


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
     * Delete Store and related Products from database.
     * If there any matching productID.
     *
     * @param observableProduct selected product by user
     */
    private void deleteProduct(ObservableProduct observableProduct) {
        Transaction transaction = null;
        String message = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaDelete<Product> criteriaDelete = criteriaBuilder.createCriteriaDelete(Product.class);
            Root<Product> root = criteriaDelete.from(Product.class);

            if (observableProduct.getBatchCode() == null || observableProduct.getBatchCode().isEmpty()) {
                // Apply only for productId
                Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId").get("productId"), observableProduct.getProductId());

                // Delete using batchCode
                criteriaDelete.where(productIdPredicate);

            } else {
                // Apply for productId and batchCode
                Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId").get("productId"), observableProduct.getProductId());
                Predicate batchCodePredicate = criteriaBuilder.equal(root.get("productId").get("batchCode"), observableProduct.getBatchCode());

                // Delete using productId and batchCode
                criteriaDelete.where(criteriaBuilder.and(productIdPredicate, batchCodePredicate));
            }

            message = "Successfully deleted product: " + observableProduct.getProductId()
                    + " with batch code: " + observableProduct.getBatchCode();

            transaction = session.beginTransaction();
            session.createQuery(criteriaDelete).executeUpdate();
            transaction.commit();

            // Logger
            logger.info(message, "Product details");

            // Send notification
            Notification.showInfoNotification(message, "Product details");


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
     * Returns true if there any productId available in VehicleStoreProduct.
     *
     * @param observableProduct Object which contains product details
     * @return true if there any matching product
     */
    private boolean isStoreExistsInVehicleStoreProduct(ObservableProduct observableProduct) {
        boolean status = false;
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using HQL
            // Query query = session.createQuery("from VehicleStoreProduct where productId =:productId", VehicleStoreProduct.class);
            // query.setParameter("productId", observableProduct.getProductId());
            // status = !query.list().isEmpty();

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<VehicleStoreProduct> criteriaQuery = criteriaBuilder.createQuery(VehicleStoreProduct.class);
            Root<VehicleStoreProduct> root = criteriaQuery.from(VehicleStoreProduct.class);
            criteriaQuery.select(root)
                    .where(criteriaBuilder.equal(root.get("productId"), observableProduct.getProductId()));

            Query<VehicleStoreProduct> query = session.createQuery(criteriaQuery);
            status = !query.getResultList().isEmpty();

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
     * Returns list of store objects from database
     *
     * @return List<Store>
     */
    private List<Store> getStoreList() {
        Transaction transaction = null;
        List<Store> list = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from Store", Store.class);

            list = query.list();

            // TODO: Test
            list.stream().forEach((store) -> System.out.println("Found products: " + store.getProducts().size()));

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

    /**
     * Shows warning message dialog with given message.
     *
     * @param message message of the dialog
     */
    private void showWarningFXDialog(String message) {
        // Show Status
        JFXDialogLayout content = Notification.showJFXDialogLayout("Product details",
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
