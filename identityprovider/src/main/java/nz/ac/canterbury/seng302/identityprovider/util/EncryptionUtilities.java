package nz.ac.canterbury.seng302.identityprovider.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtilities {

    /**
     * Encrypts the given password using the salt and an SHA-256 hash
     * @param salt a random string that is appended to the password before hashing
     * @param password teh password to be encrypted
     * @return The encrypted password
     */
    public static String encryptPassword(String salt, String password) {
        MessageDigest digest;
        byte[] hashedPassword = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            hashedPassword = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unable to find SHA-256 algorithm");
        }

        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public static String createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
