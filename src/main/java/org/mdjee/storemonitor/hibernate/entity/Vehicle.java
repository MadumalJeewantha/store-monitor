package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public Vehicle(String vehicleId, String name, String description) {
        this.vehicleId = vehicleId;
        this.name = name;
        this.description = description;
    }

    public Vehicle(){

    }

    public String getVehicleID() {
        return vehicleId;
    }

    public void setVehicleID(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
