// ============================================
// PARKING LOT MANAGEMENT SYSTEM
// ============================================

import java.util.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// ============================================
// 1. ABSTRACT CLASS & INHERITANCE
// ============================================

// Abstract Vehicle class
abstract class Vehicle {
    private String vehicleNumber;
    private String ownerName;
    private String phoneNumber;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;
    
    // Static counter for tracking vehicles
    private static int totalVehiclesProcessed = 0;
    
    // Final constant for parking area code
    protected final String PARKING_AREA_CODE = "MLP";
    
    // Constructor
    public Vehicle(String vehicleNumber, String ownerName, String phoneNumber) {
        this.vehicleNumber = vehicleNumber.toUpperCase();
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.entryTime = LocalDateTime.now();
        totalVehiclesProcessed++;
    }
    
    // Getters (Encapsulation)
    public String getVehicleNumber() { return vehicleNumber; }
    public String getOwnerName() { return ownerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    
    // Abstract methods - must be implemented by child classes
    public abstract double calculateParkingCharges();
    public abstract String getVehicleType();
    public abstract int getRequiredSlots();
    
    // Concrete method
    public long getParkingDuration() {
        LocalDateTime exit = (exitTime != null) ? exitTime : LocalDateTime.now();
        return Duration.between(entryTime, exit).toMinutes();
    }
    
    public void displayVehicleInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("  Vehicle Number: " + vehicleNumber);
        System.out.println("  Owner: " + ownerName);
        System.out.println("  Phone: " + phoneNumber);
        System.out.println("  Type: " + getVehicleType());
        System.out.println("  Entry Time: " + entryTime.format(formatter));
        if (exitTime != null) {
            System.out.println("  Exit Time: " + exitTime.format(formatter));
        }
        System.out.println("  Duration: " + getParkingDuration() + " minutes");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    // Static method
    public static int getTotalVehiclesProcessed() {
        return totalVehiclesProcessed;
    }
}

// ============================================
// 2. INHERITANCE & POLYMORPHISM
// ============================================

// Car class
class Car extends Vehicle {
    private String carModel;
    private static final double BASE_RATE = 20.0; // Rs per hour
    private static final double ADDITIONAL_RATE = 10.0; // After 2 hours
    
    public Car(String vehicleNumber, String ownerName, String phoneNumber, String carModel) {
        super(vehicleNumber, ownerName, phoneNumber);
        this.carModel = carModel;
    }
    
    @Override
    public double calculateParkingCharges() {
        long minutes = getParkingDuration();
        double hours = Math.ceil(minutes / 60.0);
        
        if (hours <= 2) {
            return hours * BASE_RATE;
        } else {
            return (2 * BASE_RATE) + ((hours - 2) * ADDITIONAL_RATE);
        }
    }
    
    @Override
    public String getVehicleType() {
        return "Car (" + carModel + ")";
    }
    
    @Override
    public int getRequiredSlots() {
        return 1; // Car takes 1 slot
    }
}

// Bike class
class Bike extends Vehicle {
    private String bikeModel;
    private static final double BASE_RATE = 10.0; // Rs per hour
    private static final double ADDITIONAL_RATE = 5.0; // After 2 hours
    
    public Bike(String vehicleNumber, String ownerName, String phoneNumber, String bikeModel) {
        super(vehicleNumber, ownerName, phoneNumber);
        this.bikeModel = bikeModel;
    }
    
    @Override
    public double calculateParkingCharges() {
        long minutes = getParkingDuration();
        double hours = Math.ceil(minutes / 60.0);
        
        if (hours <= 2) {
            return hours * BASE_RATE;
        } else {
            return (2 * BASE_RATE) + ((hours - 2) * ADDITIONAL_RATE);
        }
    }
    
    @Override
    public String getVehicleType() {
        return "Bike (" + bikeModel + ")";
    }
    
    @Override
    public int getRequiredSlots() {
        return 1; // Bike takes 1 slot
    }
}

// Truck class
class Truck extends Vehicle {
    private int loadCapacity;
    private static final double BASE_RATE = 50.0; // Rs per hour
    private static final double ADDITIONAL_RATE = 30.0;
    
    public Truck(String vehicleNumber, String ownerName, String phoneNumber, int loadCapacity) {
        super(vehicleNumber, ownerName, phoneNumber);
        this.loadCapacity = loadCapacity;
    }
    
