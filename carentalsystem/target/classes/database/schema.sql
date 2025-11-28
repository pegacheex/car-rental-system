-- Create database
CREATE DATABASE IF NOT EXISTS car_rental_system;
USE car_rental_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    daily_rate DECIMAL(10,2) NOT NULL,
    status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE') NOT NULL,
    description TEXT,
    image_url VARCHAR(255)
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'UPI') NOT NULL,
    transaction_id VARCHAR(100),
    status ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- Insert sample admin user
INSERT INTO users (username, password, email, full_name, phone, role)
VALUES ('admin', 'admin123', 'admin@carental.com', 'System Admin', '9876543210', 'ADMIN');

-- Insert sample vehicles
INSERT INTO vehicles (make, model, year, license_plate, daily_rate, status, description)
VALUES 
('Toyota', 'Camry', 2022, 'MH01AB1234', 2500.00, 'AVAILABLE', 'Comfortable sedan with excellent fuel efficiency'),
('Honda', 'City', 2023, 'MH02CD5678', 2000.00, 'AVAILABLE', 'Compact sedan perfect for city driving'),
('Hyundai', 'Creta', 2023, 'MH03EF9012', 3000.00, 'AVAILABLE', 'SUV with spacious interior and modern features'); 