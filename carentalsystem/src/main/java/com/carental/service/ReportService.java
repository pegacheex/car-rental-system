package com.carental.service;

import com.carental.model.Booking;
import com.carental.util.DatabaseUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportService {
    
    public void generateInvoice(Booking booking) throws IOException, SQLException {
        // Get booking details with vehicle and user information
        String sql = "SELECT b.*, v.make, v.model, v.daily_rate, u.full_name, u.email, u.phone " +
                    "FROM bookings b " +
                    "JOIN vehicles v ON b.vehicle_id = v.vehicle_id " +
                    "JOIN users u ON b.user_id = u.user_id " +
                    "WHERE b.booking_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getBookingId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create PDF document
                    PDDocument document = new PDDocument();
                    PDPage page = new PDPage();
                    document.addPage(page);
                    
                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        // Add header
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, 750);
                        contentStream.showText("Car Rental System - Invoice");
                        contentStream.endText();
                        
                        // Add invoice details
                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, 700);
                        contentStream.showText("Invoice Number: " + booking.getBookingId());
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Date: " + booking.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Customer: " + rs.getString("full_name"));
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Email: " + rs.getString("email"));
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Phone: " + rs.getString("phone"));
                        contentStream.endText();
                        
                        // Add vehicle details
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, 550);
                        contentStream.showText("Vehicle Details:");
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Car: " + rs.getString("make") + " " + rs.getString("model"));
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Daily Rate: ₹" + rs.getBigDecimal("daily_rate"));
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("Start Date: " + booking.getStartDate());
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText("End Date: " + booking.getEndDate());
                        contentStream.endText();
                        
                        // Add total amount
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, 400);
                        contentStream.showText("Total Amount: ₹" + booking.getTotalAmount());
                        contentStream.endText();
                    }
                    
                    // Save the document
                    String fileName = "invoice_" + booking.getBookingId() + ".pdf";
                    document.save(fileName);
                    document.close();
                }
            }
        }
    }
    
    public Map<String, Object> generateRevenueReport() throws SQLException {
        String sql = "SELECT DATE_FORMAT(b.created_at, '%Y-%m') as month, " +
                    "COUNT(*) as total_bookings, " +
                    "SUM(b.total_amount) as total_revenue " +
                    "FROM bookings b " +
                    "WHERE b.status != 'CANCELLED' " +
                    "GROUP BY DATE_FORMAT(b.created_at, '%Y-%m') " +
                    "ORDER BY month DESC";
        
        Map<String, Object> report = new HashMap<>();
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("month", rs.getString("month"));
                    monthData.put("totalBookings", rs.getInt("total_bookings"));
                    monthData.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                    monthlyData.add(monthData);
                }
            }
        }
        
        report.put("monthlyData", monthlyData);
        return report;
    }
    
    public Map<String, Object> generatePopularVehiclesReport() throws SQLException {
        String sql = "SELECT v.make, v.model, " +
                    "COUNT(*) as total_bookings, " +
                    "SUM(b.total_amount) as total_revenue " +
                    "FROM bookings b " +
                    "JOIN vehicles v ON b.vehicle_id = v.vehicle_id " +
                    "WHERE b.status != 'CANCELLED' " +
                    "GROUP BY v.vehicle_id " +
                    "ORDER BY total_bookings DESC";
        
        Map<String, Object> report = new HashMap<>();
        List<Map<String, Object>> vehicleData = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> vehicle = new HashMap<>();
                    vehicle.put("car", rs.getString("make") + " " + rs.getString("model"));
                    vehicle.put("totalBookings", rs.getInt("total_bookings"));
                    vehicle.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                    vehicleData.add(vehicle);
                }
            }
        }
        
        report.put("vehicleData", vehicleData);
        return report;
    }
    
    public void exportReports() throws IOException, SQLException {
        // Generate revenue report
        Map<String, Object> revenueReport = generateRevenueReport();
        
        // Generate popular vehicles report
        Map<String, Object> popularVehiclesReport = generatePopularVehiclesReport();
        
        // Create PDF document
        PDDocument document = new PDDocument();
        
        // Add revenue report page
        PDPage revenuePage = new PDPage();
        document.addPage(revenuePage);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, revenuePage)) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Revenue Report");
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Month\t\tBookings\tRevenue");
            contentStream.endText();
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> monthlyData = (List<Map<String, Object>>) revenueReport.get("monthlyData");
            float yOffset = 680;
            
            for (Map<String, Object> month : monthlyData) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yOffset);
                contentStream.showText(String.format("%s\t\t%d\t₹%s",
                    month.get("month"),
                    month.get("totalBookings"),
                    month.get("totalRevenue")));
                contentStream.endText();
                yOffset -= 20;
            }
        }
        
        // Add popular vehicles report page
        PDPage vehiclesPage = new PDPage();
        document.addPage(vehiclesPage);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, vehiclesPage)) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Popular Vehicles Report");
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Vehicle\t\tBookings\tRevenue");
            contentStream.endText();
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> vehicleData = (List<Map<String, Object>>) popularVehiclesReport.get("vehicleData");
            float yOffset = 680;
            
            for (Map<String, Object> vehicle : vehicleData) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yOffset);
                contentStream.showText(String.format("%s\t\t%d\t₹%s",
                    vehicle.get("car"),
                    vehicle.get("totalBookings"),
                    vehicle.get("totalRevenue")));
                contentStream.endText();
                yOffset -= 20;
            }
        }
        
        // Save the document
        String fileName = "reports_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf";
        document.save(fileName);
        document.close();
    }
} 