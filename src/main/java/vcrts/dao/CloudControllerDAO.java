package vcrts.dao;

import vcrts.db.FileManager;
import vcrts.models.Job;
import vcrts.models.Vehicle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Cloud Controller operations.
 * Implements job scheduling and completion time calculation logic.
 */
public class CloudControllerDAO {
    private static final Logger logger = Logger.getLogger(CloudControllerDAO.class.getName());
    private static final String SCHEDULE_FILE = "job_schedule.txt";
    private static final String JOB_STATE_FILE = "job_states.txt";
    private static final String DELIMITER = "\\|";
    private static final String SEPARATOR = "|";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Job states
    public static final String STATE_QUEUED = "Queued";
    public static final String STATE_PROGRESS = "In Progress";
    public static final String STATE_COMPLETED = "Completed";

    private JobDAO jobDAO;
    private VehicleDAO vehicleDAO;

    // HashMap to store job duration (in minutes) for each job ID
    private Map<String, Long> jobDurations = new HashMap<>();

    public CloudControllerDAO() {
        this.jobDAO = new JobDAO();
        this.vehicleDAO = new VehicleDAO();
    }

    /**
     * Calculates job completion times using FIFO (First In First Out) scheduling
     * and updates job states.
     * @return A map of job IDs to their calculated completion times.
     */
    public Map<String, String> calculateCompletionTimes() {
        // Get all jobs and sort them by creation timestamp (FIFO)
        List<Job> allJobs = jobDAO.getAllJobs();

        // Sort by creation timestamp (FIFO)
        allJobs.sort(Comparator.comparing(Job::getCreatedTimestamp));

        // Maps to store results
        Map<String, String> completionTimes = new LinkedHashMap<>();
        Map<String, String> jobStates = new LinkedHashMap<>();

        // Clear job durations map
        jobDurations.clear();

        // Track current time for completion calculation
        LocalDateTime currentTime = LocalDateTime.now();

        // Running total for completion time in minutes (for relative time calculation)
        long totalMinutes = 0;

        // Determine which job is currently in progress (if any)
        Job inProgressJob = null;
        for (Job job : allJobs) {
            if (STATE_PROGRESS.equals(job.getStatus())) {
                inProgressJob = job;
                break;
            }
        }

        // Process all jobs
        for (Job job : allJobs) {
            // Skip already completed jobs but include them in results
            if (STATE_COMPLETED.equals(job.getStatus())) {
                // For completed jobs, use their existing completion time if available
                String existingCompletionTime = getJobCompletionTime(job.getJobId());
                if (existingCompletionTime != null) {
                    completionTimes.put(job.getJobId(), existingCompletionTime);
                } else {
                    // If no completion time record exists, use a placeholder
                    completionTimes.put(job.getJobId(), "Already completed");
                }
                jobStates.put(job.getJobId(), STATE_COMPLETED);
                continue;
            }

            // Calculate job duration
            Duration jobDuration = parseJobDuration(job);

            // Store job duration in minutes for reporting
            long durationMinutes = jobDuration.toMinutes();
            jobDurations.put(job.getJobId(), durationMinutes);

            // Add this job's duration to the running total
            totalMinutes += durationMinutes;

            // If no job is in progress, set the first non-completed job to "In Progress"
            if (inProgressJob == null && !STATE_COMPLETED.equals(job.getStatus())) {
                inProgressJob = job;

                // Update job status to "In Progress"
                job.setStatus(STATE_PROGRESS);
                jobDAO.updateJob(job);
                jobStates.put(job.getJobId(), STATE_PROGRESS);
            }
            // If this is not the in-progress job and not completed, set to "Queued"
            else if (!job.equals(inProgressJob) && !STATE_COMPLETED.equals(job.getStatus())) {
                job.setStatus(STATE_QUEUED);
                jobDAO.updateJob(job);
                jobStates.put(job.getJobId(), STATE_QUEUED);
            }

            // Calculate completion time
            LocalDateTime completionTime = currentTime.plus(jobDuration);
            String completionTimeStr = completionTime.format(TIMESTAMP_FORMATTER);
            completionTimes.put(job.getJobId(), completionTimeStr);

            // Update current time for next job
            currentTime = completionTime;
        }

        // Save the schedule and job states to files
        saveSchedule(completionTimes);
        saveJobStates(jobStates);

        return completionTimes;
    }

