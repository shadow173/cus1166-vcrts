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

        vehicles.add(new Vehicle("A101", "In Use", 5, 500));
        vehicles.add(new Vehicle("B202", "Idle", 2, 200));
    }

    public Object[][] getFilteredJobs(String status) {
        return jobs.stream()
            .filter(j -> status.equals("All") || j.getStatus().equals(status))
            .map(Job::toArray)
            .toArray(Object[][]::new);
    }

    public Object[][] getFilteredVehicles(String status) {
        return vehicles.stream()
            .filter(v -> status.equals("All") || v.getStatus().equals(status))
            .map(Vehicle::toArray)
            .toArray(Object[][]::new);
    }
}
