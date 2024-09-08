package com.example.crm_gym.dao;

import com.example.crm_gym.config.AppConfig;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class TrainingDaoTest {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private TrainingDAO trainingDAO;

    @BeforeEach
    void setUp() {
    }

    @Test
    @Sql("/application.properties/initial_data.sql")
    void testSaveTrainer() throws ParseException {
        Training training = new Training(20, 3, 5, "Yoga group 2", TrainingType.YOGA, formatter.parse("28-08-2024"), "58");
        trainingDAO.save(training);

        Optional<Training> found = trainingDAO.findById(20);

        assertTrue(found.isPresent());
        assertEquals(training.getTraineeId(), found.get().getTraineeId());
        assertEquals(training.getTrainerId(), found.get().getTrainerId());
        assertEquals(training.getTrainingName(), found.get().getTrainingName());
        assertEquals(training.getTrainingType(), found.get().getTrainingType());
        assertEquals(training.getTrainingDate(), found.get().getTrainingDate());
        assertEquals(training.getTrainingDuration(), found.get().getTrainingDuration());
    }

    @Test
    void testUpdateTraining() throws ParseException {
        Training training = new Training(20, 3, 5, "Yoga group 2", TrainingType.YOGA, formatter.parse("28-08-2024"), "58");
        trainingDAO.save(training);

        Training updatedTraining = new Training(20, 3, 5, "Yoga group 2", TrainingType.YOGA, formatter.parse("28-08-2024"), "58");
        trainingDAO.update(20, updatedTraining);

        Optional<Training> found = trainingDAO.findById(20);
        assertTrue(found.isPresent());
        assertEquals(updatedTraining.getTraineeId(), found.get().getTraineeId());
        assertEquals(updatedTraining.getTrainerId(), found.get().getTrainerId());
        assertEquals(updatedTraining.getTrainingName(), found.get().getTrainingName());
        assertEquals(updatedTraining.getTrainingType(), found.get().getTrainingType());
        assertEquals(updatedTraining.getTrainingDate(), found.get().getTrainingDate());
        assertEquals(updatedTraining.getTrainingDuration(), found.get().getTrainingDuration());
    }

    @Test
    void testDeleteTraining() throws ParseException {
        int id = 20;
        Training training = new Training(id, 3, 5, "Yoga group 2", TrainingType.YOGA, formatter.parse("28-08-2024"), "58");
        trainingDAO.save(training);
        Optional<Training> foundBeforeDelete = trainingDAO.findById(id);
        assertTrue(foundBeforeDelete.isPresent());

        trainingDAO.delete(id);

        DaoException exception = assertThrows(DaoException.class, () -> {
            trainingDAO.findById(id);
        });

        assertEquals("Error finding training with id: " + id, exception.getMessage());
    }

    @Test
    void testFindAllTrainings() {
        Optional<List<Training>> optionalTrainings = trainingDAO.findAll();
        assertTrue(optionalTrainings.isPresent());
        List<Training> trainings = optionalTrainings.get();
        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
    }
}
