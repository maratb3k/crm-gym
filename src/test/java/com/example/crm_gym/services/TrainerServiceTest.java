package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.models.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Trainer trainer = new Trainer(1, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        doNothing().when(trainerDAO).save(any(Trainer.class));
        trainerService.createTrainer(trainer.getUserId(), trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization());
        verify(trainerDAO, times(1)).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer(55, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        doNothing().when(trainerDAO).update(55, trainer);

        trainerService.updateTrainer(55, trainer);

        verify(trainerDAO, times(1)).update(55, trainer);
    }

    @Test
    void testDeleteTrainer() {
        doNothing().when(trainerDAO).delete(1);
        trainerService.deleteTrainer(1);
        verify(trainerDAO, times(1)).delete(1);
    }

    @Test
    void testGetTrainer() {
        Trainer trainer = new Trainer(11, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2");
        when(trainerDAO.findById(11)).thenReturn(trainer);

        Trainer result = trainerService.getTrainer(11);

        assertEquals(trainer, result);
        verify(trainerDAO, times(1)).findById(11);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = Arrays.asList(new Trainer(1, "Emma", "Stown", "emmastown@gmail.com", "pa11s", true, "spec2"));
        when(trainerDAO.findAll()).thenReturn(trainers);
        List<Trainer> result = trainerService.getAllTrainers();
        assertEquals(trainers, result);
        verify(trainerDAO, times(1)).findAll();
    }
}
