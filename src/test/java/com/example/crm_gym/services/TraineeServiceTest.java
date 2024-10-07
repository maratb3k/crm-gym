package com.example.crm_gym.services;

import com.example.crm_gym.dao.TraineeDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        user.setUsername("John.Doe");
        trainee = new Trainee(new Date(), "St.April, 123", user);

        User trainerUser1 = new User("Jane", "Doe");
        trainerUser1.setUsername("Jane.Doe");
        User trainerUser2 = new User("Alex", "Smith");
        trainerUser2.setUsername("Alex.Smith");
        Trainer trainer1 = new Trainer(null, trainerUser1);
        Trainer trainer2 = new Trainer(null, trainerUser2);

        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);
    }

    @Test
    void testCreateTrainee() {
        String transactionId = "12345";

        when(traineeDAO.save(any(Trainee.class))).thenReturn(Optional.of(trainee));

        Optional<Trainee> createdTrainee = traineeService.create("John", "Doe", new Date(), "St.April, 123", transactionId);

        assertTrue(createdTrainee.isPresent());
        assertEquals("John", createdTrainee.get().getUser().getFirstName());
        assertEquals("Doe", createdTrainee.get().getUser().getLastName());
        verify(traineeDAO, times(1)).save(any(Trainee.class));

        when(traineeDAO.save(any(Trainee.class))).thenThrow(new RuntimeException("Database error"));

        ServiceException exception = assertThrows(ServiceException.class, () ->
                traineeService.create("John", "Doe", new Date(), "St.April, 123", transactionId)
        );

        assertEquals("[Transaction ID: 12345] - 12345.Error creating trainee", exception.getMessage());
        verify(traineeDAO, times(2)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() {
        String transactionId = "12345";

        when(traineeDAO.findByUsername(anyString())).thenReturn(Optional.of(trainee));
        when(userDAO.update(any(User.class))).thenReturn(Optional.of(user));
        when(traineeDAO.update(any(Trainee.class))).thenReturn(Optional.of(trainee));

        Optional<Trainee> updatedTrainee = traineeService.update(trainee, transactionId);

        assertTrue(updatedTrainee.isPresent());
        assertEquals("John", updatedTrainee.get().getUser().getFirstName());
        assertEquals("Doe", updatedTrainee.get().getUser().getLastName());
        verify(traineeDAO, times(1)).findByUsername(anyString());
        verify(userDAO, times(1)).update(any(User.class));
        verify(traineeDAO, times(1)).update(any(Trainee.class));

        when(traineeDAO.findByUsername(anyString())).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () ->
                traineeService.update(trainee, transactionId)
        );
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeDAO, times(2)).findByUsername(anyString());
        verify(userDAO, times(1)).update(any(User.class));

        when(traineeDAO.findByUsername(anyString())).thenReturn(Optional.of(trainee));
        doThrow(new IllegalArgumentException("Invalid input data")).when(userDAO).update(any(User.class));

        exception = assertThrows(ServiceException.class, () ->
                traineeService.update(trainee, transactionId)
        );
        assertEquals("Invalid or empty input data for trainer update", exception.getMessage());
        verify(userDAO, times(2)).update(any(User.class));
    }

    @Test
    void testUpdateTraineeActiveStatus() {
        String transactionId = "transaction123";

        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeDAO.update(any(Trainee.class))).thenReturn(Optional.of(trainee));

        boolean result = traineeService.updateTraineeActiveStatus("John.Doe", true, transactionId);

        assertTrue(result);
        assertTrue(trainee.getUser().isActive());
        verify(traineeDAO, times(1)).findByUsername("John.Doe");
        verify(traineeDAO, times(1)).update(trainee);

        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.empty());

        result = traineeService.updateTraineeActiveStatus("John.Doe", true, transactionId);

        assertFalse(result);
        verify(traineeDAO, times(2)).findByUsername("John.Doe");

        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        doThrow(new RuntimeException("Database error")).when(traineeDAO).update(any(Trainee.class));

        ServiceException exception = assertThrows(ServiceException.class, () ->
                traineeService.updateTraineeActiveStatus("John.Doe", true, transactionId)
        );

        assertEquals("Error updating trainee with username: John.Doe", exception.getMessage());
        verify(traineeDAO, times(3)).findByUsername("John.Doe");
        verify(traineeDAO, times(2)).update(any(Trainee.class));
    }
}
