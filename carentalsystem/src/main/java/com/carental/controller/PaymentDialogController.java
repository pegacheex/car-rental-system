package com.carental.controller;

import com.carental.model.Payment;
import com.carental.model.Booking;
import com.carental.service.PaymentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentDialogController {
    @FXML private Label bookingIdLabel;
    @FXML private Label carLabel;
    @FXML private Label amountLabel;
    @FXML private ComboBox<Payment.PaymentMethod> paymentMethodComboBox;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;
    @FXML private TextField upiIdField;
    @FXML private VBox cardDetailsBox;
    @FXML private VBox upiDetailsBox;
    
    private Booking booking;
    private PaymentService paymentService;
    
    @FXML
    public void initialize() {
        paymentService = new PaymentService();
        paymentMethodComboBox.getItems().addAll(Payment.PaymentMethod.values());
        
        paymentMethodComboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    switch (newValue) {
                        case CREDIT_CARD:
                        case DEBIT_CARD:
                            cardDetailsBox.setVisible(true);
                            upiDetailsBox.setVisible(false);
                            break;
                        case UPI:
                            cardDetailsBox.setVisible(false);
                            upiDetailsBox.setVisible(true);
                            break;
                        case CASH:
                            cardDetailsBox.setVisible(false);
                            upiDetailsBox.setVisible(false);
                            break;
                    }
                }
            });
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
        bookingIdLabel.setText(String.valueOf(booking.getBookingId()));
        carLabel.setText(booking.getCarDetails());
        amountLabel.setText("$" + booking.getTotalAmount().toString());
    }
    
    @FXML
    private void handlePay() {
        if (!validateInputs()) {
            return;
        }
        
        try {
            Payment payment = new Payment();
            payment.setBookingId(booking.getBookingId());
            payment.setAmount(booking.getTotalAmount());
            payment.setPaymentMethod(paymentMethodComboBox.getValue());
            payment.setStatus(Payment.PaymentStatus.PENDING);
            
            // Set transaction details based on payment method
            switch (payment.getPaymentMethod()) {
                case CREDIT_CARD:
                case DEBIT_CARD:
                    payment.setTransactionId("CARD-" + cardNumberField.getText().substring(cardNumberField.getText().length() - 4));
                    break;
                case UPI:
                    payment.setTransactionId("UPI-" + upiIdField.getText());
                    break;
                case CASH:
                    payment.setTransactionId("CASH-" + UUID.randomUUID().toString().substring(0, 8));
                    break;
            }
            
            if (paymentService.processPayment(payment)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment processed successfully!");
                closeDialog();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to process payment. Please try again.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateInputs() {
        Payment.PaymentMethod method = paymentMethodComboBox.getValue();
        if (method == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a payment method.");
            return false;
        }
        
        switch (method) {
            case CREDIT_CARD:
            case DEBIT_CARD:
                if (cardNumberField.getText().isEmpty() || !cardNumberField.getText().matches("\\d{16}")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid 16-digit card number.");
                    return false;
                }
                if (expiryDateField.getText().isEmpty() || !expiryDateField.getText().matches("\\d{2}/\\d{2}")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid expiry date (MM/YY).");
                    return false;
                }
                if (cvvField.getText().isEmpty() || !cvvField.getText().matches("\\d{3,4}")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid CVV.");
                    return false;
                }
                break;
            case UPI:
                if (upiIdField.getText().isEmpty() || !upiIdField.getText().contains("@")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid UPI ID.");
                    return false;
                }
                break;
        }
        return true;
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) bookingIdLabel.getScene().getWindow();
        stage.close();
    }
} 