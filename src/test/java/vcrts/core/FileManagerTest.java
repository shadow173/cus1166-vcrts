package vcrts.core;

import junit.framework.TestCase;
import java.sql.Connection;
import java.sql.SQLException;
import vcrts.db.FileManager;

public class FileManagerTest extends TestCase {
    public void testConnection() {
        try {
            Connection conn = FileManager.getConnection();
            assertNotNull("Database connection should not be null", conn);
            conn.close();
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
}
