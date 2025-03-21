package vcrts.dao;

import vcrts.db.FileManager;
import vcrts.models.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobDAO {
    private static final Logger logger = Logger.getLogger(JobDAO.class.getName());
    private static final String JOBS_FILE = "jobs.txt";
    private static final String DELIMITER = "\\|";
    private static final String SEPARATOR = "|";

    /**
     * Converts a Job object to a line of text for storage.
     */
    private String jobToLine(Job job) {
        return job.getJobId() + SEPARATOR +
                job.getJobName() + SEPARATOR +
                job.getJobOwnerId() + SEPARATOR +
                job.getDuration() + SEPARATOR +
                job.getDeadline() + SEPARATOR +
                job.getStatus() + SEPARATOR +
                job.getCreatedTimestamp();
    }

    /**
     * Converts a line of text to a Job object.
     */
    private Job lineToJob(String line) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 6) {
            logger.warning("Invalid job data format: " + line);
            return null;
        }

        try {
            // Check if the timestamp is included in the line
            String timestamp = parts.length >= 7 ? parts[6] : Job.getCurrentTimestamp();

            return new Job(
                    parts[0],                       // jobId
                    parts[1],                       // jobName
                    Integer.parseInt(parts[2]),     // jobOwnerId
                    parts[3],                       // duration
                    parts[4],                       // deadline
                    parts[5],                       // status
                    timestamp                       // createdTimestamp
            );
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing job owner ID: " + parts[2], e);
            return null;
        }
    }

    /**
     * Retrieves all jobs - SHOULD ONLY BE CALLED BY CLOUD CONTROLLER
     * @return a list of {@code Job} objects representing all jobs in the file.
     */
    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(JOBS_FILE);

        for (String line : lines) {
            Job job = lineToJob(line);
            if (job != null) {
                jobs.add(job);
            }
        }

        return jobs;
    }

    /**
     * Adds a new job to the file.
     * @param job the {Job} object containing job details.
     * @return true if the job was successfully added, otherwise false
     */
    public boolean addJob(Job job) {
        // Jobs already have IDs set by the application
        String jobLine = jobToLine(job);
        return FileManager.appendLine(JOBS_FILE, jobLine);
    }

    /**
     * Deletes a job from the file.
     * @param jobId the unique identifier of the job to be deleted.
     * @return true if the job was successfully deleted, otherwise false
     */
    public boolean deleteJob(String jobId) {
        List<String> lines = FileManager.readAllLines(JOBS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean deleted = false;

        for (String line : lines) {
            Job job = lineToJob(line);
            if (job != null && jobId.equals(job.getJobId())) {
                deleted = true;
            } else {
                updatedLines.add(line);
            }
        }

        return deleted && FileManager.writeAllLines(JOBS_FILE, updatedLines);
    }

    /**
     * Retrieves jobs for a given client (job owner) filtered by status.
     * If status equals "All" (case-insensitive), all jobs for the client are returned.
     * This method ensures clients can only see their own jobs.
     *
     * @param clientId The job owner's ID.
     * @param status The status filter ("All", "Queued", "In Progress", "Completed", etc.).
     * @return A list of Job objects
     */
    public List<Job> getJobsByClient(int clientId, String status) {
        List<Job> jobs = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(JOBS_FILE);

        for (String line : lines) {
            Job job = lineToJob(line);

            // Only return jobs that belong to this client
            if (job != null && job.getJobOwnerId() == clientId) {
                if ("All".equalsIgnoreCase(status) || status.equalsIgnoreCase(job.getStatus())) {
                    jobs.add(job);
                }
            }
        }

        return jobs;
    }

    /**
     * Updates an existing job's details.
     * @param job A Job object with updated information.
     * @return true if the update is successful; false otherwise.
     */
    public boolean updateJob(Job job) {
        List<String> lines = FileManager.readAllLines(JOBS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (String line : lines) {
            Job existingJob = lineToJob(line);
            if (existingJob != null && existingJob.getJobId().equals(job.getJobId())) {
                // Preserve the original timestamp
                job.setCreatedTimestamp(existingJob.getCreatedTimestamp());
                updatedLines.add(jobToLine(job));
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }

        return updated && FileManager.writeAllLines(JOBS_FILE, updatedLines);
    }
}
