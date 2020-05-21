package org.mdjee.storemonitor.reports;

public class Profit {

    private String productId;
    private String batchCode;
    private double buyingPrice;
    private double sellingPrice;
    private double profitAmount;
    private double quantity;
    private String vehicleId;

    public Profit(String productId, String batchCode, double buyingPrice, double sellingPrice, double profitAmount,
                  double quantity, String vehicleId) {
        this.productId = productId;
        this.batchCode = batchCode;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.profitAmount = profitAmount;
        this.quantity = quantity;
        this.vehicleId = vehicleId;
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

    public double getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}
