package org.mdjee.storemonitor.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservableVehicle {

    private StringProperty vehicleId;
    private StringProperty name;
    private StringProperty description;

    public ObservableVehicle(String vehicleId, String name, String description) {
        this.vehicleId = new SimpleStringProperty(vehicleId);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
    }

    public String getVehicleId() {
        return vehicleId.get();
    }

    public StringProperty vehicleIdProperty() {
        return vehicleId;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
