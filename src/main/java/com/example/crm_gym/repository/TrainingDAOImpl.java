package com.example.crm_gym.repository;

import com.example.crm_gym.dao.TrainingDAO;
import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.models.Training;
import com.example.crm_gym.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class TrainingDAOImpl implements TrainingDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);

    private final Storage storage;

    @Autowired
    public TrainingDAOImpl(Storage storage) {
        this.storage = storage;
    }

    @Override
    public boolean save(Training training) {
        try {
            storage.save(storage.getTrainings(), training.getId(), training);
            return true;
        } catch (Exception e) {
            logger.error("Error saving training: {}", training, e);
            return false;
        }
    }

    @Override
    public boolean update(int id, Training training) {
        try {
            storage.update(storage.getTrainings(), id, training);
            return true;
        } catch (Exception e) {
            logger.error("Error updating training with id: {}", id, e);
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            storage.remove(storage.getTrainings(), id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting training with id: {}", id, e);
            return false;
        }
    }

    @Override
    public Optional<Training> findById(int id) {
        try {
            Training training = storage.get(storage.getTrainings(), id);
            if (training == null) {
                logger.warn("No training found with id: {}", id);
                throw new DaoException("No training found with id: " + id);
            }
            return Optional.of(training);
        } catch (Exception e) {
            logger.error("Error finding training with id: {}", id, e);
            throw new DaoException("Error finding training with id: " + id, e);
        }
    }

    @Override
    public Optional<List<Training>> findAll() {
        try {
            List<Training> trainings = storage.findAll(storage.getTrainings());
            if (trainings == null || trainings.isEmpty()) {
                logger.warn("No trainings found.");
                throw new DaoException("No trainings found.");
            }
            return Optional.of(trainings);
        } catch (Exception e) {
            logger.error("Error retrieving all trainings", e);
            throw new DaoException("Error retrieving all trainings", e);
        }
    }
}
