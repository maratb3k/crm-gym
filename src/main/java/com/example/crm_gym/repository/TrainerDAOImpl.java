package com.example.crm_gym.repository;

import com.example.crm_gym.exception.DaoException;
import com.example.crm_gym.storage.Storage;
import com.example.crm_gym.dao.TrainerDAO;
import com.example.crm_gym.models.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class TrainerDAOImpl implements TrainerDAO {
    private static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);

    private Storage storage;

    @Autowired
    public TrainerDAOImpl(Storage storage) {
        this.storage = storage;
    }

    @Override
    public boolean save(Trainer trainer) {
        try {
            storage.save(storage.getTrainers(), trainer.getUserId(), trainer);
            return true;
        } catch (Exception e) {
            logger.error("Error saving trainer: {}", trainer, e);
            return false;
        }
    }

    @Override
    public boolean update(int userId, Trainer trainer) {
        try {
            storage.update(storage.getTrainers(), userId, trainer);
            return true;
        } catch (Exception e) {
            logger.error("Error updating trainer with userId: {}", userId, e);
            return false;
        }
    }

    @Override
    public boolean delete(int userId) {
        try {
            storage.remove(storage.getTrainers(), userId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting trainer with userId: {}", userId, e);
            return false;
        }
    }

    @Override
    public Optional<Trainer> findById(int userId) {
        try {
            Trainer trainer = storage.get(storage.getTrainers(), userId);
            if (trainer == null) {
                logger.warn("No trainer found with id: {}", userId);
                throw new DaoException("No trainer found with id: " + userId);
            }
            return Optional.of(trainer);
        } catch (Exception e) {
            logger.error("Error finding trainer with id: {}", userId, e);
            throw new DaoException("Error finding trainer with id: " + userId, e);
        }
    }

    @Override
    public Optional<List<Trainer>> findAll() {
        try {
            List<Trainer> trainers = storage.findAll(storage.getTrainers());
            if (trainers == null || trainers.isEmpty()) {
                logger.warn("No trainers found.");
                throw new DaoException("No trainers found.");
            }
            return Optional.of(trainers);
        } catch (Exception e) {
            logger.error("Error retrieving all trainers", e);
            throw new DaoException("Error retrieving all trainers", e);
        }
    }
}