    @Override
    public double calculateParkingCharges() {
        long minutes = getParkingDuration();
        double hours = Math.ceil(minutes / 60.0);
        double baseCharge = (hours <= 2) ? hours * BASE_RATE : (2 * BASE_RATE) + ((hours - 2) * ADDITIONAL_RATE);
        
        // Additional charge based on capacity
        double capacityCharge = (loadCapacity > 5) ? 100.0 : 0.0;
        return baseCharge + capacityCharge;
    }
    
    @Override
    public String getVehicleType() {
        return "Truck (" + loadCapacity + " tons)";
    }
    
    @Override
    public int getRequiredSlots() {
        return 2; // Truck takes 2 slots
    }
}

// ============================================
// 3. CUSTOM EXCEPTION HANDLING
// ============================================

class ParkingFullException extends Exception {
    public ParkingFullException(String message) {
        super(message);
    }
}

class VehicleNotFoundException extends Exception {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}

class InvalidVehicleException extends Exception {
    public InvalidVehicleException(String message) {
        super(message);
    }
}

class SlotNotAvailableException extends Exception {
    public SlotNotAvailableException(String message) {
        super(message);
    }
}

// ============================================
// 4. PARKING SLOT CLASS
// ============================================

class ParkingSlot {
    private int slotNumber;
    private boolean isOccupied;
    private String vehicleNumber;
    private String slotType; // CAR, BIKE, TRUCK
    
    public ParkingSlot(int slotNumber, String slotType) {
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.isOccupied = false;
    }
    
    // Encapsulation
    public int getSlotNumber() { return slotNumber; }
    public boolean isOccupied() { return isOccupied; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getSlotType() { return slotType; }
    
    public void occupySlot(String vehicleNumber) {
        this.isOccupied = true;
        this.vehicleNumber = vehicleNumber;
    }
    
    public void vacateSlot() {
        this.isOccupied = false;
        this.vehicleNumber = null;
    }
    
    @Override
    public String toString() {
        String status = isOccupied ? "❌ OCCUPIED" : "✓ AVAILABLE";
        return String.format("Slot %d [%s] - %s", slotNumber, slotType, status);
    }
}

// ============================================
// 5. PARKING TICKET CLASS
// ============================================

class ParkingTicket {
    private String ticketId;
    private String vehicleNumber;
    private int slotNumber;
    private LocalDateTime issueTime;
    private static int ticketCounter = 1000;
    
    public ParkingTicket(String vehicleNumber, int slotNumber) {
        this.ticketId = "TICKET" + (++ticketCounter);
        this.vehicleNumber = vehicleNumber;
        this.slotNumber = slotNumber;
        this.issueTime = LocalDateTime.now();
    }
    
    public String getTicketId() { return ticketId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public int getSlotNumber() { return slotNumber; }
    public LocalDateTime getIssueTime() { return issueTime; }
    
    public void displayTicket() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║         PARKING TICKET                  ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║ Ticket ID: " + ticketId + "                     ║");
        System.out.println("║ Vehicle: " + vehicleNumber + "                    ║");
        System.out.println("║ Slot: " + slotNumber + "                          ║");
        System.out.println("║ Entry Time: " + issueTime.format(formatter) + " ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println("   Please keep this ticket safe!");
    }
}

// ============================================
// 6. PARKING RECORD CLASS
// ============================================

class ParkingRecord {
    private String recordId;
    private String vehicleNumber;
    private String vehicleType;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double charges;
    private static int recordCounter = 0;
    
    public ParkingRecord(Vehicle vehicle, double charges) {
        this.recordId = "REC" + (++recordCounter);
        this.vehicleNumber = vehicle.getVehicleNumber();
        this.vehicleType = vehicle.getVehicleType();
        this.entryTime = vehicle.getEntryTime();
        this.exitTime = vehicle.getExitTime();
        this.charges = charges;
    }
    
