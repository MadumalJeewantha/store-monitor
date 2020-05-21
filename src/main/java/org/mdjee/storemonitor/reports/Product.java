package org.mdjee.storemonitor.reports;

import java.time.LocalDate;

public class Product {
    String productId;
    String batchCode;
    String remark;
    Double sellingPrice;
    Double buyingPrice;
    Double reorderLevel;
    Double availableQuantity;
    LocalDate manufactureDate;
    LocalDate expiryDate;

    public Product(String productId, String batchCode, String remark, Double sellingPrice, Double buyingPrice,
                   Double reorderLevel, Double quantityAvailable, LocalDate manufactureDate, LocalDate expiryDate) {
        this.productId = productId;
        this.batchCode = batchCode;
        this.remark = remark;
        this.sellingPrice = sellingPrice;
        this.buyingPrice = buyingPrice;
        this.reorderLevel = reorderLevel;
        this.availableQuantity = quantityAvailable;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(Double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public Double getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Double reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}

