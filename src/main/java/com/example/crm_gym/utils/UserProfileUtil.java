package com.example.crm_gym.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class UserProfileUtil {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileUtil.class);

    public static String generateUsername(String firstName, String lastName, int suffix) {
        logger.info("Entering generateUsername() with firstName: {}, lastName: {}, suffix: {}", firstName, lastName, suffix);

        String username = firstName + "." + lastName + (suffix > 0 ? suffix : "");
        logger.info("Generated username: {}", username);
        return username;
    }

    public static String generatePassword() {
        logger.info("Entering generatePassword() method");

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        logger.info("Generated random password: {}", password.toString());
        return password.toString();
    }

}
