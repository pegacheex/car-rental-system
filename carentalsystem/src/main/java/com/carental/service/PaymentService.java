package com.carental.service;

import com.carental.model.Payment;
import com.carental.model.Booking;
import com.carental.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PaymentService {
    
    public boolean processPayment(Payment payment) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Insert payment record
            String sql = "INSERT INTO payments (booking_id, amount, payment_method, transaction_id, status, payment_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, payment.getBookingId());
                stmt.setBigDecimal(2, payment.getAmount());
                stmt.setString(3, payment.getPaymentMethod().name());
                stmt.setString(4, payment.getTransactionId());
                stmt.setString(5, payment.getStatus().name());
                stmt.setTimestamp(6, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                
                if (stmt.executeUpdate() > 0) {
                    // Update booking status
                    updateBookingStatus(conn, payment.getBookingId(), Booking.BookingStatus.CONFIRMED);
                    conn.commit();
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public boolean updatePaymentStatus(int paymentId, Payment.PaymentStatus status) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE payment_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, paymentId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public Payment getPaymentByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }
        return null;
    }
    
    private void updateBookingStatus(Connection conn, int bookingId, Booking.BookingStatus status) 
            throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        }
    }
    
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setStatus(Payment.PaymentStatus.valueOf(rs.getString("status")));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        return payment;
    }
} 