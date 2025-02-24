package vcrts.models;

public class Vehicle {
    private int ownerId;
    private String model;
    private String make;
    private String year;
    private String vin;
    private String residencyTime;

    public Vehicle(int ownerId, String model, String make, String year, String vin, String residencyTime) {
        this.ownerId = ownerId;
        this.model = model;
        this.make = make;
        this.year = year;
        this.vin = vin;
        this.residencyTime = residencyTime;
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
}
