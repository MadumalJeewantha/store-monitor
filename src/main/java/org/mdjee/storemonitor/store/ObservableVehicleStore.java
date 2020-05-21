package org.mdjee.storemonitor.store;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservableVehicleStore {
    StringProperty productId;
    StringProperty batchCode;
    DoubleProperty availableQuantity;

    public ObservableVehicleStore(String productId, String batchCode, Double availableQuantity) {
        this.productId = new SimpleStringProperty(productId);
        this.batchCode = new SimpleStringProperty(batchCode);
        this.availableQuantity = new SimpleDoubleProperty(availableQuantity);
    }

    public String getProductId() {
        return productId.get();
    }

    public StringProperty productIdProperty() {
        return productId;
    }

    public String getBatchCode() {
        return batchCode.get();
    }

    public StringProperty batchCodeProperty() {
        return batchCode;
    }

    public double getAvailableQuantity() {
        return availableQuantity.get();
    }

    public DoubleProperty availableQuantityProperty() {
        return availableQuantity;
    }

    public void setAvailableQuantity(double availableQuantity) {
        this.availableQuantity.set(availableQuantity);
    }
}
