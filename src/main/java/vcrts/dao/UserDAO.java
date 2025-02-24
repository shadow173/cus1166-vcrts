package vcrts.dao;

import io.github.cdimascio.dotenv.Dotenv;
import vcrts.db.DatabaseManager;
import vcrts.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    private Dotenv dotenv = Dotenv.load();
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * Adds a new user to the database.
     * The provided User object's passwordHash field should contain the plain-text password.
     * This method will hash it before storing.
     *
     * @param user A User object with plain-text password in its passwordHash field.
     * @return true if the user is added successfully; false otherwise.
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (full_name, email, role, password) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setString(4, hashedPassword);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    // Now you have the generated user_id
                    user.setUserId(userId);
                    return true;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves a user from the database by user ID.
     *
     * @param userId The user ID.
     * @return A User object if found; null otherwise.
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id, full_name, email, role, password FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getString("password")
                    );
                    user.setUserId(userId);
                    return user;
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving user by ID: " + ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A List of User objects.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, full_name, email, role, password FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("password")
                );
                user.setUserId(rs.getInt("user_id"));
                users.add(user);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving all users: " + ex.getMessage(), ex);
        }
        return users;
    }

    /**
     * Updates an existing user's details (except the password).
     *
     * @param user A User object with updated information.
     * @return true if the update is successful; false otherwise.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getUserId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("User updated: " + user.getUserId());
                return true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating user: " + ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * Updates a user's password.
     *
     * @param userId The user's ID.
     * @param newPlainPassword The new plain-text password.
     * @return true if the update is successful; false otherwise.
     */
    public boolean updatePassword(String userId, String newPlainPassword) {
        String hashedPassword = BCrypt.hashpw(newPlainPassword, dotenv.get("BCRYPT_SALT"));
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("Password updated for user: " + userId);
                return true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating password: " + ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The user's ID.
     * @return true if the deletion is successful; false otherwise.
     */
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("User deleted: " + userId);
                return true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error deleting user: " + ex.getMessage(), ex);
        }
        return false;
    }
    /**
     * Retrieves a list of all users who have the role of "vehicle_owner" from the database
     * @return a list of `User` objects representing vehicle owners. If no users are found or an error occurs,
     *         an empty list is returned.
     */
    public List<User> getAllVehicleOwners() {
        List<User> owners = new ArrayList<>();
        String sql = "SELECT user_id, full_name, email, role, password FROM users WHERE role = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "vehicle_owner");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getString("password")
                    );
                    user.setUserId(rs.getInt("user_id"));
                    owners.add(user);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Error retrieving vehicle owners: " + ex.getMessage(), ex);
        }
        return owners;
    }

    /**
     * Authenticates a user by email and plain-text password.
     * Uses BCrypt to check the password against the stored hash.
     *
     * @param email The user's email.
     * @param plainPassword The plain-text password.
     * @return A User object if authentication is successful; null otherwise.
     */
    public User authenticate(String email, String plainPassword) {
        String sql = "SELECT user_id, full_name, email, role, password FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    try {
                        if (BCrypt.checkpw(plainPassword, storedHash)) {
                            logger.info("User authenticated: " + rs.getString("user_id"));
                            User user = new User(
                                    rs.getString("full_name"),
                                    rs.getString("email"),
                                    rs.getString("role"),
                                    storedHash
                            );
                            user.setUserId(rs.getInt("user_id"));
                            return user;
                        } else {
                            logger.warning("Authentication failed for email: " + email);
                        }
                    } catch (IllegalArgumentException ex) {
                        logger.log(Level.SEVERE, "Invalid salt version for stored hash: " + storedHash, ex);
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error during authentication: " + ex.getMessage(), ex);
        }
        return null;
    }

}
