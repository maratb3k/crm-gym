package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainingDAOImpl implements TrainingDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);

    private final Storage storage;
    private Map<Integer, Training> trainingStorage;

    @Autowired
    public TrainingDAOImpl(Storage storage) {
        this.storage = storage;
        this.trainingStorage = storage.getTrainings();
        logger.info("TrainingDAOImpl initialized with Storage: {}", storage);
    }

    @Override
    public void save(Training training) {
        logger.info("Entering save() with training: {}", training);
        try {
            storage.save(trainingStorage, training.getId(), training);
            logger.info("Training saved successfully: {}", training);
        } catch (Exception e) {
            logger.error("Error saving training: {}", training, e);
        } finally {
            logger.info("Exiting save() with training: {}", training);
        }
    }

    @Override
    public void update(int id, Training training) {
        logger.info("Entering update() with id: {} and training: {}", id, training);
        try {
            storage.update(trainingStorage, id, training);
            logger.info("Training updated successfully: {}", training);
        } catch (Exception e) {
            logger.error("Error updating training with id: {}", id, e);
        } finally {
            logger.info("Exiting update() with id: {}", id);
        }
    }

    @Override
    public void delete(int id) {
        logger.info("Entering delete() with id: {}", id);
        try {
            storage.remove(trainingStorage, id);
            logger.info("Training deleted successfully with id: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting training with id: {}", id, e);
        } finally {
            logger.info("Exiting delete() with id: {}", id);
        }
    }

    @Override
    public Training findById(int id) {
        logger.info("Entering findById() with id: {}", id);
        try {
            Training training = storage.get(trainingStorage, id);
            if (training != null) {
                logger.info("Training found: {}", training);
            } else {
                logger.warn("No training found with id: {}", id);
            }
            return training;
        } catch (Exception e) {
            logger.error("Error finding training with id: {}", id, e);
            return null;
        } finally {
            logger.info("Exiting findById() with id: {}", id);
        }
    }

    @Override
    public List<Training> findAll() {
        logger.info("Entering findAll()");
        try {
            List<Training> trainings = storage.findAll(trainingStorage);
            logger.info("Returning all trainings, count: {}", trainings.size());
            return trainings;
        } catch (Exception e) {
            logger.error("Error retrieving all trainings", e);
            return null;
        } finally {
            logger.info("Exiting findAll()");
        }
    }
}
