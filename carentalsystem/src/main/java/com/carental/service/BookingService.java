package com.carental.service;

import com.carental.model.Booking;
import com.carental.model.Vehicle;
import com.carental.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class BookingService {
    
    public List<Booking> getUserBookings(int userId) throws SQLException {
        String sql = "SELECT b.*, v.make, v.model FROM bookings b " +
                    "JOIN vehicles v ON b.vehicle_id = v.vehicle_id " +
                    "WHERE b.user_id = ? ORDER BY b.created_at DESC";
        
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }
    
    public boolean createBooking(Booking booking) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Check if vehicle is available
            if (!isVehicleAvailable(conn, booking.getVehicleId(), booking.getStartDate(), booking.getEndDate())) {
                throw new SQLException("Vehicle is not available for the selected dates");
            }
            
            // Insert booking
            String sql = "INSERT INTO bookings (user_id, vehicle_id, start_date, end_date, total_amount, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, booking.getUserId());
                stmt.setInt(2, booking.getVehicleId());
                stmt.setDate(3, java.sql.Date.valueOf(booking.getStartDate()));
                stmt.setDate(4, java.sql.Date.valueOf(booking.getEndDate()));
                stmt.setBigDecimal(5, booking.getTotalAmount());
                stmt.setString(6, booking.getStatus().name());
                
                if (stmt.executeUpdate() > 0) {
                    // Update vehicle status
                    updateVehicleStatus(conn, booking.getVehicleId(), Vehicle.VehicleStatus.RENTED);
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
    
    public boolean cancelBooking(int bookingId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Get booking details
            String selectSql = "SELECT vehicle_id FROM bookings WHERE booking_id = ?";
            int vehicleId = -1;
            
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setInt(1, bookingId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        vehicleId = rs.getInt("vehicle_id");
                    }
                }
            }
            
            if (vehicleId == -1) {
                throw new SQLException("Booking not found");
            }
            
            // Update booking status
            String updateSql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, bookingId);
                if (stmt.executeUpdate() > 0) {
                    // Update vehicle status
                    updateVehicleStatus(conn, vehicleId, Vehicle.VehicleStatus.AVAILABLE);
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
    
    public BigDecimal calculateTotalAmount(int vehicleId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT daily_rate FROM vehicles WHERE vehicle_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal dailyRate = rs.getBigDecimal("daily_rate");
                    long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    return dailyRate.multiply(BigDecimal.valueOf(days));
                }
            }
        }
        throw new SQLException("Vehicle not found");
    }
    
    private boolean isVehicleAvailable(Connection conn, int vehicleId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE vehicle_id = ? AND status != 'CANCELLED' " +
                    "AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?) " +
                    "OR (start_date >= ? AND end_date <= ?))";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            stmt.setDate(3, java.sql.Date.valueOf(startDate));
            stmt.setDate(4, java.sql.Date.valueOf(endDate));
            stmt.setDate(5, java.sql.Date.valueOf(startDate));
            stmt.setDate(6, java.sql.Date.valueOf(startDate));
            stmt.setDate(7, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }
    
    private void updateVehicleStatus(Connection conn, int vehicleId, Vehicle.VehicleStatus status) 
            throws SQLException {
        String sql = "UPDATE vehicles SET status = ? WHERE vehicle_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, vehicleId);
            stmt.executeUpdate();
        }
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setVehicleId(rs.getInt("vehicle_id"));
        booking.setStartDate(rs.getDate("start_date").toLocalDate());
        booking.setEndDate(rs.getDate("end_date").toLocalDate());
        booking.setTotalAmount(rs.getBigDecimal("total_amount"));
        booking.setStatus(Booking.BookingStatus.valueOf(rs.getString("status")));
        booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Set car details for display
        String carDetails = rs.getString("make") + " " + rs.getString("model");
        booking.setCarDetails(carDetails);
        
        return booking;
    }
} 