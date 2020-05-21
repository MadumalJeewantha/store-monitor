package org.mdjee.storemonitor.store;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservableProduct {
    StringProperty productId;
    StringProperty batchCode;
    StringProperty manufacturedDate;
    StringProperty expiryDate;
    StringProperty availableQuantity;
    StringProperty reorderLevel;
    StringProperty buyingPrice;
    StringProperty sellingPrice;
    StringProperty remark;

    public ObservableProduct(String productId, String batchCode, String manufacturedDate, String expiryDate, String availableQuantity,
                             String reorderLevel, String buyingPrice, String sellingPrice, String remark) {
        this.productId = new SimpleStringProperty(productId);
        this.batchCode = new SimpleStringProperty(batchCode);
        this.manufacturedDate = new SimpleStringProperty(manufacturedDate);
        this.expiryDate = new SimpleStringProperty(expiryDate);
        this.availableQuantity = new SimpleStringProperty(availableQuantity);
        this.reorderLevel = new SimpleStringProperty(reorderLevel);
        this.buyingPrice = new SimpleStringProperty(buyingPrice);
        this.sellingPrice = new SimpleStringProperty(sellingPrice);
        this.remark = new SimpleStringProperty(remark);
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

    public String getManufacturedDate() {
        return manufacturedDate.get();
    }

    public StringProperty manufacturedDateProperty() {
        return manufacturedDate;
    }

    public String getExpiryDate() {
        return expiryDate.get();
    }

    public StringProperty expiryDateProperty() {
        return expiryDate;
    }

    public String getAvailableQuantity() {
        return availableQuantity.get();
    }

    public StringProperty availableQuantityProperty() {
        return availableQuantity;
    }

    public String getReorderLevel() {
        return reorderLevel.get();
    }

    public StringProperty reorderLevelProperty() {
        return reorderLevel;
    }

    public String getBuyingPrice() {
        return buyingPrice.get();
    }

    public StringProperty buyingPriceProperty() {
        return buyingPrice;
    }

    public String getSellingPrice() {
        return sellingPrice.get();
    }

    public StringProperty sellingPriceProperty() {
        return sellingPrice;
    }

    public String getRemark() {
        return remark.get();
    }

    public StringProperty remarkProperty() {
        return remark;
    }
}
