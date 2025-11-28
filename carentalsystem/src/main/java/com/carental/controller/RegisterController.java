package com.carental.controller;

import com.carental.model.User;
import com.carental.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField fullNameField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private Label messageLabel;
    
    private final UserService userService = new UserService();
    
    @FXML
    private void handleRegister() {
        // Validate input fields
        if (!validateInputs()) {
            return;
        }
        
        try {
            // Check if username is available
            if (!userService.isUsernameAvailable(usernameField.getText())) {
                messageLabel.setText("Username is already taken");
                return;
            }
            
            // Check if email is available
            if (!userService.isEmailAvailable(emailField.getText())) {
                messageLabel.setText("Email is already registered");
                return;
            }
            
            // Create new user
            User newUser = new User(
                usernameField.getText(),
                passwordField.getText(),
                emailField.getText(),
                fullNameField.getText(),
                phoneField.getText(),
                User.UserRole.CUSTOMER
            );
            
            // Register user
            if (userService.registerUser(newUser)) {
                messageLabel.setText("Registration successful! Please login.");
                // Wait for 2 seconds before returning to login
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::handleBackToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Registration failed. Please try again.");
            }
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login - Car Rental System");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
            
            // Close the registration window
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            messageLabel.setText("Error returning to login: " + e.getMessage());
        }
    }
    
    private boolean validateInputs() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() ||
            confirmPasswordField.getText().isEmpty() || emailField.getText().isEmpty() ||
            fullNameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return false;
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            messageLabel.setText("Passwords do not match");
            return false;
        }
        
        if (passwordField.getText().length() < 6) {
            messageLabel.setText("Password must be at least 6 characters long");
            return false;
        }
        
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            messageLabel.setText("Please enter a valid email address");
            return false;
        }
        
        if (!phoneField.getText().matches("\\d{10}")) {
            messageLabel.setText("Please enter a valid 10-digit phone number");
            return false;
        }
        
        return true;
    }
} 