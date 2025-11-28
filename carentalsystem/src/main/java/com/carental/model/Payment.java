package com.carental.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javafx.beans.property.*;

public class Payment {
    private final IntegerProperty paymentId = new SimpleIntegerProperty();
    private final IntegerProperty bookingId = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> paymentDate = new SimpleObjectProperty<>();
    private final ObjectProperty<PaymentMethod> paymentMethod = new SimpleObjectProperty<>();
    private final StringProperty transactionId = new SimpleStringProperty();
    private final ObjectProperty<PaymentStatus> status = new SimpleObjectProperty<>();

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        UPI,
        CASH
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public Payment() {
    }

    public Payment(int bookingId, BigDecimal amount, PaymentMethod paymentMethod, 
                  String transactionId, PaymentStatus status) {
        setBookingId(bookingId);
        setAmount(amount);
        setPaymentMethod(paymentMethod);
        setTransactionId(transactionId);
        setStatus(status);
        setPaymentDate(LocalDateTime.now());
    }

    // Property getters
    public IntegerProperty paymentIdProperty() {
        return paymentId;
    }

    public IntegerProperty bookingIdProperty() {
        return bookingId;
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public ObjectProperty<LocalDateTime> paymentDateProperty() {
        return paymentDate;
    }

    public ObjectProperty<PaymentMethod> paymentMethodProperty() {
        return paymentMethod;
    }

    public StringProperty transactionIdProperty() {
        return transactionId;
    }

    public ObjectProperty<PaymentStatus> statusProperty() {
        return status;
    }

    // Regular getters and setters
    public int getPaymentId() {
        return paymentId.get();
    }

    public void setPaymentId(int paymentId) {
        this.paymentId.set(paymentId);
    }

    public int getBookingId() {
        return bookingId.get();
    }

    public void setBookingId(int bookingId) {
        this.bookingId.set(bookingId);
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public String getTransactionId() {
        return transactionId.get();
    }

    public void setTransactionId(String transactionId) {
        this.transactionId.set(transactionId);
    }

    public PaymentStatus getStatus() {
        return status.get();
    }

    public void setStatus(PaymentStatus status) {
        this.status.set(status);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + getPaymentId() +
                ", bookingId=" + getBookingId() +
                ", amount=" + getAmount() +
                ", paymentMethod=" + getPaymentMethod() +
                ", transactionId='" + getTransactionId() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
} 