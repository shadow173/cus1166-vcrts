package vcrts.dao;

import vcrts.db.FileManager;
import vcrts.models.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VehicleDAO {
    private static final Logger logger = Logger.getLogger(VehicleDAO.class.getName());
    private static final String VEHICLES_FILE = "vehicles.txt";
    private static final String DELIMITER = "\\|";
    private static final String SEPARATOR = "|";

    /**
     * Converts a Vehicle object to a line of text for storage.
     */
    private String vehicleToLine(Vehicle vehicle) {
        return vehicle.getOwnerId() + SEPARATOR +
                vehicle.getModel() + SEPARATOR +
                vehicle.getMake() + SEPARATOR +
                vehicle.getYear() + SEPARATOR +
                vehicle.getVin() + SEPARATOR +
                vehicle.getResidencyTime() + SEPARATOR +
                vehicle.getRegisteredTimestamp();
    }

    /**
     * Converts a line of text to a Vehicle object.
     */
    private Vehicle lineToVehicle(String line) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 6) {
            logger.warning("Invalid vehicle data format: " + line);
            return null;
        }

        try {
            // Check if the timestamp is included in the line
            String timestamp = parts.length >= 7 ? parts[6] : Vehicle.getCurrentTimestamp();

            return new Vehicle(
                    Integer.parseInt(parts[0]),  // ownerId
                    parts[1],                     // model
                    parts[2],                     // make
                    parts[3],                     // year
                    parts[4],                     // vin
                    parts[5],                     // residencyTime
                    timestamp                     // registeredTimestamp
            );
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing owner ID: " + parts[0], e);
            return null;
        }
    }

    /**
     * Retrieves all vehicles from the file.
     * @return A list of all vehicles.
     */
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(VEHICLES_FILE);

        for (String line : lines) {
            Vehicle vehicle = lineToVehicle(line);
            if (vehicle != null) {
                vehicles.add(vehicle);
            }
        }

        return vehicles;
    }

    /**
     * Retrieves a list of vehicles owned by a specific user.
     *
     * @param ownerId The ID of the vehicle owner.
     * @return A list of vehicles belonging to the specified owner.
     */
    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(VEHICLES_FILE);

        for (String line : lines) {
            Vehicle vehicle = lineToVehicle(line);
            if (vehicle != null && vehicle.getOwnerId() == ownerId) {
                vehicles.add(vehicle);
            }
        }

        return vehicles;
    }

    /**
     * Adds a new vehicle record to the file.
     *
     * @param vehicle The Vehicle object to be added.
     * @return true if the vehicle was successfully added, false otherwise.
     */
    public boolean addVehicle(Vehicle vehicle) {
        String vehicleLine = vehicleToLine(vehicle);
        return FileManager.appendLine(VEHICLES_FILE, vehicleLine);
    }

    /**
     * Deletes a vehicle from the file based on its VIN.
     *
     * @param vin The VIN of the vehicle to be deleted.
     * @return true if the vehicle was successfully deleted, false otherwise.
     */
    public boolean deleteVehicle(String vin) {
        List<String> lines = FileManager.readAllLines(VEHICLES_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean deleted = false;

        for (String line : lines) {
            Vehicle vehicle = lineToVehicle(line);
            if (vehicle != null && vin.equals(vehicle.getVin())) {
                deleted = true;
            } else {
                updatedLines.add(line);
            }
        }

        return deleted && FileManager.writeAllLines(VEHICLES_FILE, updatedLines);
    }

    /**
     * Updates an existing vehicle's details.
     * @param vehicle A Vehicle object with updated information.
     * @return true if the update is successful; false otherwise.
     */
    public boolean updateVehicle(Vehicle vehicle) {
        List<String> lines = FileManager.readAllLines(VEHICLES_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (String line : lines) {
            Vehicle existingVehicle = lineToVehicle(line);
            if (existingVehicle != null && existingVehicle.getVin().equals(vehicle.getVin())) {
                // Preserve the original timestamp
                vehicle.setRegisteredTimestamp(existingVehicle.getRegisteredTimestamp());
                updatedLines.add(vehicleToLine(vehicle));
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }

        return updated && FileManager.writeAllLines(VEHICLES_FILE, updatedLines);
    }
}
