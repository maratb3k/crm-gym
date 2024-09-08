package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.models.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TrainerServiceTest {
    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainer() {
        String firstName = "Emma";
        String lastName = "Stown";
        String spec = "spec2";
        List<Trainer> existingTrainers = new ArrayList<>();
        when(trainerDAO.findAll()).thenReturn(Optional.of(existingTrainers));
        when(trainerDAO.save(any(Trainer.class))).thenReturn(true);

        boolean result = trainerService.createTrainer(5, firstName, lastName, spec);

        assertTrue(result, "The trainer should be created successfully.");
        verify(trainerDAO, times(1)).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer(55, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        when(trainerDAO.update(55, trainer)).thenReturn(true);

        boolean result = trainerService.updateTrainer(55, trainer);
        assertTrue(result);
        verify(trainerDAO, times(1)).update(55, trainer);
    }

    @Test
    void testDeleteTrainer() {
        when(trainerDAO.delete(1)).thenReturn(true);
        boolean result = trainerService.deleteTrainer(1);
        assertTrue(result);
        verify(trainerDAO, times(1)).delete(1);
    }

    @Test
    void testGetTrainer() {
        Trainer trainer = new Trainer(10, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        when(trainerDAO.findById(10)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.getTrainer(10);
        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
        verify(trainerDAO, times(1)).findById(10);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = Arrays.asList(new Trainer(1, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2"));
        when(trainerDAO.findAll()).thenReturn(Optional.of(trainers));
        List<Trainer> result = trainerService.getAllTrainers();
        assertEquals(trainers, result);
        verify(trainerDAO, times(1)).findAll();
    }
}
