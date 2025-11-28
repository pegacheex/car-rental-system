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

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label messageLabel;
    
    private final UserService userService = new UserService();
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            return;
        }
        
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                openDashboard(user);
            } else {
                messageLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            messageLabel.setText("Error during login: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Register - Car Rental System");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
            
            // Close the login window
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            messageLabel.setText("Error opening registration form: " + e.getMessage());
        }
    }
    
    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the user
            DashboardController controller = loader.getController();
            controller.setUser(user);
            
            Stage stage = new Stage();
            stage.setTitle("Dashboard - Car Rental System");
            stage.setScene(new Scene(root, 1024, 768));
            stage.show();
            
            // Close the login window
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            messageLabel.setText("Error opening dashboard: " + e.getMessage());
        }
    }
} 