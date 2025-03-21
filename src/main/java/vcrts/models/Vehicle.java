package vcrts.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vehicle {
    private int ownerId;
    private String model;
    private String make;
    private String year;
    private String vin;
    private String residencyTime;
    private String registeredTimestamp;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Vehicle(int ownerId, String model, String make, String year, String vin, String residencyTime) {
        this.ownerId = ownerId;
        this.model = model;
        this.make = make;
        this.year = year;
        this.vin = vin;
        this.residencyTime = residencyTime;
        this.registeredTimestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    // Constructor with timestamp parameter for loading from file
    public Vehicle(int ownerId, String model, String make, String year, String vin, String residencyTime, String registeredTimestamp) {
        this.ownerId = ownerId;
        this.model = model;
        this.make = make;
        this.year = year;
        this.vin = vin;
        this.residencyTime = residencyTime;
        this.registeredTimestamp = registeredTimestamp;
    }

    // Getters and setters
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getResidencyTime() { return residencyTime; }
    public void setResidencyTime(String residencyTime) { this.residencyTime = residencyTime; }
    public String getRegisteredTimestamp() { return registeredTimestamp; }
    public void setRegisteredTimestamp(String registeredTimestamp) { this.registeredTimestamp = registeredTimestamp; }

    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
}
