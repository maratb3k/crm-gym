package com.example.crm_gym.services;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Training;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private TrainingDAO trainingDAO;

    public boolean createTraining(Training training) {
        try {
            return trainingDAO.save(training);
        } catch (Exception e) {
            logger.error("Error creating training: {}", training, e);
            return false;
        }
    }

    public boolean updateTraining(int id, Training training) {
        try {
            return trainingDAO.update(id, training);
        } catch (Exception e) {
            logger.error("Error updating training with ID {}: {}", id, e);
            return false;
        }
    }

    public boolean deleteTraining(int id) {
        try {
            return trainingDAO.delete(id);
        } catch (Exception e) {
            logger.error("Error deleting training with ID {}", id, e);
            return false;
        }
    }

    public Optional<Training> getTraining(int id) {
        try {
            return trainingDAO.findById(id);
        } catch (DaoException e) {
            logger.error("Error fetching training with ID {}", id, e);
            return Optional.empty();
        }
    }

    public List<Training> getAllTrainings() {
        try {
            Optional<List<Training>> trainings = trainingDAO.findAll();
            if (trainings.isPresent()) {
                return trainings.get();
            } else {
                logger.warn("No trainings found.");
                return Collections.emptyList();
            }
        } catch (DaoException e) {
            logger.error("Error fetching all trainings", e);
            return Collections.emptyList();
        }
    }
}
