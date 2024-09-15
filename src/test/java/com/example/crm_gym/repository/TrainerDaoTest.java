package com.example.crm_gym.repository;

import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.models.Trainer;
import com.example.crm_gym.models.Training;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainerDaoTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private UserDaoImpl userDao;

    @Mock
    private TrainingDaoImpl trainingDao;

    @InjectMocks
    private TrainerDaoImpl trainerDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        Trainer trainer = new Trainer();
        doNothing().when(entityManager).persist(any(Trainer.class));
        boolean successResult = trainerDao.save(trainer);
        assertTrue(successResult);
        verify(entityManager, times(1)).persist(trainer);

        reset(entityManager);

        doThrow(new RuntimeException()).when(entityManager).persist(any(Trainer.class));
        Exception exception = assertThrows(DaoException.class, () -> {
            trainerDao.save(trainer);
        });

        assertTrue(exception.getMessage().contains("Error saving trainer"));
        verify(entityManager, times(1)).persist(trainer);
    }


    @Test
    void testAddTrainee() {
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();

        when(entityManager.find(Trainer.class, trainer.getId())).thenReturn(trainer);
        when(entityManager.find(Trainee.class, trainee.getId())).thenReturn(trainee);

        boolean successResult = trainerDao.addTrainee(trainer.getId(), trainee.getId());

        assertTrue(successResult);
        assertTrue(trainer.getTrainees().contains(trainee));
        verify(entityManager, times(1)).find(Trainer.class, trainer.getId());
        verify(entityManager, times(1)).find(Trainee.class, trainee.getId());
        verify(entityManager, times(1)).merge(trainer);

        reset(entityManager);

        when(entityManager.find(Trainer.class, trainer.getId())).thenReturn(null);

        boolean failureResult = trainerDao.addTrainee(trainer.getId(), trainee.getId());

        assertFalse(failureResult);
        verify(entityManager, times(1)).find(Trainer.class, trainer.getId());
    }

    @Test
    void testFindById() {
        Trainer trainer = new Trainer();
        when(entityManager.find(Trainer.class, trainer.getId())).thenReturn(trainer);

        Optional<Trainer> result = trainerDao.findById(trainer.getId());

        assertTrue(result.isPresent());
        assertEquals(trainer.getId(), result.get().getId());
        verify(entityManager, times(1)).find(Trainer.class, trainer.getId());
    }

    @Test
    void testAddTraining() {
        Trainer trainer = new Trainer();
        Training training = new Training();

        when(entityManager.find(Trainer.class, trainer.getId())).thenReturn(trainer);
        when(entityManager.find(Training.class, training.getId())).thenReturn(training);

        boolean successResult = trainerDao.addTraining(trainer.getId(), training.getId());

        assertTrue(successResult);
        assertTrue(trainer.getTrainings().contains(training));
        verify(entityManager, times(1)).find(Trainer.class, trainer.getId());
        verify(entityManager, times(1)).find(Training.class, training.getId());
        verify(entityManager, times(1)).merge(trainer);

        reset(entityManager);

        when(entityManager.find(Trainer.class, trainer.getId())).thenReturn(null);

        boolean failureResult = trainerDao.addTraining(trainer.getId(), training.getId());

        assertFalse(failureResult);
        verify(entityManager, times(1)).find(Trainer.class, trainer.getId());
    }

}
