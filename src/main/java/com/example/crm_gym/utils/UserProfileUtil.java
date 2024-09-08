package com.example.crm_gym.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Slf4j
public class UserProfileUtil {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileUtil.class);

    public static String generateUsername(String firstName, String lastName, int suffix) {
        String username = firstName + "." + lastName + (suffix > 0 ? suffix : "");
        return username;
    }

    public static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

}
