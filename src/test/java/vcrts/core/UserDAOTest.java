package vcrts.core;

import junit.framework.TestCase;
import vcrts.dao.UserDAO;
import vcrts.models.User;

public class UserDAOTest extends TestCase {
    private UserDAO userDAO;

    protected void setUp() throws Exception {
        super.setUp();
        userDAO = new UserDAO();
    }

    public void testAddAuthenticateAndDeleteUser() {
        // Create test user data.


        // need to fix this with userids and stuff
        String userId = "testUse69";
        String fullName = "Test User";
        String email = "testuser69@stjohns.edu";
        String role = "vehicle_owner";
        String plainPassword = "testpassword";

        // Create  new User
        // The User constructor expects args - fullName, email, role, and passwordHash.
        User user = new User( fullName, email, role, plainPassword);

        // Add the user to the database.
        boolean added = userDAO.addUser(user);
        assertTrue("User should be added successfully", added);

        // Authenticate the user.
        User authUser = userDAO.authenticate(email, plainPassword);
        assertNotNull("User should be authenticated successfully", authUser);
        assertEquals("User ID should match", userId, authUser.getUserId());

        // Delete the user.
        boolean deleted = userDAO.deleteUser(userId);
        assertTrue("User should be deleted successfully", deleted);
    }
}
