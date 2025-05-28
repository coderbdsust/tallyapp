package com.udayan.tallyapp.utils;

import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.util.Base64;

public class Utils {
    public static String generateOTP(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public static String generateActivationCodeKey(int length) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public static String generateSalt(int length) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public static String generateSecretKey(int length) {
        // Create a secure random generator
        SecureRandom secureRandom = new SecureRandom();

        // Create a byte array to hold the random bytes
        byte[] keyBytes = new byte[length];

        // Generate the random bytes
        secureRandom.nextBytes(keyBytes);

        // Encode the key in Base64 format for easier storage and usage
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            throw new IllegalArgumentException("Phone number must be 11 digits");
        }

        // Keep first 3 and last 2 digits, mask the middle 6 digits
        String prefix = phoneNumber.substring(0, 3);
        String suffix = phoneNumber.substring(9); // last 2 digits
        return prefix + "******" + suffix;
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];

        if (local.length() <= 4) {
            return email;
        }

        String firstTwo = local.substring(0, 2);
        String lastTwo = local.substring(local.length() - 2);
        String masked = "*".repeat(local.length() - 4);

        return firstTwo + masked + lastTwo + "@" + domain;
    }

    public static void main(String[] args) {
        System.out.println(Utils.generateActivationCodeKey(50));
        System.out.println(Utils.generateOTP(6));
        System.out.println(generateSecretKey(64));
        System.out.println(generateSalt(32));
        System.out.println(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
