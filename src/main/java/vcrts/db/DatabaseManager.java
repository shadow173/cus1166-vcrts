package vcrts.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private List<Job> jobs;
    private List<Vehicle> vehicles;

    public DatabaseManager() {
        jobs = new ArrayList<>();
        vehicles = new ArrayList<>();
        loadJobsFromFile();
        loadVehiclesFromFile();
    }

    private void loadJobsFromFile() {
        jobs.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get("client_jobs.txt"));
            for (int i = 0; i + 5 < lines.size(); i += 6) { // Each job entry has 6 lines
                String clientId = lines.get(i + 1).split(": ")[1];
                String jobId = lines.get(i + 2).split(": ")[1];
                String status = "Queued"; // Default status
                String duration = lines.get(i + 4).split(": ")[1];
                String deadline = lines.get(i + 5).split(": ")[1];
                jobs.add(new Job(clientId, jobId, status, duration, deadline));
            }
        } catch (IOException e) {
            System.out.println("No jobs found yet.");
        }
    }

    private void loadVehiclesFromFile() {
        vehicles.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get("owner_vehicles.txt"));
            for (int i = 0; i + 6 < lines.size(); i += 7) { // Each vehicle entry has 7 lines
                String ownerId = lines.get(i + 1).split(": ")[1];
                String model = lines.get(i + 2).split(": ")[1];
                String make = lines.get(i + 3).split(": ")[1];
                String year = lines.get(i + 4).split(": ")[1];
                String vin = lines.get(i + 5).split(": ")[1];
                vehicles.add(new Vehicle(ownerId, model, make, year, vin, 0, "N/A", "N/A", "Idle"));
            }
        } catch (IOException e) {
            System.out.println("No vehicles found yet.");
        }
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
