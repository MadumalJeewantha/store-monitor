package org.mdjee.storemonitor.store;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservableSendToVehicleProduct {
    StringProperty vehicleId;
    StringProperty productId;
    StringProperty batchCode;
    DoubleProperty quantity;

    public ObservableSendToVehicleProduct(String vehicleId, String productId, String batchCode, Double quantity) {
        this.vehicleId = new SimpleStringProperty(vehicleId);
        this.productId = new SimpleStringProperty(productId);
        this.batchCode = new SimpleStringProperty(batchCode);
        this.quantity = new SimpleDoubleProperty(quantity);
    }

    public String getVehicleId() {
        return vehicleId.get();
    }

    public StringProperty vehicleIdProperty() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId.set(vehicleId);
    }

    public String getProductId() {
        return productId.get();
    }

    public StringProperty productIdProperty() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId.set(productId);
    }

    public String getBatchCode() {
        return batchCode.get();
    }

    public StringProperty batchCodeProperty() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode.set(batchCode);
    }

    public double getQuantity() {
        return quantity.get();
    }

    public DoubleProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }
}
