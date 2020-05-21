package org.mdjee.storemonitor.settings;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mdjee.storemonitor.App;
import org.mdjee.storemonitor.hibernate.entity.BalanceHistory;
import org.mdjee.storemonitor.hibernate.entity.Profit;
import org.mdjee.storemonitor.hibernate.entity.VehicleStore;
import org.mdjee.storemonitor.hibernate.utils.HibernateUtil;
import org.mdjee.storemonitor.reports.Product;
import org.mdjee.storemonitor.reports.VehicleStoreProduct;
import org.mdjee.storemonitor.utils.Notification;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportsController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private PieChart pieChart;

    @FXML
    private CategoryAxis monthAxis;

    @FXML
    private NumberAxis profitAmountAxis;

    @FXML
    private LineChart<String, Double> lineChart;

    @FXML
    private JFXComboBox<String> productIdComboBoxProductDetailsPane;

    @FXML
    private JFXComboBox<String> vehicleIdComboBoxProductDetailsPane;

    @FXML
    private JFXDatePicker expiredDatePickerProductDetailsPane;

    @FXML
    private JFXComboBox<String> productIdComboBoxPriceDetails;

    @FXML
    private JFXComboBox<String> vehicleIdComboBoxVehicleBalanceDetails;

    @FXML
    private JFXDatePicker balanceDateDatePickerVehicleBalanceDetails;

    @FXML
    private JFXRadioButton todayRadioButtonProfitPane;

    @FXML
    private ToggleGroup period;

    @FXML
    private JFXRadioButton currentMonthRadioButtonProfitPane;

    @FXML
    private JFXRadioButton currentYearRadioButtonProfitPane;

    @FXML
    private JFXRadioButton fromRadioButtonProfitPane;

    @FXML
    private JFXDatePicker fromDatePickerProfitPane;

    @FXML
    private JFXDatePicker toDateDatePickerProfitPane;

    @FXML
    private JFXComboBox<String> vehicleIdComboBoxProfitPane;

    @FXML
    private JFXComboBox<String> productIdComboBoxProfitPane;

    /**
     * Logger instance for ReportsController class.
     */
    private static final Logger logger = LogManager.getLogger(ReportsController.class);


    private ObservableList<String> vehicleIdObservableListFromVehicleStore = FXCollections.observableArrayList();
    private ObservableList<String> vehicleIdObservableListFromBalanceHistory = FXCollections.observableArrayList();
    private ObservableList<String> productIdObservableList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        // Validations
        // Initialize required field validations
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Required field");

        // Get vehicle ID
        populateVehicleIdObservableListFromVehicleStore();
        // Get products
        populateProductIdObservableList();
        // Get balanceHistory
        populateVehicleIdObservableListFromBalanceHistory();

        // productIdComboBoxProductDetailsPane
        productIdComboBoxProductDetailsPane.getValidators().add(requiredFieldValidator);
        productIdComboBoxProductDetailsPane.setItems(productIdObservableList);

        // vehicleIdComboBoxProductDetailsPane
        vehicleIdComboBoxProductDetailsPane.getValidators().add(requiredFieldValidator);
        vehicleIdComboBoxProductDetailsPane.setItems(vehicleIdObservableListFromVehicleStore);

        // productIdComboBoxPriceDetails
        productIdComboBoxPriceDetails.getValidators().add(requiredFieldValidator);
        productIdComboBoxPriceDetails.setItems(productIdObservableList);

        // vehicleIdComboBoxVehicleBalanceDetails
        vehicleIdComboBoxVehicleBalanceDetails.getValidators().add(requiredFieldValidator);
        vehicleIdComboBoxVehicleBalanceDetails.setItems(vehicleIdObservableListFromBalanceHistory);

        // vehicleIdComboBoxProfitPane
        vehicleIdComboBoxProfitPane.getValidators().add(requiredFieldValidator);
        vehicleIdComboBoxProfitPane.setItems(vehicleIdObservableListFromBalanceHistory);

        // productIdComboBoxProfitPane
        productIdComboBoxProfitPane.getValidators().add(requiredFieldValidator);
        productIdComboBoxProfitPane.setItems(productIdObservableList);

        // expiredDatePickerProductDetailsPane
        expiredDatePickerProductDetailsPane.getValidators().add(requiredFieldValidator);

        // fromDatePickerProfitPane
        fromDatePickerProfitPane.getValidators().add(requiredFieldValidator);

        // toDateDatePickerProfitPane
        toDateDatePickerProfitPane.getValidators().add(requiredFieldValidator);

        // balanceDateDatePickerVehicleBalanceDetails
        balanceDateDatePickerVehicleBalanceDetails.getValidators().add(requiredFieldValidator);

        // Draw charts
        Task pieChartWorker = pieChartWorker();
        new Thread(pieChartWorker).start();

        Task lineChartWorker = lineChartWorker();
        new Thread(lineChartWorker).start();
    }

    @FXML
    void showBelowReorderLevelProducts(ActionEvent event) throws JRException {
        List<Product> products = getProducts().stream()
                .filter(product -> product.getAvailableQuantity() <= product.getReorderLevel())
                .collect(Collectors.toList());

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(products);

        Map<String, Object> params = new HashMap();
        params.put("beanCollectionParam", jrBeanCollectionDataSource);
        params.put("subTitle", "Products below re-order level");


        // Using compiled .jasper file
        InputStream inputStream = App.class.getResourceAsStream("reports/product-details-landscape.jasper");
        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
        JasperViewer.viewReport(jasperPrint, false);
    }

    @FXML
    void showExpiredProducts(ActionEvent event) throws JRException {

        boolean validate = expiredDatePickerProductDetailsPane.validate();

        if (validate) {

            List<Product> products = getProducts().stream()
                    .filter(product -> {

                        boolean status = false;
                        if (product.getExpiryDate() != null) {
                            status = product.getExpiryDate().compareTo(expiredDatePickerProductDetailsPane.getValue()) <= 0;
                        }

                        return status;
                    })
                    .collect(Collectors.toList());

            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(products);

            Map<String, Object> params = new HashMap();
            params.put("beanCollectionParam", jrBeanCollectionDataSource);
            params.put("subTitle", "Expired products as at " + expiredDatePickerProductDetailsPane.getValue());


            // Using compiled .jasper file
            InputStream inputStream = App.class.getResourceAsStream("reports/product-details-landscape.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
            JasperViewer.viewReport(jasperPrint, false);
        }

    }

    @FXML
    void showProductPrices(ActionEvent event) throws JRException {
        boolean validate = productIdComboBoxPriceDetails.validate();


        if (validate) {

            List<Product> products = null;
            // Check for the selection
            if (productIdComboBoxPriceDetails.getSelectionModel().getSelectedItem().equals("All products")) {
                // Get all products
                products = getProducts();
            } else {
                // Get product only for selected product
                String productId = productIdComboBoxPriceDetails.getSelectionModel().getSelectedItem();
                products = getProducts(productId);
            }

            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(products);

            Map<String, Object> params = new HashMap();
            params.put("beanCollectionParam", jrBeanCollectionDataSource);

            // Using compiled .jasper file
            InputStream inputStream = App.class.getResourceAsStream("reports/product-price.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
            JasperViewer.viewReport(jasperPrint, false);

        }
    }

    @FXML
    void showProducts(ActionEvent event) throws FileNotFoundException, JRException {

        boolean validate = productIdComboBoxProductDetailsPane.validate();

        if (validate) {

            List<Product> products = null;
            // Check for the selection
            if (productIdComboBoxProductDetailsPane.getSelectionModel().getSelectedItem().equals("All products")) {
                // Get all products
                products = getProducts();
            } else {
                // Get product only for selected product
                String productId = productIdComboBoxProductDetailsPane.getSelectionModel().getSelectedItem();
                products = getProducts(productId);
            }

            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(products);

            Map<String, Object> params = new HashMap();
            params.put("beanCollectionParam", jrBeanCollectionDataSource);
            params.put("subTitle", "Product details");

            // If using .jrxml - not compiled version
            // String xmlFile = "src/main/resources/product-details-landscape.jrxml";
            // InputStream inputStream = new FileInputStream(new File(xmlFile));
            // JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            // JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            // JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
            // JasperViewer.viewReport(jasperPrint);

            // Using compiled .jasper file
            InputStream inputStream = App.class.getResourceAsStream("reports/product-details-landscape.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
            JasperViewer.viewReport(jasperPrint, false);

        }

    }

    /**
     * Get all the products and create Product beans.
     *
     * @return list of reports.Product
     */
    private List<Product> getProducts() {

        List<Product> productList = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<org.mdjee.storemonitor.hibernate.entity.Product> criteriaQuery = criteriaBuilder
                    .createQuery(org.mdjee.storemonitor.hibernate.entity.Product.class);
            Root<org.mdjee.storemonitor.hibernate.entity.Product> root = criteriaQuery
                    .from(org.mdjee.storemonitor.hibernate.entity.Product.class);
            criteriaQuery.select(root);

            Query<org.mdjee.storemonitor.hibernate.entity.Product> query = session.createQuery(criteriaQuery);
            List<org.mdjee.storemonitor.hibernate.entity.Product> list = query.list();

            // Create reports.Product beans
            list.forEach(product -> productList.add(
                    new Product(product.getProductId().getProductId(), product.getProductId().getBatchCode(),
                            product.getRemark(), product.getSellingPrice(), product.getBuyingPrice(),
                            product.getReorderLevel(), product.getAvailableQuantity(), product.getManufacturedDate(),
                            product.getExpiryDate())
            ));


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return productList;
    }

    /**
     * Get products and create Product beans for given productId.
     *
     * @return list of reports.Product
     */
    private List<Product> getProducts(String productId) {

        Transaction transaction = null;
        List<Product> productList = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<org.mdjee.storemonitor.hibernate.entity.Product> criteriaQuery = criteriaBuilder
                    .createQuery(org.mdjee.storemonitor.hibernate.entity.Product.class);
            Root<org.mdjee.storemonitor.hibernate.entity.Product> root = criteriaQuery
                    .from(org.mdjee.storemonitor.hibernate.entity.Product.class);
            criteriaQuery.select(root)
                    .where(criteriaBuilder.equal(root.get("productId").get("productId"), productId));

            Query<org.mdjee.storemonitor.hibernate.entity.Product> query = session.createQuery(criteriaQuery);
            List<org.mdjee.storemonitor.hibernate.entity.Product> list = query.list();

            // Create reports.Product beans
            list.forEach(product -> productList.add(
                    new Product(product.getProductId().getProductId(), product.getProductId().getBatchCode(),
                            product.getRemark(), product.getSellingPrice(), product.getBuyingPrice(),
                            product.getReorderLevel(), product.getAvailableQuantity(), product.getManufacturedDate(),
                            product.getExpiryDate())
            ));


        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return productList;
    }

    @FXML
    void showProfitByProduct(ActionEvent event) {

        // Validations
        boolean vehicleIdValidate = productIdComboBoxProfitPane.validate();
        boolean dateValidate = true;

        if (fromRadioButtonProfitPane.isSelected()) {
            boolean fromDateValidate = fromDatePickerProfitPane.validate();
            boolean toDateValidate = toDateDatePickerProfitPane.validate();

            if (fromDateValidate && toDateValidate) {
                dateValidate = true;
            } else {
                dateValidate = false;
            }
        }

        if (vehicleIdValidate && dateValidate) {
            // Selected vehicle
            String productId = productIdComboBoxProfitPane.getSelectionModel().getSelectedItem();

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Profit> criteriaQuery = criteriaBuilder.createQuery(Profit.class);
                Root<Profit> root = criteriaQuery.from(Profit.class);

                Predicate datePredicate = null;
                Map<String, Object> params = new HashMap();


                // If todayRadioButtonProfitPane checked
                if (todayRadioButtonProfitPane.isSelected()) {

                    if (productId.equals("All products")) {
                        params.put("subTitle", "Profit report for all products on " + LocalDate.now());
                    } else {
                        params.put("subTitle", "Profit report for " + productId + " on " + LocalDate.now());
                    }

                    datePredicate = criteriaBuilder.equal(criteriaBuilder.function("DATE", Date.class,
                            root.get("balanceHistory").get("stockBalancedDate")),
                            new Date());
                }

                // currentMonthRadioButtonProfitPane
                if (currentMonthRadioButtonProfitPane.isSelected()) {

                    if (productId.equals("All products")) {
                        params.put("subTitle", "Profit report for all products of month " + LocalDate.now().getMonth()
                                + " " + LocalDate.now().getYear());
                    } else {
                        params.put("subTitle", "Profit report for " + productId + " of month " + LocalDate.now().getMonth()
                                + " " + LocalDate.now().getYear());
                    }

                    datePredicate = criteriaBuilder.equal(
                            criteriaBuilder.function("MONTH", Integer.class,
                                    root.get("balanceHistory").get("stockBalancedDate")),
                            LocalDate.now().getMonth().getValue());
                }

                // currentYearRadioButtonProfitPane
                if (currentYearRadioButtonProfitPane.isSelected()) {

                    if (productId.equals("All products")) {
                        params.put("subTitle", "Profit report for all products of year " + LocalDate.now().getYear());
                    } else {
                        params.put("subTitle", "Profit report for " + productId + " of year " + LocalDate.now().getYear());
                    }

                    datePredicate = criteriaBuilder.equal(
                            criteriaBuilder.function("YEAR", Integer.class,
                                    root.get("balanceHistory").<LocalDateTime>get("stockBalancedDate")),
                            LocalDate.now().getYear());
                }

                // fromRadioButtonProfitPane
                if (fromRadioButtonProfitPane.isSelected()) {
                    LocalDate fromDate = fromDatePickerProfitPane.getValue();
                    LocalDate toDate = toDateDatePickerProfitPane.getValue();
                    // Default time zone
                    ZoneId defaultZoneId = ZoneId.systemDefault();

                    if (productId.equals("All products")) {
                        params.put("subTitle", "Profit report for all products between " +
                                fromDate.toString() + " and " + toDate.toString());
                    } else {
                        params.put("subTitle", "Profit report for " + productId + " between " +
                                fromDate.toString() + " and " + toDate.toString());
                    }

                    Expression<Date> dateExpression = criteriaBuilder.function("DATE", Date.class,
                            root.get("balanceHistory").get("stockBalancedDate"));

                    Predicate fromDatePredicate = criteriaBuilder
                            .greaterThanOrEqualTo(dateExpression, Date.from(fromDate.atStartOfDay(defaultZoneId).toInstant()));
                    Predicate toDatePredicate = criteriaBuilder
                            .lessThanOrEqualTo(dateExpression, Date.from(toDate.atStartOfDay(defaultZoneId).toInstant()));
                    datePredicate = criteriaBuilder.and(fromDatePredicate, toDatePredicate);
                }

                // Check vehicle
                if (productId.equals("All products")) {
                    criteriaQuery.select(root)
                            .where(datePredicate);
                } else {
                    Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId"), productId);
                    criteriaQuery.select(root)
                            .where(criteriaBuilder.and(productIdPredicate, datePredicate));
                }


                List<org.mdjee.storemonitor.reports.Profit> profitList = new ArrayList<>();

                Query<org.mdjee.storemonitor.hibernate.entity.Profit> query = session.createQuery(criteriaQuery);
                List<org.mdjee.storemonitor.hibernate.entity.Profit> list = query.list();

                // Create reports.Product beans
                list.forEach(profit -> profitList.add(
                        new org.mdjee.storemonitor.reports.Profit(profit.getProductId(), profit.getBatchCode(),
                                profit.getBuyingPrice(), profit.getSoldPrice(), profit.getProfitAmount(),
                                profit.getQuantity(), profit.getBalanceHistory().getVehicleId())
                ));


                JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(profitList);

                params.put("beanCollectionParam", jrBeanCollectionDataSource);

                // Using compiled .jasper file
                InputStream inputStream = App.class.getResourceAsStream("reports/profit-by-product.jasper");
                JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
                JasperViewer.viewReport(jasperPrint, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showProfitByVehicle(ActionEvent event) {

        // Validations
        boolean vehicleIdValidate = vehicleIdComboBoxProfitPane.validate();
        boolean dateValidate = true;

        if (fromRadioButtonProfitPane.isSelected()) {
            boolean fromDateValidate = fromDatePickerProfitPane.validate();
            boolean toDateValidate = toDateDatePickerProfitPane.validate();

            if (fromDateValidate && toDateValidate) {
                dateValidate = true;
            } else {
                dateValidate = false;
            }
        }

        if (vehicleIdValidate && dateValidate) {
            // Selected vehicle
            String vehicleId = vehicleIdComboBoxProfitPane.getSelectionModel().getSelectedItem();

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Profit> criteriaQuery = criteriaBuilder.createQuery(Profit.class);
                Root<Profit> root = criteriaQuery.from(Profit.class);

                Predicate datePredicate = null;
                Map<String, Object> params = new HashMap();


                // If todayRadioButtonProfitPane checked
                if (todayRadioButtonProfitPane.isSelected()) {

                    if (vehicleId.equals("All vehicles")) {
                        params.put("subTitle", "Profit report for all vehicles on " + LocalDate.now());
                    } else {
                        params.put("subTitle", "Profit report for " + vehicleId + " on " + LocalDate.now());
                    }

                    datePredicate = criteriaBuilder.equal(criteriaBuilder.function("DATE", Date.class,
                            root.get("balanceHistory").get("stockBalancedDate")),
                            new Date());
                }

                // currentMonthRadioButtonProfitPane
                if (currentMonthRadioButtonProfitPane.isSelected()) {

                    if (vehicleId.equals("All vehicles")) {
                        params.put("subTitle", "Profit report for all vehicles of month " + LocalDate.now().getMonth()
                                + " " + LocalDate.now().getYear());
                    } else {
                        params.put("subTitle", "Profit report for " + vehicleId + " of month " + LocalDate.now().getMonth()
                                + " " + LocalDate.now().getYear());
                    }

                    datePredicate = criteriaBuilder.equal(
                            criteriaBuilder.function("MONTH", Integer.class,
                                    root.get("balanceHistory").get("stockBalancedDate")),
                            LocalDate.now().getMonth().getValue());
                }

                // currentYearRadioButtonProfitPane
                if (currentYearRadioButtonProfitPane.isSelected()) {

                    if (vehicleId.equals("All vehicles")) {
                        params.put("subTitle", "Profit report for all vehicles of year " + LocalDate.now().getYear());
                    } else {
                        params.put("subTitle", "Profit report for " + vehicleId + " of year " + LocalDate.now().getYear());
                    }

                    datePredicate = criteriaBuilder.equal(
                            criteriaBuilder.function("YEAR", Integer.class,
                                    root.get("balanceHistory").<LocalDateTime>get("stockBalancedDate")),
                            LocalDate.now().getYear());
                }

                // fromRadioButtonProfitPane
                if (fromRadioButtonProfitPane.isSelected()) {
                    LocalDate fromDate = fromDatePickerProfitPane.getValue();
                    LocalDate toDate = toDateDatePickerProfitPane.getValue();
                    // Default time zone
                    ZoneId defaultZoneId = ZoneId.systemDefault();

                    if (vehicleId.equals("All vehicles")) {
                        params.put("subTitle", "Profit report for all vehicles between " +
                                fromDate.toString() + " and " + toDate.toString());
                    } else {
                        params.put("subTitle", "Profit report for " + vehicleId + " between " +
                                fromDate.toString() + " and " + toDate.toString());
                    }

                    Expression<Date> dateExpression = criteriaBuilder.function("DATE", Date.class,
                            root.get("balanceHistory").get("stockBalancedDate"));

                    Predicate fromDatePredicate = criteriaBuilder
                            .greaterThanOrEqualTo(dateExpression, Date.from(fromDate.atStartOfDay(defaultZoneId).toInstant()));
                    Predicate toDatePredicate = criteriaBuilder
                            .lessThanOrEqualTo(dateExpression, Date.from(toDate.atStartOfDay(defaultZoneId).toInstant()));
                    datePredicate = criteriaBuilder.and(fromDatePredicate, toDatePredicate);
                }

                // Check vehicle
                if (vehicleId.equals("All vehicles")) {
                    criteriaQuery.select(root)
                            .where(datePredicate);
                } else {
                    Predicate vehicleIdPredicate = criteriaBuilder.equal(root.get("balanceHistory").get("vehicleId"), vehicleId);
                    criteriaQuery.select(root)
                            .where(criteriaBuilder.and(vehicleIdPredicate, datePredicate));
                }


                List<org.mdjee.storemonitor.reports.Profit> profitList = new ArrayList<>();

                Query<org.mdjee.storemonitor.hibernate.entity.Profit> query = session.createQuery(criteriaQuery);
                List<org.mdjee.storemonitor.hibernate.entity.Profit> list = query.list();

                // Create reports.Product beans
                list.forEach(profit -> profitList.add(
                        new org.mdjee.storemonitor.reports.Profit(profit.getProductId(), profit.getBatchCode(),
                                profit.getBuyingPrice(), profit.getSoldPrice(), profit.getProfitAmount(),
                                profit.getQuantity(), profit.getBalanceHistory().getVehicleId())
                ));


                JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(profitList);

                params.put("beanCollectionParam", jrBeanCollectionDataSource);

                // Using compiled .jasper file
                InputStream inputStream = App.class.getResourceAsStream("reports/profit-by-vehicle.jasper");
                JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
                JasperViewer.viewReport(jasperPrint, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showSellingPriceNotSaved(ActionEvent event) throws JRException {

        List<Product> products = getProducts().stream()
                .filter(product -> product.getSellingPrice() == 0)
                .collect(Collectors.toList());

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(products);

        Map<String, Object> params = new HashMap();
        params.put("beanCollectionParam", jrBeanCollectionDataSource);
        params.put("subTitle", "Products do not have selling price");


        // Using compiled .jasper file
        InputStream inputStream = App.class.getResourceAsStream("reports/product-details-landscape.jasper");
        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
        JasperViewer.viewReport(jasperPrint, false);
    }

    @FXML
    void showVehicleBalanceReport(ActionEvent event) {
        boolean vehicleIdValidate = vehicleIdComboBoxVehicleBalanceDetails.validate();
        boolean dateValidate = balanceDateDatePickerVehicleBalanceDetails.validate();

        // Validate
        if (vehicleIdValidate && dateValidate) {
            // Selected vehicle
            String vehicleId = vehicleIdComboBoxVehicleBalanceDetails.getSelectionModel().getSelectedItem();
            //default time zone
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Date date = Date.from(balanceDateDatePickerVehicleBalanceDetails.getValue().atStartOfDay(defaultZoneId).toInstant());

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                Map<String, Object> params = new HashMap();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss a");

                if (vehicleId.equals("All vehicles")) {
                    ObservableList<String> vehicleIdObservableList = vehicleIdComboBoxVehicleBalanceDetails.getItems();
                    // Remove first string - All vehicles
                    vehicleIdObservableList.forEach(currentVehicleId -> {
                        if(!currentVehicleId.equals("All vehicles")){
                            makeBalanceHistoryReport(params, formatter, date, criteriaBuilder, currentVehicleId, session);
                        }
                    });
                } else {
                    makeBalanceHistoryReport(params, formatter, date, criteriaBuilder, vehicleId, session);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param params          parameters map to pass report.
     * @param formatter       date formatter with full date and time.
     * @param date            selected date.
     * @param criteriaBuilder builder for query.
     * @param vehicleId       selected vehicle number.
     * @param session         session object.
     */
    private void makeBalanceHistoryReport(Map<String, Object> params, DateTimeFormatter formatter, Date date,
                                          CriteriaBuilder criteriaBuilder, String vehicleId, Session session) {

        params.put("subTitle", "Stock balance report");

        CriteriaQuery<BalanceHistory> criteriaQuery = criteriaBuilder.createQuery(BalanceHistory.class);
        Root<BalanceHistory> root = criteriaQuery.from(BalanceHistory.class);

        Predicate datePredicate = criteriaBuilder.equal(criteriaBuilder.function("DATE", Date.class,
                root.get("stockBalancedDate")), date);

        Predicate vehicleIdPredicate = criteriaBuilder.equal(root.get("vehicleId"), vehicleId);
        criteriaQuery.select(root)
                .where(criteriaBuilder.and(vehicleIdPredicate, datePredicate));


        Query<BalanceHistory> query = session.createQuery(criteriaQuery);
        List<BalanceHistory> balanceHistoryList = query.list();

        // Send notification
        if (balanceHistoryList.size() == 0) {
            Notification.showInfoNotification("No data found for " + vehicleId + " on selected date.",
                    "Reports manager");
        }

        // There can be multiple reports per day!
        balanceHistoryList.forEach(balanceHistory -> {

            params.put("balancedDate", formatter.format(balanceHistory.getStockBalancedDate()));
            params.put("loadedDate", formatter.format(balanceHistory.getStockLoadedDate()));
            params.put("vehicleId", balanceHistory.getVehicleId());

            List<org.mdjee.storemonitor.reports.Profit> profitList = new ArrayList<>();
            List<org.mdjee.storemonitor.reports.Product> productNotSoledList = new ArrayList<>();
            List<org.mdjee.storemonitor.reports.Product> productReturnedList = new ArrayList<>();

            // Create reports.Profit beans for Profit
            balanceHistory.getProfits().forEach(profit -> profitList.add(
                    new org.mdjee.storemonitor.reports.Profit(profit.getProductId(), profit.getBatchCode(),
                            profit.getBuyingPrice(), profit.getSoldPrice(), profit.getProfitAmount(),
                            profit.getQuantity(), profit.getBalanceHistory().getVehicleId())
            ));
            JRBeanCollectionDataSource profitJrBeanCollectionDataSource = new JRBeanCollectionDataSource(profitList);

            // Create reports.Product beans for Not Soled Products
            balanceHistory.getBalanceHistoryNotSoldProducts().forEach(product -> productNotSoledList.add(
                    new org.mdjee.storemonitor.reports.Product(product.getProductId(), product.getBatchCode(),
                            "", 0.0, 0.0, 0.0,
                            product.getAvailableQuantity(), null, null)
            ));
            JRBeanCollectionDataSource productNotSoledJrBeanCollectionDataSource =
                    new JRBeanCollectionDataSource(productNotSoledList);

            // Create reports.Product beans for Return Products
            balanceHistory.getBalanceHistoryReturns().forEach(product -> productReturnedList.add(
                    new org.mdjee.storemonitor.reports.Product(product.getProductId(), product.getBatchCode(),
                            "", 0.0, 0.0, 0.0,
                            product.getAvailableQuantity(), null, null)
            ));
            JRBeanCollectionDataSource returnedProductJrBeanCollectionDataSource =
                    new JRBeanCollectionDataSource(productReturnedList);

            params.put("profitBeanCollection", profitJrBeanCollectionDataSource);
            params.put("notSoledProductBeansCollection", productNotSoledJrBeanCollectionDataSource);
            params.put("returnedProductBeanCollection", returnedProductJrBeanCollectionDataSource);

            // Using compiled .jasper file
            InputStream inputStream = App.class.getResourceAsStream("reports/balance-history.jasper");
            JasperPrint jasperPrint = null;
            try {
                jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
            } catch (JRException e) {
                e.printStackTrace();
            }
            JasperViewer.viewReport(jasperPrint, false);
        });
    }

    @FXML
    void showVehicleProducts(ActionEvent event) throws JRException {

        boolean validate = vehicleIdComboBoxProductDetailsPane.validate();

        if (validate) {
            List<VehicleStoreProduct> products = null;
            if (vehicleIdComboBoxProductDetailsPane.getSelectionModel().getSelectedItem().equals("All vehicles")) {
                // Show all vehicles
                products = getVehicleStoreProducts();
            } else {
                // Only for selected vehicle
                String vehicleId = vehicleIdComboBoxProductDetailsPane.getSelectionModel().getSelectedItem();
                products = getVehicleStoreProducts(vehicleId);
            }
            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(products);

            Map<String, Object> params = new HashMap();
            params.put("beanCollectionParam", jrBeanCollectionDataSource);

            // Using compiled .jasper file
            InputStream inputStream = App.class.getResourceAsStream("reports/vehicle-product-details.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JREmptyDataSource());
            JasperViewer.viewReport(jasperPrint, false);

        }

    }

    /**
     * Get VehicleStoreProduct from database and return beans of reports.VehicleStoreProduct.
     *
     * @return list of reports.VehicleStoreProduct
     */
    private List<VehicleStoreProduct> getVehicleStoreProducts() {

        Transaction transaction = null;
        List<VehicleStoreProduct> productList = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> criteriaQuery = criteriaBuilder
                    .createQuery(org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct.class);
            Root<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> root = criteriaQuery
                    .from(org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct.class);
            criteriaQuery.select(root);

            Query<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> query = session.createQuery(criteriaQuery);
            List<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> list = query.list();

            // Create reports.Product beans
            list.forEach(product -> productList.add(
                    new VehicleStoreProduct(product.getVehicleStore().getVehicleId(), product.getProductId(),
                            product.getBatchCode(), product.getAvailableQuantity())
            ));


        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return productList;
    }

    /**
     * Get VehicleStoreProduct from database and return beans of reports.VehicleStoreProduct.
     *
     * @return list of reports.VehicleStoreProduct
     */
    private List<VehicleStoreProduct> getVehicleStoreProducts(String vehicleId) {

        Transaction transaction = null;
        List<VehicleStoreProduct> productList = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> criteriaQuery = criteriaBuilder
                    .createQuery(org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct.class);
            Root<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> root = criteriaQuery
                    .from(org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct.class);
            criteriaQuery.select(root)
                    .where(criteriaBuilder.equal(root.get("vehicleStore").get("vehicleId"), vehicleId));

            Query<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> query = session.createQuery(criteriaQuery);
            List<org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct> list = query.list();

            // Create reports.Product beans
            list.forEach(product -> productList.add(
                    new VehicleStoreProduct(product.getVehicleStore().getVehicleId(), product.getProductId(),
                            product.getBatchCode(), product.getAvailableQuantity())
            ));


        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return productList;
    }

    /**
     * Populate productIdObservableList with productId from Product table.
     */
    public void populateProductIdObservableList() {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<org.mdjee.storemonitor.hibernate.entity.Store> criteriaQuery = criteriaBuilder
                    .createQuery(org.mdjee.storemonitor.hibernate.entity.Store.class);
            Root<org.mdjee.storemonitor.hibernate.entity.Store> root = criteriaQuery
                    .from(org.mdjee.storemonitor.hibernate.entity.Store.class);
            criteriaQuery.select(root);

            Query<org.mdjee.storemonitor.hibernate.entity.Store> query = session.createQuery(criteriaQuery);
            List<org.mdjee.storemonitor.hibernate.entity.Store> list = query.list();

            productIdObservableList.add("All products");
            list.forEach(store -> productIdObservableList.add(store.getProductId()));

            // If there is no any products remove 'All products' item
            if (list.size() == 0) {
                productIdObservableList.remove(0);
            }

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
     * Populate vehicleIdObservableListFromVehicleStore with vehicleId from VehicleStoreProduct table.
     */
    public void populateVehicleIdObservableListFromVehicleStore() {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<VehicleStore> criteriaQuery = criteriaBuilder
                    .createQuery(VehicleStore.class);
            Root<VehicleStore> root = criteriaQuery
                    .from(VehicleStore.class);
            criteriaQuery.select(root);


            Query<VehicleStore> query = session.createQuery(criteriaQuery);
            List<VehicleStore> list = query.list();

            vehicleIdObservableListFromVehicleStore.add("All vehicles");
            list.forEach(vehicleStore -> vehicleIdObservableListFromVehicleStore.add(vehicleStore.getVehicleId()));

            // If there is no any results remove 'All vehicles'
            if (list.size() == 0) {
                vehicleIdObservableListFromVehicleStore.remove(0);
            }

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
     * Populate vehicleIdObservableListFromBalanceHistory with vehicleId from BalanceHistory table.
     */
    private void populateVehicleIdObservableListFromBalanceHistory() {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Using CriteriaBuilder API
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
            Root<org.mdjee.storemonitor.hibernate.entity.BalanceHistory> root = criteriaQuery
                    .from(org.mdjee.storemonitor.hibernate.entity.BalanceHistory.class);
            criteriaQuery.select(root.get("vehicleId")).distinct(true);

            Query<String> query = session.createQuery(criteriaQuery);
            List<String> list = query.list();

            vehicleIdObservableListFromBalanceHistory.add("All vehicles");
            list.forEach(balanceHistory -> vehicleIdObservableListFromBalanceHistory.add(balanceHistory));

            // If there is no any result remove'All vehicles' item
            if (list.size() == 0) {
                vehicleIdObservableListFromBalanceHistory.remove(0);
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction is null, rolling back.");
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }


    public Task pieChartWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {

                // Monthly profit
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

                    CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
                    Root<Profit> root = criteriaQuery.from(Profit.class);

                    Predicate datePredicate = criteriaBuilder.equal(
                            criteriaBuilder.function("MONTH", Integer.class,
                                    root.get("balanceHistory").get("stockBalancedDate")),
                            LocalDate.now().getMonth().getValue());

                    criteriaQuery.select(criteriaBuilder.tuple(criteriaBuilder.sum(root.<Double>get("profitAmount")),
                            root.get("balanceHistory").get("vehicleId")))
                            .where(datePredicate)
                            .groupBy(root.get("balanceHistory").get("vehicleId"));

                    List<Tuple> resultList = session.createQuery(criteriaQuery).getResultList();

                    // Profit by vehicle
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                    resultList.forEach(tuple -> pieChartData.add(new PieChart.Data((String) tuple.get(1), (Double) tuple.get(0))));

                    Platform.runLater(() -> {
                        pieChart.setData(pieChartData);

                        pieChart.getData().forEach(data -> {
                            String amount = "LKR " + data.getPieValue();
                            Tooltip toolTip = new Tooltip(amount);
                            Tooltip.install(data.getNode(), toolTip);
                        });
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info(e.getMessage());
                }

                return true;
            }
        };
    }

    public Task lineChartWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {

                // Yearly profit
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

                    CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
                    Root<Profit> root = criteriaQuery.from(Profit.class);

                    Predicate datePredicate = criteriaBuilder.equal(
                            criteriaBuilder.function("YEAR", Integer.class,
                                    root.get("balanceHistory").get("stockBalancedDate")),
                            LocalDate.now().getYear());

                    criteriaQuery.select(criteriaBuilder.tuple(criteriaBuilder.sum(root.<Double>get("profitAmount")),
                            criteriaBuilder.function("MONTH", Integer.class,
                                    root.get("balanceHistory").get("stockBalancedDate"))))
                            .where(datePredicate)
                            .groupBy(criteriaBuilder.function("MONTH", Integer.class,
                                    root.get("balanceHistory").get("stockBalancedDate")));

                    List<Tuple> resultList = session.createQuery(criteriaQuery).getResultList();

                    ObservableList<XYChart.Series<String, Double>> seriesObservableList = FXCollections.observableArrayList();

                    XYChart.Series series = new XYChart.Series();
                    series.setName("Total profit of year " + LocalDate.now().getYear());

                    resultList.forEach(tuple -> {
                        // Profit amount
                        Double amount = (Double) tuple.get(0);
                        // Month
                        Month month = Month.of((int) tuple.get(1));

                        series.getData().add(new XYChart.Data(month.toString(), amount));
                    });

                    seriesObservableList.add(series);

                    Platform.runLater(() -> {
                        // TODO: To resolve 'FX Chart xAxis only shows last label' bug
                        // https://stackoverflow.com/questions/31774771/javafx-chart-axis-only-shows-last-label
                        lineChart.getXAxis().setAnimated(false);

                        // without using observableList
                        // lineChart.getData().addAll(series);

                        // Using observableList
                        lineChart.setData(seriesObservableList);

                        ObservableList<XYChart.Data<String, Double>> dataset = series.getData();
                        dataset.forEach(stringDoubleData -> {
                            Tooltip tooltip = new Tooltip(stringDoubleData.getYValue().toString());
                            Tooltip.install(stringDoubleData.getNode(), tooltip);
                        });

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info(e.getMessage());
                }

                return true;
            }
        };
    }

}
