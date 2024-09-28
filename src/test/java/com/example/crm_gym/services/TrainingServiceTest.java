package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.exception.ServiceException;
import com.example.crm_gym.models.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;

    @InjectMocks
    private TrainingService trainingService;

    private Training training;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        training = new Training();
        training.setId(1L);
        training.setTrainingName("Strength Training");
    }

    @Test
    void testCreateTraining() {
        when(trainingDAO.save(any(Training.class))).thenReturn(Optional.of(training));

        Optional<Training> createdTraining = trainingService.create(training);

        assertTrue(createdTraining.isPresent());
        assertEquals("Strength Training", createdTraining.get().getTrainingName());
        verify(trainingDAO, times(1)).save(any(Training.class));
    }

    @Test
    void testCreateTrainingThrowsException() {
        when(trainingDAO.save(any(Training.class))).thenThrow(new ServiceException("Error creating training"));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingService.create(training);
        });

        assertEquals("Error creating training", exception.getMessage());
        verify(trainingDAO, times(1)).save(any(Training.class));
    }

    @Test
    void testUpdateTraining() {
        when(trainingDAO.findById(1L)).thenReturn(Optional.of(training));
        when(trainingDAO.update(any(Training.class))).thenReturn(Optional.of(training));

        Optional<Training> updatedTraining = trainingService.update(training);

        assertTrue(updatedTraining.isPresent());
        verify(trainingDAO, times(1)).findById(1L);
        verify(trainingDAO, times(1)).update(any(Training.class));
    }

    @Test
    void testUpdateTrainingThrowsException() {
        when(trainingDAO.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingService.update(training);
        });

        assertEquals("Training not found", exception.getMessage());
        verify(trainingDAO, times(1)).findById(1L);
        verify(trainingDAO, times(0)).update(any(Training.class));
    }

    @Test
    void testDeleteTraining() {
        when(trainingDAO.findById(1L)).thenReturn(Optional.of(training));
        when(trainingDAO.delete(training)).thenReturn(true);

        boolean result = trainingService.delete(1L);

        assertTrue(result);
        verify(trainingDAO, times(1)).findById(1L);
        verify(trainingDAO, times(1)).delete(training);
    }

    @Test
    void testDeleteTrainingThrowsException() {
        when(trainingDAO.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingService.delete(1L);
        });

        assertEquals("Trainer not found", exception.getMessage());
        verify(trainingDAO, times(1)).findById(1L);
        verify(trainingDAO, times(0)).delete(any(Training.class));
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainingList = Arrays.asList(training);
        when(trainingDAO.findAll()).thenReturn(Optional.of(trainingList));

        List<Training> result = trainingService.getAllTrainings();

        assertEquals(1, result.size());
        verify(trainingDAO, times(1)).findAll();
    }

    @Test
    void testGetAllThrowsException() {
        when(trainingDAO.findAll()).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            trainingService.getAllTrainings();
        });

        assertEquals("No trainings found.", exception.getMessage());
        verify(trainingDAO, times(1)).findAll();
    }
}
