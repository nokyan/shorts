package net.nokyan.shorts.utils;

import java.util.Base64;
import java.util.Random;

public final class RandomUtils {
    /**
     * Generates a random string of the specified length.
     *
     * @param length The desired length of the random string.
     * @return A randomly generated string of the specified length.
     */
    public static String generateRandomString(int length) {
        Random random = new Random();
        // Calculate the number of random bytes needed for the desired string length
        int byteLength = (int) Math.ceil(length * 6.0 / 8.0); // 6 bits per character
        byte[] randomBytes = new byte[byteLength];
        random.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
    }
}
