package com.example.deiteu.sercutiry;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;':\",./<>?\\";
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + NUMBERS + SYMBOLS;

    private static SecureRandom secureRandom = new SecureRandom();

    public static String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        // Generate at least one character from each character set
        sb.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        sb.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        sb.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        // Fill the remaining length with random characters from all character sets
        for (int i = 4; i < length; i++) {
            sb.append(ALL_CHARACTERS.charAt(secureRandom.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the password characters randomly
        for (int i = sb.length() - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(j));
            sb.setCharAt(j, temp);
        }

        return sb.toString();
    }
}

