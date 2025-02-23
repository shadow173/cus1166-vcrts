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
                try {
                    String clientId = getValue(lines.get(i + 1));
                    String jobId = getValue(lines.get(i + 2));
                    String duration = getValue(lines.get(i + 4));
                    String deadline = getValue(lines.get(i + 5));
                    String status = "Queued"; // Default status

                    jobs.add(new Job(clientId, jobId, status, duration, deadline));
                } catch (Exception e) {
                    System.out.println("Skipping invalid job entry at line " + (i + 1));
                }
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
                try {
                    String ownerId = getValue(lines.get(i + 1));
                    String model = getValue(lines.get(i + 2));
                    String make = getValue(lines.get(i + 3));
                    String year = getValue(lines.get(i + 4));
                    String vin = getValue(lines.get(i + 5));

                    if (!ownerId.equals("N/A") && !vin.equals("N/A")) {
                        vehicles.add(new Vehicle(ownerId, model, make, year, vin, 0, "N/A", "N/A", "Idle"));
                    }
                } catch (Exception e) {
                    System.out.println("Skipping invalid vehicle entry at line " + (i + 1));
                }
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

    private String getValue(String line) {
        if (line == null || !line.contains(": ")) return "N/A";
        String[] parts = line.split(": ", 2);
        return (parts.length > 1) ? parts[1].trim() : "N/A";
    }
}