    // Getters
    public String getRecordId() { return recordId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getVehicleType() { return vehicleType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getCharges() { return charges; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm");
        return String.format("%-10s %-15s %-20s %-15s %-15s ₹%.2f",
            recordId, vehicleNumber, vehicleType,
            entryTime.format(formatter), 
            exitTime.format(formatter), charges);
    }
}

// ============================================
// 7. COLLECTIONS FRAMEWORK & FINAL CLASS
// ============================================

final class ParkingLotSystem {
    // Collections
    private HashMap<String, Vehicle> parkedVehicles; // vehicleNumber -> Vehicle
    private HashMap<String, ParkingTicket> tickets; // ticketId -> Ticket
    private ArrayList<ParkingSlot> carSlots;
    private ArrayList<ParkingSlot> bikeSlots;
    private ArrayList<ParkingSlot> truckSlots;
    private ArrayList<ParkingRecord> parkingHistory;
    private PriorityQueue<Integer> availableCarSlots;
    private PriorityQueue<Integer> availableBikeSlots;
    private PriorityQueue<Integer> availableTruckSlots;
    
    // Final constants
    private final int TOTAL_CAR_SLOTS;
    private final int TOTAL_BIKE_SLOTS;
    private final int TOTAL_TRUCK_SLOTS;
    
    private double totalRevenue;
    
    // Constructor
    public ParkingLotSystem(int carSlots, int bikeSlots, int truckSlots) {
        this.TOTAL_CAR_SLOTS = carSlots;
        this.TOTAL_BIKE_SLOTS = bikeSlots;
        this.TOTAL_TRUCK_SLOTS = truckSlots;
        
        parkedVehicles = new HashMap<>();
        tickets = new HashMap<>();
        parkingHistory = new ArrayList<>();
        
        // Initialize slots
        this.carSlots = new ArrayList<>();
        this.bikeSlots = new ArrayList<>();
        this.truckSlots = new ArrayList<>();
        
        availableCarSlots = new PriorityQueue<>();
        availableBikeSlots = new PriorityQueue<>();
        availableTruckSlots = new PriorityQueue<>();
        
        initializeSlots();
        totalRevenue = 0.0;
    }
    
    private void initializeSlots() {
        // Initialize car slots (1-100)
        for (int i = 1; i <= TOTAL_CAR_SLOTS; i++) {
            carSlots.add(new ParkingSlot(i, "CAR"));
            availableCarSlots.offer(i);
        }
        
        // Initialize bike slots (101-200)
        for (int i = 101; i <= 100 + TOTAL_BIKE_SLOTS; i++) {
            bikeSlots.add(new ParkingSlot(i, "BIKE"));
            availableBikeSlots.offer(i);
        }
        
        // Initialize truck slots (201-220)
        for (int i = 201; i <= 200 + TOTAL_TRUCK_SLOTS; i++) {
            truckSlots.add(new ParkingSlot(i, "TRUCK"));
            availableTruckSlots.offer(i);
        }
    }
    
    // Park vehicle
    public ParkingTicket parkVehicle(Vehicle vehicle) throws ParkingFullException, SlotNotAvailableException {
        String vehicleType = vehicle.getVehicleType().split(" ")[0].toUpperCase();
        PriorityQueue<Integer> availableSlots;
        ArrayList<ParkingSlot> slots;
        
        // Determine slot type
        if (vehicleType.equals("CAR")) {
            availableSlots = availableCarSlots;
            slots = carSlots;
        } else if (vehicleType.equals("BIKE")) {
            availableSlots = availableBikeSlots;
            slots = bikeSlots;
        } else {
            availableSlots = availableTruckSlots;
            slots = truckSlots;
        }
        
        // Check availability
        if (availableSlots.isEmpty()) {
            throw new ParkingFullException("No available slots for " + vehicleType);
        }
        
        // Allocate slot
        int slotNumber = availableSlots.poll();
        ParkingSlot slot = findSlot(slots, slotNumber);
        slot.occupySlot(vehicle.getVehicleNumber());
        
        // Store vehicle and create ticket
        parkedVehicles.put(vehicle.getVehicleNumber(), vehicle);
        ParkingTicket ticket = new ParkingTicket(vehicle.getVehicleNumber(), slotNumber);
        tickets.put(ticket.getTicketId(), ticket);
        
        System.out.println("\n✓ Vehicle parked successfully!");
        ticket.displayTicket();
        
        return ticket;
    }
    
    // Exit vehicle
    public void exitVehicle(String vehicleNumber) throws VehicleNotFoundException {
        Vehicle vehicle = parkedVehicles.get(vehicleNumber.toUpperCase());
        
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found: " + vehicleNumber);
        }
        
        // Set exit time and calculate charges
        vehicle.setExitTime(LocalDateTime.now());
        double charges = vehicle.calculateParkingCharges();
        totalRevenue += charges;
        
        // Find and vacate slot
        ParkingTicket ticket = findTicketByVehicle(vehicleNumber);
        if (ticket != null) {
            vacateSlot(vehicle, ticket.getSlotNumber());
            tickets.remove(ticket.getTicketId());
        }
        
        // Create record
        ParkingRecord record = new ParkingRecord(vehicle, charges);
        parkingHistory.add(record);
        
        // Remove from parked vehicles
        parkedVehicles.remove(vehicleNumber.toUpperCase());
        
        // Display receipt
        displayReceipt(vehicle, charges);
    }
    
    private void vacateSlot(Vehicle vehicle, int slotNumber) {
        String vehicleType = vehicle.getVehicleType().split(" ")[0].toUpperCase();
        
        if (vehicleType.equals("CAR")) {
            ParkingSlot slot = findSlot(carSlots, slotNumber);
            slot.vacateSlot();
            availableCarSlots.offer(slotNumber);
        } else if (vehicleType.equals("BIKE")) {
            ParkingSlot slot = findSlot(bikeSlots, slotNumber);
            slot.vacateSlot();
            availableBikeSlots.offer(slotNumber);
        } else {
            ParkingSlot slot = findSlot(truckSlots, slotNumber);
            slot.vacateSlot();
            availableTruckSlots.offer(slotNumber);
        }
    }
    
    private ParkingSlot findSlot(ArrayList<ParkingSlot> slots, int slotNumber) {
        for (ParkingSlot slot : slots) {
            if (slot.getSlotNumber() == slotNumber) {
                return slot;
            }
        }
        return null;
    }
    
    private ParkingTicket findTicketByVehicle(String vehicleNumber) {
        for (ParkingTicket ticket : tickets.values()) {
            if (ticket.getVehicleNumber().equalsIgnoreCase(vehicleNumber)) {
                return ticket;
            }
        }
        return null;
    }
    
    private void displayReceipt(Vehicle vehicle, double charges) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║          PARKING RECEIPT                  ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║ Vehicle: " + vehicle.getVehicleNumber() + "                    ║");
        System.out.println("║ Type: " + vehicle.getVehicleType() + "                ║");
        System.out.println("║ Entry: " + vehicle.getEntryTime().format(formatter) + " ║");
        System.out.println("║ Exit:  " + vehicle.getExitTime().format(formatter) + " ║");
        System.out.println("║ Duration: " + vehicle.getParkingDuration() + " minutes              ║");
        System.out.println("║ CHARGES: ₹" + String.format("%.2f", charges) + "                  ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println("    Thank you for parking with us!");
    }
    
    // Display available slots
    public void displayAvailableSlots() {
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║        AVAILABLE PARKING SLOTS            ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║ Car Slots:   " + availableCarSlots.size() + " / " + TOTAL_CAR_SLOTS + "                       ║");
        System.out.println("║ Bike Slots:  " + availableBikeSlots.size() + " / " + TOTAL_BIKE_SLOTS + "                      ║");
        System.out.println("║ Truck Slots: " + availableTruckSlots.size() + " / " + TOTAL_TRUCK_SLOTS + "                   ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }
    
    // Display all parked vehicles
    public void displayParkedVehicles() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        CURRENTLY PARKED VEHICLES");
        System.out.println("═══════════════════════════════════════");
        
        if (parkedVehicles.isEmpty()) {
            System.out.println("No vehicles currently parked.");
            return;
        }
        
        for (Vehicle vehicle : parkedVehicles.values()) {
            vehicle.displayVehicleInfo();
        }
    }
    
    // Display parking history
    public void displayParkingHistory() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("                     PARKING HISTORY");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        
        if (parkingHistory.isEmpty()) {
            System.out.println("No parking history available.");
            return;
        }
        
        System.out.printf("%-10s %-15s %-20s %-15s %-15s %s%n",
            "RECORD", "VEHICLE", "TYPE", "ENTRY", "EXIT", "CHARGES");
        System.out.println("───────────────────────────────────────────────────────────────────────────────");
        
        for (ParkingRecord record : parkingHistory) {
            System.out.println(record);
        }
    }
    
    // Search vehicle
    public void searchVehicle(String vehicleNumber) throws VehicleNotFoundException {
        Vehicle vehicle = parkedVehicles.get(vehicleNumber.toUpperCase());
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found: " + vehicleNumber);
        }
        vehicle.displayVehicleInfo();
    }
    
