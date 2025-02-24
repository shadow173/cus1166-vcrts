package vcrts.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        Dotenv dotenv = Dotenv.load();

        String databaseName = "production";
        String host = dotenv.get("POSTGRES_HOST");
        String port = dotenv.get("POSTGRES_PORT");
        String username = dotenv.get("POSTGRES_USERNAME");
        String password = dotenv.get("POSTGRES_PASSWORD");

        // Construct the PostgreSQL JDBC URL
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, databaseName);
        System.out.println("Connecting to " + jdbcUrl);

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
