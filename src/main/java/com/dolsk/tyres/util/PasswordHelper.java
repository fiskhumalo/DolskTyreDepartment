package com.dolsk.tyres.util;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHelper {
    public static void main(String[] args) {
        // Create BCrypt encoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Password you want to hash
        String rawPassword = "Dolskov7";

        // Generate BCrypt hash
        String hashedPassword = encoder.encode(rawPassword);

        // Print the hash to console
        System.out.println("Raw password: " + rawPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
    }
}
