package com.example.crm_gym.services;

import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.User;
import com.example.crm_gym.utils.UserProfileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDAO);
    }

    @Test
    void testCreate() {
        String generatedPassword = "password123";
        String generatedUsername = "johndoe";

        when(userDAO.save(any(User.class))).thenReturn(true);
        when(userDAO.findAll()).thenReturn(Optional.of(Collections.emptyList()));
        mockStatic(UserProfileUtil.class);
        when(UserProfileUtil.generatePassword()).thenReturn(generatedPassword);
        when(UserProfileUtil.generateUsername("John", "Doe", 0)).thenReturn(generatedUsername);

        boolean successResult = userService.create("John", "Doe");
        assertTrue(successResult);
        verify(userDAO, times(1)).save(any(User.class));
        reset(userDAO);
        when(userDAO.save(any(User.class))).thenThrow(new DaoException("Error saving user"));
        when(userDAO.findAll()).thenReturn(Optional.of(Collections.emptyList()));
        boolean failureResult = userService.create("John", "Doe");
        assertFalse(failureResult);
        verify(userDAO, times(1)).save(any(User.class));
    }

    @Test
    void testUpdate() {
        User user = new User();

        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(true);
        when(userDAO.update(anyLong(), any(User.class))).thenReturn(true);

        boolean successResult = userService.update("johndoe", "password123", 1L, user);
        assertTrue(successResult);
        verify(userDAO, times(1)).update(anyLong(), any(User.class));
        reset(userDAO);
        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(false);

        boolean failureResult = userService.update("johndoe", "wrongpassword", 1L, user);
        assertFalse(failureResult);
        verify(userDAO, times(0)).update(anyLong(), any(User.class));
    }

    @Test
    void testUpdatePassword() {
        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(true);
        when(userDAO.updatePassword(anyLong(), anyString())).thenReturn(true);

        boolean successResult = userService.updatePassword("johndoe", "password123", 1L, "newpassword");
        assertTrue(successResult);
        verify(userDAO, times(1)).updatePassword(anyLong(), anyString());
        reset(userDAO);
        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(false);
        boolean failureResult = userService.updatePassword("johndoe", "wrongpassword", 1L, "newpassword");
        assertFalse(failureResult);
        verify(userDAO, times(0)).updatePassword(anyLong(), anyString());
    }

    @Test
    void testDelete() {
        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(true);
        when(userDAO.delete(anyLong())).thenReturn(true);

        boolean successResult = userService.delete("johndoe", "password123", 1L);
        assertTrue(successResult);
        verify(userDAO, times(1)).delete(anyLong());
        reset(userDAO);
        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(false);

        boolean failureResult = userService.delete("johndoe", "wrongpassword", 1L);
        assertFalse(failureResult);
        verify(userDAO, times(0)).delete(anyLong());
    }

    @Test
    void testGetUserById() {
        User user = new User("John", "Doe", "johndoe", "password123", true);

        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(true);
        when(userDAO.findById(anyLong())).thenReturn(Optional.of(user));

        Optional<User> successResult = userService.getUserById("johndoe", "password123", 1L);

        assertTrue(successResult.isPresent());
        assertEquals("johndoe", successResult.get().getUsername());
        verify(userDAO, times(1)).findById(anyLong());
        reset(userDAO);

        when(userDAO.checkUsernameAndPassword(anyString(), anyString())).thenReturn(false);
        Optional<User> failureResult = userService.getUserById("johndoe", "wrongpassword", 1L);

        assertFalse(failureResult.isPresent());
        verify(userDAO, times(0)).findById(anyLong());
    }

}
