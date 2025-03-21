package vcrts.dao;

import vcrts.db.FileManager;
import vcrts.models.User;
import vcrts.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    private static final String USERS_FILE = "users.txt";
    private static final String DELIMITER = "\\|";
    private static final String SEPARATOR = "|";

    /**
     * Converts a User object to a line of text for storage.
     */
    private String userToLine(User user) {
        return user.getUserId() + SEPARATOR +
                user.getFullName() + SEPARATOR +
                user.getEmail() + SEPARATOR +
                user.getRolesAsString() + SEPARATOR +
                user.getPasswordHash();
    }

    /**
     * Converts a line of text to a User object.
     */
    private User lineToUser(String line) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 5) {
            logger.warning("Invalid user data format: " + line);
            return null;
        }

        User user = new User(
                parts[1], // fullName
                parts[2], // email
                parts[3], // roles
                parts[4]  // passwordHash
        );
        user.setUserId(Integer.parseInt(parts[0]));
        return user;
    }

    /**
     * Adds a new user to the file.
     * The provided User object's passwordHash field should contain the plain-text password.
     * This method will hash it before storing.
     *
     * @param user A User object with plain-text password in its passwordHash field.
     * @return true if the user is added successfully; false otherwise.
     */
    public boolean addUser(User user) {
        String hashedPassword = PasswordUtil.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        // Generate a unique ID for the new user
        int userId = FileManager.generateUniqueNumericId(USERS_FILE);
        user.setUserId(userId);

        String userLine = userToLine(user);
        return FileManager.appendLine(USERS_FILE, userLine);
    }

    /**
     * Retrieves a user from the file by user ID.
     *
     * @param userId The user ID.
     * @return A User object if found; null otherwise.
     */
    public User getUserById(int userId) {
        List<String> lines = FileManager.readAllLines(USERS_FILE);
        for (String line : lines) {
            User user = lineToUser(line);
            if (user != null && user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves all users from the file.
     *
     * @return A List of User objects.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(USERS_FILE);

        for (String line : lines) {
            User user = lineToUser(line);
            if (user != null) {
                users.add(user);
            }
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
        List<String> lines = FileManager.readAllLines(USERS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (String line : lines) {
            User existingUser = lineToUser(line);
            if (existingUser != null && existingUser.getUserId() == user.getUserId()) {
                // Keep the existing password hash
                user.setPasswordHash(existingUser.getPasswordHash());
                updatedLines.add(userToLine(user));
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }

        return updated && FileManager.writeAllLines(USERS_FILE, updatedLines);
    }

    /**
     * Updates a user's password.
     *
     * @param userId The user's ID as a string.
     * @param newPlainPassword The new plain-text password.
     * @return true if the update is successful; false otherwise.
     */
    public boolean updatePassword(String userId, String newPlainPassword) {
        int id;
        try {
            id = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid user ID format: " + userId);
            return false;
        }

        List<String> lines = FileManager.readAllLines(USERS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        String hashedPassword = PasswordUtil.hashPassword(newPlainPassword);

        for (String line : lines) {
            User user = lineToUser(line);
            if (user != null && user.getUserId() == id) {
                user.setPasswordHash(hashedPassword);
                updatedLines.add(userToLine(user));
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }

        return updated && FileManager.writeAllLines(USERS_FILE, updatedLines);
    }

    /**
     * Deletes a user from the file.
     *
     * @param userId The user's ID as a string.
     * @return true if the deletion is successful; false otherwise.
     */
    public boolean deleteUser(String userId) {
        int id;
        try {
            id = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid user ID format: " + userId);
            return false;
        }

        List<String> lines = FileManager.readAllLines(USERS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean deleted = false;

        for (String line : lines) {
            User user = lineToUser(line);
            if (user != null && user.getUserId() == id) {
                deleted = true;
            } else {
                updatedLines.add(line);
            }
        }

        return deleted && FileManager.writeAllLines(USERS_FILE, updatedLines);
    }

    /**
     * Retrieves a list of all users who have the role of "vehicle_owner" from the file.
     * @return a list of `User` objects representing vehicle owners.
     */
    public List<User> getAllVehicleOwners() {
        List<User> owners = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(USERS_FILE);

        for (String line : lines) {
            User user = lineToUser(line);
            if (user != null && user.hasRole("vehicle_owner")) {
                owners.add(user);
            }
        }

        return owners;
    }

    /**
     * Authenticates a user by email and plain-text password.
     * Uses PasswordUtil to check the password against the stored hash.
     *
     * @param email The user's email.
     * @param plainPassword The plain-text password.
     * @return A User object if authentication is successful; null otherwise.
     */
    public User authenticate(String email, String plainPassword) {
        List<String> lines = FileManager.readAllLines(USERS_FILE);

        for (String line : lines) {
            User user = lineToUser(line);
            if (user != null && email.equals(user.getEmail())) {
                String storedHash = user.getPasswordHash();
                try {
                    if (PasswordUtil.checkPassword(plainPassword, storedHash)) {
                        logger.info("User authenticated: " + user.getUserId());
                        return user;
                    } else {
                        logger.warning("Authentication failed for email: " + email);
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Invalid stored hash for user: " + user.getUserId(), ex);
                }
                break;  // Exit once we've found the user with this email
            }
        }

        return null;
    }
}
