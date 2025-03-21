package vcrts.util;

import vcrts.db.FileManager;
import vcrts.models.User;
import vcrts.dao.UserDAO;

/**
 * Utility class to create a cloud controller user.
 * This can be run once to set up the cloud controller account.
 */
public class CloudControllerSetup {

    public static void main(String[] args) {
        createCloudControllerUser("admin", "admin@vcrts.com", "admin");
    }
    /**
     * Creates a cloud controller user in the system.
     *
     * @param fullName The full name of the cloud controller
     * @param email The email of the cloud controller
     * @param password The password for the account
     * @return true if creation was successful, false otherwise
     */
    public static boolean createCloudControllerUser(String fullName, String email, String password) {
        UserDAO userDAO = new UserDAO();

        // Create user with cloud_controller role
        User cloudController = new User(fullName, email, "cloud_controller", password);

        // Add the user to the database
        boolean success = userDAO.addUser(cloudController);

        if (success) {
            System.out.println("Cloud Controller created successfully!");
            System.out.println("Username: " + email);
            System.out.println("Password: " + password);
        } else {
            System.out.println("Failed to create Cloud Controller user.");
        }

        return success;
    }
}
