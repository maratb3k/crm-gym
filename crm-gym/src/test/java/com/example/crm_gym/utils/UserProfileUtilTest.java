package com.example.crm_gym.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserProfileUtilTest {

    @Test
    void testGenerateUsernameWithoutSuffix() {
        String firstName = "John";
        String lastName = "Doe";
        int suffix = 0;
        String username = UserProfileUtil.generateUsername(firstName, lastName, suffix);

        assertEquals("John.Doe", username);
    }

    @Test
    void testGenerateUsernameWithSuffix() {
        String firstName = "Jane";
        String lastName = "Smith";
        int suffix = 2;
        String username = UserProfileUtil.generateUsername(firstName, lastName, suffix);

        assertEquals("Jane.Smith2", username);
    }

    @Test
    void testGeneratePasswordLength() {
        String password = UserProfileUtil.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void testGeneratePasswordCharacterSet() {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String password = UserProfileUtil.generatePassword();

        assertTrue(password.chars().allMatch(c -> validChars.indexOf(c) >= 0),
                "Password contains invalid characters.");
    }
}