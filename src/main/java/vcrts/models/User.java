package vcrts.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private List<String> roles;
    private String passwordHash; // Hashed password only
    private String currentRole; // Track the currently active role

    public User(String fullName, String email, String roles, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        setRoles(roles);
        this.passwordHash = passwordHash;

        // Default to first role
        if (this.roles != null && !this.roles.isEmpty()) {
            this.currentRole = this.roles.get(0);
        }
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(String rolesStr) {
        // Parse comma-separated roles
        if (rolesStr != null && !rolesStr.isEmpty()) {
            this.roles = new ArrayList<>(Arrays.asList(rolesStr.split(",")));
        } else {
            this.roles = new ArrayList<>();
        }
    }

    public String getRolesAsString() {
        if (roles == null || roles.isEmpty()) {
            return "";
        }
        return String.join(",", roles);
    }

    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public String getRole() {
        return currentRole;
    }

    public void setRole(String role) {
        if (roles != null && roles.contains(role)) {
            this.currentRole = role;
        } else if (role != null && !role.isEmpty()) {
            // If role is not in the list but is provided, add it
            addRole(role);
            this.currentRole = role;
        }
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        if (roles != null && roles.contains(currentRole)) {
            this.currentRole = currentRole;
        }
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", fullName=" + fullName + ", email=" + email + ", roles=" + roles + ", currentRole=" + currentRole + "]";
    }
}
