package vcrts.dao;

import vcrts.db.DatabaseManager;
import vcrts.models.Job;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobDAO {
    private static final Logger logger = Logger.getLogger(JobDAO.class.getName());

    /**
     * Retrieves all jobs

     * @return a list of {@code Job} objects representing all jobs in the database.
     *         Returns an empty list if no jobs are found or an error occurs.
     */
    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String query = "SELECT job_id, job_name, job_owner_id, duration, deadline, status FROM jobs";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Job job = new Job(
                        rs.getString("job_id"),
                        rs.getString("job_name"),
                        rs.getInt("job_owner_id"),
                        rs.getString("duration"),
                        rs.getString("deadline"),
                        rs.getString("status")
                );
                jobs.add(job);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all jobs: " + e.getMessage(), e);
        }
        return jobs;
    }
    /**
     * Adds a new job to the database.

     * @param job the {Job} object containing job details.
     * @return  true if the job was successfully added, otherwise false
     */
    public boolean addJob(Job job) {
        String insertQuery = "INSERT INTO jobs (job_id, job_name, job_owner_id, duration, deadline, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, job.getJobId());
            pstmt.setString(2, job.getJobName());
            pstmt.setInt(3, job.getJobOwnerId());
            pstmt.setString(4, job.getDuration());
            pstmt.setString(5, job.getDeadline());
            pstmt.setString(6, job.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding job: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Deletes a job from the database.
     * @param jobId the unique identifier of the job to be deleted.
     * @return true if the job was successfully deleted, otherwise false
     */
    public boolean deleteJob(String jobId) {
        String deleteQuery = "DELETE FROM jobs WHERE job_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, jobId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting job: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Retrieves jobs for a given client (job owner) filtered by status.
     * If status equals "All" (case-insensitive), all jobs for that client are returned.
     *
     * @param clientId The job owner's ID.
     * @param status The status filter ("All", "Queued", "In Progress", "Completed", etc.).
     * @return A list of Job objects
     */
    public List<Job> getJobsByClient(int clientId, String status) {
        List<Job> jobs = new ArrayList<>();
        String query;
        if ("All".equalsIgnoreCase(status)) {
            query = "SELECT job_id, job_name, job_owner_id, duration, deadline, status FROM jobs WHERE job_owner_id = ?";
        } else {
            query = "SELECT job_id, job_name, job_owner_id, duration, deadline, status FROM jobs WHERE job_owner_id = ? AND status = ?";
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, clientId);
            if (!"All".equalsIgnoreCase(status)) {
                pstmt.setString(2, status);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Job job = new Job(
                            rs.getString("job_id"),
                            rs.getString("job_name"),
                            rs.getInt("job_owner_id"),
                            rs.getString("duration"),
                            rs.getString("deadline"),
                            rs.getString("status")
                    );
                    jobs.add(job);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving jobs for client " + clientId + ": " + e.getMessage(), e);
        }
        return jobs;
    }
}
