package com.carental.controller;

import com.carental.model.User;
import com.carental.model.Vehicle;
import com.carental.model.Booking;
import com.carental.service.BookingService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingDialogController {
    @FXML private Label vehicleLabel;
    @FXML private Label rateLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalAmountLabel;
    @FXML private Button bookButton;
    @FXML private Button cancelButton;
    
    private Vehicle vehicle;
    private User user;
    private final BookingService bookingService = new BookingService();
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        vehicleLabel.setText(vehicle.getMake() + " " + vehicle.getModel());
        rateLabel.setText("₹" + vehicle.getDailyRate() + " per day");
        
        // Set minimum dates
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));
        
        // Add listeners for date changes
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                endDatePicker.setValue(newVal.plusDays(1));
                calculateTotalAmount();
            }
        });
        
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                calculateTotalAmount();
            }
        });
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    private void calculateTotalAmount() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            if (startDate != null && endDate != null && !endDate.isBefore(startDate)) {
                BigDecimal totalAmount = bookingService.calculateTotalAmount(
                    vehicle.getVehicleId(), startDate, endDate);
                totalAmountLabel.setText("Total Amount: ₹" + totalAmount);
            }
        } catch (Exception e) {
            showError("Error calculating total amount", e);
        }
    }
    
    @FXML
    private void handleBook() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            if (startDate == null || endDate == null) {
                showError("Please select both start and end dates");
                return;
            }
            
            if (endDate.isBefore(startDate)) {
                showError("End date cannot be before start date");
                return;
            }
            
            // Create booking
            Booking booking = new Booking();
            booking.setUserId(user.getUserId());
            booking.setVehicleId(vehicle.getVehicleId());
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setTotalAmount(bookingService.calculateTotalAmount(
                vehicle.getVehicleId(), startDate, endDate));
            booking.setStatus(Booking.BookingStatus.PENDING);
            
            if (bookingService.createBooking(booking)) {
                closeDialog();
            } else {
                showError("Failed to create booking");
            }
        } catch (Exception e) {
            showError("Error creating booking", e);
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        ((Stage) bookButton.getScene().getWindow()).close();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
} 