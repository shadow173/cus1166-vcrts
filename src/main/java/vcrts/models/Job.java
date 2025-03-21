package vcrts.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Job {
    private String jobId;
    private String jobName;
    private int jobOwnerId;
    private String duration;
    private String deadline;
    private String status;
    private String createdTimestamp;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Job(String jobId, String jobName, int jobOwnerId, String duration, String deadline, String status) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobOwnerId = jobOwnerId;
        this.duration = duration;
        this.deadline = deadline;
        this.status = status;
        this.createdTimestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    // Constructor with timestamp parameter for loading from file
    public Job(String jobId, String jobName, int jobOwnerId, String duration, String deadline, String status, String createdTimestamp) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobOwnerId = jobOwnerId;
        this.duration = duration;
        this.deadline = deadline;
        this.status = status;
        this.createdTimestamp = createdTimestamp;
    }

    // Getters and setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public int getJobOwnerId() { return jobOwnerId; }
    public void setJobOwnerId(int jobOwnerId) { this.jobOwnerId = jobOwnerId; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(String createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
}
