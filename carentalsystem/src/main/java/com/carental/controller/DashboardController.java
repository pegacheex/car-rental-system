package com.carental.controller;

import com.carental.model.User;
import com.carental.model.Vehicle;
import com.carental.model.Booking;
import com.carental.service.VehicleService;
import com.carental.service.BookingService;
import com.carental.service.ReportService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {
    @FXML private Label userLabel;
    @FXML private Button manageVehiclesBtn;
    @FXML private Button manageUsersBtn;
    @FXML private Button reportsBtn;
    
    @FXML private VBox availableCarsView;
    @FXML private VBox myBookingsView;
    @FXML private VBox manageVehiclesView;
    @FXML private VBox reportsView;
    
    @FXML private TextField searchField;
    @FXML private TableView<Vehicle> carsTable;
    @FXML private TableColumn<Vehicle, String> makeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, Integer> yearColumn;
    @FXML private TableColumn<Vehicle, BigDecimal> rateColumn;
    @FXML private TableColumn<Vehicle, String> statusColumn;
    
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> carColumn;
    @FXML private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML private TableColumn<Booking, LocalDate> endDateColumn;
    @FXML private TableColumn<Booking, BigDecimal> totalAmountColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;
    
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, Integer> vehicleIdColumn;
    @FXML private TableColumn<Vehicle, String> vehicleMakeColumn;
    @FXML private TableColumn<Vehicle, String> vehicleModelColumn;
    @FXML private TableColumn<Vehicle, Integer> vehicleYearColumn;
    @FXML private TableColumn<Vehicle, BigDecimal> vehicleRateColumn;
    @FXML private TableColumn<Vehicle, String> vehicleStatusColumn;
    
    private User currentUser;
    private final VehicleService vehicleService = new VehicleService();
    private final BookingService bookingService = new BookingService();
    private final ReportService reportService = new ReportService();
    
    private ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
    private ObservableList<Booking> bookings = FXCollections.observableArrayList();
    
    public void setUser(User user) {
        this.currentUser = user;
        userLabel.setText("Welcome, " + user.getFullName());
        
        // Show/hide admin controls
        boolean isAdmin = user.getRole() == User.UserRole.ADMIN;
        manageVehiclesBtn.setVisible(isAdmin);
        manageUsersBtn.setVisible(isAdmin);
        reportsBtn.setVisible(isAdmin);
        
        // Initialize views
        initializeAvailableCarsView();
        initializeMyBookingsView();
        if (isAdmin) {
            initializeManageVehiclesView();
            initializeReportsView();
        }
        
        // Show available cars by default
        showAvailableCars();
    }
    
    private void initializeAvailableCarsView() {
        // Initialize table columns
        makeColumn.setCellValueFactory(cellData -> cellData.getValue().makeProperty());
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().modelProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        rateColumn.setCellValueFactory(cellData -> cellData.getValue().dailyRateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty().asString());
        
        // Set up search functionality
        FilteredList<Vehicle> filteredData = new FilteredList<>(vehicles, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(vehicle -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return vehicle.getMake().toLowerCase().contains(lowerCaseFilter) ||
                       vehicle.getModel().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        SortedList<Vehicle> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(carsTable.comparatorProperty());
        carsTable.setItems(sortedData);
    }
    
    private void initializeMyBookingsView() {
        // Initialize table columns
        bookingIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookingIdProperty().asObject());
        carColumn.setCellValueFactory(cellData -> cellData.getValue().carDetailsProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty());
        bookingStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty().asString());
    }
    
    private void initializeManageVehiclesView() {
        // Initialize table columns
        vehicleIdColumn.setCellValueFactory(cellData -> cellData.getValue().vehicleIdProperty().asObject());
        vehicleMakeColumn.setCellValueFactory(cellData -> cellData.getValue().makeProperty());
        vehicleModelColumn.setCellValueFactory(cellData -> cellData.getValue().modelProperty());
        vehicleYearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        vehicleRateColumn.setCellValueFactory(cellData -> cellData.getValue().dailyRateProperty());
        vehicleStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty().asString());
    }
    
    private void initializeReportsView() {
        // Initialize revenue report table
        // Initialize popular vehicles table
    }
    
    @FXML
    private void showAvailableCars() {
        hideAllViews();
        availableCarsView.setVisible(true);
        refreshAvailableCars();
    }
    
    @FXML
    private void showMyBookings() {
        hideAllViews();
        myBookingsView.setVisible(true);
        refreshMyBookings();
    }
    
    @FXML
    private void showManageVehicles() {
        hideAllViews();
        manageVehiclesView.setVisible(true);
        refreshVehicles();
    }
    
    @FXML
    private void showReports() {
        hideAllViews();
        reportsView.setVisible(true);
        refreshReports();
    }
    
    private void hideAllViews() {
        availableCarsView.setVisible(false);
        myBookingsView.setVisible(false);
        manageVehiclesView.setVisible(false);
        reportsView.setVisible(false);
    }
    
    private void refreshAvailableCars() {
        try {
            List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
            vehicles.setAll(availableVehicles);
        } catch (Exception e) {
            showError("Error loading available cars", e);
        }
    }
    
    private void refreshMyBookings() {
        try {
            List<Booking> userBookings = bookingService.getUserBookings(currentUser.getUserId());
            bookings.setAll(userBookings);
        } catch (Exception e) {
            showError("Error loading bookings", e);
        }
    }
    
    private void refreshVehicles() {
        try {
            List<Vehicle> allVehicles = vehicleService.getAllVehicles();
            vehicles.setAll(allVehicles);
        } catch (Exception e) {
            showError("Error loading vehicles", e);
        }
    }
    
    private void refreshReports() {
        try {
            // Load and display reports
        } catch (Exception e) {
            showError("Error loading reports", e);
        }
    }
    
    @FXML
    private void handleBookCar() {
        Vehicle selectedVehicle = carsTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            try {
                showBookingDialog(selectedVehicle);
            } catch (Exception e) {
                showError("Error booking car", e);
            }
        }
    }
    
    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            try {
                if (bookingService.cancelBooking(selectedBooking.getBookingId())) {
                    refreshMyBookings();
                }
            } catch (Exception e) {
                showError("Error canceling booking", e);
            }
        }
    }
    
    @FXML
    private void handleGenerateInvoice() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            try {
                reportService.generateInvoice(selectedBooking);
            } catch (Exception e) {
                showError("Error generating invoice", e);
            }
        }
    }
    
    @FXML
    private void handleAddVehicle() {
        try {
            showVehicleDialog(null);
        } catch (Exception e) {
            showError("Error adding vehicle", e);
        }
    }
    
    @FXML
    private void handleEditVehicle() {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            try {
                showVehicleDialog(selectedVehicle);
            } catch (Exception e) {
                showError("Error editing vehicle", e);
            }
        }
    }
    
    @FXML
    private void handleDeleteVehicle() {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            try {
                if (vehicleService.deleteVehicle(selectedVehicle.getVehicleId())) {
                    refreshVehicles();
                }
            } catch (Exception e) {
                showError("Error deleting vehicle", e);
            }
        }
    }
    
    @FXML
    private void handleExportReports() {
        try {
            reportService.exportReports();
        } catch (Exception e) {
            showError("Error exporting reports", e);
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login - Car Rental System");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
            
            // Close the dashboard window
            ((Stage) userLabel.getScene().getWindow()).close();
        } catch (IOException e) {
            showError("Error during logout", e);
        }
    }
    
    @FXML
    private void handlePayment() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            try {
                openPaymentDialog(selectedBooking);
            } catch (Exception e) {
                showError("Error processing payment", e);
            }
        }
    }
    
    private void showBookingDialog(Vehicle vehicle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/booking_dialog.fxml"));
        Parent root = loader.load();
        
        BookingDialogController controller = loader.getController();
        controller.setVehicle(vehicle);
        controller.setUser(currentUser);
        
        Stage stage = new Stage();
        stage.setTitle("Book Vehicle");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        
        refreshAvailableCars();
        refreshMyBookings();
    }
    
    private void showVehicleDialog(Vehicle vehicle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vehicle_dialog.fxml"));
        Parent root = loader.load();
        
        VehicleDialogController controller = loader.getController();
        controller.setVehicle(vehicle);
        
        Stage stage = new Stage();
        stage.setTitle(vehicle == null ? "Add Vehicle" : "Edit Vehicle");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        
        refreshVehicles();
    }
    
    private void openPaymentDialog(Booking booking) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment_dialog.fxml"));
        Parent root = loader.load();
        
        PaymentDialogController controller = loader.getController();
        controller.setBooking(booking);
        
        Stage stage = new Stage();
        stage.setTitle("Payment - Car Rental System");
        stage.setScene(new Scene(root, 500, 600));
        stage.showAndWait();
        
        // Refresh bookings after payment
        refreshMyBookings();
    }
    
    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
} 