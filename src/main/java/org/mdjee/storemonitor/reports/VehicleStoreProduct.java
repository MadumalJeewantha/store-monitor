package org.mdjee.storemonitor.reports;

public class VehicleStoreProduct {

    private String vehicleId;
    private String productId;
    private String batchCode;
    private double availableQuantity;

    public VehicleStoreProduct(String vehicleId, String productId, String batchCode, double availableQuantity) {
        this.vehicleId = vehicleId;
        this.productId = productId;
        this.batchCode = batchCode;
        this.availableQuantity = availableQuantity;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
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

    public double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
