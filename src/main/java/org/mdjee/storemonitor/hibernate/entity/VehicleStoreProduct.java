package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "vehicle_store_product")
public class VehicleStoreProduct {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "batch_code", nullable = false)
    private String batchCode;

    @Column(name = "available_quantity", nullable = false)
    private double availableQuantity;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleStore vehicleStore;

    public VehicleStoreProduct() {
    }

    public VehicleStoreProduct(String productId, String batchCode, double availableQuantity, VehicleStore vehicleStore) {
        this.productId = productId;
        this.batchCode = batchCode;
        this.availableQuantity = availableQuantity;
        this.vehicleStore = vehicleStore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public VehicleStore getVehicleStore() {
        return vehicleStore;
    }

    public void setVehicleStore(VehicleStore vehicleStore) {
        this.vehicleStore = vehicleStore;
    }
}
