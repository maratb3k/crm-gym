package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.models.Training;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private TrainingDAO trainingDAO;

    public void createTraining(Training training) {
        logger.info("Creating training: {}", training);
        try {
            trainingDAO.save(training);
            logger.info("Training created successfully: {}", training);
        } catch (Exception e) {
            logger.error("Error creating training: {}", training, e);
        }
    }

    public void updateTraining(int id, Training training) {
        logger.info("Updating training with ID {}: {}", id, training);
        try {
            trainingDAO.update(id, training);
            logger.info("Training updated successfully: {}", training);
        } catch (Exception e) {
            logger.error("Error updating training with ID {}: {}", id, e);
        }
    }

    public void deleteTraining(int id) {
        logger.info("Deleting training with ID {}", id);
        try {
            trainingDAO.delete(id);
            logger.info("Training deleted successfully with ID {}", id);
        } catch (Exception e) {
            logger.error("Error deleting training with ID {}", id, e);
        }
    }

    public Training getTraining(int id) {
        logger.info("Fetching training with ID {}", id);
        try {
            Training training = trainingDAO.findById(id);
            logger.info("Training fetched successfully: {}", training);
            return training;
        } catch (Exception e) {
            logger.error("Error fetching training with ID {}", id, e);
            return null;
        }
    }

    public List<Training> getAllTrainings() {
        logger.info("Fetching all trainings");
        try {
            List<Training> trainings = trainingDAO.findAll();
            logger.info("All trainings fetched successfully, count: {}", trainings.size());
            return trainings;
        } catch (Exception e) {
            logger.error("Error fetching all trainings", e);
            return null;
        }
    }
}
