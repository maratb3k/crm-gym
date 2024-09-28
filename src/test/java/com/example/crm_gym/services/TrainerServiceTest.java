package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.dao.TrainingTypeDAO;
import com.example.crm_gym.dao.UserDAO;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.TrainingType;
import com.example.crm_gym.models.TrainingTypeName;
import com.example.crm_gym.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private TrainingTypeDAO trainingTypeDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;
    private User user;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("John", "Doe");
        trainingType = new TrainingType(TrainingTypeName.CARDIO);
        trainer = new Trainer(trainingType, user);
    }

    @Test
    void testCreateTrainer() {
        when(trainingTypeDAO.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerDAO.save(any(Trainer.class))).thenReturn(Optional.of(trainer));

        Optional<Trainer> createdTrainer = trainerService.create("John", "Doe", 1L);

        assertTrue(createdTrainer.isPresent());
        assertEquals("John", createdTrainer.get().getUser().getFirstName());
        verify(trainerDAO, times(1)).save(any(Trainer.class));
    }

    @Test
    void testCreateTrainerThrowsException() {
        when(trainingTypeDAO.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainerService.create("John", "Doe", 1L);
        });

        assertEquals("Training type not found", exception.getMessage());
        verify(trainerDAO, times(0)).save(any(Trainer.class));
    }

    @Test
    void testUpdatePassword() {
        when(trainerDAO.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerDAO.update(trainer)).thenReturn(Optional.of(trainer));

        boolean result = trainerService.updatePassword(1L, "newPassword");

        assertTrue(result);
        assertEquals("newPassword", trainer.getUser().getPassword());
        verify(trainerDAO, times(1)).findById(1L);
        verify(trainerDAO, times(1)).update(trainer);
    }

    @Test
    void testUpdatePasswordThrowsException() {
        when(trainerDAO.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainerService.updatePassword(1L, "newPassword");
        });

        assertEquals("Trainer not found", exception.getMessage());
        verify(trainerDAO, times(1)).findById(1L);
        verify(trainerDAO, times(0)).update(any(Trainer.class));
    }
}
