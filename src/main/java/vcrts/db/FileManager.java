package vcrts.db;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File-based storage manager that replaces database operations.
 * This class provides methods to read from and write to text files.
 */
public class FileManager {
    private static final Logger logger = Logger.getLogger(FileManager.class.getName());
    private static final String DATA_DIR = "data";

    static {
        // Create data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create data directory", e);
        }
    }

    /**
     * Reads all lines from a file.
     * @param fileName The name of the file.
     * @return A list of strings, where each string is a line in the file.
     */
    public static List<String> readAllLines(String fileName) {
        Path filePath = Paths.get(DATA_DIR, fileName);
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                return new ArrayList<>();
            }
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading file: " + fileName, e);
            return new ArrayList<>();
        }
    }

    /**
     * Writes all lines to a file.
     * @param fileName The name of the file.
     * @param lines The lines to write.
     * @return true if the operation was successful, false otherwise.
     */
    public static boolean writeAllLines(String fileName, List<String> lines) {
        Path filePath = Paths.get(DATA_DIR, fileName);
        try {
            Files.write(filePath, lines);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to file: " + fileName, e);
            return false;
        }
    }

    /**
     * Appends a single line to a file.
     * @param fileName The name of the file.
     * @param line The line to append.
     * @return true if the operation was successful, false otherwise.
     */
    public static boolean appendLine(String fileName, String line) {
        Path filePath = Paths.get(DATA_DIR, fileName);
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.write(filePath, (line + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error appending to file: " + fileName, e);
            return false;
        }
    }

    /**
     * Generates a unique ID.
     * @param fileName The file to check for existing IDs.
     * @param idPrefix A prefix for the ID (optional).
     * @return A unique ID string.
     */
    public static String generateUniqueId(String fileName, String idPrefix) {
        return idPrefix + System.currentTimeMillis();
    }

    /**
     * Generates a unique numeric ID.
     * @param fileName The file to check for existing IDs.
     * @return A unique numeric ID.
     */
    public static int generateUniqueNumericId(String fileName) {
        List<String> lines = readAllLines(fileName);
        int maxId = 0;

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length > 0) {
                    int id = Integer.parseInt(parts[0]);
                    if (id > maxId) {
                        maxId = id;
                    }
                }
            } catch (NumberFormatException e) {
                // Skip lines that don't start with a number
            }
        }

        return maxId + 1;
    }
}
