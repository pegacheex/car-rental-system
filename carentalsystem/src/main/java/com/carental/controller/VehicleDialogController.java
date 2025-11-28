package com.carental.controller;

import com.carental.model.Vehicle;
import com.carental.service.VehicleService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class VehicleDialogController {
    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField licensePlateField;
    @FXML private TextField dailyRateField;
    @FXML private ComboBox<Vehicle.VehicleStatus> statusComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private Vehicle vehicle;
    private final VehicleService vehicleService = new VehicleService();
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        
        if (vehicle != null) {
            // Edit mode
            makeField.setText(vehicle.getMake());
            modelField.setText(vehicle.getModel());
            yearField.setText(String.valueOf(vehicle.getYear()));
            licensePlateField.setText(vehicle.getLicensePlate());
            dailyRateField.setText(vehicle.getDailyRate().toString());
            statusComboBox.setValue(vehicle.getStatus());
            descriptionArea.setText(vehicle.getDescription());
        } else {
            // Add mode
            statusComboBox.setValue(Vehicle.VehicleStatus.AVAILABLE);
        }
        
        // Initialize status combo box
        statusComboBox.getItems().setAll(Vehicle.VehicleStatus.values());
    }
    
    @FXML
    private void handleSave() {
        try {
            if (!validateInputs()) {
                return;
            }
            
            if (vehicle == null) {
                // Create new vehicle
                vehicle = new Vehicle();
            }
            
            // Update vehicle details
            vehicle.setMake(makeField.getText());
            vehicle.setModel(modelField.getText());
            vehicle.setYear(Integer.parseInt(yearField.getText()));
            vehicle.setLicensePlate(licensePlateField.getText());
            vehicle.setDailyRate(new BigDecimal(dailyRateField.getText()));
            vehicle.setStatus(statusComboBox.getValue());
            vehicle.setDescription(descriptionArea.getText());
            
            boolean success;
            if (vehicle.getVehicleId() == 0) {
                success = vehicleService.addVehicle(vehicle);
            } else {
                success = vehicleService.updateVehicle(vehicle);
            }
            
            if (success) {
                closeDialog();
            } else {
                showError("Failed to save vehicle");
            }
        } catch (Exception e) {
            showError("Error saving vehicle", e);
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateInputs() {
        if (makeField.getText().isEmpty()) {
            showError("Please enter the make");
            return false;
        }
        
        if (modelField.getText().isEmpty()) {
            showError("Please enter the model");
            return false;
        }
        
        try {
            Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            showError("Please enter a valid year");
            return false;
        }
        
        if (licensePlateField.getText().isEmpty()) {
            showError("Please enter the license plate");
            return false;
        }
        
        try {
            new BigDecimal(dailyRateField.getText());
        } catch (NumberFormatException e) {
            showError("Please enter a valid daily rate");
            return false;
        }
        
        if (statusComboBox.getValue() == null) {
            showError("Please select a status");
            return false;
        }
        
        return true;
    }
    
    private void closeDialog() {
        ((Stage) saveButton.getScene().getWindow()).close();
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