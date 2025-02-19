package vcrts.db;

public class Job {
    private String jobId;
    private String status;
    private String duration;
    private String deadline;

    public Job(String jobId, String status, String duration, String deadline, double cost) {
        this.jobId = jobId;
        this.status = status;
        this.duration = duration;
        this.deadline = deadline;
    }

    public String getJobId() { return jobId; }
    public String getStatus() { return status; }
    public String getDuration() { return duration; }
    public String getDeadline() { return deadline; }

    // Converts a Job object to an array for table display
    public Object[] toArray() {
        return new Object[] { jobId, status, duration, deadline};
    }
}
