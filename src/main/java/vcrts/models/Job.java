package vcrts.models;

public class Job {
    private String jobId;
    private String jobName;
    private int jobOwnerId;
    private String duration;
    private String deadline;
    private String status;

    public Job(String jobId, String jobName, int jobOwnerId, String duration, String deadline, String status) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobOwnerId = jobOwnerId;
        this.duration = duration;
        this.deadline = deadline;
        this.status = status;
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
}
