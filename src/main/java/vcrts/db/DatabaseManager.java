package vcrts.db;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private List<Job> jobs;
    private List<Vehicle> vehicles;

    public DatabaseManager() {
        jobs = new ArrayList<>();
        vehicles = new ArrayList<>();

        // Sample Data (Replace with real database queries later)
        jobs.add(new Job("001", "In Progress", "30 min", "02/20/2025", 50));
        jobs.add(new Job("002", "Completed", "5 min", "02/19/2025", 100));
        jobs.add(new Job("002", "Queued", "1 hr", "02/21/2025", 100));


        vehicles.add(new Vehicle("A101", "Toyota", "Camry", "2022", "VIN123456789", 5, "10:00 AM", "1:15 PM", "In Use"));
        vehicles.add(new Vehicle("B202", "Honda", "Civic", "2021", "VIN987654321", 3, "9:30 AM", "12:00 PM", "Idle"));
        vehicles.add(new Vehicle("C303", "Tesla", "Model S", "2023", "VIN555222333", 10, "11:00 AM", "", "Offline"));
    }

    public Object[][] getFilteredJobs(String status) {
        return jobs.stream()
            .filter(j -> status.equals("All") || j.getStatus().equals(status))
            .map(Job::toArray)
            .toArray(Object[][]::new);
    }

    public Object[][] getFilteredVehicles(String status) {
        return vehicles.stream()
            
            .map(Vehicle::toArray)
            .toArray(Object[][]::new);
    }
}