    /**
     * Gets job duration in a human-readable format (e.g., "2 hours 30 minutes")
     * @param jobId The job ID
     * @return Human-readable duration string
     */
    public String getJobDurationFormatted(String jobId) {
        Long minutes = jobDurations.get(jobId);
        if (minutes == null) {
            return "Unknown";
        }

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours > 0) {
            return String.format("%d hour%s %d minute%s",
                    hours, hours != 1 ? "s" : "",
                    remainingMinutes, remainingMinutes != 1 ? "s" : "");
        } else {
            return String.format("%d minute%s", remainingMinutes, remainingMinutes != 1 ? "s" : "");
        }
    }

    /**
     * Parse job duration from string format to Duration
     * @param job The job to parse duration from
     * @return Duration object representing the job's processing time
     */
    private Duration parseJobDuration(Job job) {
        // Parse job duration (assuming format: HH:mm:ss)
        LocalTime durationTime;
        try {
            durationTime = LocalTime.parse(job.getDuration(), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Invalid duration format for job " + job.getJobId() + ": " + job.getDuration());
            // Default to 1 hour if parsing fails
            durationTime = LocalTime.of(1, 0, 0);
        }

        // Convert LocalTime to Duration (hours, minutes, seconds)
        return Duration.ofHours(durationTime.getHour())
                .plusMinutes(durationTime.getMinute())
                .plusSeconds(durationTime.getSecond());
    }

    /**
     * Marks the currently in-progress job as completed and advances the queue.
     * @return The ID of the newly in-progress job, or null if no jobs are available.
     */
    public String advanceJobQueue() {
        List<Job> allJobs = jobDAO.getAllJobs();
        allJobs.sort(Comparator.comparing(Job::getCreatedTimestamp)); // FIFO order

        Job inProgressJob = null;
        List<Job> queuedJobs = new ArrayList<>();

        // Find the current in-progress job and all queued jobs
        for (Job job : allJobs) {
            if (STATE_PROGRESS.equals(job.getStatus())) {
                inProgressJob = job;
            } else if (STATE_QUEUED.equals(job.getStatus())) {
                queuedJobs.add(job);
            }
        }

        // If there's a job in progress, mark it as completed
        if (inProgressJob != null) {
            inProgressJob.setStatus(STATE_COMPLETED);
            jobDAO.updateJob(inProgressJob);

            // If there are queued jobs, move the next one to in-progress
            if (!queuedJobs.isEmpty()) {
                Job nextJob = queuedJobs.get(0);
                nextJob.setStatus(STATE_PROGRESS);
                jobDAO.updateJob(nextJob);

                // Recalculate completion times
                calculateCompletionTimes();

                return nextJob.getJobId();
            }
        }
        // If no job was in progress but there are queued jobs, move the first one to in-progress
        else if (!queuedJobs.isEmpty()) {
            Job nextJob = queuedJobs.get(0);
            nextJob.setStatus(STATE_PROGRESS);
            jobDAO.updateJob(nextJob);

            // Recalculate completion times
            calculateCompletionTimes();

            return nextJob.getJobId();
        }

        // Recalculate completion times to ensure consistency
        calculateCompletionTimes();

        return null; // No job was advanced
    }

    /**
     * Gets a summary of the current job queue status.
     * @return A map with count of jobs in each state.
     */
    public Map<String, Integer> getJobQueueSummary() {
        List<Job> allJobs = jobDAO.getAllJobs();
        Map<String, Integer> summary = new HashMap<>();

        summary.put(STATE_QUEUED, 0);
        summary.put(STATE_PROGRESS, 0);
        summary.put(STATE_COMPLETED, 0);

        for (Job job : allJobs) {
            String state = job.getStatus();
            summary.put(state, summary.getOrDefault(state, 0) + 1);
        }

        return summary;
    }

    /**
     * Saves the calculated job schedule to a file.
     * @param completionTimes Map of job IDs to completion times.
     * @return true if saved successfully, false otherwise.
     */
    private boolean saveSchedule(Map<String, String> completionTimes) {
        List<String> lines = new ArrayList<>();

        for (Map.Entry<String, String> entry : completionTimes.entrySet()) {
            String line = entry.getKey() + SEPARATOR + entry.getValue();
            lines.add(line);
        }

        return FileManager.writeAllLines(SCHEDULE_FILE, lines);
    }

    /**
     * Saves the job states to a file.
     * @param jobStates Map of job IDs to states.
     * @return true if saved successfully, false otherwise.
     */
    private boolean saveJobStates(Map<String, String> jobStates) {
        List<String> lines = new ArrayList<>();

        for (Map.Entry<String, String> entry : jobStates.entrySet()) {
            String line = entry.getKey() + SEPARATOR + entry.getValue();
            lines.add(line);
        }

        return FileManager.writeAllLines(JOB_STATE_FILE, lines);
    }

    /**
     * Loads the job schedule from file.
     * @return Map of job IDs to completion times.
     */
    public Map<String, String> loadSchedule() {
        Map<String, String> completionTimes = new LinkedHashMap<>();
        List<String> lines = FileManager.readAllLines(SCHEDULE_FILE);

        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            if (parts.length >= 2) {
                completionTimes.put(parts[0], parts[1]);
            }
        }

        return completionTimes;
    }

    /**
     * Gets a specific job's completion time.
     * @param jobId The ID of the job.
     * @return The completion time as a string, or null if not found.
     */
    public String getJobCompletionTime(String jobId) {
        Map<String, String> schedule = loadSchedule();
        return schedule.get(jobId);
    }

    /**
     * Generate a formatted text output showing completion time for all jobs in order.
     * @return Formatted output string showing job scheduling results.
     */
    public String generateSchedulingOutput() {
        List<Job> allJobs = jobDAO.getAllJobs();
        allJobs.sort(Comparator.comparing(Job::getCreatedTimestamp)); // FIFO order

        Map<String, String> completionTimes = loadSchedule();

        StringBuilder output = new StringBuilder();
        output.append("Job Scheduling Results (FIFO)\n");
        output.append("============================\n");
        output.append("Job ID | Duration | Time to Complete | Completion Time | Status\n");
        output.append("--------------------------------------------------------------\n");

        // Running total of time to complete all jobs
        long runningTotalMinutes = 0;

        for (Job job : allJobs) {
            // Get the job's duration in minutes
            Duration jobDuration = parseJobDuration(job);
            long durationMinutes = jobDuration.toMinutes();

            // Add to running total (for FIFO calculation)
            runningTotalMinutes += durationMinutes;

            // Format the running total as hours and minutes
            long totalHours = runningTotalMinutes / 60;
            long totalMinutes = runningTotalMinutes % 60;
            String timeToComplete = totalHours > 0 ?
                    String.format("%dh %dm", totalHours, totalMinutes) :
                    String.format("%dm", totalMinutes);

            // Get formatted job duration (HH:MM:SS)
            String jobDurationFormatted = job.getDuration();

            // Get completion time from the schedule
            String completionTime = completionTimes.getOrDefault(job.getJobId(), "Not calculated");

            output.append(String.format("%-7s| %-9s| %-16s| %-15s| %s\n",
                    job.getJobId(),
                    jobDurationFormatted,
                    timeToComplete,
                    completionTime,
                    job.getStatus()));
        }

        return output.toString();
    }

    /**
     * Assigns available vehicles to jobs based on FIFO order.
     * @return The number of assignments made.
     */
    public int assignVehiclesToJobs() {
        List<Job> jobs = jobDAO.getAllJobs();
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();

        // Filter for unassigned jobs and sort by creation time (FIFO)
        List<Job> unassignedJobs = new ArrayList<>();
        for (Job job : jobs) {
            if (STATE_QUEUED.equalsIgnoreCase(job.getStatus())) {
                unassignedJobs.add(job);
            }
        }
        unassignedJobs.sort(Comparator.comparing(Job::getCreatedTimestamp));

        // Filter for available vehicles
        List<Vehicle> availableVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            // In a real system, we would check vehicle availability here
            availableVehicles.add(vehicle);
        }

        // Assign vehicles to jobs (simple implementation)
        int assignmentCount = 0;
        for (Job job : unassignedJobs) {
            if (assignmentCount < availableVehicles.size()) {
                // Update job status to "In Progress"
                job.setStatus(STATE_PROGRESS);
                jobDAO.updateJob(job);
                assignmentCount++;
            } else {
                break; // No more available vehicles
            }
        }

        // Recalculate completion times after assignments
        calculateCompletionTimes();

        return assignmentCount;
    }
}
