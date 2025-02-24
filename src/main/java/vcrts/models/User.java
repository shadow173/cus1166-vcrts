package vcrts.models;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String role;
    private String passwordHash; // Hashed password only

    public User(String fullName, String email, String role, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", fullName=" + fullName + ", email=" + email + ", role=" + role + "]";
    }
}
