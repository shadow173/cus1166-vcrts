package vcrts.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class DatabaseManager {

    public static Connection getConnection() throws SQLException {
        Dotenv dotenv = Dotenv.load();



        String databaseName = "production";
        String instanceConnectionName = dotenv.get("SQL_INSTANCE_NAME");
        String username = dotenv.get("SQL_USERNAME");
        String password = dotenv.get("SQL_PASSWORD");

        // Construct the JDBC URL using environment variables.
        String jdbcUrl = String.format(
                "jdbc:mysql:///%s?cloudSqlInstance=%s&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName, instanceConnectionName
        );
        System.out.println("Connecting to " + jdbcUrl);

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
