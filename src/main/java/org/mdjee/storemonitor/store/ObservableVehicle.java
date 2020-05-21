package org.mdjee.storemonitor.store;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.mdjee.storemonitor.hibernate.entity.VehicleStoreProduct;

import java.util.List;

public class ObservableVehicle {

    StringProperty vehicleId;
    StringProperty loadedDate;
    StringProperty remark;
    List<VehicleStoreProduct> vehicleStoreProduct;

    public ObservableVehicle(String vehicleId, String loadedDate, String remark,
                             List<VehicleStoreProduct> vehicleStoreProduct) {
        this.vehicleId = new SimpleStringProperty(vehicleId);
        this.loadedDate = new SimpleStringProperty(loadedDate);
        this.remark = new SimpleStringProperty(remark);
        this.vehicleStoreProduct = vehicleStoreProduct;
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

    public String getLoadedDate() {
        return loadedDate.get();
    }

    public StringProperty loadedDateProperty() {
        return loadedDate;
    }

    public void setLoadedDate(String loadedDate) {
        this.loadedDate.set(loadedDate);
    }

    public String getRemark() {
        return remark.get();
    }

    public StringProperty remarkProperty() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark.set(remark);
    }

    public List<VehicleStoreProduct> getVehicleStoreProduct() {
        return vehicleStoreProduct;
    }

    public void setVehicleStoreProduct(VehicleStoreProduct vehicleStoreProduct) {
        this.vehicleStoreProduct.add(vehicleStoreProduct);
    }
}