    // Statistics
    public void displayStatistics() {
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║          PARKING STATISTICS               ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║ Currently Parked: " + parkedVehicles.size() + "                  ║");
        System.out.println("║ Total Processed: " + Vehicle.getTotalVehiclesProcessed() + "                 ║");
        System.out.println("║ Total Revenue: ₹" + String.format("%.2f", totalRevenue) + "             ║");
        System.out.println("║ History Records: " + parkingHistory.size() + "                  ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }
    
    public double getTotalRevenue() { return totalRevenue; }
    public int getCurrentlyParked() { return parkedVehicles.size(); }
    public HashMap<String, Vehicle> getParkedVehicles() { return parkedVehicles; }
}

// ============================================
// 8. DATABASE MANAGER (JDBC)
// ============================================

class ParkingDatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parkingdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    private Connection connection;
    
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ MySQL Driver not found!");
        } catch (SQLException e) {
            System.out.println("✗ Database connection failed: " + e.getMessage());
        }
    }
    
    public void saveVehicleEntry(Vehicle vehicle, int slotNumber) {
        String query = "INSERT INTO parking_entries (vehicle_number, owner_name, phone, vehicle_type, entry_time, slot_number, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicle.getVehicleNumber());
            pstmt.setString(2, vehicle.getOwnerName());
            pstmt.setString(3, vehicle.getPhoneNumber());
            pstmt.setString(4, vehicle.getVehicleType());
            pstmt.setTimestamp(5, Timestamp.valueOf(vehicle.getEntryTime()));
            pstmt.setInt(6, slotNumber);
            pstmt.setString(7, "PARKED");
            
            pstmt.executeUpdate();
            System.out.println("✓ Entry saved to database!");
        } catch (SQLException e) {
            System.out.println("✗ Error saving entry: " + e.getMessage());
        }
    }
    
