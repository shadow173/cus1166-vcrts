package vcrts.db;

public class Vehicle {
    private String vehicleId;
    private String status;
    private int jobsCompleted;

    public Vehicle(String vehicleId, String status, int jobsCompleted, double earnings) {
        this.vehicleId = vehicleId;
        this.status = status;
        this.jobsCompleted = jobsCompleted;
    }

    public String getVehicleId() { return vehicleId; }
    public String getStatus() { return status; }
    public int getJobsCompleted() { return jobsCompleted; }

    // Converts a Vehicle object to an array for table display
    public Object[] toArray() {
        return new Object[] { vehicleId, status, jobsCompleted};
    }
}
