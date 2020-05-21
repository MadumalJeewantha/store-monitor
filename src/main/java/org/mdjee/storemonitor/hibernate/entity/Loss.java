package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "loss")
public class Loss {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // This can be lost related to the Vehicles or In Hand Store
    // So can be nullable
    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "product_id", nullable = false)
    private String productId ;

    @Column(name = "batch_code", nullable = false)
    private String batchCode;

    @Column(name = "description")
    private String description;

    @Column(name = "loss_amount", nullable = false)
    private double lossAmount;

    @Column(name = "loss_quantity")
    private double lossQuantity;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "remark")
    private String remark;

    public Loss(){

    }

    public Loss(String vehicleId, String productId, String batchCode, String description, double lossAmount,
                double lossQuantity, LocalDateTime date, String remark) {
        this.vehicleId = vehicleId;
        this.productId = productId;
        this.batchCode = batchCode;
        this.description = description;
        this.lossAmount = lossAmount;
        this.lossQuantity = lossQuantity;
        this.date = date;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(double lossAmount) {
        this.lossAmount = lossAmount;
    }

    public double getLossQuantity() {
        return lossQuantity;
    }

    public void setLossQuantity(double lossQuantity) {
        this.lossQuantity = lossQuantity;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