    public void updateVehicleExit(String vehicleNumber, double charges) {
        String query = "UPDATE parking_entries SET exit_time = ?, charges = ?, status = ? WHERE vehicle_number = ? AND status = 'PARKED'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setDouble(2, charges);
            pstmt.setString(3, "COMPLETED");
            pstmt.setString(4, vehicleNumber);
            
            pstmt.executeUpdate();
            System.out.println("✓ Exit updated in database!");
        } catch (SQLException e) {
            System.out.println("✗ Error updating exit: " + e.getMessage());
        }
    }
    
    public void displayDatabaseRecords() {
        String query = "SELECT * FROM parking_entries ORDER BY entry_time DESC LIMIT 20";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("            DATABASE RECORDS (Last 20)");
            System.out.println("═══════════════════════════════════════════════════════");
            
            while (rs.next()) {
                System.out.printf("%-15s %-20s %-15s %s%n",
                    rs.getString("vehicle_number"),
                    rs.getString("owner_name"),
                    rs.getString("vehicle_type"),
                    rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("✗ Error fetching records: " + e.getMessage());
        }
    }
    
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database disconnected!");
            }
        } catch (SQLException e) {
            System.out.println("✗ Error closing connection: " + e.getMessage());
        }
    }
}

// ============================================
// 9. MAIN CLASS
// ============================================

public class ParkingLotManagementSystem {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ParkingLotSystem parkingSystem = new ParkingLotSystem(50, 100, 20);
        ParkingDatabaseManager db = new ParkingDatabaseManager();
        
        displayWelcome();
        
        boolean running = true;
        
        while (running) {
            displayMenu();
            
            try {
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        parkNewVehicle(scanner, parkingSystem);
                        break;
                        
                    case 2:
                        exitVehicleFromParking(scanner, parkingSystem);
                        break;
                        
                    case 3:
                        parkingSystem.displayAvailableSlots();
                        break;
                        
                    case 4:
                        parkingSystem.displayParkedVehicles();
                        break;
                        
                    case 5:
                        searchVehicleInParking(scanner, parkingSystem);
                        break;
                        
                    case 6:
                        parkingSystem.displayParkingHistory();
                        break;
                        
                    case 7:
                        parkingSystem.displayStatistics();
                        break;
                        
                    case 8:
                        databaseOperations(scanner, parkingSystem, db);
                        break;
                        
                    case 9:
                        demonstrateGC();
                        break;
                        
                    case 0:
                        System.out.println("\n✓ Thank you for using Parking System!");
                        System.out.println("Drive safely!");
                        running = false;
                        break;
                        
                    default:
                        System.out.println("✗ Invalid choice!");
                }
                
            } catch (InputMismatchException e) {
                System.out.println("✗ Invalid input! Please enter a number.");
                scanner.nextLine();
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
        db.disconnect();
    }
    
    private static void displayWelcome() {
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║   PARKING LOT MANAGEMENT SYSTEM           ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }
    
