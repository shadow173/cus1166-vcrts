package vcrts.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for password hashing and verification.
 * This replaces the BCrypt dependency.
 */
public class PasswordUtil {
    private static final Logger logger = Logger.getLogger(PasswordUtil.class.getName());
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = ":";

    /**
     * Hashes a password with a randomly generated salt.
     *
     * @param password The password to hash.
     * @return A string containing the salt and hashed password, separated by a colon.
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert both salt and hash to Base64 for storage
            String saltStr = Base64.getEncoder().encodeToString(salt);
            String hashStr = Base64.getEncoder().encodeToString(hashedPassword);

            // Return salt + hash
            return saltStr + SEPARATOR + hashStr;
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Error hashing password", e);
            return null;
        }
    }

    /**
     * Verifies a password against a stored hash.
     *
     * @param password The password to verify.
     * @param storedHash The stored hash string (salt + hash).
     * @return true if the password matches, false otherwise.
     */
    public static boolean checkPassword(String password, String storedHash) {
        try {
            // Split the stored hash into salt and hash
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) {
                return false;
            }

            // Decode the salt and hash
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);

            // Hash the password with the retrieved salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] newHashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // Compare the hashes
            if (storedHashBytes.length != newHashBytes.length) {
                return false;
            }

            // Time-constant comparison to prevent timing attacks
            int diff = 0;
            for (int i = 0; i < storedHashBytes.length; i++) {
                diff |= storedHashBytes[i] ^ newHashBytes[i];
            }

            return diff == 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying password", e);
            return false;
        }
    }
}
