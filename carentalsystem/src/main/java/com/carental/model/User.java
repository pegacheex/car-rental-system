package com.carental.model;

import java.time.LocalDateTime;
import javafx.beans.property.*;

public class User {
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final ObjectProperty<UserRole> role = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    public enum UserRole {
        ADMIN, CUSTOMER
    }

    public User() {}

    public User(String username, String password, String email, String fullName, String phone, UserRole role) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setFullName(fullName);
        setPhone(phone);
        setRole(role);
    }

    // Property getters
    public IntegerProperty userIdProperty() {
        return userId;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public ObjectProperty<UserRole> roleProperty() {
        return role;
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    // Regular getters and setters
    public int getUserId() {
        return userId.get();
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getFullName() {
        return fullName.get();
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public UserRole getRole() {
        return role.get();
    }

    public void setRole(UserRole role) {
        this.role.set(role);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", role=" + getRole() +
                '}';
    }
} 