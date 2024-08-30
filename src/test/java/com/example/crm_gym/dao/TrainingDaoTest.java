package com.example.crm_gym.dao;

import com.example.crm_gym.models.Training;
import com.example.crm_gym.models.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

        Training found = trainingDAO.findById(20);

        assertNotNull(found);
        assertEquals(training.getTraineeId(), found.getTraineeId());
        assertEquals(training.getTrainerId(), found.getTrainerId());
        assertEquals(training.getTrainingName(), found.getTrainingName());
        assertEquals(training.getTrainingType(), found.getTrainingType());
        assertEquals(training.getTrainingDate(), found.getTrainingDate());
        assertEquals(training.getTrainingDuration(), found.getTrainingDuration());
    }

    @Test
    void testUpdateTraining() throws ParseException {
        Training training = new Training(20, 3, 5, "Yoga group 2", TrainingType.YOGA, formatter.parse("28-08-2024"), "58");
        trainingDAO.save(training);

        Training updatedTraining = new Training(20, 3, 5, "Yoga group 2", TrainingType.YOGA, formatter.parse("28-08-2024"), "58");
        trainingDAO.update(20, updatedTraining);

        Training found = trainingDAO.findById(20);
        assertNotNull(found);
        assertEquals(updatedTraining.getTraineeId(), found.getTraineeId());
        assertEquals(updatedTraining.getTrainerId(), found.getTrainerId());
        assertEquals(updatedTraining.getTrainingName(), found.getTrainingName());
        assertEquals(updatedTraining.getTrainingType(), found.getTrainingType());
        assertEquals(updatedTraining.getTrainingDate(), found.getTrainingDate());
        assertEquals(updatedTraining.getTrainingDuration(), found.getTrainingDuration());
    }

    @Test
    void testDeleteTraining() throws ParseException {
        Training training = new Training(20, 3, 5, "Yoga group 2", TrainingType.YOGA, formatter.parse("28-08-2024"), "58");
        trainingDAO.save(training);

        trainingDAO.delete(20);

        Training found = trainingDAO.findById(20);
        assertNull(found);
    }

    @Test
    void testFindAllTrainings() {
        List<Training> trainings = trainingDAO.findAll();
        assertNotNull(trainings);
        assertTrue(trainings.size() > 0);
    }
}
