package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TrainingServiceTest {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    @Mock
    private TrainingDAO trainingDAO;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTraining() throws ParseException {
        Training training = new Training(1, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55");
        when(trainingDAO.save(training)).thenReturn(true);
        boolean result = trainingService.createTraining(training);
        assertTrue(result);
        verify(trainingDAO, times(1)).save(training);
    }

    @Test
    void testUpdateTraining() throws ParseException {
        Training training = new Training(1, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55");
        when(trainingDAO.update(1, training)).thenReturn(true);
        boolean result = trainingService.updateTraining(1, training);
        assertTrue(result);
        verify(trainingDAO, times(1)).update(1, training);
    }

    @Test
    void testDeleteTraining() {
        when(trainingDAO.delete(1)).thenReturn(true);
        boolean result = trainingService.deleteTraining(1);
        assertTrue(result);
        verify(trainingDAO, times(1)).delete(1);
    }

    @Test
    void testGetTraining() throws ParseException {
        Training training = new Training(1, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55");
        when(trainingDAO.findById(1)).thenReturn(Optional.of(training));
        Optional<Training> result = trainingService.getTraining(1);
        assertTrue(result.isPresent());
        assertEquals(training, result.get());
        verify(trainingDAO, times(1)).findById(1);
    }

    @Test
    void testGetAllTrainings() throws ParseException {
        List<Training> trainings = Arrays.asList(new Training(1, 2,4,"name1", TrainingType.STRENGTH_TRAINING, formatter.parse("29-08-2024"), "55"));
        when(trainingDAO.findAll()).thenReturn(Optional.of(trainings));
        List<Training> result = trainingService.getAllTrainings();
        assertEquals(trainings, result);
        verify(trainingDAO, times(1)).findAll();
    }
}
