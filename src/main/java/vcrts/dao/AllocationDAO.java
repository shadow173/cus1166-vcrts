package vcrts.dao;

import vcrts.db.DatabaseManager;
import vcrts.models.Allocation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllocationDAO {
    public List<Allocation> getAllAllocations() {
        List<Allocation> allocations = new ArrayList<>();
        String query = "SELECT allocation_id, user_id, job_id FROM allocations";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Allocation allocation = new Allocation(
                        rs.getInt("allocation_id"),
                        rs.getString("user_id"),
                        rs.getString("job_id")
                );
                allocations.add(allocation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allocations;
    }

    public boolean addAllocation(Allocation allocation) {
        String insertQuery = "INSERT INTO allocations (user_id, job_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, allocation.getUserId());
            pstmt.setString(2, allocation.getJobId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllocation(int allocationId) {
        String deleteQuery = "DELETE FROM allocations WHERE allocation_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, allocationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
