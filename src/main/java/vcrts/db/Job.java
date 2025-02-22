package vcrts.db;

public class Job {
	private String clientId;
    private String jobId;
    private String status;
    private String duration;
    private String deadline;

    public Job(String clientId, String jobId, String status, String duration, String deadline) {
        this.clientId = clientId;
        this.jobId = jobId;
        this.status = status;
        this.duration = duration;
        this.deadline = deadline;
    }

    public String getclientId() { return clientId; }
    public String getJobId() { return jobId; }
    public String getStatus() { return status; }
    public String getDuration() { return duration; }
    public String getDeadline() { return deadline; }

    // Converts a Job object to an array for table display
    public Object[] toArray() {
        return new Object[] { jobId, status, duration, deadline};
    }
}
