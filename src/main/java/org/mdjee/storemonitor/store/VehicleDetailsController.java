package org.mdjee.storemonitor.store;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.hibernate.entity.VehicleStore;
import org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleDetailsController {

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField searchTextBox;

    @FXML
    private TreeTableView<ObservableVehicleStore> treeTableView;

    @FXML
    private TreeTableColumn<ObservableVehicleStore, String> productIdColumn;

    @FXML
    private TreeTableColumn<ObservableVehicleStore, String> batchCodeColumn;

    @FXML
    private TreeTableColumn<ObservableVehicleStore, Number> availableQuantityColumn;


    private static final Logger logger = LogManager.getLogger(AddToStoreController.class);
    private List<VehicleStore> storeList;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");


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
                List<VehicleStore> filteredStoreList = storeList.stream()
                        .filter(storeItem -> storeItem.getVehicleId().contains(newValue))
                        .collect(Collectors.toList());

                initializeTreeTableView(filteredStoreList);
            } else {
                initializeTreeTableView(storeList);
            }
        });
    }

    private void initializeTreeTableView(List<VehicleStore> storeList) {

        // Dummy root
        // This will be root for all the elements
        TreeItem<ObservableVehicleStore> root = new TreeItem<>(new ObservableVehicleStore("", "",
                0.0));

        // Create Store item array
        TreeItem[] storeItemArray = new TreeItem[storeList.size()];
        // Loop through storeList to populate storeItemList
        for (int i = 0; i < storeList.size(); i++) {
            // Create new TreeItem and add to storeItemArray
            ObservableVehicleStore newObservableVehicleStore = new ObservableVehicleStore(storeList.get(i).getVehicleId(),
                    storeList.get(i).getLoadedDate().format(formatter) + " " + storeList.get(i).getRemark(),
                    0.0);
            storeItemArray[i] = new TreeItem<>(newObservableVehicleStore);

            // Create related product
            List<VehicleStoreProduct> productList = storeList.get(i).getVehicleStoreProducts();
            TreeItem[] productTreeItemArray = new TreeItem[productList.size()];
            for (int j = 0; j < productList.size(); j++) {

                // Get values
                // productId
                String productId = productList.get(j).getProductId();
                // batchCode
                String batchCode = productList.get(j).getBatchCode();
                // availableQuantity
                Double availableQuantity = productList.get(j).getAvailableQuantity();


                productTreeItemArray[j] = new TreeItem<>(new ObservableVehicleStore(productId, batchCode, availableQuantity));

                // TODO: Update total quantity
                newObservableVehicleStore.setAvailableQuantity(newObservableVehicleStore.getAvailableQuantity() + availableQuantity);
            }

            // Add TreeItems to secondary root (Store)
            storeItemArray[i].getChildren().setAll(productTreeItemArray);

        }

        root.getChildren().setAll(storeItemArray);

        productIdColumn.setCellValueFactory(param -> param.getValue().getValue().productIdProperty());
        batchCodeColumn.setCellValueFactory(param -> param.getValue().getValue().batchCodeProperty());
        availableQuantityColumn.setCellValueFactory(param -> param.getValue().getValue().availableQuantityProperty());

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    private List<VehicleStore> getStoreList() {
        Transaction transaction = null;
        List<VehicleStore> list = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from VehicleStore ", VehicleStore.class);

            list = query.list();

            // TODO: Test
            list.stream().forEach((store) -> System.out.println("Found products: " + store.getVehicleId()));

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
