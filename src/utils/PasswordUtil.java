package utils;
import java.security.*;

/**
 * Utility class for password security operations.
 * This class provides methods for generating salts, hashing passwords, and verifying hashed passwords.
 */
public class PasswordUtil {

    /**
     * Generates a random salt using a secure random generator.
     *
     * @return A hexadecimal string representation of the generated salt.
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * Hashes a password using SHA-256, combined with a salt for added security.
     *
     * @param password The password to hash.
     * @param salt     The salt to use in the hashing process.
     * @return A hexadecimal string representation of the hashed password.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return bytesToHex(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies a password by hashing it with the provided salt and comparing it to the stored hash.
     *
     * @param password The password to verify.
     * @param hash     The expected hash value of the password.
     * @param salt     The salt that was used to hash the password.
     * @return True if the hashed password matches the expected hash; false otherwise.
     */
    public static boolean verifyPassword(String password, String hash, String salt) {
        String hashedPw = hashPassword(password, salt);
        return hashedPw.equals(hash);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes The byte array to convert.
     * @return A hexadecimal string representation of the byte array.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
