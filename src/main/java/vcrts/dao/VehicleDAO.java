package vcrts.dao;

import vcrts.db.DatabaseManager;
import vcrts.models.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    /**
     * Retrieves a list of vehicles owned by a specific user.
     *
     * @param ownerId The ID of the vehicle owner.
     * @return A list of vehicles belonging to the specified owner.
     */
    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT owner_id, model, make, year, vin, residency_time FROM vehicles WHERE owner_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ownerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = new Vehicle(
                            rs.getInt("owner_id"),
                            rs.getString("model"),
                            rs.getString("make"),
                            rs.getString("year"),
                            rs.getString("vin"),
                            rs.getString("residency_time")
                    );
                    vehicles.add(v);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return vehicles;
    }

    /**
     * Adds a new vehicle record to the database.
     *
     * @param vehicle The Vehicle object to be added.
     * @return true if the vehicle was successfully added, false otherwise.
     */
    public boolean addVehicle(Vehicle vehicle) {
        String query = "INSERT INTO vehicles (owner_id, model, make, year, vin, residency_time) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters for the vehicle details
            stmt.setInt(1, vehicle.getOwnerId());
            stmt.setString(2, vehicle.getModel());
            stmt.setString(3, vehicle.getMake());
            stmt.setString(4, vehicle.getYear());
            stmt.setString(5, vehicle.getVin());
            stmt.setString(6, vehicle.getResidencyTime());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false; // Return false ifff exception
    }

    /**
     * Deletes a vehicle from the database based on its VIN.
     *
     * @param vin The VIN of the vehicle to be deleted.
     * @return true if the vehicle was successfully deleted, false otherwise.
     */
    public boolean deleteVehicle(String vin) {
        String query = "DELETE FROM vehicles WHERE vin = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, vin);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
