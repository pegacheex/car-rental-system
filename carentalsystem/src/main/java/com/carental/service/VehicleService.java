package com.carental.service;

import com.carental.model.Vehicle;
import com.carental.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleService {
    
    public List<Vehicle> getAvailableVehicles() throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE status = 'AVAILABLE'";
        return executeVehicleQuery(sql);
    }
    
    public List<Vehicle> getAllVehicles() throws SQLException {
        String sql = "SELECT * FROM vehicles";
        return executeVehicleQuery(sql);
    }
    
    public Vehicle getVehicleById(int vehicleId) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        }
        return null;
    }
    
    public boolean addVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (make, model, year, license_plate, daily_rate, status, description) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getLicensePlate());
            stmt.setBigDecimal(5, vehicle.getDailyRate());
            stmt.setString(6, vehicle.getStatus().name());
            stmt.setString(7, vehicle.getDescription());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET make = ?, model = ?, year = ?, license_plate = ?, " +
                    "daily_rate = ?, status = ?, description = ? WHERE vehicle_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getLicensePlate());
            stmt.setBigDecimal(5, vehicle.getDailyRate());
            stmt.setString(6, vehicle.getStatus().name());
            stmt.setString(7, vehicle.getDescription());
            stmt.setInt(8, vehicle.getVehicleId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateVehicleStatus(int vehicleId, Vehicle.VehicleStatus status) throws SQLException {
        String sql = "UPDATE vehicles SET status = ? WHERE vehicle_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, vehicleId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private List<Vehicle> executeVehicleQuery(String sql) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        }
        return vehicles;
    }
    
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setLicensePlate(rs.getString("license_plate"));
        vehicle.setDailyRate(rs.getBigDecimal("daily_rate"));
        vehicle.setStatus(Vehicle.VehicleStatus.valueOf(rs.getString("status")));
        vehicle.setDescription(rs.getString("description"));
        vehicle.setImageUrl(rs.getString("image_url"));
        return vehicle;
    }
} 