package org.mdjee.storemonitor.store;

public class Price {
    String productId;
    String batchCode;
    double buyingPrice;
    double sellingPrice;
    double availableQuantity;

    public Price(String productId, String batchCode, double buyingPrice, double sellingPrice, double availableQuantity) {
        this.productId = productId;
        this.batchCode = batchCode;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.availableQuantity = availableQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(double availableQuantity) {
        this.availableQuantity = availableQuantity;
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

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