    private static void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Park a new vehicle");
        System.out.println("2. Exit a vehicle");
        System.out.println("3. View available slots");
        System.out.println("4. View parked vehicles");
        System.out.println("5. Search for a vehicle");
        System.out.println("6. View parking history");
        System.out.println("7. View statistics");
        System.out.println("8. Database Operations");
        System.out.println("9. Demonstrate Garbage Collection");
        System.out.println("0. Exit Application");
    }
    
    private static void parkNewVehicle(Scanner scanner, ParkingLotSystem parkingSystem) {
        try {
            System.out.println("\n--- PARK VEHICLE ---");
            System.out.print("Enter vehicle type (car/bike/truck): ");
            String type = scanner.nextLine().toLowerCase();
            
            System.out.print("Enter vehicle number: ");
            String vehicleNumber = scanner.nextLine().toUpperCase();
            
            System.out.print("Enter owner's name: ");
            String ownerName = scanner.nextLine();
            
            System.out.print("Enter owner's phone number: ");
            String phoneNumber = scanner.nextLine();
            
            Vehicle newVehicle = null;
            
            switch (type) {
                case "car":
                    System.out.print("Enter car model: ");
                    String carModel = scanner.nextLine();
                    newVehicle = new Car(vehicleNumber, ownerName, phoneNumber, carModel);
                    break;
                case "bike":
                    System.out.print("Enter bike model: ");
                    String bikeModel = scanner.nextLine();
                    newVehicle = new Bike(vehicleNumber, ownerName, phoneNumber, bikeModel);
                    break;
                case "truck":
                    System.out.print("Enter load capacity (tons): ");
                    int loadCapacity = scanner.nextInt();
                    scanner.nextLine();
                    newVehicle = new Truck(vehicleNumber, ownerName, phoneNumber, loadCapacity);
                    break;
                default:
                    throw new InvalidVehicleException("Invalid vehicle type entered.");
            }
            
            ParkingTicket ticket = parkingSystem.parkVehicle(newVehicle);
            // Optionally, save to database
            // new ParkingDatabaseManager().saveVehicleEntry(newVehicle, ticket.getSlotNumber());
            
        } catch (InvalidVehicleException | ParkingFullException | SlotNotAvailableException | InputMismatchException e) {
            System.out.println("✗ Error: " + e.getMessage());
            if (e instanceof InputMismatchException) {
                scanner.nextLine();
            }
        }
    }
    
    private static void exitVehicleFromParking(Scanner scanner, ParkingLotSystem parkingSystem) {
        System.out.println("\n--- EXIT VEHICLE ---");
        System.out.print("Enter vehicle number to exit: ");
        String vehicleNumber = scanner.nextLine();
        
        try {
            parkingSystem.exitVehicle(vehicleNumber);
            // Optionally, update database
            // new ParkingDatabaseManager().updateVehicleExit(vehicleNumber, 0); // Need to pass actual charges
        } catch (VehicleNotFoundException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void searchVehicleInParking(Scanner scanner, ParkingLotSystem parkingSystem) {
        System.out.println("\n--- SEARCH VEHICLE ---");
        System.out.print("Enter vehicle number to search: ");
        String vehicleNumber = scanner.nextLine();
        
        try {
            parkingSystem.searchVehicle(vehicleNumber);
        } catch (VehicleNotFoundException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void databaseOperations(Scanner scanner, ParkingLotSystem parkingSystem, ParkingDatabaseManager db) {
        System.out.println("\n--- DATABASE OPERATIONS ---");
        System.out.println("1. Connect to Database");
        System.out.println("2. Display last 20 records");
        System.out.println("3. Disconnect from Database");
        System.out.print("Enter choice: ");
        
        try {
            int dbChoice = scanner.nextInt();
            scanner.nextLine();
            
            switch (dbChoice) {
                case 1:
                    db.connect();
                    break;
                case 2:
                    db.displayDatabaseRecords();
                    break;
                case 3:
                    db.disconnect();
                    break;
                default:
                    System.out.println("✗ Invalid database choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("✗ Invalid input! Please enter a number.");
            scanner.nextLine();
        }
    }
    
    private static void demonstrateGC() {
        System.out.println("\n--- GARBAGE COLLECTION DEMO ---");
        System.out.println("Creating a large number of temporary objects...");
        
        for (int i = 0; i < 10000; i++) {
            new String("Garbage" + i);
        }
        
        System.out.println("Objects created. Requesting Garbage Collection...");
        System.gc();
        
        System.out.println("Garbage collection requested. Some memory may have been freed.");
    }
}