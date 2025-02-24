package vcrts.models;

public class Allocation {
    private int allocationId; // auto-generated
    private String userId;
    private String jobId;

    // Constructor without allocationId (for inserting new records)
    public Allocation(String userId, String jobId) {
        this.userId = userId;
        this.jobId = jobId;
    }

    // Constructor with allocationId (for records read from the DB)
    public Allocation(int allocationId, String userId, String jobId) {
        this.allocationId = allocationId;
        this.userId = userId;
        this.jobId = jobId;
    }

    // Getters and setters
    public int getAllocationId() { return allocationId; }
    public void setAllocationId(int allocationId) { this.allocationId = allocationId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
}
