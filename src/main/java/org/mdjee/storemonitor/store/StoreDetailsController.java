package org.mdjee.storemonitor.store;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.hibernate.entity.Product;
import org.mdjee.storemonitor.hibernate.entity.Store;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;

import java.util.*;
import java.util.stream.Collectors;


public class StoreDetailsController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private TreeTableView<ObservableProduct> treeTableView;

    @FXML
    private TreeTableColumn<ObservableProduct, String> productIdColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> batchCodeColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> remarkColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> manufacturedDateColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> expiryDateColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> availableQuantityColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> reorderLevelColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> buyingPriceColumn;

    @FXML
    private TreeTableColumn<ObservableProduct, String> sellingPriceColumn;

    @FXML
    private JFXTextField searchTextBox;

    @FXML
    private JFXButton showSelectTableColumnsButton;

    @FXML
    void showSelectTableColumnsDialog(ActionEvent event) {

    }

    private static final Logger logger = LogManager.getLogger(AddToStoreController.class);
    private List<Store> storeList;

    @FXML
    void initialize() {

        // Get current Store objects
        storeList = getStoreList();

        // Initialize table
        initializeTreeTableView(storeList);

        // Add listener to searchTextBox
        searchTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {

                // Check for the filter
                List<Store> filteredStoreList = storeList.stream()
                        .filter(storeItem -> storeItem.getProductId().contains(newValue))
                        .collect(Collectors.toList());

                initializeTreeTableView(filteredStoreList);
            }else{
                initializeTreeTableView(storeList);
            }
        });
    }

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
                    "", "", "", "",
                    "", "", storeList.get(i).getDescription()));

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
    }

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
}