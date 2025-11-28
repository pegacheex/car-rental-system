package com.carental.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.*;

public class Booking {
    private final IntegerProperty bookingId = new SimpleIntegerProperty();
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalAmount = new SimpleObjectProperty<>();
    private final ObjectProperty<BookingStatus> status = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final StringProperty carDetails = new SimpleStringProperty();

    public enum BookingStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    public Booking() {}

    public Booking(int userId, int vehicleId, LocalDate startDate, LocalDate endDate, 
                  BigDecimal totalAmount, BookingStatus status) {
        setUserId(userId);
        setVehicleId(vehicleId);
        setStartDate(startDate);
        setEndDate(endDate);
        setTotalAmount(totalAmount);
        setStatus(status);
    }

    // Property getters
    public IntegerProperty bookingIdProperty() {
        return bookingId;
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public IntegerProperty vehicleIdProperty() {
        return vehicleId;
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public ObjectProperty<BigDecimal> totalAmountProperty() {
        return totalAmount;
    }

    public ObjectProperty<BookingStatus> statusProperty() {
        return status;
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public StringProperty carDetailsProperty() {
        return carDetails;
    }

    // Regular getters and setters
    public int getBookingId() {
        return bookingId.get();
    }

    public void setBookingId(int bookingId) {
        this.bookingId.set(bookingId);
    }

    public int getUserId() {
        return userId.get();
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public int getVehicleId() {
        return vehicleId.get();
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId.set(vehicleId);
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount.set(totalAmount);
    }

    public BookingStatus getStatus() {
        return status.get();
    }

    public void setStatus(BookingStatus status) {
        this.status.set(status);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public String getCarDetails() {
        return carDetails.get();
    }

    public void setCarDetails(String carDetails) {
        this.carDetails.set(carDetails);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + getBookingId() +
                ", userId=" + getUserId() +
                ", vehicleId=" + getVehicleId() +
                ", startDate=" + getStartDate() +
                ", endDate=" + getEndDate() +
                ", totalAmount=" + getTotalAmount() +
                ", status=" + getStatus() +
                '}';
    }
} 