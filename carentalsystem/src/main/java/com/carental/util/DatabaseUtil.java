package com.carental.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseUtil {
    private static final Properties properties = new Properties();
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean initialized = false;

    private static void initialize() {
        if (initialized) {
            return;
        }

        try {
            System.out.println("Initializing database connection...");
            
            // Load properties file
            try (InputStream input = DatabaseUtil.class.getClassLoader()
                    .getResourceAsStream("database/database.properties")) {
                if (input == null) {
                    throw new RuntimeException("Unable to find database.properties in the classpath");
                }
                properties.load(input);
                System.out.println("Successfully loaded database.properties");
            }
            
            // Get properties
            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            driver = properties.getProperty("db.driver");

            // Validate properties
            if (url == null || url.trim().isEmpty()) {
                throw new RuntimeException("Database URL is not specified in database.properties");
            }
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("Database username is not specified in database.properties");
            }
            if (password == null) {
                throw new RuntimeException("Database password is not specified in database.properties");
            }
            if (driver == null || driver.trim().isEmpty()) {
                throw new RuntimeException("Database driver is not specified in database.properties");
            }

            System.out.println("Database configuration:");
            System.out.println("URL: " + url);
            System.out.println("Username: " + username);
            System.out.println("Driver: " + driver);

            // Load driver
            try {
                System.out.println("Loading database driver...");
                Class.forName(driver);
                System.out.println("Successfully loaded database driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Database driver class not found: " + driver, e);
            }

            // Test connection and create database if it doesn't exist
            try {
                System.out.println("Testing database connection...");
                
                // First try to connect to MySQL server
                String serverUrl = url.substring(0, url.lastIndexOf("/"));
                try (Connection serverConn = DriverManager.getConnection(serverUrl, username, password)) {
                    System.out.println("Successfully connected to MySQL server");
                    
                    // Create database if it doesn't exist
                    String dbName = url.substring(url.lastIndexOf("/") + 1);
                    try (Statement stmt = serverConn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                        System.out.println("Ensured database exists: " + dbName);
                    }
                }
                
                // Now test connection to the specific database
                try (Connection dbConn = DriverManager.getConnection(url, username, password)) {
                    System.out.println("Successfully connected to the database");
                    
                    // Create tables if they don't exist
                    try (InputStream schemaInput = DatabaseUtil.class.getClassLoader()
                            .getResourceAsStream("database/schema.sql")) {
                        if (schemaInput != null) {
                            String schema = new String(schemaInput.readAllBytes());
                            try (Statement stmt = dbConn.createStatement()) {
                                for (String sql : schema.split(";")) {
                                    if (!sql.trim().isEmpty()) {
                                        stmt.executeUpdate(sql);
                                    }
                                }
                                System.out.println("Successfully initialized database schema");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to connect to the database: " + e.getMessage(), e);
            }

            initialized = true;
            System.out.println("Database initialization completed successfully");
            
        } catch (IOException e) {
            throw new RuntimeException("Error loading database.properties file", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
} 