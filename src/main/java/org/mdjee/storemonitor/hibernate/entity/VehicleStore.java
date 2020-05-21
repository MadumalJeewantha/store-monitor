package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle_store")
public class VehicleStore {
    @Id
    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "loaded_date", nullable = false)
    private LocalDateTime loadedDate;

    @Column(name = "remark")
    private String remark;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "vehicleStore",  cascade = CascadeType.ALL)
    private List<VehicleStoreProduct> vehicleStoreProducts = new ArrayList<>();

    public VehicleStore() {
    }

    public VehicleStore(String vehicleId, LocalDateTime loadedDate, String remark,
                        List<VehicleStoreProduct> vehicleStoreProducts) {
        this.vehicleId = vehicleId;
        this.loadedDate = loadedDate;
        this.remark = remark;
        this.vehicleStoreProducts = vehicleStoreProducts;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getLoadedDate() {
        return loadedDate;
    }

    public void setLoadedDate(LocalDateTime loadedDate) {
        this.loadedDate = loadedDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<VehicleStoreProduct> getVehicleStoreProducts() {
        return vehicleStoreProducts;
    }

    public void setVehicleStoreProducts(List<VehicleStoreProduct> vehicleStoreProducts) {
        this.vehicleStoreProducts = vehicleStoreProducts;
    }
}
