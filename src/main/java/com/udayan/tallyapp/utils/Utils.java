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

    public static String convertToTitleCase(HttpStatus status) {
        String input = status.name();
        // Split the string by underscores
        String[] parts = input.split("_");

        // Convert each part to title case and join them with spaces
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                // Convert the first letter to uppercase and the rest to lowercase
                result.append(part.substring(0, 1).toUpperCase())
                        .append(part.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        // Trim any trailing space and return
        return result.toString().trim();
    }



    public static void main(String[] args) {
        System.out.println(Utils.generateActivationCodeKey(50));
        System.out.println(Utils.generateOTP(6));
        System.out.println(generateSecretKey(64));
        System.out.println(generateSalt(32));
        System.out.println(convertToTitleCase(HttpStatus.MULTI_STATUS));
        System.out.println(convertToTitleCase(HttpStatus.INTERNAL_SERVER_ERROR));
        System.out.println(convertToTitleCase(HttpStatus.BAD_REQUEST));
    }
}
