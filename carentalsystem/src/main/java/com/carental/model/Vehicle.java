package com.carental.model;

import java.math.BigDecimal;
import javafx.beans.property.*;

public class Vehicle {
    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty make = new SimpleStringProperty();
    private final StringProperty model = new SimpleStringProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final StringProperty licensePlate = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> dailyRate = new SimpleObjectProperty<>();
    private final ObjectProperty<VehicleStatus> status = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty imageUrl = new SimpleStringProperty();

    public enum VehicleStatus {
        AVAILABLE, RENTED, MAINTENANCE
    }

    public Vehicle() {}

    public Vehicle(String make, String model, int year, String licensePlate, 
                  BigDecimal dailyRate, VehicleStatus status, String description) {
        setMake(make);
        setModel(model);
        setYear(year);
        setLicensePlate(licensePlate);
        setDailyRate(dailyRate);
        setStatus(status);
        setDescription(description);
    }

    // Property getters
    public IntegerProperty vehicleIdProperty() {
        return vehicleId;
    }

    public StringProperty makeProperty() {
        return make;
    }

    public StringProperty modelProperty() {
        return model;
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public StringProperty licensePlateProperty() {
        return licensePlate;
    }

    public ObjectProperty<BigDecimal> dailyRateProperty() {
        return dailyRate;
    }

    public ObjectProperty<VehicleStatus> statusProperty() {
        return status;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    // Regular getters and setters
    public int getVehicleId() {
        return vehicleId.get();
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId.set(vehicleId);
    }

    public String getMake() {
        return make.get();
    }

    public void setMake(String make) {
        this.make.set(make);
    }

    public String getModel() {
        return model.get();
    }

    public void setModel(String model) {
        this.model.set(model);
    }

    public int getYear() {
        return year.get();
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public String getLicensePlate() {
        return licensePlate.get();
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate.set(licensePlate);
    }

    public BigDecimal getDailyRate() {
        return dailyRate.get();
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate.set(dailyRate);
    }

    public VehicleStatus getStatus() {
        return status.get();
    }

    public void setStatus(VehicleStatus status) {
        this.status.set(status);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + getVehicleId() +
                ", make='" + getMake() + '\'' +
                ", model='" + getModel() + '\'' +
                ", year=" + getYear() +
                ", licensePlate='" + getLicensePlate() + '\'' +
                ", dailyRate=" + getDailyRate() +
                ", status=" + getStatus() +
                '}';
    }
} 