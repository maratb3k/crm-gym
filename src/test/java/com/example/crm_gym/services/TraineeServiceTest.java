package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.User;
import com.example.crm_gym.services.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("John", "Doe");
        trainee = new Trainee(new Date(), "123 Street", user);
    }

    @Test
    void testCreateTrainee() {
        when(traineeDAO.save(any(Trainee.class))).thenReturn(Optional.of(trainee));

        Optional<Trainee> createdTrainee = traineeService.create("John", "Doe", new Date(), "123 Street");

        assertTrue(createdTrainee.isPresent());
        assertEquals("John", createdTrainee.get().getUser().getFirstName());
        verify(traineeDAO, times(1)).save(any(Trainee.class));
    }

    @Test
    void testCreateTraineeThrowsException() {
        when(traineeDAO.save(any(Trainee.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(ServiceException.class, () -> {
            traineeService.create("John", "Doe", new Date(), "123 Street");
        });

        assertEquals("Error creating trainee", exception.getMessage());
        verify(traineeDAO, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() {
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeDAO.update(any(Trainee.class))).thenReturn(Optional.of(trainee));

        trainee.getUser().setFirstName("Updated Name");
        Optional<Trainee> updatedTrainee = traineeService.update(trainee);

        assertTrue(updatedTrainee.isPresent());
        assertEquals("Updated Name", updatedTrainee.get().getUser().getFirstName());
        verify(traineeDAO, times(1)).findByUsername("john_doe");
        verify(traineeDAO, times(1)).update(any(Trainee.class));
    }

    @Test
    void testUpdateThrowsException() {
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ServiceException.class, () -> {
            traineeService.update(trainee);
        });

        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeDAO, times(1)).findByUsername("John.Doe");
        verify(traineeDAO, times(0)).update(any(Trainee.class));
    }
}
