package com.example.crm_gym.services;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("John", "Doe");
        user.setUsername("John.Doe");
        user.setPassword("password123");
    }

    @ParameterizedTest
    @MethodSource("provideAuthenticateUserTestCases")
    void testAuthenticateUser(String username, String password, Optional<User> userOptional, String expectedResult) {
        when(userDAO.findByUsername(username)).thenReturn(userOptional);

        if ("success".equals(expectedResult)) {
            boolean result = userService.authenticateUser(username, password);
            assertTrue(result);
        } else {
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                userService.authenticateUser(username, password);
            });
            assertEquals(expectedResult, exception.getMessage());
        }

        verify(userDAO, times(1)).findByUsername(username);
    }

    static Stream<Arguments> provideAuthenticateUserTestCases() {
        User validUser = new User("John", "Doe");
        validUser.setUsername("John.Doe");
        validUser.setPassword("password123");

        return Stream.of(
                Arguments.of("John.Doe", "password123", Optional.of(validUser), "success"),
                Arguments.of("John.Doe", "wrongPassword", Optional.of(validUser), "Invalid password for username: john.doe"),
                Arguments.of("nonexistent.user", "password123", Optional.empty(), "User not found for username: nonexistent.user"),
                Arguments.of("John.Doe", "password123", Optional.of(validUser), "Error during authentication for username: john.doe")
        );
    }
}
